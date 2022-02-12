package com.dreamlike.IM


import com.dreamlike.IM.server.Service.data.User
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.netty.buffer.Unpooled
import io.vertx.core.AbstractVerticle
import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.eventbus.MessageCodec
import io.vertx.core.json.Json
import io.vertx.core.json.jackson.DatabindCodec
import io.vertx.kotlin.coroutines.CoroutineVerticle
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import java.io.File
import java.net.URLDecoder
import java.nio.file.Files
import java.nio.file.Path
import java.time.LocalDateTime
import kotlin.coroutines.CoroutineContext

//@ExtendWith(VertxExtension::class)
class TestMainVerticle {


  @Test
  fun verticle_deployed() {



  }






}



