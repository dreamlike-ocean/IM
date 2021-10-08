package com.dreamlike.IM.server.Service.data

import com.dreamlike.IM.message.MessageHeader
import io.vertx.core.json.JsonObject

open class SequenceMessageBase(val header:MessageHeader){}

class MessageRequest(header: MessageHeader,var param: JsonObject,var requestType:String) :SequenceMessageBase(header){}
class MessageResponse(header: MessageHeader,var body:JsonObject,var requestType:String) :SequenceMessageBase(header){}
