package com.dreamlike.IM.exception

class TooBigBodyException(private val maxLength:Int):RuntimeException("请求体过大 超过${maxLength}") {

}
