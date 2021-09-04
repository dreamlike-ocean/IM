package com.dreamlike.IM.server.handler

import com.dreamlike.IM.message.Imp.AbstractFullBodyHandler
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.MessageService
import com.dreamlike.IM.server.Service.data.User
import io.netty.buffer.ByteBuf
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.vertx.core.json.Json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch

class AuthenticateHandler(private val allMessageDecoder: AllMessageDecoder):ChannelInboundHandlerAdapter(){
  override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
    if (msg !is MessageHeader){
      ctx.fireChannelRead(msg)
      return
    }
    allMessageDecoder.bodyHandler = AuthenticateBodyHandler(msg,ctx.channel())
  }
  private class AuthenticateBodyHandler(val messageHeader: MessageHeader,val channel: Channel):AbstractFullBodyHandler(){
    override fun work(fullBody: ByteBuf) {
      val user = Json.decodeValue(fullBody.readCharSequence(fullBody.readableBytes(), Charsets.UTF_8).toString(),User::class.java)
      channel.config().isAutoRead = false
      CoroutineScope(channel.eventLoop().asCoroutineDispatcher()).launch {
        val userFromDB = MessageService.login(user.username)
        if (userFromDB != null && userFromDB.password == user.password){
          MessageService.authenticateChannel(channel,userFromDB.userId)
          channel.config().isAutoRead = true
          channel.pipeline().remove(AuthenticateHandler::class.java)
        }else {
          channel.close()
        }
      }
    }
  }

  override fun exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable){
    ctx.channel().close()
  }
}
