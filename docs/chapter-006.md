
## Chapter 005. Why Reactive Programming? - Part 2

* asynchronous options in Java
    * Callbacks
    * Future
* Callbacks
    * asynchronous method that accept a callback as a parameter
    * invokes it when the blocking call completes
    * writing code with Callbacks are hard to compose and difficult to read and maintain
* Future
    * released in java 5
    * write asynchronous code
    * disadvantages
        * no easy way to combine the result from multiple futures
    * Future.get() - this is blocking call
* CompletableFuture
    * released in java 8
    * write asynchronous code in a functional style
    * easy to compose/combine multiple futures
    * disadvatages
        * Future that returns many elements
        * e.g. `CompletableFuture<List<Result>>` will nee to wait for the whole collection to built and readily available
* Drawbacks of Spring MVC
    * concurrency is limited in Spring MVC
    * blocking code leads to inefficient usage of threads
    * Servlet API at the server level is a blocking one
