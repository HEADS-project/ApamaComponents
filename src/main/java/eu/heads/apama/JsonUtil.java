package eu.heads.apama;

import com.apama.event.Event;
import com.apama.event.parser.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class JsonUtil {

	public void initEventType(String o) {

		JSONArray objs = new JSONArray(o);
		for (int i = 0; i < objs.length(); i++) {
			JSONObject obj = objs.getJSONObject(i);
			EventType t = new EventType(obj.get("EventTypeName").toString());
			String[] fields = {"id",
					"reference","streamId","title","tags","uid","pageUrl","publicationTime","insertionTime","mediaIds",""
					+ "sentiment","keywords","entities","original","likes","shares","comments","numOfComments","isSearched","indexed","alethiometerUserScore",""
					+ "positiveVotes","negativeVotes","votes"};

			//			for (int j = obj.length() - 1; j >= 0; j--) {
<<<<<<< HEAD
//				}
			for (String f : fields){
					t.addField(new Field(f, getType(obj.get(f).toString())));
=======
			for (String f : fields) {
				String type = obj.get(f).toString();
				if (type.equals("boolean")) {
					t.addField(new Field<Boolean>(f, FieldTypes.BOOLEAN));
				} else if (type.equals("int")) {
					t.addField(new Field<Long>(f, FieldTypes.INTEGER));
				} else if (type.equals("float")) {
					t.addField(new Field<Double>(f, FieldTypes.FLOAT));
				} else if (type.equals("decimal")) {
					t.addField(new Field<DecimalFieldValue>(f, FieldTypes.DECIMAL));
				} else if (type.equals("location")) {
					t.addField(new Field<LocationType>(f, FieldTypes.LOCATION));
				} else if (type.equals("string")) {
					t.addField(new Field<String>(f, FieldTypes.STRING));
				} else if (type.equals("sequence<string>")) {
					t.addField(new Field<List<String>>(f, FieldTypes.sequence(FieldTypes.STRING)));
				} else {
					t.addField(new Field<List<String>>(f, FieldTypes.sequence(FieldTypes.STRING)));
				}
>>>>>>> refs/remotes/origin/master
			}
			types.put(obj.get("EventTypeName").toString(), t);
		}
	}

	Map<String, EventType> types = new HashMap<String, EventType>();

	public Map<String, EventType> getTypes() {
		return types;
	}

	public Event toEvent(String o) {
		JSONObject obj = new JSONObject(o);
		EventType t = types.get(obj.get("EventTypeName").toString());
		Event e = new Event(t);

		String[] fields = {"id",
				"reference","streamId","title","tags","uid","pageUrl","publicationTime","insertionTime","mediaIds",""
				+ "sentiment","keywords","entities","original","likes","shares","comments","numOfComments","isSearched","indexed","alethiometerUserScore",""
				+ "positiveVotes","negativeVotes","votes"};
		for (String val : fields) {
			//		System.err.println(" set field " + val + " " + obj.get(val));
			Object o1 = obj.get(val);
			if (o1 instanceof JSONArray){
				List<String> s = new ArrayList<String>();
				for (int i= 0; i<((JSONArray)o1).length();i++ ){
					s.add(((JSONArray)o1).getString(i));
				}
				o1=s;
			}
			e.setField(val, o1);
		}


		//e.setField("name", obj.get("name"));
		//e.setField("price", obj.get("price"));
		return e;
	}

	public JSONObject toJson(Event evt) {
		JSONObject obj = new JSONObject();
		EventType t = types.get(evt.getEventType().getName());
		obj.put("EventTypeName", evt.getEventType().getName());
		for (Entry<String, Object> field : evt.getFieldsMap().entrySet()) {

			if (t.getField(field.getKey()) != null && (!"EventTypeName".equals(field.getKey())))
				obj.put(field.getKey(), field.getValue());
		}
		return obj;
	}
}
