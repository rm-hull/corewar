# Core War [![Build Status](https://secure.travis-ci.org/rm-hull/corewar.svg)](http://travis-ci.org/rm-hull/corewar)

A clojure / clojurescript / core.async implementation of A. K. Dewdney's Core War

## An introduction to hostile programming

Corewar is a game from the 1980's, played between computer programs written
in Redcode, a language similar to assembly. The programmers design their 
battle programs to remove opponents from the memory of the MARS virtual 
computer by any means possible.

Some of the simpler techniques include blindly overwriting memory, 
searching for the opponent or spawning off new processes. These are 
commonly known as stone, scissors, paper after the popular playground 
game. Stone usually wins against scissors, scissors normally defeat paper, 
and paper mostly beats stone.

Here's an example of a typical Corewar program:

```redcode
     org   wipe

     step  equ 5
     first equ bomb-10

bomb:mov.i #1,       -1

ptr: sub   #step,    #first
wipe:jmz.f ptr,      @ptr

     mov   bomb,     >ptr
     djn.f wipe,     {ptr-5

     end
```

This simple example of scissors once held a 20 point lead over it's rivals.
The first instruction is never executed, it's the bomb used to overwrite 
opponents. The next two instructions form a loop which looks through memory
for an opponent, and the final two instructions actually overwrite it.

Corewar is still going strong, and celebrated it's 25th anniversary in 2009.
If you'd like to discover more about Corewar, here are the top resources:

* [The Beginner's Guide to Redcode](http://vyznev.net/corewar/guide.html) 
  will teach you the language of Corewar
* [pMARS](http://corewar.co.uk/pmars) is a portable implementation of the 
  Corewar virtual machine
* [Corewar Tutorials](http://corewar.co.uk/guides.htm) exist on virtually 
  every aspect of the game
* [Koenigstuhl](http://users.obs.carnegiescience.edu/birk/COREWAR/koenigstuhl.html)
  is an archive of thousands of published Corewar programs
* [SAL](http://sal.discontinuity.info) organises a number of on-going king 
  of the hill tournaments
* [sfghoul](http://sfghoul.blogspot.com) and [impomatic](http://impomatic.blogspot.com)
  report the latest Corewar news on their blogs

[#corewars](irc://irc.freenode.net/#COREWARS) is the official Corewar 
discussion channel, hosted by [irc.freenode.net](irc://irc.freenode.net/#COREWARS).

What are your experiences with Corewar, have you ever had any success?

## Implementation details

### Pre-requisites

You will need [Leiningen](https://github.com/technomancy/leiningen) 2.4.2 or above installed.

### Building

To build and install the library locally, run:

    $ lein test
    $ lein cljsbuild once
    $ lein install

### Including in your project

There _will be_ a version hosted at [Clojars](https://clojars.org/rm-hull/corewar).
For leiningen include a dependency:

```clojure
[rm-hull/corewar "0.0.1-SNAPSHOT"]
```

For maven-based projects, add the following to your `pom.xml`:

```xml
<dependency>
  <groupId>rm-hull</groupId>
  <artifactId>corewar</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## TODO

## Known Bugs

## References

* http://corewar.co.uk/cwg.txt
* https://en.wikipedia.org/wiki/Core%20War

## License

The MIT License (MIT)

Copyright (c) 2014 Richard Hull

Permission is hereby granted, free of charge, to any person obtaining a copy of
this software and associated documentation files (the "Software"), to deal in
the Software without restriction, including without limitation the rights to
use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
the Software, and to permit persons to whom the Software is furnished to do so,
subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

