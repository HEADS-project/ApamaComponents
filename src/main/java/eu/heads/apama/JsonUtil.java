package eu.heads.apama;

import java.util.HashMap;
import java.util.Iterator;
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

	void initEventType(String o) {

		JSONArray objs = new JSONArray(o);
		for (int i = 0; i < objs.length(); i++) {
			JSONObject obj = objs.getJSONObject(i);
			EventType t = new EventType(obj.get("EventTypeName").toString());
			for (int j = obj.length()-1; j >= 0; j--) {
				String f = obj.getNames(obj)[j];
				if (!"EventTypeName".equals(f)) {
					t.addField(new Field(f, getType(obj.get(f).toString())));
				}
			}
			types.put(obj.get("EventTypeName").toString(), t);
		}
	}

	Map<String, EventType> types = new HashMap<String, EventType>();

	Event toEvent(String o) {
		JSONObject obj = new JSONObject(o);
		EventType t = types.get(obj.get("EventTypeName").toString());
		Event e = new Event(t);
		/*
		 * for (String val :t.getFieldNames()) { System.err.println(
		 * " set field " + val + " " + obj.get(val)); e.setField(val,
		 * obj.get(val)); }
		 */
		e.setField("name", obj.get("name"));
		e.setField("price", obj.get("price"));
		return e;
	}

	JSONObject toJson(Event evt) {
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
		else
			return FieldTypes.STRING;
	}

}
