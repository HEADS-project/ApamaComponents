
#Run the example

## Start Apama Without docker

On Linux:
```bash
LD_LIBRARY_PATH=lib ./bin/correlator
```

and then start the Kevoree components
```bash
mvn kev:run
```

On Windows:
```cmd
\SoftwareAG\Apama\bin\apama_env.bat

correlator
```

and then start the Kevoree components in the folder of the pom.xml:
```cmd
mvn kev:run
```
#Run Apama in docker


On Linux:

```bash
sudo service docker stop
sudo docker daemon --insecure-registry www.barais.fr:5000

docker pull www.barais.fr:5000/apama
docker run -i -t -p 15903:15903 -e LD_LIBRARY_PATH=/opt/SoftwareAG/Apama_5.3/lib www.barais.fr:5000/apama /opt/SoftwareAG/Apama_5.3/bin/correlator

docker stop apama
docker start apama
```



