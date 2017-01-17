package eu.heads.apama;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.KevoreeInject;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;
import org.kevoree.api.Context;
import org.kevoree.api.Port;
import org.kevoree.log.Log;

import com.apama.engine.EngineStatus;
import com.apama.engine.MonitorScript;
import com.apama.engine.beans.EngineClientFactory;
import com.apama.engine.beans.interfaces.ConsumerOperationsInterface;
import com.apama.engine.beans.interfaces.EngineClientInterface;
import com.apama.event.Event;
import com.apama.event.EventListenerAdapter;
import com.apama.event.parser.EventParser;
import com.apama.event.parser.EventType;
import com.apama.event.parser.Field;
import com.apama.event.parser.FieldTypes;
import com.apama.util.CompoundException;

/**
 * The Kevoree component <code>ApamaReceiver</code> receives Apama Events and
 * sends these to the Kevoree port. The receiver listens to Apama channel
 * configured by the parameter {@link #channelName}.
 * 
 * The {@link #start()} method starts a separate thread, which runs the receiver.
 * 
 * The connection to the Apama correlator is configured with the parameters
 * <code>host</code> and <code>port</code>. The correlator listens at
 * <code>host:port</code>.
 *
 * Prerequisite: Apama Java client API.
 */
@ComponentType(version = 3, description = "Receives Apama events on the specified Apama channel and forwards these to a provided Kevoree port.")
public class ApamaReceiver {

	private static final String FIELD_MESSAGE = "message";
	private static final String EVENT_TYPE_NAME = "JsonEvent";
	static final Field<String> FIELD_TEXT = FieldTypes.STRING.newField(FIELD_MESSAGE);
	static final EventType JSON_EVENT = new EventType(EVENT_TYPE_NAME, FIELD_TEXT);
	static final EventParser EVENT_PARSER = new EventParser(JSON_EVENT);

	@Param(defaultValue = "localhost")
	String host = "localhost";

	@Param(defaultValue = "15903")
	int port = 15903;

	@Param(defaultValue = "apama-channel")
	String channelName = "apama-channel";

	@Param(defaultValue = "apama-receiver")
	String consumerName = "apama-receiver";

	@Param(defaultValue = "apama-receiver-process")
	String processName = "apama-receiver-process";

	EngineClientInterface engineClient;

	@KevoreeInject
	private Context context;

	@Output
	private Port out;

	private ConsumerOperationsInterface eventConsumer;

	private boolean stopped = false;

	@Start
	public void start() {
		stopped = false;
		Thread receivingThread = new Thread(new ReceiverRunnable());
		receivingThread.start();
	}

	@Stop
	public void stop() {
		eventConsumer.removeAllEventListeners();
		logInfo("Apama consumer " + consumerName + " removed.");
		try {
			engineClient.disconnect();
		} catch (CompoundException e) {
			logError("Error while disconnecting Apama engine client", e);
		}
		stopped = true;
	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

	/**
	 * Log a message at ERROR level. If the Kevoree context is
	 * <code>null</code>, message is written with <code>System.out</code>.
	 * Otherwise use the Kevoree logger from the Kevoree context.
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
	 * message is written with <code>System.out</code>. Otherwise use the
	 * Kevoree logger from the Kevoree context.
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

	public void setChannel(String channel) {
		this.channelName = channel;
	}

	/**
	 * ReceiverRunnable
	 *
	 */
	class ReceiverRunnable implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			try {
				engineClient = EngineClientFactory.createEngineClient(host, port, processName);
				logInfo("Engine client created: " + host + ":" + port + ", " + processName);
				MonitorScript script = new MonitorScript(
						"event " + EVENT_TYPE_NAME + "{ string " + FIELD_MESSAGE + "; }");
				engineClient.injectMonitorScript(script, EVENT_TYPE_NAME);
				eventConsumer = engineClient.addConsumer(consumerName, channelName);
				logInfo("Consumer '" + consumerName + "' added, channel '" + channelName + "'.");
				// Monitor the connectedness of named consumers
				eventConsumer.addPropertyChangeListener(new PropertyChangeListener() {
					public void propertyChange(PropertyChangeEvent event) {
						if (ConsumerOperationsInterface.PROPERTY_CONSUMER_CONNECTED.equals(event.getPropertyName())) {
							ConsumerOperationsInterface theConsumer = (ConsumerOperationsInterface) event.getSource();

							boolean connected = ((Boolean) event.getNewValue()).booleanValue();
							logInfo("Property changed for Apama consumer \"" + theConsumer.getName() + "\": now "
									+ (connected ? "connected" : "disconnected"));
						}
					}
				});
				// Set up listener for events.
				eventConsumer.addEventListener(new EventListenerAdapter() {
					@Override
					public void handleEvent(Event evt) {
						String eventTypeName = evt.getName();
						evt.setEventParser(EVENT_PARSER);
						if (EVENT_TYPE_NAME.equals(eventTypeName)) {
							String message = evt.getField(FIELD_MESSAGE, FieldTypes.STRING);
							if (message != null) {
								if (out == null) {
									logInfo("Received " + EVENT_TYPE_NAME + ": " + message);
								} else {
									out.send(message);
								}
							} else {
								if (out == null) {
									logInfo("Received " + evt.getText());
								} else {
									out.send(evt.getText());
								}
							}
						} else {
							if (out == null) {
								logInfo("Received " + evt.getText());
							} else {
								out.send(evt.getText());
							}
						}
						logInfo("Event listener received Apama event with message: '" + evt.toString() + "'");
					}
				});
				engineClient.connectNow();
			} catch (CompoundException e) {
				logError("Error during start of run method", e);
			}
			while (!stopped) {
				try {
					if (engineClient != null) {
						EngineStatus engineStatus = engineClient.getStatus();
						if (engineStatus != null) {
							logInfo(engineStatus.toString());
						}
					}
					Thread.sleep(100);
				} catch (InterruptedException e) {
					if (!"sleep interrupted".equals(e.getMessage())) {
						logError("Error while waiting for events", e);
					}
				}
			}
		}

	}
}
