# Code to bulk insert records from excel to Database

The code in this repo is to migrate data from excel to db 

### Dependencies

* Automation anywhere dependencies which come along with the package template

* implementation group: 'com.microsoft.sqlserver', name: 'mssql-jdbc', version: '8.2.2.jre11'(if you're using ms sql database, if you're using a different add your dependency accordingly)

### To create and edit your own jar 
* Download the package template from docs.automationanywhere.com

* Unzip it and open it an IDE.

* Delete all the sample classes from the package 

* Add the class BulkInsert from this repo(src\main\java\com\automationanywhere\botcommand)

* In the method action, you can edit the logic as per your convenience and build it to a jar

* I've used PreparedStatement which stores the records before it is committed to the database, so make it faster

* You can view the Main class to check how the example values of the parameters for method action

* To build the jar, open a terminal from the same project and enter the command "gradlew.bat clean build shadowJar"

* To add it to your control room, open your control room , in the manage sections click on packages and click on add package.

* Browse thru your project on local and choose the jar that you've just built.

* You can find the jar in the same project build/libs/ folder.

* Create a new bot and in the action section, you'll find your package there.

* Drag and drop your action from the package.


#### To add the built jar from this repo directly to your control room

* Download the jar from this project to your local.

* To add it to your control room, open your control room , in the manage sections click on packages and click on add package.

* Select the jar that you've just downloaded , click on accept and enable.

* Create a new bot and in the action section, you'll find your package there.






