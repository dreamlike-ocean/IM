package com.dreamlike.IM.server

import com.dreamlike.IM.Util.awaitGet
import com.dreamlike.IM.message.dispatcher.MessageDispatcher
import com.dreamlike.IM.server.handler.*
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.nio.NioServerSocketChannel
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.impl.VertxImpl

class IMServer(var port:Int) {

  companion object{
    suspend fun create(port: Int, vertx:Vertx, config: ((ServerBootstrap) -> Unit)? =null):IMServer{
      val vertxImpl = vertx as VertxImpl
      var serverBootstrap = ServerBootstrap()
      serverBootstrap
        .channel(NioServerSocketChannel::class.java)
        .group(vertx.acceptorEventLoopGroup, vertx.eventLoopGroup)
        .childHandler(ChildChannelInitializer())
      config?.run {
        this(serverBootstrap)
      }

      serverBootstrap.bind(port)
        .awaitGet()
      return IMServer(port);
    }
  }

  class ChildChannelInitializer: ChannelInitializer<Channel>() {
    override fun initChannel(ch: Channel) {
      val decoder = AllMessageDecoder()
      ch.pipeline()
        .addLast(decoder)
        .addLast(AuthenticateHandler(decoder))
        .addLast(MessageHeaderHandler(decoder,MessageDispatcher.create()))
        .addLast(IMMessageEncoder())
        .addLast(IMRequestEncoder())
    }

  }

}
