package com.example.statemachine.config

import com.example.statemachine.model.EventsEnum
import com.example.statemachine.model.StatesEnum
import com.example.statemachine.service.State11ActionService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer
import org.springframework.statemachine.listener.StateMachineListener
import org.springframework.statemachine.listener.StateMachineListenerAdapter
import org.springframework.statemachine.state.State


@Configuration
@EnableStateMachine
class StateMachineConfig : EnumStateMachineConfigurerAdapter<StatesEnum, EventsEnum>() {
    @Autowired
    private lateinit var state11Action: State11ActionService

    override fun configure(config: StateMachineConfigurationConfigurer<StatesEnum, EventsEnum>) {
        config
            .withConfiguration()
            .autoStartup(true)
            .listener(listener())
    }

    @Bean
    fun listener(): StateMachineListener<StatesEnum, EventsEnum>? {
        return object : StateMachineListenerAdapter<StatesEnum, EventsEnum>() {
            override fun stateChanged(from: State<StatesEnum, EventsEnum>?, to: State<StatesEnum, EventsEnum>?) {
                println("State change from ${from?.id} to ${to?.id}")
            }

            override fun stateMachineStarted(stateMachine: StateMachine<StatesEnum, EventsEnum>) {
                println("State machine started.")
            }

            override fun stateMachineStopped(stateMachine: StateMachine<StatesEnum, EventsEnum>?) {
                println("State machine stopped.")
            }
        }
    }

    override fun configure(states: StateMachineStateConfigurer<StatesEnum, EventsEnum>) {
        states.withStates()
            .initial(StatesEnum.INIT)
            .state(StatesEnum.INNER)
            .end(StatesEnum.END)

            .and().withStates().parent(StatesEnum.INNER)
            .initial(StatesEnum.INNER_INIT)
            .fork(StatesEnum.FORK)
            .join(StatesEnum.JOIN)
            .end(StatesEnum.INNER_END)

            .and().withStates().parent(StatesEnum.FORK)
            .initial(StatesEnum.STATE_11)
            .state(StatesEnum.STATE_11) {
                state11Action.invoke()
            }
            .end(StatesEnum.STATE_12)

            .and().withStates().parent(StatesEnum.FORK)
            .initial(StatesEnum.STATE_21)
            .end(StatesEnum.STATE_22)
    }

    override fun configure(transitions: StateMachineTransitionConfigurer<StatesEnum, EventsEnum>) {
        transitions
            .withExternal()
            .source(StatesEnum.INIT)
            .target(StatesEnum.INNER)
            .event(EventsEnum.INIT_COMPLETED)

            .and().withExternal()
            .source(StatesEnum.INNER_INIT)
            .target(StatesEnum.FORK)
            .event(EventsEnum.INNER_INIT_COMPLETED)

            .and().withFork()
            .source(StatesEnum.FORK)
            .target(StatesEnum.STATE_11)
            .target(StatesEnum.STATE_21)

            .and().withExternal()
            .source(StatesEnum.STATE_11)
            .target(StatesEnum.STATE_12)
            .event(EventsEnum.STATE_11_COMPLETED)

            .and().withExternal()
            .source(StatesEnum.STATE_21)
            .target(StatesEnum.STATE_22)
            .event(EventsEnum.STATE_21_COMPLETED)

            .and().withJoin()
            .source(StatesEnum.STATE_12)
            .source(StatesEnum.STATE_22)
            .target(StatesEnum.JOIN)

            .and().withExternal()
            .source(StatesEnum.JOIN)
            .target(StatesEnum.INNER_END)
            .event(EventsEnum.JOIN_COMPLETED)

            .and().withExternal()
            .source(StatesEnum.INNER)
            .target(StatesEnum.END)
            .event(EventsEnum.INNER_COMPLETED)
    }
}