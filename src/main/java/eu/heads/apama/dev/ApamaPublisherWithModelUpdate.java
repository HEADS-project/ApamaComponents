package eu.heads.apama.dev;

import com.apama.EngineException;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.util.CompoundException;
import eu.heads.apama.JsonUtil;
import org.kevoree.Channel;
import org.kevoree.ContainerRoot;
import org.kevoree.annotation.*;
import org.kevoree.api.Context;
import org.kevoree.api.ModelService;
import org.kevoree.api.Port;
import org.kevoree.api.handler.ModelListener;
import org.kevoree.api.handler.UpdateContext;

@ComponentType(version=2)
public class ApamaPublisherWithModelUpdate implements ModelListener {

	@Param(defaultValue = "localhost")
	private String host;

	@Param(defaultValue = "[  {  \"EventTypeName\" : \"Tick\",  \"name\": \"string\",  \"price\": \"float\"}]")
	private String eventTypeDefinition;

	@Param(defaultValue = "15903")
	private int port;

	@Param(defaultValue = "samplechannel")
	private String channelName;

	@Param(defaultValue = "my-sample-process")
	private String processName;

	@KevoreeInject
	private Context context;

	@Output
	private Port out;

	@KevoreeInject
	private ModelService service;

	private final JsonUtil utils = new JsonUtil();
	private EngineClientInterface engineClient;
	private boolean getApamaInstance = false;
	private ContainerRoot model;

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

	public boolean afterLocalUpdate(UpdateContext arg0) {
		model = arg0.getProposedModel();
		return true;
	}

	public boolean initUpdate(UpdateContext arg0) {
		return true;
	}

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

	public void postRollback(UpdateContext arg0) {}

	public void preRollback(UpdateContext arg0) {}

	public boolean preUpdate(UpdateContext arg0) {
		return true;
	}
}
