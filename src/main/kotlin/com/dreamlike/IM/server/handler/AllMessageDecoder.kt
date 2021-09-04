package com.dreamlike.IM.server.handler

import com.dreamlike.IM.message.BodyHandler
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.Service.MessageService.Companion.getUserIdFromChannel
import io.netty.buffer.ByteBuf
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import io.netty.util.AttributeKey
import io.vertx.kotlin.ext.auth.webauthn.authenticatorOf


/**
 * header格式
 *用户发给server：
 * 前四位（int）type
 * 再四位（int）sequenceId 如果不是type不是请求则为-1
 * 后四位（int）payLoadLength
 *server发给用户
 * 前四位（int）type
 * 再四位（int）sequenceId 如果不是type不是请求则为-1
 * 后四位（int）payLoadLength
 * 最后为（int）senderId
 */
class AllMessageDecoder:ChannelInboundHandlerAdapter(){
  private var nowState = ParseState.EMPTY
  private val emptyByteBuf:ByteBuf = Unpooled.EMPTY_BUFFER
  private var headerBuffer:ByteBuf = emptyByteBuf
  var bodyHandler:BodyHandler? = null

  override fun channelActive(ctx: ChannelHandlerContext?) {
    println("connect")
  }

  private var hasReceived:Int = 0
  private var targetLength:Int = 0

  private enum class ParseState{
    //标识解析到哪个阶段了 EMPTY当前还未有解析 HEADER正在解析header BODY正在解析body
    EMPTY,HEADER,BODY
  }
  override fun channelRead(ctx: ChannelHandlerContext, msg: Any) {
    val rawMsg = msg as ByteBuf
    when(nowState){
      ParseState.EMPTY -> emptyStageHandler(ctx,rawMsg)
      ParseState.HEADER -> headerStageHandler(ctx, rawMsg)
      ParseState.BODY -> bodyStageHandler(ctx,rawMsg)
    }
  }
  private fun emptyStageHandler(ctx: ChannelHandlerContext, msg: ByteBuf){
    if (msg.readableBytes() < 12){
      nowState = ParseState.HEADER
      headerBuffer = msg
    }else {
      switchToParseBody(msg,ctx)
    }
  }

  private fun headerStageHandler(ctx: ChannelHandlerContext, msg: ByteBuf){
    if (msg.readableBytes()+headerBuffer.readableBytes() >= 12){
      val headerAndBodyPart = ctx.alloc().compositeBuffer().addComponent(true,headerBuffer).addComponent(true,msg)
      headerBuffer = emptyByteBuf
      switchToParseBody(headerAndBodyPart,ctx)
      return
    }

    //很少走这个 放在最后 msg.readableBytes()+headerBuffer.readableBytes() < 12
    if(headerBuffer.maxCapacity() > 12){
      headerBuffer.writeBytes(msg)
      msg.release()
    }else {
      headerBuffer = ctx.alloc().compositeBuffer().addComponent(headerBuffer).addComponent(msg)
    }
  }

  private fun bodyStageHandler(ctx:ChannelHandlerContext, msg:ByteBuf){
    val afterRead = hasReceived + msg.readableBytes()
    //还未全部处理或者正好处理完毕
    if (afterRead <= targetLength){
      hasReceived += msg.readableBytes()
      bodyHandler?.accept(msg)
      if (afterRead == targetLength){
        bodyHandler?.end()
        resume()
      }
      return
    }
    //读取完msg后上一个完毕同时也包含了下一个 增加一个计数供bodyHandler释放
    val lastBody = msg.retainedSlice(msg.readerIndex(), targetLength - hasReceived)
    val remainingMsg = msg.readerIndex(msg.readerIndex() + targetLength - hasReceived)
    bodyHandler?.accept(lastBody)
    bodyHandler?.end()
    resume()

    channelRead(ctx,remainingMsg)
  }

  /**
   * 重置解析状态和bodyHandler
   */
  private fun resume(){
    hasReceived = 0
    targetLength = 0
    nowState = ParseState.EMPTY
    bodyHandler = null
  }

  /**
   * 由其他解析状态跳转到body解析状态
   * 由于header解析会被暂时保留下来所以 header由这个函数传递给下一个channel handler
   * @param msg 包含header和部分body的ByteBuf
   */
  private fun switchToParseBody(msg: ByteBuf,ctx: ChannelHandlerContext){
    nowState = ParseState.BODY
    val sender= getUserIdFromChannel(ctx.channel()) ?: -1
    val messageHeader = MessageHeader(type = msg.readInt(), sequenceId = msg.readInt(),payloadLength = msg.readInt(), senderId = sender)
    targetLength = messageHeader.payloadLength
    //期望设置bodyHandler
    ctx.fireChannelRead(messageHeader)
    bodyStageHandler(ctx,msg)
  }

  override fun channelUnregistered(ctx: ChannelHandlerContext?) {
    headerBuffer.release()
    super.channelUnregistered(ctx)
  }
}
