package eu.heads.apama;

import org.json.JSONObject;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;

import com.apama.EngineException;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.ConsumerOperationsInterface;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.event.EventListenerAdapter;
import com.apama.util.CompoundException;

@ComponentType(version=1)
public class ApamaReceiver {

	@Param(defaultValue = "localhost")
	String host;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	String eventTypeDefinition;

	@Param(defaultValue = "15903")
	int port;

	@Param(defaultValue = "samplechannel")
	String channelName;

	@Param(defaultValue = "myconsummer")
	String consummerName;

	@Param(defaultValue = "my-sample-process")
	String processName;
	
	final JsonUtil utils = new JsonUtil();
	
	@Input
	public void in(Object i) {
			try {
				// Listen for events on the 'channelName' channel
				ConsumerOperationsInterface eventConsumer = engineClient.addConsumer(consummerName, channelName);
				eventConsumer.addEventListener(new EventListenerAdapter() {
					@Override
					public void handleEvent(Event evt) {
						JSONObject jsonObject = utils.toJson(evt);
						System.out.println("Event listener received event with message: '" + jsonObject.toString() + "'");
					}
				});
			} catch (EngineException e) {
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
