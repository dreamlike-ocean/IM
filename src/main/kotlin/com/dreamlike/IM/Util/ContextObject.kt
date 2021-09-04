package com.dreamlike.IM.Util

import com.dreamlike.IM.server.ComponentFactory
import io.vertx.core.Context
import io.vertx.core.Vertx
import io.vertx.core.impl.ContextInternal

fun nowContext() = ContextInternal.current() as Context







