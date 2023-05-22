package com.example.movieinfoservice;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinksTest {

    @Test
    void example01() {

        // replay - 새로운 subscriber 접근 시 다시 실행하겠다는 의미
        Sinks.Many<Integer> replaySinks = Sinks.many().replay().all();
        replaySinks.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySinks.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> flux1 = replaySinks.asFlux();
        flux1.subscribe(number -> System.out.printf("subscriber-1: %s\n", number));
        Flux<Integer> flux2 = replaySinks.asFlux();
        flux2.subscribe(number -> System.out.printf("subscriber-2: %s\n", number));

        for (int index = 3; index < 100; index++) {
            replaySinks.emitNext(index, Sinks.EmitFailureHandler.FAIL_FAST);
        }
        replaySinks.tryEmitNext(100);

        Flux<Integer> flux3 = replaySinks.asFlux();
        flux3.subscribe(number -> System.out.printf("subscriber-3: %s\n", number));
    }

    @Test
    void example02() {

        Sinks.Many<Integer> multicastSinks = Sinks.many().multicast().onBackpressureBuffer();
        multicastSinks.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        multicastSinks.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> flux1 = multicastSinks.asFlux();
        flux1.subscribe(number -> System.out.printf("subscriber-1: %s\n", number));
        Flux<Integer> flux2 = multicastSinks.asFlux();
        flux2.subscribe(number -> System.out.printf("subscriber-2: %s\n", number));


        multicastSinks.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }

    @Test
    void example03() {

        Sinks.Many<Integer> unicastSinks = Sinks.many().unicast().onBackpressureBuffer();
        unicastSinks.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        unicastSinks.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);


        Flux<Integer> flux1 = unicastSinks.asFlux();
        flux1.subscribe(number -> System.out.printf("subscriber-1: %s\n", number));

        // error setting other subscribe
//        Flux<Integer> flux2 = unicastSinks.asFlux();
//        flux2.subscribe(number -> System.out.printf("subscriber-2: %s\n", number));


        unicastSinks.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);
    }
}
