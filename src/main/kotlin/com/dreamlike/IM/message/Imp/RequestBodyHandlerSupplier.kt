package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.message.BodyHandlerSupplier
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.message.MessageHeader.Companion.isRequest


class RequestBodyHandlerSupplier:BodyHandlerSupplier<RequestBodyHandler> {
  override fun support(header: MessageHeader) = isRequest(header.type)

  override fun newInstance(header: MessageHeader)= RequestBodyHandler(header)

}
