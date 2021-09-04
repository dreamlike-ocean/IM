package com.dreamlike.IM.server.Service

import com.dreamlike.IM.Util.use
import com.dreamlike.IM.server.Service.data.User
import io.vertx.kotlin.coroutines.await
import io.vertx.sqlclient.Pool
import io.vertx.sqlclient.templates.SqlTemplate

class UserDBStore(private val pool: Pool) {
  suspend fun selectUserByUsername(username:String):User?{
    val res = pool.connection.await().use {
      SqlTemplate.forQuery(it,"SELECT * FROM user WHERE username = #{username}")
        .mapTo(User.UserRowMapper)
        .execute(mapOf("username" to username)).await()
    }
    return res.first()
  }

  suspend fun insertUser(user:User) = pool.connection.await().use {
      SqlTemplate.forUpdate(it,"INSERT INTO user (username, password) values (#{username},#{password})")
        .mapFrom(User.UserTupleMapper)
        .execute(user).await().size() == 1
    }
}
