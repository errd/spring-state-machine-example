package com.example.statemachine

import com.example.statemachine.model.EventsEnum
import com.example.statemachine.model.StatesEnum
import com.example.statemachine.service.State11ActionService
import com.ninjasquad.springmockk.MockkBean
import io.mockk.Runs
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import io.mockk.just
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.messaging.support.MessageBuilder
import org.springframework.statemachine.StateMachine
import reactor.core.publisher.Mono

@SpringBootTest
@ExtendWith(MockKExtension::class)
class StateMachineTest {
    @Autowired
    private lateinit var sm: StateMachine<StatesEnum, EventsEnum>

    @MockkBean
    private lateinit var state11Action: State11ActionService

    @Test
    fun `should run state machine and call action once`() {
        sm.startReactively().subscribe()

        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.INIT_COMPLETED).build())).subscribe()
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.INNER_INIT_COMPLETED).build())).subscribe()
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.STATE_11_COMPLETED).build())).subscribe()
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.STATE_21_COMPLETED).build())).subscribe()
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.JOIN_COMPLETED).build())).subscribe()
        sm.sendEvent(Mono.just(MessageBuilder.withPayload(EventsEnum.INNER_COMPLETED).build())).subscribe()

        while(!sm.isComplete) {
            Thread.sleep(100)
        }

        verify(exactly = 1) {
            state11Action.invoke()
        }
    }
}