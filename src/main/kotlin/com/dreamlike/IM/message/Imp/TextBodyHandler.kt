package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.Util.use
import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.MessageService
import com.dreamlike.IM.server.Service.data.MessageRecord
import io.netty.buffer.ByteBuf
import io.vertx.core.buffer.Buffer
import java.nio.charset.Charset

//[1,2,3,4 {text}]
class TextBodyHandler(val messageHeader:MessageHeader):AbstractFullBodyHandler() {
  companion object{
    private const val delimited = ' '.code.toByte()
    private const val IdDelimited = ','
  }

  //0 1 2 3
  override fun work(fullBody: ByteBuf) {
    val index = fullBody.indexOf(0,fullBody.readableBytes(),delimited)
    val ids = fullBody.readCharSequence(index, Charset.defaultCharset()).split(IdDelimited).map(String::toInt)
    fullBody.readerIndex(fullBody.readerIndex() + 1)
    val content = fullBody.readCharSequence(fullBody.readableBytes(), Charset.defaultCharset()).toString()
    for (id in ids) {
      MessageService.sentMessageRecord(messageRecord = MessageRecord(messageHeader.senderId,id,messageHeader.type,content,messageHeader.timestamp))
    }
  }
}
