package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.Util.use
import com.dreamlike.IM.exception.TooBigBodyException
import com.dreamlike.IM.message.BodyHandler
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled

abstract class AbstractFullBodyHandler(private val max:Int = Int.MAX_VALUE):BodyHandler {
  private val body = Unpooled.buffer()
  override fun accept(msg: ByteBuf) {
    if (body.readableBytes() + msg.readableBytes() <= max) {
      msg.use {
        body.writeBytes(msg)
      }
    }else {
      body.release()
      throw TooBigBodyException(max)
    }
  }

  override fun end() {
    body.use {
      work(body)
    }

  }

  /**
   * fullbody为堆内内存 不用担心释放 无需手动释放
   */
  protected abstract fun work(fullBody: ByteBuf)
}
