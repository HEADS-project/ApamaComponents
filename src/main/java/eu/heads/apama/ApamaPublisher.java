package eu.heads.apama;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;

import com.apama.EngineException;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.util.CompoundException;

@ComponentType(version=2)
public class ApamaPublisher {

	@Param(defaultValue = "localhost")
	String host;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	String eventTypeDefinition;

	@Param(defaultValue = "15903")
	int port;

	

	
	
	@Param(defaultValue = "my-sample-process")
	String processName;
	final JsonUtil utils = new JsonUtil();
	@Input
	public void in(Object i) {
			try {
				Event e = utils.toEvent((String) i);
				//System.err.println("Will send to Apama " + e);
				engineClient.sendEvents(e);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	EngineClientInterface engineClient;

	@Start
	public void start() {
		System.err.println(eventTypeDefinition);
		utils.initEventType(eventTypeDefinition);
		try {
			engineClient = EngineClientFactory.createEngineClient(host, port,
					processName);
		} catch (CompoundException e) {
			e.printStackTrace();
		}

	}

	@Stop
	public void stop() {

		engineClient.dispose();

	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

}
