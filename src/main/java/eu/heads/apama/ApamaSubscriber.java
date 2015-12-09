package eu.heads.apama;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;

import com.apama.EngineException;
import com.apama.engine.MonitorScript;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.ConsumerOperationsInterface;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.event.EventListenerAdapter;
import com.apama.event.parser.EventParser;
import com.apama.util.CompoundException;

@ComponentType
public class ApamaSubscriber {
	@KevoreeInject
	org.kevoree.api.Context context;

	@Output
	org.kevoree.api.Port out;

	@Param(defaultValue = "localhost")
	String host;

	@Param(defaultValue = "event Tick { string name; float price; } monitor simplePrint { Tick t; action onload { on all Tick(*, >10.0): t { send Tick(t.name, t.price) to \"samplechannel\";}}}")
	String query;

	@Param(defaultValue = "my-sample-process")
	String processName;

	@Param(defaultValue = "samplechannel")
	String channelName;

	@Param(defaultValue = "myconsummer")
	String consummerName;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	String eventTypeDefinition;

	@Param(defaultValue = "15903")
	int port;

	EngineClientInterface engineClient;

	
	 EventParser parser;
	
	@Start
	public void start() {
		final JsonUtil utils = new JsonUtil();
		utils.initEventType(eventTypeDefinition);

		try {
			engineClient = EngineClientFactory.createEngineClient(host, port,
					processName);

			// Listen for events sent to the "samplechannel" channel
			ConsumerOperationsInterface myConsumer = engineClient.addConsumer(
					consummerName, channelName);
			
			for (String key : utils.types.keySet()){
				parser = new EventParser(utils.types.get(key));
			}

			myConsumer.addEventListener(new EventListenerAdapter() {
				@Override
				public void handleEvent(Event evt) {
					evt = parser.parse(evt.getText());
					System.err.println("Will receive notification from Apama " + evt.getText());
					if (out!= null && out.getConnectedBindingsSize()>0)
						out.send(utils.toJson(evt).toString(), null);
				}
			});

			// Inject some MonitorScript
			MonitorScript epl = new MonitorScript(query);
			engineClient.injectMonitorScript(epl);
		} catch (CompoundException e) {
			e.printStackTrace();
			this.stop();
		} 
	}

	@Stop
	public void stop() {
		try {
			engineClient.deleteAll();
		} catch (EngineException e) {
			e.printStackTrace();
		} finally {
			engineClient.dispose();
		}
	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

}
