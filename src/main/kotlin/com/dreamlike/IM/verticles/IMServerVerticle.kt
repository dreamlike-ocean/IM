package com.dreamlike.IM.verticles

import com.dreamlike.IM.server.ComponentFactory.Companion.sqlPool
import com.dreamlike.IM.server.Service.MessageDBStore
import io.vertx.kotlin.coroutines.CoroutineVerticle

class IMServerVerticle:CoroutineVerticle() {
  override suspend fun start() {
    try {
      println(
        MessageDBStore(sqlPool)
          .selectMessageRecordBySender(132)
      )
    } catch (e: Exception) {
      e.printStackTrace()
    }
    vertx.close()
  }

}
