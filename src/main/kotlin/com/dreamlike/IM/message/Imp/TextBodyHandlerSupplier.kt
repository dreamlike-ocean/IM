package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.message.BodyHandlerSupplier
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.message.MessageHeader.Companion.isText


class TextBodyHandlerSupplier:BodyHandlerSupplier<TextBodyHandler> {
  override fun support(header: MessageHeader) = isText(header.type)

  override fun newInstance(header: MessageHeader) = TextBodyHandler(header)
}
