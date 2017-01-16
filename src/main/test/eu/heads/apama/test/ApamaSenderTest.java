package eu.heads.apama.test;

import org.junit.Test;

import eu.heads.apama.ApamaSender;

/**
 * ApamaSenderTest
 *
 */
public class ApamaSenderTest {

	private String message = "{" + "\"_id\" : ObjectId(\"55a67c84d45034b1f63de3e1\"),"
			+ "\"id\" : \"GooglePlus#104935925071337186391.6168806304423177409\","
			+ "\"url\" : \"https://lh3.googleusercontent.com/-9E4DAUXjXEM/VaVBn139xdI/AAAAAAAAsxk/SBr0Xwj_IqQ/w379-h379/My%2Beyes%2Bare%2Bfor%2Bmy%2BBrother%2Blarry.jpg\","
			+ "\"thumbnail\" : \"https://lh3.googleusercontent.com/-9E4DAUXjXEM/VaVBn139xdI/AAAAAAAAsxk/SBr0Xwj_IqQ/w379-h379/My%2Beyes%2Bare%2Bfor%2Bmy%2BBrother%2Blarry.jpg\","
			+ "\"pageUrl\" : \"https://plus.google.com/115030062187031548879/posts/2vuktR8hEHk\","
			+ "\"streamId\" : \"GooglePlus\"," + "\"reference\" : \"GooglePlus#z12bwjdjypfywhl0f22mcxwgxnmzujyjt04\","
			+ "\"uid\" : \"GooglePlus#115030062187031548879\","
			+ "\"title\" : \"I LOVE how close and Loving   #larry  and #Laurent   are....we may not Know  all The true story of all...\","
			+ "\"description\" : \"Beautiful EDITs Of LESTWINS!!!!!! wow\"," + "\"tags\" : [ " + "\"larry\","
			+ "\"Laurent\"," + "\"LESTWINS\"," + "\"Brotherlylove\"," + "\"togetheralways\"," + "\"unitedinlove\""
			+ "\"]," + "\"sentiment\" : \"positive\"," + "\"keywords\" : []," + "\"entities\" : [ \"Entity 1\" ],"
			+ "\"type\" : \"image\"," + "\"publicationTime\" : NumberLong(1436945211665)," + "\"likes\" : 0,"
			+ "\"shares\" : 0," + "\"comments\" : 0," + "\"views\" : 0," + "\"ratings\" : 0.0000000000000000,"
			+ "\"sentiment\" : 0," + "\"width\" : 379," + "\"height\" : 379," + "\"vIndexed\" : false,"
			+ "\"indexed\" : false," + "\"status\" : \"new\"" + "}";

	private String message1 = "com.softwareag.research.rest.RestJsonResponse(20,200,{\"Content-type\":\"application/json\","
			+ "\"Date\":\"Tue, 21 Jun 2016 07:28:44 GMT\","
			+ "\"Transfer-encoding\":\"chunked\"},com.softwareag.research.rest.JSONObject({\"sentiment\":\"positive\","
			+ "\"streamId\":\"GooglePlus\",\"insertionTime\":\"0\",\"keywords\":\"[]\","
			+ "\"description\":\"All you need is love but a little chocolate now and then doesn't hurt - CM. Schulz#MarliesDekkers #womenswear #womensfashion #womensclothing #dutch Ã‚Â \","
			+ "\"title\":\"All you need is love but a little chocolate now and then doesn't hurt - CM. Schulz#MarliesDekkers #womenswear #womensfashion #womensclothing #dutch Ã‚Â \","
			+ "\"reference\":\"GooglePlus#z13hsl2ijruehxvu4222tfxyzlifvdmms\",\"shares\":\"0\",\"uid\":\"GooglePlus#102815765117969522660\","
			+ "\"negativeVotes\":\"0\",\"id\":\"GooglePlus#106340594352135019688.6163572118998557218\",\"likes\":\"0\",\"original\":\"true\","
			+ "\"comments\":\"[]\",\"indexed\":\"false\",\"alethiometerUserScore\":\"0\",\"positiveVotes\":\"0\",\"numOfComments\":\"0\","
			+ "\"tags\":\"[MarliesDekkers,womenswear,womensfashion,womensclothing,dutch]\","
			+ "\"entities\":\"[com.softwareag.research.rest.JSONObject({\\\"name\\\":\\\"Location B\\\",\\\"cont\\\":\\\"0.0\\\",\\\"type\\\":\\\"LOCATION\\\"})]\","
			+ "\"publicationTime\":\"1466494124953\",\"pageUrl\":\"https://plus.google.com/102815765117969522660/posts/CX9vBM7T9i2\","
			+ "\"mediaIds\":\"[]\",\"votes\":\"[]\",\"isSearched\":\"false\"}))";

	/**
	 * Test method for {@link eu.heads.apama.ApamaSender#start()}.
	 */
	@Test
	public final void testStart() {
		ApamaSender sender = new ApamaSender();
		sender.start();
		for (int i = 0; i < 10; i++) {
			sender.in(message);
		}
		for (int i = 0; i < 10; i++) {
			sender.in(message1);
		}
		sender.stop();
	}

}
