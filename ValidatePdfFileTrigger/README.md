# Trigger to execute when a pdf file is created or added in a directory

The code in this repo is for a trigger which executes a bots when a new pdf is added 

### Dependencies

* Automation anywhere dependencies which come along with the package template

### To create and edit your own jar 
* Download the package template from docs.automationanywhere.com

* Unzip it and open it an IDE.

* Delete all the sample classes from the package 

* Add the class ValidatePdfFile from this repo(src\main\java\com\automationanywhere\botcommand\psd\trigger\validatepdf)

* In the method startTrigger, you can edit the logic as per your convenience and build it to a jar

* To build the jar, open a terminal from the same project and enter the command "gradlew.bat clean build shadowJar"

* To add it to your control room, open your control room , in the manage sections click on packages and click on add package.

* Browse thru your project on local and choose the jar that you've just built.

* You can find the jar in the same project build/libs/ folder.

#### To add the built jar from this repo directly to your control room
* Download the jar from this repo to your local.

* To add it to your control room, open your control room , in the manage sections click on packages and click on add package.

* Select the jar that you've just downloaded , click on accept and enable.


