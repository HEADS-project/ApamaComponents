package eu.heads.apama;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.kevoree.annotation.ComponentType;
import org.kevoree.annotation.Output;
import org.kevoree.annotation.Param;
import org.kevoree.annotation.Start;
import org.kevoree.annotation.Stop;
import org.kevoree.annotation.Update;

@ComponentType
public class TestApamaTicker {

	@Output
	org.kevoree.api.Port out;

	private ScheduledExecutorService ser;

	@Param(defaultValue = "2000")
	Long delay;

	@Param(defaultValue = "1000")
	Long period;

	@Start
	public void start() {
		ser = Executors.newSingleThreadScheduledExecutor();
		ser.scheduleAtFixedRate(new Runnable() {

			public void run() {
				Random r = new Random();
				// out.send("{ \"EventTypeName\": \"Tick\", \"name\": \"toto\",
				// \"price\": "+r.nextFloat()*30+"}", null);
				// out.send("{ \"EventTypeName\": \"Tick\", \"name\": \"toto\",
				// \"price\": "+r.nextFloat()*30+"}", null);

				String message = "{ \"EventTypeName\": \"Item\", \"id\":\"Twitter#621342480370388992\",\"reference\":\"Twitter#621342481184129024\",\"streamId\":\"Twitter\",\"title\":\"FARAGE IN DC: Blasts #Obama, Scots Nats, Cowardly #Tsipras; Says @realDonaldTrump Resonated - http://t.co/uYiqrgacPs http://t.co/w3d4JrOjuv\",\"tags\":[\"Obama\",\"Tsipras\"],\"uid\":\"Twitter#2339238427\",\"pageUrl\":\"http://twitter.com/BreitbartLondon/status/621342481184129024/photo/1\",\"publicationTime\":1455651753767,\"insertionTime\":0,\"mediaIds\":[],\"sentiment\":\"positive\",\"keywords\":[],\"entities\":[],\"original\":true,\"likes\":0,\"shares\":11,\"comments\":[],\"numOfComments\":0,\"isSearched\":false,\"indexed\":false,\"alethiometerUserScore\":0,\"positiveVotes\":0,\"negativeVotes\":0,\"votes\":[]}";
				out.send(message);
			}

		}, delay, period, TimeUnit.MILLISECONDS);

	}

	@Stop
	public void stop() {

		ser.shutdown();
	}

	@Update
	public void update() {
		this.stop();
		this.start();
	}

}
