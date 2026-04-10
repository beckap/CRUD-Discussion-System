# Discussion System
CSE360 project - PHASE 3

The application is a JavaFX-based desktop application inspired by ED Discussion. 
It allows users to create and participate in discussion threads with role-based access control.

There are three user roles:
- Admin
- Staff
- Student

The system supports:
- Creating discussion posts
- Replying to threads
- Role-based permissions
- Persistent data storage using a database
- Admin role permissions for managament
- Staff grading and review system

## Requirements
- Java 25
- JavaFX SDK
- Eclipse IDE
- JUnit Libs (folder is in repo)

## Getting Started

1. Clone REPO:  
   Open command line/terminal and clone repo.  
   In your files, rename the folder to Phase3.
3. Import into Eclipse:  
   File -> New -> Java Project  
   Project Name: Phase3    
   Location: Browse where you have cloned repo directory  
   Click Finish
4. Manually link JUnit JARS:  
   Build Path -> Configure build path -> Modulepath -> Add JARS -> Select all JARS inside JUnit lib folder (Add JUnit 5 Lib as well to Modulepath)
6. Enable usual Build Paths:   
   JavaFX, Java SDK 25, and H2 database
7. Run Configurations:
   - Usual Run configuration for foundationsMain with the arguments
   - JUnit needs manual Run Configuration:
        Add this to run configuration VM arguments:
        -ea
        -XX:+EnableDynamicAgentLoading
        -Dnet.bytebuddy.experimental=true
