package org.lukasj.idea.torquescript.telnet


import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

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
    private var input: BufferedReader? = null
    private var output: PrintWriter? = null
    private val loginQueue = ArrayBlockingQueue<Boolean>(1)
    val evalSubscribers = ConcurrentHashMap<String, ArrayBlockingQueue<String>>()
    val outputQueue = ArrayBlockingQueue<String>(100)
    val breakpointQueue = ArrayBlockingQueue<TSExecutionStackLines>(100)
    val movedBreakpointQueue = ArrayBlockingQueue<TSBreakpointMovedEvent>(100)
    private var thread: Thread? = null
    private var isStopped = false

    private fun retry(retries: Int, fn: () -> Unit) {
        for (i in 1..retries) {
            try {
                fn()
                break
            } catch (ex: Exception) {
                if (i == retries) {
                    throw ex
                }

                Thread.sleep(1000)
                continue
            }
        }
    }

    private fun processOutput(line: String) {
        val trimmed = line.trim()
        when {
            trimmed.startsWith("PASS") -> {
                loginQueue.add(trimmed == "PASS Connected.")
            }
            trimmed.startsWith("COUT") -> {
                outputQueue.add(trimmed.substring(4).trim())
            }
            trimmed.startsWith("BREAK") -> {
                breakpointQueue.add(TSExecutionStackLines(trimmed.substring(5).trim()))
            }
            trimmed.startsWith("BRKMOV") -> {
                trimmed.substring(7)
                    .trim()
                    .split(' ')
                    .let {
                        movedBreakpointQueue.add(TSBreakpointMovedEvent(it[0], it[1].toInt() - 1, it[2].toInt() - 1))
                    }
            }
            trimmed.startsWith("BRKCLR") -> {
                trimmed.substring(7)
                    .trim()
                    .split(' ')
                    .let {
                        movedBreakpointQueue.add(TSBreakpointMovedEvent(it[0], it[1].toInt() - 1, null))
                    }
            }
            trimmed.startsWith("EVALOUT") -> {
                line.substring(8).split(' ').let {
                    val subQueue = evalSubscribers[it[0]]
                    evalSubscribers.remove(it[0])
                    subQueue?.add(it.drop(1).joinToString(" "))
                }
            }
            else ->
                println(trimmed)
        }
    }

    fun subscribeForEval(key: String): ArrayBlockingQueue<String> =
        ArrayBlockingQueue<String>(1).let { queue ->
            evalSubscribers[key] = queue
            queue
        }

    fun connect() {
        if (thread != null) {
            return
        }

        retry(3) {
            socket = Socket(address, port)
            input = BufferedReader(InputStreamReader(socket!!.getInputStream()))
            output = PrintWriter(socket!!.getOutputStream(), true)
        }

        thread = thread {
            try {
                while (!isStopped) {
                    val input = this.input
                    if (input != null && input.ready()) {
                        processOutput(input.readLine())
                    }
                    Thread.sleep(10)
                }
            } catch (ioe: IOException) {
                // Do nothing
            }
        }
    }

    fun disconnect() {
        runBlocking {
            isStopped = true
            thread?.join()
            thread = null
            withTimeout(5000) {
                socket?.close()
            }
        }
    }

    private fun writeString(string: String) =
        output!!.println(string)

    fun login(password: String): Boolean {
        writeString(password)
        return loginQueue.poll(3, TimeUnit.SECONDS) ?: false
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
        val queue = subscribeForEval(tag)
        writeString("EVAL $tag $level $expression")
        return queue.poll(5, TimeUnit.SECONDS) ?: "<Timeout in eval>"
    }
}
