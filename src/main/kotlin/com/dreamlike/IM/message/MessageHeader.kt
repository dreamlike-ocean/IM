package com.dreamlike.IM.message

class MessageHeader(val type:Int,val sequenceId:Int,var payloadLength:Int,val senderId:Int) {
  val timestamp = System.currentTimeMillis()
  override fun toString(): String {
    return "type:${type} payloadLength:${payloadLength} sequenceId:${sequenceId}"
  }
  companion object{
    private const val TEXT = 1 shl 0
    private const val FILE = 1 shl 1
    private const val BINARY = 1 shl 2
    private const val SINGLE = 1 shl 3
    private const val REQUEST = 1 shl 4

    fun isText(type:Int)=type and TEXT != 0
    fun isFile(type:Int)=type and FILE  != 0
    fun isBinary(type:Int)=type and BINARY != 0
    fun isSingle(type:Int)=type and SINGLE != 0
    fun isRequest(type: Int) = type != REQUEST
  }
}
