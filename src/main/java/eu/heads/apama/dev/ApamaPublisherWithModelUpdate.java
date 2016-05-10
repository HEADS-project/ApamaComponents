package eu.heads.apama.dev;

import java.util.List;

import org.kevoree.Channel;
import org.kevoree.ContainerRoot;
import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;
import org.kevoree.api.ModelService;
import org.kevoree.api.Port;
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

import com.apama.EngineException;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.util.CompoundException;

import eu.heads.apama.JsonUtil;

@ComponentType
public class ApamaPublisherWithModelUpdate implements ModelListener {

	@Param(defaultValue = "localhost")
	String host;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	String eventTypeDefinition;

	@Param(defaultValue = "15903")
	int port;

	

	@Param(defaultValue = "samplechannel")
	String channelName;

	
	@Param(defaultValue = "my-sample-process")
	String processName;
	final JsonUtil utils = new JsonUtil();
	@Input
	public void in(Object i) {
			try {
				Event e = utils.toEvent((String) i);
//				System.err.println("Will send to Apama " + e);
				engineClient.sendEvents(e);
			} catch (EngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	}

	EngineClientInterface engineClient;

	@Start
	public void start() {
		utils.initEventType(eventTypeDefinition);
		try {
			engineClient = EngineClientFactory.createEngineClient(host, port,
					processName);
		} catch (CompoundException e) {
			e.printStackTrace();
		}
		service.registerModelListener(this);

	}

	@Stop
	public void stop() {

		engineClient.dispose();
		service.unregisterModelListener(this);

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

	boolean getApamaInstance = false;
	
	@KevoreeInject
	org.kevoree.api.Context context;
	
	@Output
	Port out;
	
	@KevoreeInject
	ModelService service;
	public void modelUpdated() {
		
		if (!getApamaInstance) {
			System.err.println("update");
			for (Channel c:  model.getHubs()){
				System.err.println(c.getName());
			}
			getApamaInstance=true;
			String nodeName =context.getNodeName();
			String compName = context.getInstanceName();
			System.err.println("add "+channelName+" : eu.heads.ApamaBus\n"
					+ "bind "+nodeName+ "." + compName+".out "+channelName);
			
/*			service.submitScript("add "+channelName+" : eu.heads.ApamaBus\n"
					+ "bind "+nodeName+ "." + compName+".out "+channelName, new UpdateCallback() {
						public void run(Boolean arg0) {
							//System.err.println(arg0);
						}
					});*/
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
