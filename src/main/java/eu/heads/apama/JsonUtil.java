package eu.heads.apama;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import com.apama.event.Event;
import com.apama.event.parser.EventType;
import com.apama.event.parser.Field;
import com.apama.event.parser.FieldType;
import com.apama.event.parser.FieldTypes;

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
			for (String f : fields){
					t.addField(new Field(f, getType(obj.get(f).toString())));
//				}
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

	private FieldType getType(String t) {
		if (t.equals("boolean"))
			return FieldTypes.BOOLEAN;
		else if (t.equals("int"))
			return FieldTypes.INTEGER;
		else if (t.equals("float"))
			return FieldTypes.FLOAT;
		else if (t.equals("decimal"))
			return FieldTypes.DECIMAL;
		else if (t.equals("location"))
			return FieldTypes.LOCATION;
		else if (t.equals("string"))
			return FieldTypes.STRING;
		else if (t.equals("sequence<string>"))
			return FieldTypes.sequence(FieldTypes.STRING);
		else
			return FieldTypes.sequence(FieldTypes.STRING);

	}

}
