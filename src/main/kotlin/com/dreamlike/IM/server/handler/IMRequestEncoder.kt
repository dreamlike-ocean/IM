package com.dreamlike.IM.server.handler

import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.data.MessageResponse
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandler
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import java.nio.charset.Charset

class IMRequestEncoder:ChannelOutboundHandlerAdapter() {
  override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
    if(msg !is MessageResponse){
      ctx.write(msg)
      return
    }
    msg.body.put("requestType",msg.requestType)
    var byteArray = msg.body.encode().toByteArray(Charset.defaultCharset())
    msg.header.payloadLength = byteArray.size
    writeHeader(ctx,msg.header)
    ctx.writeAndFlush(ctx.alloc().directBuffer(byteArray.size).writeBytes(byteArray))
  }
  private fun writeHeader(ctx: ChannelHandlerContext,messageHeader: MessageHeader){
    val header= ctx.alloc().directBuffer(16).writeInt(messageHeader.type).writeInt(-1).writeInt(messageHeader.payloadLength).writeInt(messageHeader.senderId)
    ctx.write(header)
  }
}
