# TrackMeUp

## About

Side project about time tracking and task management

The overall idea is to have fun creating a tool that allows people to keep track
of TODO lists, manage and organize tasks.

## Design vission

Some ideas I'd like to visit are:

* Webservice based application design
    * This allows for easy portability accross multiple devices without the need for 'synching' or installations
* Plain text data storage
    * Follow Todo.txt guidelines and design (https://github.com/todotxt/todo.txt)
* Learning paths and skill trees
* Task/occupation suggestions based on amount of free time

## Technical requirements

If you want to get hacking at my code, or contribute to the effort, 
you will need the following

* JDK 8
* Maven
* a git client

## Running the code from source

If you just want to run a local version of the application,
follow these steps:

* Download the source code
* In the root directory (which contains the pom) execute 'mvn clean install'
* Eventually, a 'target' directory will be created
* Open a terminal window and navigate to the 'target' directory
* Execute 'java -jar XXXX' , where XXXX is the name of the TraMBU jar
* Navigate your browser to 'http://localhost:9666'
* Set up the application to use your own 'todo.txt' fileó
