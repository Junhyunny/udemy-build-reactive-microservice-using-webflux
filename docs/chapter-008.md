
## Chapter 008. Introduction to Reactive Streams

* reactive streams specification is created by engineers from multiple organications
    * lightbend
    * netflix
    * VMWare(Pivotal)
* Reactive Streams Specification
    * publisher
    * subscriber
    * subscription
    * processor

* publisher
    * represents the data source
        * database
        * remote service
* subscriber
    * onSubscribe
    * onNext
    * onError
    * onComplete
* subscription
    * request
    * cancel
* processor
    * extends subscriber and publisher
    * processor can behave as a subscriber and publisher
    * not really used this on a data to dat basis

* how it works together? - happy case

1. subscribe(from subsriber to publisher)
1. onSubscribe(from publisher to subscriber)
1. request(from subscriber to publisher)
1. onNext(from publisher to subscriber)
1. onComplete(from publisher to subscriber)

* how it works together? - error/exception case
    * excpetions are treated like the data
    * reactive stream is dead when an exception is thrown

1. subscribe(from subsriber to publisher)
1. onSubscribe(from publisher to subscriber)
1. request(from subscriber to publisher)
1. onError(from publisher to subscriber)

* flow API  
    * release as part of java 9
    * this hold the contract for reactive streams but no implementation is available as part of JRE