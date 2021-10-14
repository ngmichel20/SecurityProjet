# SecurityProjet

======== How to launch the project ===========

1) Having NetBeans 11.2

2) In NetBeans, open the 3 differents projects : 

		- "Security_Client"
		- "Security_Common"
		- "Security_Server"
		
3) Before lauching the project, you must change the path that will store all the differents clients's files (so it will be store like in a server).

   For that, go into the class "ThreadServer" in the "Security_Server" project, and change the path of the variable "destination".
   It looks like this in the project : 
   - private static final String destination = "C:\\Users\\ng-20\\Desktop\\examSECI5"; //Here, it is my path that I choose in my computer.
   
   So now, you must choose a path where you want to put all the files in your computer.

4) Once you've done that, you can now launch the project. To do that :

   - Run the server class in the "Security_Server" project. The class to run named : "Main.java"
   - After that, run the class "MainClient.java" in the project named : "Security_Client"
 
5) Now you can enter all the client's functionalities can do.

   /!\ But don't forget, you must register first and then connect to the server to be able to do all the other functionalities like download, upload, delete, etc... /!\

6) Enjoy it.
