package com.dreamlike.IM.server.Service

import com.dreamlike.IM.Util.use
import com.dreamlike.IM.server.ComponentFactory
import com.dreamlike.IM.server.Service.data.MessageRecord
import io.vertx.core.Vertx
import io.vertx.kotlin.coroutines.await
import io.vertx.mysqlclient.MySQLClient
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate
import java.util.*


class MessageDBStore(private val pool: Pool) {
  private val vertx:Vertx = ComponentFactory.vertx

  suspend fun insertMessageRecord(messageRecord: MessageRecord):Int{
    //language=sql
    val res= pool.connection.await().use {
      SqlTemplate.forUpdate(it, "INSERT INTO message_record (sender, receiver, type, content, timestamp) VALUE (#{sender},#{receiver},#{type},#{content},#{timestamp})")
        .mapFrom(MessageRecord.tupleMapper)
        .execute(messageRecord).await()
    }
    if (res.rowCount() != 0) {
      messageRecord.recordId = res.property(MySQLClient.LAST_INSERTED_ID).toInt()
    }
    return res.rowCount()
  }

  suspend fun insertBatchMessageRecord(messageRecords:List<MessageRecord>):Int{
    if (messageRecords.isEmpty()) return 0
    val map = mutableMapOf<String,Any?>()
    val sqlJoiner =
      StringJoiner(",",  "INSERT INTO message_record (sender, receiver, type, content, timestamp) VALUES ", "")
    messageRecords.forEachIndexed{ i,m ->
      sqlJoiner.add("(#{sender${i}},#{receiver${i}},#{type${i}},#{content${i}},#{timestamp${i}})")
      map.putAll(m.transToLineMap(i))
    }

    val res = pool.connection.await().use {
      SqlTemplate.forUpdate(it,sqlJoiner.toString())
        .execute(map).await()
    }
    return res.rowCount()
  }

  suspend fun selectMessageRecordBySender(senderId:Int):List<MessageRecord>{
    val res = pool.connection.await().use {
      SqlTemplate.forQuery(it,"SELECT * FROM message_record WHERE sender = #{sender}")
        .collecting(MessageRecord.collector)
        .execute(mapOf("sender" to senderId))
        .await()
    }
    return res.value()
  }







}
