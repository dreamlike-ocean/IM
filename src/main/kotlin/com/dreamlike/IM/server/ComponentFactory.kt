package com.dreamlike.IM.server

import com.dreamlike.IM.Util.registerLocalDefaultCodec
import com.dreamlike.IM.server.Service.data.MessageRecord
import com.dreamlike.IM.server.Service.data.MessageRequest
import com.dreamlike.IM.server.Service.data.MessageResponse
import com.dreamlike.IM.server.Service.data.User
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json
import io.vertx.core.json.JsonObject
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.core.vertxOptionsOf
import io.vertx.kotlin.mysqlclient.mySQLConnectOptionsOf
import io.vertx.kotlin.sqlclient.poolOptionsOf
import io.vertx.mysqlclient.MySQLPool
import io.vertx.sqlclient.Pool

class ComponentFactory {
  companion object{
    val vertx by lazy {
      val option = vertxOptionsOf(blockedThreadCheckInterval = 1000_00000)
      val vertx = Vertx.vertx(option)
      vertx.eventBus().registerLocalDefaultCodec(MessageRecord::class.java)
      vertx.eventBus().registerLocalDefaultCodec(MessageRequest::class.java)
      vertx.eventBus().registerLocalDefaultCodec(MessageResponse::class.java)
      vertx.eventBus().registerLocalDefaultCodec(User::class.java)
      DatabindCodec.mapper().registerModule(KotlinModule())
      vertx
    }
    val sqlPool:Pool by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
      val mySQLConnectOptions = mySQLConnectOptionsOf(
        user = "root",
        password = "12345678",
        useAffectedRows = true,
        charset = "utf8mb4",
        logActivity = true,
        database = "app",
        host = "localhost",
        port = 3306
      )
      val poolOption = poolOptionsOf(maxSize = 1)
      MySQLPool.pool(vertx,mySQLConnectOptions,poolOption)
    }
    val eventbus = vertx.eventBus()
  }

}
