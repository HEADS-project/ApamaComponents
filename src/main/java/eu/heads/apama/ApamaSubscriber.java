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
import org.kevoree.api.Context;
import org.kevoree.api.Port;

@ComponentType(version=2)
public class ApamaSubscriber {
	@KevoreeInject
	private Context context;

	@Output
	private Port out;

	@Param(defaultValue = "172.17.0.2")
	private String host;

	// @Param(defaultValue = "event Tick { string name; float price; } monitor
	// simplePrint { Tick t; action onload { on all Tick(*, >10.0): t { send
	// Tick(t.name, t.price) to \"samplechannel\";}}}")
	@Param(defaultValue = "event Item { string id; string reference; string streamId; string title; sequence<string> tags;  "
			+ "string uid; string pageUrl; integer publicationTime; integer insertionTime; sequence<string> mediaIds; "
			+ "string sentiment;sequence<string> keywords;  sequence<string> entities; boolean original; integer likes; "
			+ "integer shares; sequence<string> comments; integer numOfComments; boolean isSearched; boolean indexed;"
			+ " integer alethiometerUserScore; integer positiveVotes; integer negativeVotes; sequence<string> votes; } "
			+ "monitor simplePrint { Item t; action onload { on all Item(*,*,*,*,*,*,*,*,*,*,*,*,*,*,*,>5,*,*,*,*,*,*,*,*):"
			+ "t { send Item(t.id,t.reference,t.streamId,t.title,t.tags,t.uid,t.pageUrl,t.publicationTime,t.insertionTime,t.mediaIds,"
			+ "t.sentiment,t.keywords,t.entities,t.original,t.likes,t.shares,t.comments,t.numOfComments,t.isSearched,t.indexed,t.alethiometerUserScore,"
			+ "t.positiveVotes,t.negativeVotes,t.votes) to \"samplechannel\";}}}")
	private String query;

	@Param(defaultValue = "my-sample-process")
	private String processName;

	@Param(defaultValue = "samplechannel")
	private String channelName;

	@Param(defaultValue = "myconsummer")
	private String consummerName;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	private String eventTypeDefinition;

	@Param(defaultValue = "15903")
	private int port;

	private EngineClientInterface engineClient;
	private EventParser parser;

	@Start
	public void start() {
		final JsonUtil utils = new JsonUtil();
		utils.initEventType(eventTypeDefinition);

		try {
			engineClient = EngineClientFactory.createEngineClient(host, port, processName);

			// Listen for events sent to the "samplechannel" channel
			ConsumerOperationsInterface myConsumer = engineClient.addConsumer(consummerName, channelName);

			for (String key : utils.types.keySet()) {
				parser = new EventParser(utils.types.get(key));
			}

			myConsumer.addEventListener(new EventListenerAdapter() {
				@Override
				public void handleEvent(Event evt) {
					evt = parser.parse(evt.getText());
					System.err.println("Will receive notification from Apama " + evt.getText());
					if (out != null && out.getConnectedBindingsSize() > 0)
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
