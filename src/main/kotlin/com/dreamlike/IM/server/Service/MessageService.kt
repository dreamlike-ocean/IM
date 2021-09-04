package com.dreamlike.IM.server.Service

import com.dreamlike.IM.server.ComponentFactory.Companion.eventbus
import com.dreamlike.IM.server.ComponentFactory.Companion.sqlPool
import com.dreamlike.IM.server.Service.data.MessageRecord
import com.dreamlike.IM.server.Service.data.MessageRequest
import com.dreamlike.IM.server.Service.data.MessageResponse
import com.dreamlike.IM.server.Service.data.User
import io.netty.channel.Channel
import io.netty.channel.group.DefaultChannelGroup
import io.netty.util.AttributeKey
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextInternal
import io.vertx.kotlin.coroutines.await
import io.vertx.kotlin.coroutines.dispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

//必须跑在eventloop上面
//todo
class MessageService {
  companion object{
    val allChannel = ConcurrentHashMap<Int,Channel>()
    private const val RECORD_ADDRESS = "message_service"
    private const val REQUEST_ADDRESS = "message_request"
    private const val LOGIN_ADDRESS = "login_address"
    fun sentMessageRecord(messageRecord: MessageRecord){
      eventbus.send(RECORD_ADDRESS,messageRecord)
    }
    fun processRequest(messageRequest: MessageRequest) {
      eventbus.send(REQUEST_ADDRESS,messageRequest)
    }
    suspend fun login(username: String) = eventbus.request<User>(LOGIN_ADDRESS, username).await().body()
    fun addChannel(channel: Channel){
      val userId = getUserIdFromChannel(channel)
      allChannel[getUserIdFromChannel(channel)] = channel
      channel.closeFuture().addListener {
        allChannel.remove(userId)
      }
    }
    fun getUserIdFromChannel(channel: Channel)= channel.attr(AttributeKey.valueOf<Int>("senderId")).get()
    fun authenticateChannel(channel: Channel, userId :Int) {
      channel.attr(AttributeKey.valueOf<Int>("senderId")).set(userId)
      addChannel(channel)
    }
  }
  private var userDBStore = UserDBStore(sqlPool)
  private var messageDBStore = MessageDBStore(sqlPool)


  private fun initService(){

    val dispatcher = Vertx.currentContext().dispatcher() as CoroutineContext

    eventbus.localConsumer<String>(LOGIN_ADDRESS){
      CoroutineScope(dispatcher).launch {
        it.reply(userDBStore.selectUserByUsername(it.body()))
      }
    }
    eventbus.localConsumer<MessageRecord>(RECORD_ADDRESS){
      CoroutineScope(dispatcher).launch {
        messageDBStore.insertMessageRecord(it.body())
      }
    }

  }
}
