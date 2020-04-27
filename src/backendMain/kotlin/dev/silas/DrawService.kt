package dev.silas

import dev.silas.Model.clients
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

object Model {
    val clients = ConcurrentHashMap.newKeySet<SendChannel<DrawCommand>>()
}

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
actual class DrawService : IDrawService {
    override suspend fun socketConnection(input: ReceiveChannel<DrawCommand>, output: SendChannel<DrawCommand>) {
        clients.add(output)
        for (receivedCommands in input) {
            val tweet = receivedCommands.copy()
            clients.forEach {
                it.send(tweet)
            }
        }
        clients.remove(output)
    }
}