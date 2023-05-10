package com.example.chapter012.service;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;

@Slf4j
public class FluxAndMonoGeneratorService {

    public static void main(String[] args) {

        FluxAndMonoGeneratorService services = new FluxAndMonoGeneratorService();

        services.namesFlux()
                .subscribe(name -> log.info("Name is {}", name));
        services.nameMono()
                .subscribe(name -> log.info("Name is {}", name));
        services.capitalNamesFlux()
                .subscribe(name -> log.info("Name is {}", name));
    }

    public Flux<String> namesFlux() {
        // flux is database or remote service call
        return Flux.fromIterable(List.of("Alex", "Ben", "Choi")).log();
    }

    public Flux<String> capitalNamesFlux() {
        return namesFlux().map(String::toUpperCase);
    }

    public Flux<String> immutableNamesFlux() {
        var namesFlux = namesFlux();
        // 내부에서 새로운 Flux 객체를 반환하므로 원천 소스(source)는 변경되지 않는다.
        namesFlux.map(String::toUpperCase);
        return namesFlux;
    }

    public Flux<String> filteredNamesFlux(int length) {
        return namesFlux()
                .map(String::toUpperCase)
                .filter(name -> name.length() > length)
                .log();
    }

    public Flux<String> alphabetsOfNamesFlux() {
        return namesFlux()
                .map(String::toUpperCase)
                .flatMap(name -> Flux.fromArray(name.split("")));
    }

    public Flux<String> delayedAlphabetsFlux() {
        return namesFlux()
                .map(String::toUpperCase)
                .flatMap(name -> Flux.fromArray(name.split("")).delayElements(Duration.ofSeconds(1)))
                .log();
    }

    public Flux<String> delayedAlphabetsFluxWithConcatMap() {
        return namesFlux()
                .map(String::toUpperCase)
                .concatMap(name -> Flux.fromArray(name.split("")).delayElements(Duration.ofSeconds(1)))
                .log();
    }

    public Flux<List<String>> delayedAlphabetsFluxWithMono() {
        return namesFlux()
                .map(String::toUpperCase)
                .flatMap(name -> Mono.just(List.of(name.split(""))).delayElement(Duration.ofSeconds(1)))
                .log();
    }

    public Flux<String> delayedAlphabetsFluxWithFlatMapMany() {
        return nameMono()
                .map(String::toUpperCase)
                .flatMapMany(name -> Flux.fromArray(name.split("")))
                .log();
    }

    public Flux<String> transformFlux(int length) {

        Function<Flux<String>, Flux<String>> filterMap = (flux) -> flux.map(String::toUpperCase).filter(name -> name.length() > length);

        return namesFlux()
                .transform(filterMap)
                .flatMap(name -> Flux.fromArray(name.split("")))
                .log();
    }

    public Flux<String> defaultIfEmpty(int length) {
        return transformFlux(length)
                .defaultIfEmpty("EMPTY");
    }

    public Flux<String> switchIfEmpty(int length) {
        return transformFlux(length).switchIfEmpty(Flux.just("SWITCH"));
    }

    public Flux<String> exploreConcat() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        return Flux.concat(abcFlux, defFlux).log();
    }

    public Flux<String> exploreConcatWith() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        return abcFlux.concatWith(defFlux).log();
    }

    public Flux<String> exploreMerge() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        return Flux.merge(abcFlux, defFlux).log();
    }

    public Flux<String> exploreMergeWith() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        return abcFlux.mergeWith(defFlux).log();
    }

    public Flux<String> exploreMergeSequential() {
        var abcFlux = Flux.just("A", "B", "C")
                .delayElements(Duration.ofMillis(100));
        var defFlux = Flux.just("D", "E", "F")
                .delayElements(Duration.ofMillis(150));
        // same with concat
        return Flux.mergeSequential(abcFlux, defFlux).log();
    }

    public Flux<String> exploreZip() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        // same with concat
        return Flux.zip(abcFlux, defFlux, (item1, item2) -> item1 + item2).log();
    }

    public Flux<String> exploreZipWith() {
        var abcFlux = Flux.just("A", "B", "C");
        var defFlux = Flux.just("D", "E", "F");
        // same with concat
        return abcFlux.zipWith(defFlux, (item1, item2) -> item1 + item2).log();
    }

    public Mono<String> nameMono() {
        return Mono.just("Jun").log();
    }
}
