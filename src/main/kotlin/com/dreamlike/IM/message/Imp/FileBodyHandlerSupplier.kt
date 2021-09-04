package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.message.BodyHandlerSupplier
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.message.MessageHeader.Companion.isFile


class FileBodyHandlerSupplier:BodyHandlerSupplier<FileBodyHandler> {
  override fun support(header: MessageHeader): Boolean {
     return isFile(header.type)
  }

  override fun newInstance(header: MessageHeader): FileBodyHandler {
    return FileBodyHandler(header)
  }
}
