package eu.heads.apama;

<<<<<<< HEAD
=======
import org.kevoree.annotation.*;
import org.kevoree.api.Port;

import java.util.Random;
>>>>>>> refs/remotes/origin/master
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@ComponentType(version=2)
public class TestApamaTicker {

	@Output
	private Port out;

	@Param(defaultValue = "2000")
	private Long delay;

	@Param(defaultValue = "1000")
	private Long period;

	private ScheduledExecutorService ser;

	@Start
	public void start() {
		ser = Executors.newSingleThreadScheduledExecutor();
		ser.scheduleAtFixedRate(new Runnable() {

			public void run() {
//				Random r = new Random();
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
