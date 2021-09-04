package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.MessageService
import com.dreamlike.IM.server.Service.data.MessageRequest
import io.netty.buffer.ByteBuf
import io.vertx.core.json.JsonObject

class RequestBodyHandler(val messageHeader: MessageHeader):AbstractFullBodyHandler() {

  override fun work(fullBody: ByteBuf) {
    val json = JsonObject(fullBody.readCharSequence(fullBody.readableBytes(), Charsets.UTF_8).toString())
    MessageService.processRequest(MessageRequest(header = messageHeader,json))
  }
}
