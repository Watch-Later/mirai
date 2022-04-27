/*
 * Copyright 2019-2022 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.internal.message.protocol

import net.mamoe.mirai.internal.network.protocol.data.proto.ImMsgBody
import net.mamoe.mirai.internal.pipeline.Processor
import net.mamoe.mirai.internal.pipeline.ProcessorPipeline
import net.mamoe.mirai.internal.pipeline.ProcessorPipelineContext
import net.mamoe.mirai.message.data.Message
import net.mamoe.mirai.message.data.SingleMessage
import net.mamoe.mirai.utils.uncheckedCast
import java.util.*
import kotlin.reflect.KClass

internal interface MessageProtocol<T : SingleMessage> {
    fun forType(): KClass<T>

    val decoder: MessageDecoder?
    val encoder: MessageEncoder?
}

internal object MessageProtocols {
    val instances: List<MessageProtocol<*>> =
        ServiceLoader.load(MessageProtocol::class.java).iterator().asSequence().toList()
}


internal interface MessageDecoderContext : ProcessorPipelineContext<ImMsgBody.Elem, Message> {

}


internal interface MessageDecoder : Processor<MessageDecoderContext, ImMsgBody.Elem>
internal interface MessageDecoderPipeline : ProcessorPipeline<MessageDecoder, ImMsgBody.Elem, Message>


internal interface MessageEncoderContext : ProcessorPipelineContext<SingleMessage, ImMsgBody.Elem> {

}


internal sealed interface MessageEncoder : Processor<MessageEncoderContext, SingleMessage>
internal abstract class AbstractMessageEncoder<T : SingleMessage>(
    private val type: KClass<T>,
) : MessageEncoder, Processor<MessageEncoderContext, SingleMessage> {
    final override suspend fun process(context: MessageEncoderContext, data: SingleMessage) {
        if (type.isInstance(data)) {
            processImpl(context, data.uncheckedCast())
        }
    }

    protected abstract suspend fun processImpl(context: MessageEncoderContext, data: T)
}

internal interface MessageEncoderPipeline : ProcessorPipeline<MessageEncoder, SingleMessage, ImMsgBody.Elem>