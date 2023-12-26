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
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import kotlin.concurrent.thread
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class TSBreakPointStackLine(val file: String, val line: Int, val function: String)

class TSExecutionStackLines(rawLine: String) {
    val stackLines = rawLine.split(' ')
        .chunked(3)
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        .map { TSBreakPointStackLine(it[0], it[1].toInt() - 1, it[2]) }
}

class TSBreakpointMovedEvent(val file: String, val line: Int, val newLine: Int?)

class TSTelnetClient(private val address: String, private val port: Int) {
    private lateinit var receiverThread: Thread
    private lateinit var transmitterThread: Thread
    private var isStopped = true
    private var socket: Socket? = null
    private var inputStream: BufferedReader? = null
    private var outputStream: PrintWriter? = null
    private var telnetOutputChannel = ArrayBlockingQueue<String>(100)
    private val loginChannel = ArrayBlockingQueue<Boolean>(1)
    val output = ArrayBlockingQueue<String>(100)
    val breakpoints = ArrayBlockingQueue<TSExecutionStackLines>(100)
    val movedBreakpoints = ArrayBlockingQueue<TSBreakpointMovedEvent>(100)
    private val evalResults = ConcurrentHashMap<String,String>()
    private val outputFlow: MutableSharedFlow<String> = MutableSharedFlow()

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

    private fun processOutput(receivedLine: String) {
        val line = receivedLine.trim()
        when {
            line.startsWith("PASS") -> loginChannel.put(line == "PASS Connected.")
            line.startsWith("COUT") -> output.put(line.substring(4).trim())
            line.startsWith("BREAK") -> breakpoints.put(TSExecutionStackLines(line.substring(5).trim()))
            line.startsWith("BRKMOV") -> line.substring(7).trim().split(' ')
                .let {
                    movedBreakpoints.put(
                        TSBreakpointMovedEvent(
                            it[0],
                            it[1].toInt() - 1,
                            it[2].toInt() - 1
                        )
                    )
                }

            line.startsWith("BRKCLR") -> line.substring(7).trim().split(' ')
                .let {
                    movedBreakpoints.put(TSBreakpointMovedEvent(it[0], it[1].toInt() - 1, null))
                }

            line.startsWith("EVALOUT") -> line.substring(8).split(' ')
                .let {
                    evalResults.put(it[0], it.drop(1).joinToString(" "))
                }
            else -> logger<TSTelnetClient>().warn("unknown message from engine: $line")
        }
    }

    fun connect() {
        isStopped = false
        receiverThread = thread {
            while (!isStopped) {
                Thread.sleep(10)
                val input = inputStream ?: continue

                if (input.ready()) {
                    try {
                        val line = input.readLine() ?: continue

                        processOutput(line)
                    } catch (ioe: IOException) {
                        // Do nothing
                        disconnect()
                    }
                }
            }
        }

        transmitterThread = thread {
                socket = Socket(address, port)
                inputStream = socket!!.getInputStream().bufferedReader()
                outputStream = PrintWriter(socket!!.getOutputStream(), true)
            while (!isStopped) {
                val result = telnetOutputChannel.poll(1, TimeUnit.SECONDS) ?: continue
                outputStream!!.println(result)
            }
        }
    }

    fun disconnect() {
        isStopped = true
        output.clear()
        breakpoints.clear()
        movedBreakpoints.clear()
        socket?.close()
        transmitterThread.join()
        receiverThread.join()
    }

    private fun writeString(string: String) =
        telnetOutputChannel.put(string)

    fun login(password: String): Result<Unit> {
        writeString(password)
        val loginResult = loginChannel.poll(10, TimeUnit.SECONDS)
        return when (loginResult) {
            true -> {
                Result.success(Unit)
            }
            false -> {
                Result.failure(Exception("Login failed, password rejected"))
            }
            null -> {
                Result.failure(Exception("Login failed, timeout"))
            }
        }
    }

    fun pause() =
        writeString("BRKNEXT")

    fun resume() =
        writeString("CONTINUE")

    fun eval(cmd: String) =
        writeString("CEVAL $cmd")

    fun setBreakpoint(file: String, line: Int, clear: Boolean, passCount: Int, condition: String) =
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        writeString("BRKSET $file ${line + 1} ${if (clear) 1 else 0} $passCount $condition")

    fun clearBreakpoint(file: String, line: Int) =
        // TorqueScript has 1-index line numbers, IntelliJ uses 0-indexed
        writeString("BRKCLR $file ${line + 1}")

    fun stepOver() =
        writeString("STEPOVER")

    fun stepIn() =
        writeString("STEPIN")

    fun stepOut() =
        writeString("STEPOUT")

    fun evalAtLevel(level: Int, expression: String): String {
        val tag = UUID.randomUUID().toString()
        writeString("EVAL $tag $level $expression")
        val start = System.currentTimeMillis()
        while (!evalResults.containsKey(tag) && System.currentTimeMillis() - start < 10000) {
            Thread.sleep(100)
        }
        return evalResults.getOrDefault(tag, "<Timeout in eval>")
    }
}
