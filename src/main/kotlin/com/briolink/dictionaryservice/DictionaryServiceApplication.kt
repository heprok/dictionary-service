package com.briolink.dictionaryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class DictionaryServiceApplication

fun main(args: Array<String>) {
    runApplication<DictionaryServiceApplication>(*args)
}
