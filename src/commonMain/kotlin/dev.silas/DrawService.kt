@file:ContextualSerialization(LocalDateTime::class)
package dev.silas

import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.serialization.ContextualSerialization
import kotlinx.serialization.Serializable
import pl.treksoft.kvision.annotations.KVService
import pl.treksoft.kvision.types.LocalDateTime

@Serializable
data class DrawCommand(
    val date: LocalDateTime?,
    val nickname: String,
    val message: String,
    val tags: List<String>
)

@KVService
interface IDrawService {
    suspend fun socketConnection(input: ReceiveChannel<DrawCommand>, output: SendChannel<DrawCommand>) {}
}
