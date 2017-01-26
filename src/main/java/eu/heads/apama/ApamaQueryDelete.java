package eu.heads.apama;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;

import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.util.CompoundException;

@ComponentType(version=2, description="Delete Apama EPL Code from running Correlator.")
public class ApamaQueryDelete {
//	@KevoreeInject
//	org.kevoree.api.Context context;

	@Output
	org.kevoree.api.Port out;

	@Param(defaultValue = "localhost")
	String host;

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
	String query;

	@Param(defaultValue = "my-sample-process")
	String processName;

//	@Param(defaultValue = "samplechannel")
//	String channelName;

	@Param(defaultValue = "myconsummer")
	String consummerName;

//	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
//	String eventTypeDefinition;

	@Param(defaultValue = "15903")
	int port;

	EngineClientInterface engineClient;

//	EventParser parser;

	@Start
	public void start() {
//		final JsonUtil utils = new JsonUtil();
//		utils.initEventType(eventTypeDefinition);

		try {
			engineClient = EngineClientFactory.createEngineClient(host, port, processName);

			// Delete the MonitorScript
			engineClient.deleteName(query, false);
		} catch (CompoundException e) {
			e.printStackTrace();
			this.stop();
		}
	}

	@Stop
	public void stop() {
		// do NOT deleteAll().
		// This may delete scripts from other applications running in the same correlator.
		engineClient.dispose();
	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

}
