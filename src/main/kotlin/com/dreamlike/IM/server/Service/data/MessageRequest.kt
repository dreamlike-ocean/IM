package com.dreamlike.IM.server.Service.data

import com.dreamlike.IM.message.MessageHeader
import io.vertx.core.json.JsonObject

open class SequenceMessageBase(protected val header:MessageHeader){}

class MessageRequest(header: MessageHeader,var param: JsonObject) :SequenceMessageBase(header){}
class MessageResponse(header: MessageHeader,var body:String) :SequenceMessageBase(header){}
