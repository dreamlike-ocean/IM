package com.dreamlike.IM.message.Imp

import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.MessageService
import com.dreamlike.IM.server.Service.data.MessageRecord
import io.netty.buffer.ByteBuf
import io.vertx.core.buffer.Buffer
import java.io.File
import java.nio.channels.FileChannel
import java.nio.file.StandardOpenOption

/**
 * 文件操作基本在eventloop上面阻塞
 */
//[id1,id2,id3&filename fileBuffer]
class FileBodyHandler(val messageHeader: MessageHeader):BodyHandler {
  companion object{
    private const val FileDelimited = ' '.code.toByte()
    private const val IdDelimited = ','
    private const val IdAndNameDelimited = '&'
  }
  private var metaInfoBuffer = Buffer.buffer()
  private var hasParseFilename:Boolean = false

  private lateinit var fileChannel: FileChannel
  private lateinit var ids:List<Int>
  private lateinit var filePath:String

  override fun accept(msg: ByteBuf) {
    if (!hasParseFilename){
      parseFilename(msg)
    }else{
      writeToFile(msg)
    }

  }

  override fun end() {
//    fileChannel.force(false)
    fileChannel.close()
    for(receiverId in ids){
      MessageService.sentMessageRecord(MessageRecord(messageHeader.senderId,receiverId,messageHeader.type,filePath,messageHeader.timestamp))
    }
  }

  private fun parseFilename(msg:ByteBuf){
    val index = msg.indexOf(msg.readerIndex(),msg.readerIndex()+msg.readableBytes(), FileDelimited)
    if (index == -1){
      //拷贝到堆内（因为最终化为字符串也要放到堆内 直接放到堆内） 释放池化内存
      metaInfoBuffer.appendBuffer(Buffer.buffer(msg))
      msg.release()
      return
    }
    //找到分割符了
    hasParseFilename = true

    //slice为元信息
    val slice = msg.readSlice(index - msg.readerIndex())
    val idsAndFilename = metaInfoBuffer.appendBuffer(Buffer.buffer(slice)).toString().split(IdAndNameDelimited)
    ids = if(idsAndFilename[0].isBlank()){
      emptyList()
    }else{
      idsAndFilename[0].split(IdDelimited).map { s -> s.toInt() }
    }
    //todo 修改文件位置
    filePath = "file/${idsAndFilename[1]}"
    fileChannel = FileChannel.open(File(filePath).toPath(),StandardOpenOption.CREATE,StandardOpenOption.WRITE)

    try {
      if (msg.readableBytes() > 0){
        //skip delimited
        msg.readerIndex(msg.readerIndex()+1)
        writeToFile(msg)
      }
    } finally {
      //由于每次都生成一个新的bodyhandler实例 所以metaInfo buffer作为堆内内存可以直接被回收掉
      msg.release()
    }
  }
  /**
   * 不做复杂性规范 默认他能全写完
   * 释放msg
   */
  private fun writeToFile(msg: ByteBuf) {
    val hasWrite = msg.readBytes(fileChannel, fileChannel.position(), msg.readableBytes())
    fileChannel.position(fileChannel.position() + hasWrite)
  }
}
