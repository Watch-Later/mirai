/*
 * Copyright 2019-2022 Mamoe Technologies and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license that can be found through the following link.
 *
 * https://github.com/mamoe/mirai/blob/dev/LICENSE
 */

package net.mamoe.mirai.internal.message.protocol

import net.mamoe.mirai.message.data.SingleMessage
import kotlin.reflect.KClass

// Loaded by ServiceLoader
internal abstract class MessageProtocol(
    val priority: UInt = PRIORITY_CONTENT // the higher, the prior it being called
) {
    fun collectProcessors(processorCollector: ProcessorCollector) {
        processorCollector.collectProcessorsImpl()
    }

    protected abstract fun ProcessorCollector.collectProcessorsImpl()

    companion object {
        const val PRIORITY_METADATA: UInt = 10000u
        const val PRIORITY_CONTENT: UInt = 1000u
        const val PRIORITY_IGNORE: UInt = 500u
        const val PRIORITY_UNSUPPORTED: UInt = 100u
    }

    object PriorityComparator : Comparator<MessageProtocol> {
        override fun compare(o1: MessageProtocol, o2: MessageProtocol): Int {
            return o1.priority.compareTo(o2.priority) // no boxing
        }
    }
}

internal abstract class ProcessorCollector {
    inline fun <reified T : SingleMessage> add(encoder: MessageEncoder<T>) = add(encoder, T::class)


    abstract fun <T : SingleMessage> add(encoder: MessageEncoder<T>, elementType: KClass<T>)

    abstract fun add(decoder: MessageDecoder)
}


///////////////////////////////////////////////////////////////////////////
// refiners
///////////////////////////////////////////////////////////////////////////


//internal interface MessageRefiner : Processor<MessageRefinerContext>
//
//internal interface MessageRefinerContext : ProcessorPipelineContext<SingleMessage, Message?> {
//    /**
//     * Refine if possible (without suspension), returns self otherwise.
//     * @since 2.6
//     */ // see #1157
//    fun tryRefine(
//        bot: Bot,
//        context: MessageChain,
//        refineContext: RefineContext = EmptyRefineContext,
//    ): Message? = this
//
//    /**
//     * This message [RefinableMessage] will be replaced by return value of [refineLight]
//     */
//    suspend fun refine(
//        bot: Bot,
//        context: MessageChain,
//        refineContext: RefineContext = EmptyRefineContext,
//    ): Message? = tryRefine(bot, context, refineContext)
//}
//
