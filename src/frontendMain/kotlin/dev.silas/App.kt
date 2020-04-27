package dev.silas

import dev.silas.Model.connectToServer
import dev.silas.Model.drawChannel
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.TouchEvent
import org.w3c.dom.events.MouseEvent
import org.w3c.dom.get
import pl.treksoft.kvision.Application
import pl.treksoft.kvision.core.onEvent
import pl.treksoft.kvision.html.canvas
import pl.treksoft.kvision.panel.root
import pl.treksoft.kvision.startApplication
import kotlin.browser.window

class App : Application() {

    data class CurrentState(
        var color: String,
        var x: Double,
        var y: Double,
        var drawWidth: Double
    )

    override fun start() {
        root("kvapp") {

            val current = CurrentState(
                "black",
                0.0,
                0.0,
                2.0
            )

            canvas(window.innerWidth, window.innerHeight) {

                var drawing = false

                fun drawLine(
                    drawCommand: DrawCommand,
                    emit: Boolean = false
                ) {
                        with(context2D) {
                            beginPath()
                            moveTo(drawCommand.x0, drawCommand.y0)
                            lineTo(drawCommand.x1, drawCommand.y1)
                            strokeStyle = drawCommand.color
                            lineWidth = drawCommand.drawWidth
                            stroke()
                            closePath()
                        }
                    when (emit) {
                        false -> return
                        else -> {
                            val w = canvasWidth!!.toDouble()
                            val h = canvasWidth!!.toDouble()
                            val outDrawCommand = DrawCommand(
                                null,
                                drawCommand.x0 / w,
                                drawCommand.y0 / h,
                                drawCommand.x1 / w,
                                drawCommand.y1 / h,
                                drawCommand.color,
                                drawCommand.drawWidth
                            )
                            console.log(outDrawCommand)
                            send(outDrawCommand)
                        }
                    }
                }

                Model.drawCommands.onUpdate.add {
                    GlobalScope.launch {
                        it.forEach { command ->
                            console.log(command)
                            drawLine(command, false)
                        }
                    }
                }

                fun mouseDown(event: MouseEvent) {
                    drawing = true
                    current.x = event.clientX.toDouble()
                    current.y = event.clientY.toDouble()
                }

                fun touchDown(event: TouchEvent) {
                    drawing = true
                    current.x = event.touches[0]!!.clientX.toDouble()
                    current.y = event.touches[0]!!.clientY.toDouble()
                }

                fun mouseUp(mouseEvent: MouseEvent) {
                    when (!drawing) {
                        true -> return
                        else -> {
                            drawing = false
                            drawLine(
                                DrawCommand(
                                    null,
                                    current.x,
                                    current.y,
                                    mouseEvent.clientX.toDouble(),
                                    mouseEvent.clientX.toDouble(),
                                    current.color,
                                    current.drawWidth
                                ),
                                true
                            )
                        }
                    }
                }

                fun touchEnd(touchEvent: TouchEvent) {
                    when (!drawing) {
                        true -> return
                        else -> {
                            console.log(touchEvent)
                            drawing = false
                            drawLine(
                                DrawCommand(
                                    null,
                                    current.x,
                                    current.y,
                                    touchEvent.touches[0]!!.clientX.toDouble(),
                                    touchEvent.touches[0]!!.clientY.toDouble(),
                                    current.color,
                                    current.drawWidth
                                ),
                                true
                            )
                        }
                    }
                }

                fun mouseMove(mouseEvent: MouseEvent) {
                    when (!drawing) {
                        true -> {
                            return
                        }
                        else -> {
                            drawLine(
                                DrawCommand(
                                    null,
                                    current.x,
                                    current.y,
                                    mouseEvent.clientX.toDouble(),
                                    mouseEvent.clientY.toDouble(),
                                    current.color,
                                    2.0
                                ),
                                true
                            )
                            current.x = mouseEvent.clientX.toDouble()
                            current.y = mouseEvent.clientY.toDouble()
                        }
                    }
                }

                fun touchMove(touchEvent: TouchEvent) {
                    when (!drawing) {
                        true -> {
                            return
                        }
                        else -> {
                            drawLine(
                                DrawCommand(
                                    null,
                                    current.x,
                                    current.y,
                                    touchEvent.touches[0]!!.clientX.toDouble(),
                                    touchEvent.touches[0]!!.clientY.toDouble(),
                                    current.color,
                                    2.0
                                )
                            )
                            current.x = touchEvent.touches[0]!!.clientX.toDouble()
                            current.y = touchEvent.touches[0]!!.clientY.toDouble()
                        }
                    }
                }

                onEvent {
                    mousedown = ::mouseDown
                    mouseup = ::mouseUp
                    mouseout = ::mouseUp
                    mousemove = ::mouseMove

                    touchstart = ::touchDown
                    touchend = ::touchEnd
                    touchcancel = ::touchEnd
                    touchmove = ::touchMove
                }
            }
        }
        connectToServer()
    }

    private fun send(drawCommand: DrawCommand) =
        GlobalScope.launch {
            drawChannel.send(drawCommand)
        }
}

fun main() {
    startApplication(::App)
}
