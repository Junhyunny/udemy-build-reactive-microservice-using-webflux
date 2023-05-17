
## Chapter 072. Introduction to Functional Web?

* Functional Web
    * alternative programming model for building RESTFUL APIs in Spring WebFlux
* Functional Web module uses the functional programming aspects
    * Lambdas
    * Method References
    * Functional Interface
* Two Components
    * Router
        * rest end-points are configured
        * alternative for annotations such as @GetMapping, @PostMapping
    * Handler
        * code to handle the reqeust is located
        * business logic
* Benefits and Challenges
    * Benefits using Functional Web
        * All the restful api end-points are configured in one single file
        * Code is lightweight compared to the controller alternative
    * Challenges using Functional Web
        * Need to have knowledge about functional programming
        * Bean validation is different in Functional Web
        * Exception handling in Functional Web is different from the controller approach
