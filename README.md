# Dropship

> Deploy, instantiate, and run your Java applications from any maven repository.

Dropship **automatically** creates a classpath containing all of your project's dependencies and will run the `public static void main(String[])` method of a class you specify!

You can stop building shaded jars and pushing artifacts into production. Instead, just deploy your code to a maven repo and tell Dropship which group/artifact/version you'd like to run!

### Getting Started (from source)

* clone this repo
* run `mvn package` to build Dropship
* explore the `/examples` directory to see some examples
* try running your own applications!

#### Or...

* [Download v1.1](http://repo1.maven.org/maven2/com/zulily/dropship/dropship/1.1/dropship-1.1.jar)

### Usage

    java -jar dropship.jar mygroup:myartifact[:myversion] mygroup.myartifact.Main args...

If you omit the version, Dropship will automatically run the latest version of your artifact.  For the full usage, run

    java -jar dropship.jar --help

### Example

    // Run the latest Clojure CLI:
    java -Dverbose -jar dropship.jar org.clojure:clojure clojure.main

### Features

* automatic dependency resolution
* automatic classpath construction
* aliases to simplify common dropship tasks
* offline mode
* download-only mode

### Configuration

All configuration for Dropship can be managed via a `dropship.properties` configuration file.  By default, Dropship looks for this file in the current working directory.

An example `dropship.properties` file might look like:

    # path to the maven repo to use for artifact resolution
    repo.remote-url = http://url-of-some-maven-repo/

    # local path to use for downloading artifacts and their dependencies
    repo.local-path = /tmp/.m2

    # any additional paths that you'd like added to the classpath
    dropship.additional-paths = /tmp/resources

    # explicit artifact versions you'd like dropship to run
    mygroup.myartifact = 1.0

    # You can leave older entries for the same artifact: Dropship will use the last one it encounters
    # (and you can use this file as a deploy log!)
    mygroup.myartifact = 1.1

    # 21-Dec-2013: v2.0: Fixed some huge bug in myartifact
    mygroup.myartifact = 2.0

### Aliases

Commonly used dropship group/artifact/version/main classes can be **aliased** in order to make things even easier.  In the `dropship.properties` file:

    alias.hello = io.netty:netty-example:4.0.17.Final/io.netty.example.http.helloworld.HttpHelloWorldServer

Aliases can then be used like:

    java -jar dropship.jar hello

### Offline Mode

Dropship can be run in offline mode by using the `--offline` option, or by setting `dropship.offline=true` in the `dropship.properties` file.
When run in offline mode, Dropship will attempt to resolve all of an artifact's dependencies without contacting a remote maven repo.

### Download Mode

Dropship can be run in download mode by using the `--download=<local_path>` option.  When run in download mode, Dropship will resolve all
of an artifact's dependencies, then copy the resulting artifacts to the specified `<local_path>`.  Dropship will not attempt to build a
classpath or run a main method when run in download mode.

### JVM Stats

The statsd functionality present in 1.0 and 1.1 has been moved to [dropship-statsd-agent](https://github.com/zulily/dropship-statsd-agent/).

## License


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

