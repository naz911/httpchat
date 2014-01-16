HTTP/REST Chat
Copyright (C) 2014-2015 naz911@home (benoit.houle@gmail.com).

## HTTP/REST Chat application

Look at the HttpChatClient python script for the client API, currently supporting:

    Registration
    Login/Logout
    Poll
    Profile
        Update
        Get
    Contact
        Search
        Invite
        Accept/Deny
    Message
        Send

Next Features:

    Contact
        Delete
    Group
        Create
        Invite

Libraries/Dependencies:

    AppEngine (GAE)
    Objectivy
    Guice
    GSON
    RestEasy
    Transaction (AOP)

Requires [Apache Maven](http://maven.apache.org) 3.0 or greater, and JDK 6+ in order to run.

To build, run

    mvn package

Building will run the tests, but to explicitly run tests you can use the test target

    mvn test

To start the app, use the [App Engine Maven Plugin](http://code.google.com/p/appengine-maven-plugin/) that is already included in this demo.  Just run the command.

    mvn appengine:devserver

For further information, consult the [Java App Engine](https://developers.google.com/appengine/docs/java/overview) documentation.

To see all the available goals for the App Engine plugin, run

    mvn help:describe -Dplugin=appengine
