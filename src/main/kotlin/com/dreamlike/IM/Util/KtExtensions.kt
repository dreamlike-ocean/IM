package com.dreamlike.IM.Util

import io.netty.buffer.ByteBuf
import io.netty.util.concurrent.Future
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.EventBus
import io.vertx.core.eventbus.MessageCodec
import io.vertx.ext.web.Route
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.dispatcher
import io.vertx.sqlclient.SqlConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend fun <T> Future<T>.awaitGet() = suspendCoroutine<T> { c ->
  this.addListener {
    if (it.isSuccess) {
      c.resume(it.now as T)
    }else{
      c.resumeWithException(it.cause())
    }
  }
}

fun Route.suspendHandler(handler:suspend (RoutingContext) -> Unit){
  this.handler{
    CoroutineScope(it.vertx().dispatcher() as CoroutineContext).launch {
      try {
        handler(it)
      } catch (e: Throwable) {
        it.fail(e)
      }
    }
  }
}

inline fun <R> SqlConnection.use(fn: (SqlConnection)->R):R{
  try {
    return fn(this)
  }finally {
    this.close()
  }
}

fun <T> EventBus.registerLocalDefaultCodec(claz:Class<T>){
  registerDefaultCodec(claz,object:MessageCodec<T,T>{
    override fun encodeToWire(buffer: Buffer?, s: T) {
      TODO("Not yet implemented")
    }
    override fun decodeFromWire(pos: Int, buffer: Buffer?): T {
      TODO("Not yet implemented")
    }
    override fun transform(s: T)=s
    override fun name()=claz.name
    override fun systemCodecID():Byte=-1
  })
}


inline fun String.humpToLine() = replace("[A-Z]".toRegex(), "_$0").lowercase()

inline fun ByteBuf.use(fn:()->Unit){
  try {
    fn()
  } finally {
    this.release()
  }
}
