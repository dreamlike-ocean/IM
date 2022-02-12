package com.dreamlike.IM.message.dispatcher

import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.Imp.FileBodyHandlerSupplier
import com.dreamlike.IM.message.Imp.RequestBodyHandlerSupplier
import com.dreamlike.IM.message.Imp.TextBodyHandlerSupplier
import com.dreamlike.IM.message.MessageHeader
import io.netty.buffer.ByteBuf

interface MessageDispatcher {
  fun dispatch(header: MessageHeader): BodyHandler
  companion object{
    fun create() :MessageDispatcher{
      return MessageDispatcherImp(listOf(FileBodyHandlerSupplier(),RequestBodyHandlerSupplier(),TextBodyHandlerSupplier()))
    }
  }

}
