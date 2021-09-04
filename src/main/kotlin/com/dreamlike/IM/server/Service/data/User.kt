package com.dreamlike.IM.server.Service.data

import io.vertx.sqlclient.Row
import io.vertx.sqlclient.templates.RowMapper
import io.vertx.sqlclient.templates.TupleMapper

data class User(val username:String,val password:String,val userId:Int = 0){
  companion object{
    val UserRowMapper = RowMapper{ User(it.getString("username"),it.getString("password"),it.getInteger("user_id")) }
    val UserTupleMapper = TupleMapper.mapper<User> {user -> mapOf("username" to user.username,"password" to user.password,"userId" to user.userId) }
  }
}
