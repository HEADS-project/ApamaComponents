# Run the Example1 - Central Execution of CEP Queries

Have Maven installed and Kevoree configured. 

Unzip this project kevoreeNewsAssetCep.zip to your working directory.

Install the ApamaComponents and this example project with Maven. This is:
- in the project folder of ApamaComponents with pom.xml run `mvn install`
- in the project folder of kevoreeNewsAssetCep with pom.xml run `mvn install`

This will become obsolete when the components are available in a Maven repository.
 
## Start the Correlator 
 
Now start a correlator at the port, you like to use. Recommended is 15903. 
 
You can start the correlator with Docker, e.g. with the following command: 
 
```Shell
docker run -it -p 15903:15903 -v /path/to/your/apama/license:/apama_work/license --name dockerApama apama correlator
```	 
Apama is available in the public Docker registry.
You can also start the correlator without Docker. See Apama documentation for this.

## Start the Example

Change the value of node0.deployer.files so that the paths match your local environment. The files are from the Apama installation and the Apama project NewsAssetCEP. To start the example, run
```Shell
mvn kev:run
```
The TestApamaTicker sends messages to the correlator until the Kevoree script is stopped. Every minute the client and the receiver should log a line which shows that the correlator sends an alarm. kevoreeNewsAssetCep/logs contains an example of such a log file.  

# Run the Example2 - Distributed Execution of CEP Queries

This example uses three correlators. The first correlator filters events for named entity 'Location B' and sends them to the third correlator. The second correlator filters events for named entity 'Location C' and sends them to the third correlator. The third correlator joins the events and generates an alarm which is received by other components on the channel 'headschannel'.

Have Maven installed and Kevoree configured. 

Unzip this project kevoreeDistributedSensingNews.zip to your working directory. This contains two projects: an Apama project and a Kevoree project.

Follow the steps described in readme.txt of DistributedKevoreeNewsAsset.
Main step: run 'myStart.bat' in the folder Runnable. This uses Ant to start three correlators and Maven to run the Kevoree script.

In main.kevs check the paths to the .mon files and update according to your local installation.