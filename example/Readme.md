# Run the Example

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