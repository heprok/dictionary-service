package com.briolink.dictionaryservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

@SpringBootApplication
@ConfigurationPropertiesScan
class LocationServiceApplication

fun main(args: Array<String>) {
    runApplication<LocationServiceApplication>(*args)
}
