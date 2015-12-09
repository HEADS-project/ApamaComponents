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
					out.send("{  \"EventTypeName\": \"Tick\",  \"name\": \"toto\",  \"price\": "+r.nextFloat()*30+"}", null);
				}

			},delay,period,TimeUnit.MILLISECONDS
		);
		

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
