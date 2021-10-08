package com.dreamlike.IM.server.Service

import com.dreamlike.IM.Util.awaitGet
import com.dreamlike.IM.message.MessageHeader
import com.dreamlike.IM.server.ComponentFactory.Companion.eventbus
import com.dreamlike.IM.server.ComponentFactory.Companion.sqlPool
import com.dreamlike.IM.server.Service.data.MessageRecord
import com.dreamlike.IM.server.Service.data.MessageRequest
import com.dreamlike.IM.server.Service.data.MessageResponse
import com.dreamlike.IM.server.Service.data.User
import io.netty.channel.Channel
import io.netty.util.AttributeKey
import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.kotlin.core.json.jsonObjectOf
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

//必须跑在eventloop上面

class MessageService {
  companion object{
    private val allChannel = ConcurrentHashMap<Int,Channel>()
    private const val RECORD_ADDRESS = "message_service"
    private const val REQUEST_ADDRESS = "message_request"
    private const val LOGIN_ADDRESS = "login_address"
    fun sentMessageRecord(messageRecord: MessageRecord){
      eventbus.send(RECORD_ADDRESS,messageRecord)
    }
    fun processRequest(messageRequest: MessageRequest) {
      eventbus.send(REQUEST_ADDRESS,messageRequest)
    }
    suspend fun login(username: String):User? = eventbus.request<User>(LOGIN_ADDRESS, username).await().body()
    private fun addChannel(channel: Channel){
      val userId = getUserIdFromChannel(channel)
      allChannel[getUserIdFromChannel(channel)] = channel
      channel.closeFuture().addListener {
        allChannel.remove(userId)
      }
    }
    fun getUserIdFromChannel(channel: Channel):Int= channel.attr(AttributeKey.valueOf<Int>("senderId")).get()
    fun authenticateChannel(channel: Channel, userId :Int) {
      channel.attr(AttributeKey.valueOf<Int>("senderId")).set(userId)
      addChannel(channel)
    }
  }
  private var userDBStore = UserDBStore(sqlPool)
  private var messageDBStore = MessageDBStore(sqlPool)
  private val dispatcher = Vertx.currentContext().dispatcher() as CoroutineContext
  init {
    initService()
  }

  private fun initService(){

    eventbus.localConsumer<String>(LOGIN_ADDRESS){
      CoroutineScope(dispatcher).launch {
        it.reply(userDBStore.selectUserByUsername(it.body()))
      }
    }
    eventbus.localConsumer<MessageRecord>(RECORD_ADDRESS){ ms ->
      val mr  = ms.body()
      CoroutineScope(dispatcher).launch {
        allChannel[ms.body().receiver]?.let {
          it.write(ms.body()).awaitGet()
          mr.hasRead = true
        }
        messageDBStore.insertMessageRecord(ms.body())
      }
    }

    eventbus.localConsumer<MessageRequest>(REQUEST_ADDRESS){
      CoroutineScope(dispatcher).launch {
        val response = dispatchRequest(it.body())
        allChannel[it.body().header.senderId]?.write(response)
      }
    }

  }

  private suspend fun dispatchRequest(request: MessageRequest) = when(request.requestType){
    "pullUnread" -> {
      MessageResponse(request.header, jsonObjectOf("message" to messageDBStore.selectUnreadMessage(request.header.senderId)),"pullUnread")
    }
    "pullRecord" -> {
      MessageResponse(request.header, jsonObjectOf("message" to messageDBStore.selectMessage(request.param.getInteger("receiver"),request.header.senderId)),"pullRecord")
    }
    else -> MessageResponse(request.header, jsonObjectOf(),"unknown")
  }

}
