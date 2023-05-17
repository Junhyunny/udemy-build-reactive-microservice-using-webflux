
## Chapter 071. How does Netty handle the request?

* Netty 
    * Webflux defualt server
    * channel created for communication between client and server
* Channel
    * Channel has ChannelHandlers
    * Accepting the client connection
    * Reading the data as bytes from the network to a Java object
    * Transformation
* Channel and EventLoop
    * Netty uses EventLoop model to handle the connections in a nonblocking fashion
    * An EventLoop is powered by one single thread
    * NodeJs uses the same pattern
    * Number of eventloops to handle the request is equal to no of cores in your machine
    * EventLoops are part of the EventLoopGroup
* How Channel and EventLoop linked?
    * Any time a channel is created it gets assigned to an EventLoop
    * This EventLoop is responsible for handling the different events that occurs in the life time of a channel
* Channel Lifecycle
    1. ChannelUnregistered
        * Channel is created and its not registered with the event loop
    1. ChannelRegistered
        * Channel is registered with the event loop
    1. ChannelActive
        * Channel is active and its now possible to send and receive the data
    1. ChannelInActive
        * Channel is not connected to the client anymore and ready to be closed
    * All these lifecycle changes are treated as events
* How Netty handles the request?
    * Netty had two EventLoopGroups
    * One to just accept connections
    * Other one to handle them
