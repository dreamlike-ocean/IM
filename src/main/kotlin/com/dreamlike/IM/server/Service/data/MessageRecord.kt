package com.dreamlike.IM.server.Service.data

import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.TupleMapper
import java.util.Map
import java.util.stream.Collectors

class MessageRecord(val sender: Int,val receiver: Int,val type: Int,val content: String,val timestamp: Long,val hasRead:Boolean = false) :FieldTransform{
  public var recordId:Int? = null

  constructor(sender: Int,receiver: Int,type: Int,content: String,timestamp: Long,hasRead:Boolean = false,recordId:Int):this(sender, receiver, type, content, timestamp,hasRead){
    this.recordId = recordId
  }

  companion object {
      val tupleMapper = TupleMapper.mapper { messageRecord: MessageRecord ->
      Map.of<String, Any>(
        "sender",
        messageRecord.sender,
        "receiver",
        messageRecord.receiver,
        "type",
        messageRecord.type,
        "content",
        messageRecord.content,
        "timestamp",
        messageRecord.timestamp
      )
    }
      val rowMapper = RowMapper { row ->
        MessageRecord(sender = row.getInteger("sender"),receiver = row.getInteger("receiver"),recordId = row.getInteger("record_id"),type = row.getInteger("type")
          ,content = row.getString("content")
          ,timestamp = row.getLong("timestamp"))
      }
      val collector = Collectors.mapping(rowMapper::map,Collectors.toList())
    }

    // auto generate>>>>>>>>>>>
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o == null || javaClass != o.javaClass) return false
        val that = o as MessageRecord
        if (if (recordId != null) recordId != that.recordId else that.recordId != null) return false
        if (sender != that.sender) return false
        if (receiver != that.receiver) return false
        if (type != that.type) return false
        if (content != that.content) return false
        return timestamp == that.timestamp
    }

    override fun hashCode(): Int {
        var result = if (recordId != null) recordId.hashCode() else 0
        result = 31 * result + sender.hashCode()
        result = 31 * result + receiver.hashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + timestamp.hashCode()
        return result
    }

  override fun toString(): String {
    return "MessageRecord(recordId=$recordId, sender=$sender, receiver=$receiver, type=$type, content=$content, timestamp=$timestamp)"
  }


}
