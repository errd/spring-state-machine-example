package com.example.statemachine

import org.springframework.boot.autoconfigure.AutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@AutoConfiguration
@SpringBootApplication
class StateMachineApplication

fun main(args: Array<String>) {
	runApplication<StateMachineApplication>(*args)
}
