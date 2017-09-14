# httpbis

Check out [httpbin.org](httpbin.org).


Reproduce (in spirit) its features using Clojure and Ring, and you get httpbis.


For now it's using Compojure. That's not set in stone.

## TODO

Here is the most important stuff available in the original:

[ ] The index / documentation page
[ ] Compression routes (gzip, brotli, deflate)
[ ] Basic verb routes (get post patch put delete anything)
[ ] Redirect routes
[ ] Cookie routes
[ ] HTTP auth routes
[ ] Robots routes
[ ] Cache / ETag routes
[ ] Image routes
[ ] Link route
[ ] Delay/drip routes
[ ] Stream/range routes

Here are some extras :

[ ] Spider maze (advanced link)

## Prerequisites

You will need [Leiningen][1] 1.7.0 or above installed.

[1]: https://github.com/technomancy/leiningen

## Running

To start a web server for the application, run:

    lein ring server

## License

Copyright © 2017 mrzor
FIXME BSD 2 Clause LICENSE
