package com.briolink.dictionaryservice.exception

import com.briolink.lib.common.BlLocaleMessage
import com.briolink.lib.common.BlRestGlobalExceptionHandler
import org.springframework.web.bind.annotation.ControllerAdvice

@ControllerAdvice
class RestGlobalErrorHandler(lm: BlLocaleMessage) : BlRestGlobalExceptionHandler(lm)
