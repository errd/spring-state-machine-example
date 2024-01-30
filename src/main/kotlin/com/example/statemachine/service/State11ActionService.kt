package com.example.statemachine.service

import org.springframework.stereotype.Service

@Service
class State11ActionService {
    operator fun invoke() {
        println("===== State 11 action called.")
    }
}