package eu.heads.apama.dev;

import com.apama.EngineException;
import com.apama.engine.MonitorScript;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.ConsumerOperationsInterface;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.event.EventListenerAdapter;
import com.apama.event.parser.EventParser;
import com.apama.util.CompoundException;
import eu.heads.apama.JsonUtil;
import org.kevoree.ContainerRoot;
import org.kevoree.annotation.*;
import org.kevoree.api.ModelService;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateCallback;
import org.kevoree.api.handler.UpdateContext;
import org.kevoree.factory.DefaultKevoreeFactory;
import org.kevoree.factory.KevoreeFactory;
import org.kevoree.pmodeling.api.KMFContainer;
import org.kevoree.pmodeling.api.ModelCloner;
import org.kevoree.pmodeling.api.compare.ModelCompare;
import org.kevoree.pmodeling.api.json.JSONModelLoader;
import org.kevoree.pmodeling.api.trace.TraceSequence;

import java.util.List;

@ComponentType(version=2)
public class ApamaSubscriberWithModelUpdate implements ModelListener{
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
		service.registerModelListener(this);
		final JsonUtil utils = new JsonUtil();
		utils.initEventType(eventTypeDefinition);

		try {
			engineClient = EngineClientFactory.createEngineClient(host, port,
					processName);

			// Listen for events sent to the "samplechannel" channel
			ConsumerOperationsInterface myConsumer = engineClient.addConsumer(
					consummerName, channelName);
			
			for (String key : utils.getTypes().keySet()){
				parser = new EventParser(utils.getTypes().get(key));
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
		service.unregisterModelListener(this);
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

	ContainerRoot model;
	public boolean afterLocalUpdate(UpdateContext arg0) {
		model = arg0.getProposedModel();

		return true;
	}

	public boolean initUpdate(UpdateContext arg0) {
		return true;
	}

	boolean getApamaModel = false;
	boolean getApamaInstance = false;
	
	@Input
	public void in(Object i) {
	}

	
	@KevoreeInject
	ModelService service;
	public void modelUpdated() {
		
		
		//String channelDef = "{\"class\":\"org.kevoree.ContainerRoot@0.55733997444622221449682749209\",\"generated_KMF_ID\":\"0.55733997444622221449682749209\",\"nodes\":[],\"repositories\":[],\"hubs\":[],\"mBindings\":[],\"groups\":[],\"packages\":[{\"class\":\"org.kevoree.Package@eu\",\"name\":\"eu\",\"packages\":[{\"class\":\"org.kevoree.Package@heads\",\"name\":\"heads\",\"packages\":[],\"typeDefinitions\":[{\"class\":\"org.kevoree.ChannelType@name=ApamaBus,version=1.0.0\",\"upperFragments\":\"0\",\"abstract\":\"false\",\"upperBindings\":\"0\",\"lowerBindings\":\"0\",\"lowerFragments\":\"0\",\"name\":\"ApamaBus\",\"version\":\"1.0.0\",\"deployUnits\":[\"packages[eu]/packages[heads]/deployUnits[hashcode=,name=ApamaBus,version=1.0.0]\"],\"superTypes\":[],\"dictionaryType\":[{\"class\":\"org.kevoree.DictionaryType@0.163964800769463181449682749260\",\"generated_KMF_ID\":\"0.163964800769463181449682749260\",\"attributes\":[]}],\"metaData\":[]}],\"deployUnits\":[{\"class\":\"org.kevoree.DeployUnit@hashcode=,name=ApamaBus,version=1.0.0\",\"name\":\"ApamaBus\",\"hashcode\":\"\",\"url\":\"\",\"version\":\"1.0.0\",\"requiredLibs\":[],\"filters\":[{\"class\":\"org.kevoree.Value@platform\",\"name\":\"platform\",\"value\":\"java\"}]}]}],\"typeDefinitions\":[],\"deployUnits\":[]}]}";
		String channelDef = "{\"class\":\"org.kevoree.ContainerRoot@0.55733997444622221449682749209\",\"generated_KMF_ID\":\"0.55733997444622221449682749209\",\"nodes\":[],\"repositories\":[],\"hubs\":[],\"mBindings\":[],\"groups\":[],\"packages\":[{\"class\":\"org.kevoree.Package@eu\",\"name\":\"eu\",\"packages\":[{\"class\":\"org.kevoree.Package@heads\",\"name\":\"heads\",\"packages\":[],\"typeDefinitions\":[{\"class\":\"org.kevoree.ChannelType@name=ApamaBus,version=1.0.0\",\"upperFragments\":\"0\",\"abstract\":\"false\",\"upperBindings\":\"0\",\"lowerBindings\":\"0\",\"lowerFragments\":\"0\",\"name\":\"ApamaBus\",\"version\":\"1.0.0\",\"superTypes\":[],\"dictionaryType\":[{\"class\":\"org.kevoree.DictionaryType@0.163964800769463181449682749260\",\"generated_KMF_ID\":\"0.163964800769463181449682749260\",\"attributes\":[]}],\"metaData\":[]}]}],\"typeDefinitions\":[],\"deployUnits\":[]}]}";
		KevoreeFactory fact = new DefaultKevoreeFactory();
		if (!getApamaModel) {

			JSONModelLoader loader = new JSONModelLoader(fact);
			List<KMFContainer> list = loader.loadModelFromString(channelDef);

			ModelCloner cloner = fact.createModelCloner();
			ContainerRoot r1 = cloner.clone(model);
			
			ModelCompare merge = new ModelCompare(fact);
			TraceSequence s = merge.merge(r1, list.get(0));
			s.applyOn(r1);
		

			getApamaModel = true;
			service.update(r1, new UpdateCallback() {

				public void run(Boolean arg0) {

				}
			});
		}
		if (getApamaModel && !getApamaInstance) {
			getApamaInstance=true;
			String nodeName =context.getNodeName();
			String compName = context.getInstanceName();
			
			System.err.println("add "+channelName+" : eu.heads.ApamaBus\n"
					+ "bind "+nodeName+ "." + compName+".in "+channelName +"\n");
			service.submitScript("add "+channelName+" : eu.heads.ApamaBus\n"
					+ "bind "+nodeName+ "." + compName+".in "+channelName +"\n", new UpdateCallback() {
						public void run(Boolean arg0) {
							System.err.println(arg0);
						}
					});
		}

	}

	public void postRollback(UpdateContext arg0) {
		
	}

	public void preRollback(UpdateContext arg0) {
		
	}

	public boolean preUpdate(UpdateContext arg0) {
		return true;
	}


}
