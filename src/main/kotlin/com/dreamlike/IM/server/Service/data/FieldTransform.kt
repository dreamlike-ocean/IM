package com.dreamlike.IM.server.Service.data

import com.dreamlike.IM.Util.humpToLine
import kotlin.reflect.full.memberProperties

interface FieldTransform {

  fun transToMap(index:Int = 0)= this::class.memberProperties.associate { (it.name + index) to it.call(this) }

  fun transToLineMap(index:Int = 0)= this::class.memberProperties.associate { (it.name.humpToLine() + index) to it.call(this) }


}
