package eu.heads.apama.test;

import org.junit.Test;

import eu.heads.apama.ApamaQueryInject;

public class ApamaQueryInjectTest {

	@Test
	public void testStart() {
		ApamaQueryInject queryInject = new ApamaQueryInject();
		queryInject.setCorrelator("localhost:15903");
		queryInject.setClean("true");
		queryInject.setFiles("/SoftwareAG912/Apama/monitors/TimeFormatEvents.mon;\r\n"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/eventdefinitions/RestAdapterEvents.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/eventdefinitions/SocialSensorItem.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/eventdefinitions/LocalEntityEvent.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/eventdefinitions/SocialSensorEvent.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/monitors/AggregateFunction.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/monitors/AlarmMonitor.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/monitors/NumberOneMonitorVersionTwo.mon;"
				+ "/Workspaces/HEADS ATC Use Case/NewsAssetCEP/monitors/SocialSensorItems.mon");
		queryInject.start();
	}

}

