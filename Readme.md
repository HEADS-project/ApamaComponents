
#Run the example
Without docker
## start APAMA

```bash
LD_LIBRARY_PATH=lib ./bin/correlator
```

and then start the kevoree components
```bash
mvn kev:run
```
#Run APAM in docker


```bash
sudo service docker stop
sudo docker daemon --insecure-registry www.barais.fr:5000

docker pull www.barais.fr:5000/apama
docker run -i -t -p 15903:15903 -e LD_LIBRARY_PATH=/opt/SoftwareAG/Apama_5.3/lib www.barais.fr:5000/apama /opt/SoftwareAG/Apama_5.3/bin/correlator

docker stop apama
docker start apama
```



