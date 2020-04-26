package dev.silas

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pl.treksoft.kvision.state.observableListOf

object Model {

    val drawService = DrawService()

    val drawCommands = observableListOf<DrawCommand>()

    val drawChannel = Channel<DrawCommand>()

    fun connectToServer() {
        GlobalScope.launch {
            while (true) {
                drawService.socketConnection { output, input ->
                    coroutineScope {
                        launch {
                            for (tweet in drawChannel) {
                                output.send(tweet)
                            }
                        }
                        launch {
                            for (tweet in input) {
                                drawCommands.add(tweet)
                            }
                        }
                    }
                }
                delay(5000)
            }
        }
    }
}
