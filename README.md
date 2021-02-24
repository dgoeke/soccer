# Leapyear Soccer

Calculates ranks for a soccer league when given a list of game results.

## Installation

1. Download and install [Java](https://adoptopenjdk.net/) from the web or
   your system's package manager. This was tested with OpenJDK 11.
1. Download and install [Leiningen](https://leiningen.org/#install) (a Clojure
   build tool) from the web or from your system's package manager.

## Run Tests

    $ lein test
    lein test soccer.core-test

    Ran 6 tests containing 14 assertions.
    0 failures, 0 errors.

## Build

    $ lein uberjar
    Compiling soccer.core
    Created $PATH/target/uberjar/soccer-0.1.0-SNAPSHOT.jar
    Created $PATH/target/uberjar/soccer-0.1.0-SNAPSHOT-standalone.jar
    
Note that the "standalone" version is the one that's executable.

## Usage

Pipe input data from stdin:

    $ cat resources/sample-input.txt | java -jar target/uberjar/soccer-0.1.0-SNAPSHOT-standalone.jar
    
Or specify a filename as a parameter:

    $ java -jar target/uberjar/soccer-0.1.0-SNAPSHOT-standalone.jar resources/sample-input.txt

## Future Improvements

  * Team name parsing is brittle and can only contain A-Z, a-z, 0-9, _, or spaces.
  * Line parsing regex should be replaced with something less powerful.
  * Malformed input is never checked or handled.
  * Tests are minimal due to time pressure.
  * Inline function descriptions should be converted to docstrings.
  * `assign-ranks` function feels clumsier than it should be; I moved on when it worked
    because of time.
  * Input/output data format should be documented in the repo.
  * Functions/types should have specs added for type checking.
  * If functions/types had specs, we could do generative testing.
  * Performance not tested on large datasets, but should be roughly O(N log(N)) due to the sort
    (The rest of the map/reduce operations are lazy and should be mostly linear).
