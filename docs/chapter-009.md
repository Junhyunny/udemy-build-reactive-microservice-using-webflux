
## Chapter 009. Non-Blocking Restful API using Spring Webflux

* A non-blocking or reactive restful API has the behavior of providing end to end non-blocking communication between the client and service
* non-blocking or reactive is not blocking the thread
    * thread involved in handling the http request and http response is not blocked at all
* Spring WebFlux is a module that is going to help us in achieving the non-blocking or reactive behavior

* non blocking or reactive API using Spring WebFlux
    * embedded netty is middleware
    * netty takes care of reqeust
    * non-blocking 이 동작하는 구간
        * netty
        * database or other service - project reactor 사용
