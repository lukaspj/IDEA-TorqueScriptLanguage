package org.lukasj.idea.torquescript.telnet


import com.intellij.openapi.diagnostic.logger
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.concurrent.TimeoutException

class TSBreakPointStackLine(val file: String, val line: Int, val function: String)

class TSExecutionStackLines(rawLine: String) {
    val stackLines = rawLine.split(' ')
        .chunked(3)
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        .map { TSBreakPointStackLine(it[0], it[1].toInt() - 1, it[2]) }
}

class TSBreakpointMovedEvent(val file: String, val line: Int, val newLine: Int?)

class TSTelnetClient(private val address: String, private val port: Int) {
    private var socket: Socket? = null
    private var inputStream: BufferedReader? = null
    private var outputStream: PrintWriter? = null
    private var telnetOutputChannel = Channel<String>(100)
    private val loginChannel = Channel<Boolean>(1)
    val output = Channel<String>(100)
    val breakpoints = Channel<TSExecutionStackLines>(100)
    val movedBreakpoints = Channel<TSBreakpointMovedEvent>(100)
    private val evalResults: MutableSharedFlow<Pair<String, String>> = MutableSharedFlow(100)
    private val outputFlow: MutableSharedFlow<String> = MutableSharedFlow()
    private val scope = CoroutineScope(Dispatchers.Default + CoroutineName("TSTelnetClient"))

    private suspend fun <T> retry(retries: Int, fn: suspend () -> Result<T>): Result<T> {
        for (i in 1..retries) {
            val result = fn()

            if (result.isSuccess || i >= retries) {
                return result
            } else {
                delay(1000)
            }
        }
        return Result.failure(TimeoutException("Failed to complete task after $retries retries"))
    }

    private fun processOutput() {
        scope.launch(CoroutineName("TSTelnetClient-PASS-listener")) {
            outputFlow.map(String::trim)
                .filter { it.startsWith("PASS") }
                .collect { loginChannel.send(it == "PASS Connected.") }
        }
        scope.launch(CoroutineName("TSTelnetClient-COUT-listener")) {
            outputFlow.map(String::trim)
                .filter { it.startsWith("COUT") }
                .collect { output.send(it.substring(4).trim()) }
        }
        scope.launch(CoroutineName("TSTelnetClient-BREAK-listener")) {
            outputFlow.map(String::trim)
                .filter { it.startsWith("BREAK") }
                .collect { breakpoints.send(TSExecutionStackLines(it.substring(5).trim())) }
        }
        scope.launch(CoroutineName("TSTelnetClient-BRKMOV-listener")) {
            outputFlow.map(String::trim)
                .filter { it.startsWith("BRKMOV") }
                .map { it.substring(7).trim().split(' ') }
                .collect {
                    movedBreakpoints.send(
                        TSBreakpointMovedEvent(
                            it[0],
                            it[1].toInt() - 1,
                            it[2].toInt() - 1
                        )
                    )
                }
        }
        scope.launch(CoroutineName("TSTelnetClient-BRKCLR-listener")) {
            outputFlow.map(String::trim)
                .filter { it.startsWith("BRKCLR") }
                .map { it.substring(7).trim().split(' ') }
                .collect { movedBreakpoints.send(TSBreakpointMovedEvent(it[0], it[1].toInt() - 1, null)) }
        }
        scope.launch(CoroutineName("EVALOUT-listener")) {
            outputFlow.filter { it.startsWith("EVALOUT") }
                .map { it.substring(8).split(' ') }
                .collect {
                    evalResults.emit(Pair(it[0], it.drop(1).joinToString(" ")))
                }
        }
    }

    fun connect() {
        processOutput()

        scope.launch(CoroutineName("TSTelnetClient-receiver")) {
            while (scope.isActive) {
                delay(10L)
                val input = inputStream ?: continue

                if (input.ready()) {
                    try {
                        withContext(Dispatchers.IO) {
                            val line = input.readLine() ?: return@withContext
                            outputFlow.emit(line)
                        }
                    } catch (ioe: IOException) {
                        // Do nothing
                        disconnect()
                    }
                }
            }
        }

        scope.launch(CoroutineExceptionHandler { _, t -> logger<TSTelnetClient>().warn("Ignored ${t.message}") } + CoroutineName(
            "TSTelnetClient-transmitter"
        )) {
            while (scope.isActive) {
                val result = runCatching {
                    withContext(Dispatchers.IO) {
                        socket = Socket(address, port)
                        inputStream = socket!!.getInputStream().bufferedReader()
                        outputStream = PrintWriter(socket!!.getOutputStream(), true)
                        telnetOutputChannel.consumeAsFlow()
                            .collect {
                                outputStream!!.println(it)
                            }
                    }
                }
                if (result.isSuccess) {
                    break
                }
            }
        }

        scope.launch {
            try {
                while (scope.isActive) {
                    val input = inputStream
                    withContext(Dispatchers.IO) {
                        if (input != null && input.ready()) {
                            val line = input.readLine()
                            outputFlow.emit(line)
                        }
                    }
                    delay(10)
                }
            } catch (ioe: IOException) {
                // Do nothing
                disconnect()
            }
        }
    }

    fun disconnect() {
        runBlocking {
            scope.cancel("Disconnect requested")
            output.close()
            breakpoints.close()
            movedBreakpoints.close()
            withTimeout(5000) {
                withContext(Dispatchers.IO) {
                    socket?.close()
                }
            }
        }
    }

    private suspend fun writeString(string: String) =
        telnetOutputChannel.send(string)

    suspend fun login(password: String): Result<Unit> {
        writeString(password)
        return if (loginChannel.receive()) {
            Result.success(Unit)
        } else {
            Result.failure(Exception("Login failed, password rejected"))
        }
    }

    suspend fun pause() =
        writeString("BRKNEXT")

    suspend fun resume() =
        writeString("CONTINUE")

    suspend fun eval(cmd: String) =
        writeString("CEVAL $cmd")

    suspend fun setBreakpoint(file: String, line: Int, clear: Boolean, passCount: Int, condition: String) =
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        writeString("BRKSET $file ${line + 1} ${if (clear) 1 else 0} $passCount $condition")

    suspend fun clearBreakpoint(file: String, line: Int) =
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        writeString("BRKCLR $file ${line + 1}")

    suspend fun stepOver() =
        writeString("STEPOVER")

    suspend fun stepIn() =
        writeString("STEPIN")

    suspend fun stepOut() =
        writeString("STEPOUT")

    suspend fun evalAtLevel(level: Int, expression: String): String {
        val tag = UUID.randomUUID().toString()
        writeString("EVAL $tag $level $expression")
        return withTimeoutOrNull(10000) {
            evalResults
                .filter { it.first == tag }
                .map { it.second }
                .first()
        } ?: "<Timeout in eval>"
    }
}
