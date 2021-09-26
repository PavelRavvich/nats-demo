For fast test server from NATS company `nats://demo.nats.io:4222`

Running in docker

`docker run -p 4222:4222 -p 8222:8222 -p 6222:6222 --name nats-server -ti nats:latest`

By default the NATS server exposes multiple ports:

    4222 is for clients.
    8222 is an HTTP management port for information reporting.
    6222 is a routing port for clustering.
    Use -p or -P to customize.

