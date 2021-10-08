package com.dreamlike.IM.server.handler

import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.message.MessageHeader.Companion.isFile
import com.dreamlike.IM.message.MessageHeader.Companion.isText
import com.dreamlike.IM.server.Service.data.MessageRecord
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelOutboundHandlerAdapter
import io.netty.channel.ChannelPromise
import io.netty.channel.DefaultFileRegion
import io.netty.handler.stream.ChunkedFile
import java.io.File

//不处理request
class IMMessageEncoder(private val isSsl:Boolean = false):ChannelOutboundHandlerAdapter() {
  override fun write(ctx: ChannelHandlerContext, msg: Any, promise: ChannelPromise) {
    if(msg !is MessageRecord){
      ctx.write(msg)
      return
    }
    val type = msg.type
    when{
      isFile(type) -> writeAndFlushFile(ctx,msg)
      isText(type) -> writeAndFlushText(ctx,msg)
    }
  }
  private fun writeAndFlushFile(ctx: ChannelHandlerContext,msg: MessageRecord){
    val f = File(msg.content)
    writeHeader(ctx, MessageHeader(msg.type,-1,f.length().toInt(),msg.sender))
    if (isSsl){
      ctx.writeAndFlush(ChunkedFile(f))
    }else{
      ctx.writeAndFlush(DefaultFileRegion(f,0,f.length()))
    }
  }
  private fun writeAndFlushText(ctx: ChannelHandlerContext,msg: MessageRecord){
    val contentBytes = msg.content.toByteArray(Charsets.UTF_8)
    val content = ctx.alloc().directBuffer(contentBytes.size).writeBytes(contentBytes)
    writeHeader(ctx, MessageHeader(msg.type,-1,contentBytes.size,msg.sender))
    ctx.writeAndFlush(content)
  }

  private fun writeHeader(ctx: ChannelHandlerContext,messageHeader: MessageHeader){
    val header= ctx.alloc().directBuffer(16).writeInt(messageHeader.type).writeInt(-1).writeInt(messageHeader.payloadLength).writeInt(messageHeader.senderId)
    ctx.write(header)
  }
}
