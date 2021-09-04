package com.dreamlike.IM.server.handler

import com.dreamlike.IM.message.dispatcher.MessageDispatcher
import com.dreamlike.IM.message.MessageHeader
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter

class MessageHeaderHandler(private val allMessageDecoder: AllMessageDecoder, private val messageDispatcher: MessageDispatcher):ChannelInboundHandlerAdapter() {

  override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
    if (msg !is MessageHeader){
      ctx.fireChannelRead(msg)
      return
    }
    allMessageDecoder.bodyHandler = messageDispatcher.dispatch(msg)
  }
}


