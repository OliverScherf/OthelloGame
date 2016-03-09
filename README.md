# Othello

## Installation

You either download the compiled jar file, or compile it from source.  
- [OthelloClient](http://www.oliverscherf.de/projects/othello/Othello.jar)  
- [OthelloServer](http://www.oliverscherf.de/projects/othello/OthelloServer.jar)  
	
On some machines you can't run the .jar file with a simple doubleclick, you have to run `java -jar Othello.jar` from command line.  
	
**Note: If you want to run the server on your own server/localhost, you need to change the constant SERVER_IP in the OthelloConstants.java**

### Client

To run the Othello Client from source under windows:  
- Navigate to the src folder using "cd", i.e. `cd C:\Users\Oliver\Downloads\Othello\src`  
- run `javac -cp org\game\othello\*; org\game\othello\Othello.java` to compile the client  
- run `java org.game.othello.Othello` to start Othello  

To run the Othello Client from source under linux:  
- Navigate to the src folder using "cd", i.e. `cd /home/oliver/Othello/src`  
- run `javac -cp org/game/othello/*: org/game/othello/Othello.java` to compile the client  
- run `java org.game.othello.Othello` to start Othello  
	
### Server

To run the Othello Server from source under windows:   
- Navigate to the src folder using "cd", i.e. `cd C:\Users\Oliver\Downloads\Othello\src`  
- run `javac -cp org\game\othello\*; org\game\othello\connection\server\OthelloServer.java` to compile the client  
- run `java org/game/othello/connection/server/OthelloServer` to start Othello  

To run the Othello Server from source under linux:  
- Navigate to the src folder using "cd", i.e. `cd /home/oliver/OthelloServer/Othello/src/`  
- run `javac -cp org/game/othello/*:  org/game/othello/connection/server/OthelloServer.java` to compile the server  
- run `java org/game/othello/connection/server/OthelloServer`to run the server  
- You may want to run the server in a new screen: `screen java org/game/othello/connection/server/OthelloServer`  

## Troublshooting  
 
- make sure to have java1.8 (both JRE and JDK) installed.
- you can also check the log files, located in the logs directory
- somehow it can happend, that you can't open the jar file with a doubleclick. In that case you have to run `java -jar Othello.jar` from command line.  
  
### Windows  
- make sure the java/javac path is set in the enviroment variables, see [here](http://superuser.com/questions/12695/javac-command-not-found)  
- make sure to use dots when you about to start the client under windows. (it does not work with / or \\)

### Linux
- if you can't run javac, see [here](http://stackoverflow.com/questions/8864612/unable-to-run-javac-on-ubuntu)
- (german) also this is in general usefull when you are working with a Linux Distribution: [ubuntuuser.de](https://wiki.ubuntuusers.de/Java/)
