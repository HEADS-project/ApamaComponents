package eu.heads.apama;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Input;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;
import org.kevoree.api.Context;
import org.kevoree.log.Log;

import com.apama.EngineException;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.event.parser.EventParser;
import com.apama.event.parser.EventType;
import com.apama.event.parser.Field;
import com.apama.event.parser.FieldTypes;
import com.apama.util.CompoundException;

/**
 * The Kevoree component <code>ApamaSender</code> receives Kevoree messages in
 * the <code>in</code> method and sends these as events to the Apama correlator.
 * 
 * The events are send to the default input queue <code>com.apama.input</code>
 * of Apama.
 * 
 * If the message starts with '{', the message is wrapped into an
 * <code>JsonEvent</code> with <code>message</code> as the only field. Otherwise
 * it is assumed that the message can be parsed by the Apama event parser and
 * the message is send 'as is'.
 * 
 * The connection to the Apama correlator is configured with the parameters
 * <code>host</code> and <code>port</code>. The correlator listens at
 * <code>host:port</code>.
 *
 * Prerequisite: Apama Java client API.
 *
 */
@ComponentType(version = 3, description = "Sends Apama events to Apama input channel for messages received on a Kevoree port.")
public class ApamaSender {

	private static final String FIELD_MESSAGE = "message";
	private static final String EVENT_TYPE_NAME = "JsonEvent";
	static final Field<String> FIELD_TEXT = FieldTypes.STRING.newField(FIELD_MESSAGE);
	static final EventType JSON_EVENT = new EventType(EVENT_TYPE_NAME, FIELD_TEXT);
	static final EventParser EVENT_PARSER = new EventParser(JSON_EVENT);

	@Param(defaultValue = "localhost")
	private String host = "localhost";

	@Param(defaultValue = "15903")
	private int port = 15903;

	@Param(defaultValue = "apama-sender-process")
	private String processName = "apama-sender-process";

	private EngineClientInterface engineClient;

	@KevoreeInject
	private Context context;

	@Input
	public void in(String message) {
		try {
			Event event = null;
			if (message.startsWith("{")) {
				// assume JSON String, add as a message field to JsonEvent.
				event = new Event(JSON_EVENT);
				event.setField(FIELD_MESSAGE, FieldTypes.STRING, message);
			} else {
				event = new Event(message);
			}
			engineClient.sendEvents(event);
		} catch (EngineException e) {
			logError("Error while sending message to Apama.", e);
		}
	}

	@Start
	public void start() {
		try {
			engineClient = EngineClientFactory.createEngineClient(host, port, processName);
			logInfo("Apama engine client created: " + host + ":" + port + ", " + processName);
		} catch (CompoundException e) {
			logError("Error creating Apama engine client.", e);
		}
	}

	@Stop
	public void stop() {
		if (engineClient != null) {
			engineClient.dispose();
			logInfo("Apama engine client stopped: " + host + ":" + port + ", " + processName);
		}
	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

	/**
	 * Log a message at ERROR level. If the Kevoree context is
	 * <code>null</code>, message is written with <code>System.out</code>.
	 * 
	 * @param message
	 *            the message to log.
	 */
	private void logError(String message, Throwable ex) {
		if (this.context == null) {
			System.out.println(message);
			ex.printStackTrace(System.out);
		} else {
			Log.error("{}: " + message + System.lineSeparator() + "{}", null, this.context.getPath(), ex.toString());
		}
	}

	/**
	 * Log a message at INFO level. If the Kevoree context is <code>null</code>,
	 * message is written with <code>System.out</code>.
	 * 
	 * @param message
	 *            the message to log.
	 */
	private void logInfo(String message) {
		if (this.context == null) {
			System.out.println(message);
		} else {
			Log.info("{}: " + message, this.context.getPath());
		}
	}
}
