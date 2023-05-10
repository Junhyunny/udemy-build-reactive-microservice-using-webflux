package com.example.chapter012.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import java.util.List;

class FluxAndMonoGeneratorServiceTest {

    @Test
    void test01() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.namesFlux())
                // .expectNextCount(3)
                // .expectNext("Alex", "Ben", "Choi")
                .expectNext("Alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void test02() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.nameMono())
                .expectNext("Jun")
                .verifyComplete();
    }

    @Test
    void test03() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.capitalNamesFlux())
                .expectNext("ALEX", "BEN", "CHOI")
                .verifyComplete();
    }

    @Test
    void test04() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.immutableNamesFlux())
                .expectNext("Alex", "Ben", "Choi")
                .verifyComplete();
    }

    @Test
    void test05() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.filteredNamesFlux(3))
                .expectNext("ALEX", "CHOI")
                .verifyComplete();
    }

    @Test
    void test06() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.alphabetsOfNamesFlux())
                .expectNext("A", "L", "E", "X")
                .expectNext("B", "E", "N")
                .expectNext("C", "H", "O", "I")
                .verifyComplete();
    }

    @Test
    void test07() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.delayedAlphabetsFlux())
                // fail because flatMap asynchronously works
//                .expectNext("A", "L", "E", "X")
//                .expectNext("B", "E", "N")
//                .expectNext("C", "H", "O", "I")
                .expectNextCount(11)
                .verifyComplete();
    }

    @Test
    void test08() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.delayedAlphabetsFluxWithConcatMap())
                .expectNext("A", "L", "E", "X")
                .expectNext("B", "E", "N")
                .expectNext("C", "H", "O", "I")
                .verifyComplete();
    }

    @Test
    void test09() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.delayedAlphabetsFluxWithMono())
                .expectNext(List.of("A", "L", "E", "X"))
                .expectNext(List.of("B", "E", "N"))
                .expectNext(List.of("C", "H", "O", "I"))
                .verifyComplete();
    }

    @Test
    void test10() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.delayedAlphabetsFluxWithFlatMapMany())
                .expectNext("J", "U", "N")
                .verifyComplete();
    }

    @Test
    void test11() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.transformFlux(2))
                .expectNext("A", "L", "E", "X")
                .expectNext("B", "E", "N")
                .expectNext("C", "H", "O", "I")
                .verifyComplete();
    }

    @Test
    void test12() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.defaultIfEmpty(5))
                .expectNext("EMPTY")
                .verifyComplete();
    }

    @Test
    void test13() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.switchIfEmpty(5))
                .expectNext("SWITCH")
                .verifyComplete();
    }

    @Test
    void test14() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreConcat())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void test15() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreConcatWith())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }

    @Test
    void test16() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreMerge())
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void test17() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreMergeWith())
                .expectNext("A", "D", "B", "E", "C", "F")
                .verifyComplete();
    }

    @Test
    void test18() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreMergeSequential())
                .expectNext("A", "B", "C", "D", "E", "F")
                .verifyComplete();
    }


    @Test
    void test19() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreZip())
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }

    @Test
    void test20() {

        FluxAndMonoGeneratorService sut = new FluxAndMonoGeneratorService();

        StepVerifier.create(sut.exploreZipWith())
                .expectNext("AD", "BE", "CF")
                .verifyComplete();
    }
}