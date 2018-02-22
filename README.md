easy-protect-api
===========
[![Build Status](https://travis-ci.org/DANS-KNAW/easy-protect-api.png?branch=master)](https://travis-ci.org/DANS-KNAW/easy-protect-api)

<!-- Remove this comment and extend the descriptions below -->


SYNOPSIS
--------

    easy-protect-api (synopsis of command line parameters)
    easy-protect-api (... possibly multiple lines for subcommands)


DESCRIPTION
-----------

protect api calls with authentication


ARGUMENTS
---------

    Options:

        --help      Show help message
        --version   Show version of this program

    Subcommand: run-service - Starts EASY Protect Api as a daemon that services HTTP requests
        --help   Show help message
    ---

EXAMPLES
--------

    easy-protect-api -o value


INSTALLATION AND CONFIGURATION
------------------------------


1. Unzip the tarball to a directory of your choice, typically `/usr/local/`
2. A new directory called easy-protect-api-<version> will be created
3. Add the command script to your `PATH` environment variable by creating a symbolic link to it from a directory that is
   on the path, e.g. 
   
        ln -s /usr/local/easy-protect-api-<version>/bin/easy-protect-api /usr/bin



General configuration settings can be set in `cfg/application.properties` and logging can be configured
in `cfg/logback.xml`. The available settings are explained in comments in aforementioned files.


BUILDING FROM SOURCE
--------------------

Prerequisites:

* Java 8 or higher
* Maven 3.3.3 or higher

Steps:

        git clone https://github.com/DANS-KNAW/easy-protect-api.git
        cd easy-protect-api
        mvn install
