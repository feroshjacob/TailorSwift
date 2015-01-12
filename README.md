TailorSwift
===========

Eclipse plugin tailored for swift big data access using scalding. This is an eclipse plugin for creating a Scalding/WebScalding projects. The plugin  has the capability of deploying the fatjar of the project along with a script file (to execute the fatjar)


<b> Prerequisites </b>

1. Mac/Windows environment (Implemented in Mac, tried in Windows. see no problems in working on a Linux machine)
2. SBT installed ( Works on 0.13.7)
3. Eclipse with Scala-ide (Works on Eclipse Kepler and scala-ide 3.0.3)

<b> Installation </b>

1. Download the latest release from <a href="https://github.com/feroshjacob/TailorSwift/releases">here </a>
2. Install the plugin into your Eclipse IDE from the archive file  using the instructions  mentioned  <a href="https://developers.google.com/eclipse/docs/install-from-zip">here </a>

<b> How to use </b>

<i>Configuring the cluster and SBT installation  </i> 

  -  Selected Preferences -> Tailorswift
  -  Provide the SBT  binary location (sbt for UNIX/MAC and sbt.bat for Windows)
  -  Provide the connection string and password for the Cluster connection.

<i>Creating a WebScalding Project </i> 
  
  - Select "File" -> "New"  -> "Project" -> "Webscalding" -> "New WebScalding Project" -> "Next" 
  - Enter a name for the project
  - "Finish"

A SCALA project should be  created  and user can run it in a cluster (Currently the cluster details are hard coded, can be changed later ).


<i>Runing a WebScalding Project </i> 
   - Select "Run" -> "Run Configurations" -> "New Configuration" 
   - Select the project you want to run
   - Select the script file you want to execute
   - "Run"

An example setup video is given here
[![ScreenShot](https://github.com/feroshjacob/TailorSwift/blob/master/resources/youtube.png)](http://youtu.be/3cMv6viuwW0)


Have fun!
 

<b> Errors and fixes </b>

1.  <i>java.util.concurrent.ExecutionException: java.lang.OutOfMemoryError: Java heap space </i>
      Update -mem argument in the sbtopt file in the sbt installation directory. e.g., "-mem   23000"  


