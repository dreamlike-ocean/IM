package com.dreamlike.IM.verticles

import com.dreamlike.IM.server.ComponentFactory.Companion.sqlPool
import com.dreamlike.IM.server.IMServer
import com.dreamlike.IM.server.Service.MessageDBStore
import io.vertx.kotlin.coroutines.CoroutineVerticle

class IMServerVerticle:CoroutineVerticle() {
  override suspend fun start() {
    var imServer = IMServer.create(4399, vertx)
    println("IM server is listening on ${imServer.port}}")
  }

}
