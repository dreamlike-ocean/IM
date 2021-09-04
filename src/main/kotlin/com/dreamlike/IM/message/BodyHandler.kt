package com.dreamlike.IM.message

import io.netty.buffer.ByteBuf

interface BodyHandler {
  /**
   * 需要handler处理释放问题
   * @param msg body
   */
    fun accept(msg:ByteBuf)
    fun end()
}
