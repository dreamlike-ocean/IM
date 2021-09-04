package com.dreamlike.IM.message.dispatcher

import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.BodyHandlerSupplier
import com.dreamlike.IM.message.MessageHeader
import io.netty.buffer.ByteBuf
import io.vertx.core.impl.logging.LoggerFactory


class MessageDispatcherImp(private val suppliers: List<BodyHandlerSupplier<*>>):MessageDispatcher{

  override fun dispatch(header: MessageHeader)=suppliers.find { supplier -> supplier.support(header) }?.newInstance(header)?:DropAllMessageBodyHandler(header)

  private class DropAllMessageBodyHandler(private val header: MessageHeader) : BodyHandler{
    private val logger = LoggerFactory.getLogger(DropAllMessageBodyHandler::class.java)
    override fun accept(msg: ByteBuf) {
      logger.info("dropping $header size:${msg.readableBytes()}")
      msg.release()
    }
    override fun end() {}
  }

  private object DropAllMessageBodyHandlerSupplier:BodyHandlerSupplier<DropAllMessageBodyHandler>{
    override fun newInstance(header: MessageHeader)=DropAllMessageBodyHandler(header)
  }

}
