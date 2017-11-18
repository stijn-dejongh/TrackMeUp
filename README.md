# TrackMeUp

## About

Side project about time tracking and task management.

The overall idea is to have fun creating a tool that allows people to keep track
of TODO lists, manage and organize tasks.
Much of the functionality is based on what the 'org-mode' of eMacs already offers.
The added value of this project is to provide a user interface, and to keep the data formats used
open and portable.

### Version 0.2.0

Refactored the project structure to diferentiate between core-components, and
the frontend(s).


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

If you just want to run a local version of the web application,
follow these steps:

* Download the source code
* In the root directory (which contains the pom) execute 'mvn clean install'
* For each module, a 'target' directory will be created

### Run the web application
* Open a terminal window and navigate to the 'trambu-web/target' directory
* Execute 'java -jar XXXX' , where XXXX is the name of the TraMBU jar
* Navigate your browser to 'http://localhost:9666'
* Set up the application to use your own 'todo.txt' file

### Run the standalone application
* Open a terminal window and navigate to the 'trambu-app/target' directory
* Execute 'java -jar XXXX' , where XXXX is the name of the TraMBU jar
* The application will now open in a desktop window
* Set up the application to use your own 'todo.txt' file

## Code quality

In order to keep the code maintainable and of reasonably high quality,
I use SonarCloud to point out any issues that exist.
The report can be found at:
https://sonarcloud.io/dashboard?id=be.doji.productivity%3Atrambu
