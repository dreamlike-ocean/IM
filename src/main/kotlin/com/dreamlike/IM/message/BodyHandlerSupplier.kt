package com.dreamlike.IM.message

interface BodyHandlerSupplier<T:BodyHandler> {

  fun support(header: MessageHeader):Boolean = true

  fun newInstance(header: MessageHeader):T
}
