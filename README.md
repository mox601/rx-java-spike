rx-java-spike
=============

spike for https://github.com/Netflix/RxJava

http
https://github.com/Netflix/RxJava/tree/master/rxjava-contrib/rxjava-apache-http

rx-netty

rx-math

# testing best practices

# how scheduling works?
if we call obs.map(func()).on(scheduler), the func code is executed on the scheduler?
can we control pools?

# how to consume from an endpoint (as an infinite stream)?
e.g. we offer an http server where client pushes messages,
or a queue we consume, one message at a time,
and there is an observable that writes on System.out the messages per seconds

# what if items arrive too fast?
e.g. can we apply backpressure?

# how to control timestamps of events?
e.g. calculate events per seconds of messages loaded from db will be different
if the events are coming live from a queue or the network
can the system work with externally provided time?

min sketch
