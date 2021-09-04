package com.dreamlike.IM
import com.dreamlike.IM.server.ComponentFactory.Companion.vertx
import com.dreamlike.IM.verticles.IMServerVerticle


fun main(){
vertx.deployVerticle(IMServerVerticle())
}
