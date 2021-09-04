package com.dreamlike.IM.message.dispatcher

import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.MessageHeader
import io.netty.buffer.ByteBuf

interface MessageDispatcher {
  fun dispatch(header: MessageHeader): BodyHandler
}
