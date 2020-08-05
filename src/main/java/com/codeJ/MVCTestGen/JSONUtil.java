package com.codeJ.MVCTestGen;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;



@Component 
public class JSONUtil {
	@Autowired
	ObjectMapper mapper;

	public String classToJSONString(Class<?> type) throws JsonProcessingException {
		Class clazz=type;
		Map<String,String> fieldMap=new HashMap<>();
		List<Field> fields = new ArrayList<>();
		while (clazz != Object.class) {
			fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
			clazz=clazz.getSuperclass();
		}
		for ( Field field:fields) {
			fieldMap.put(field.getName(),field.getType().getSimpleName() );
		}
		
		return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(fieldMap);
//		return mapper.writeValueAsString(fieldMap);		
	}
	
	public JSONObject MapToJSON (Map<String, Object>   JSonMap ) {
		JSONObject jsonObject= new JSONObject();
		for (Map.Entry<String, Object> entry: JSonMap.entrySet() ) {
			String key = entry.getKey();
			Object value=entry.getValue();
			jsonObject.put(key, value);
		}
		return jsonObject;
	}
	
	public JSONObject StringToJSON (String   JSonString ) {
		JSONParser parser=new JSONParser();
		JSONObject object=null;
		ControllerGenLogger.printDebug("StringToJSON:" + JSonString);
//		StringBuffer strBuf=new StringBuffer(JSonString);\
		ControllerGenLogger.printDebug("StringToJSON:" + JSonString.replaceAll("\\n", ""));
		try {
			 object = (JSONObject) parser.parse(JSonString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			ControllerGenLogger.printInfo(e.getMessage());
		}
		return object;
	}
	

	public MultiValueMap<String,Object> JSonStringtoMap(String[] JSONStrings) {
		MultiValueMap<String, Object> valueMap=new LinkedMultiValueMap<>();;
		for ( String jsonString:JSONStrings) {
			JSONObject jsonObject=StringToJSON(jsonString);
			String key= (String) jsonObject.keySet().toArray()[0];
			String value=(String) jsonObject.get(key);
			valueMap.add(key, value);
		}
		return valueMap;
	}
	
	public Map<String,Object> JSonStringtoMap(String JSONString) {
		Map<String, Object> valueMap=null;
		try {
			valueMap = mapper.readValue(JSONString, Map.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valueMap;
	}
		
	public Map<String,Object> JSontoMap(JSONObject jsonObject) {
		Map<String, Object> valueMap=null;
		try {
			valueMap = mapper.readValue(jsonObject.toJSONString(), Map.class);
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return valueMap;
	}
	
	public <T> List<T> JSONToObjectList(Class<T> type, String   JSONString) {
		List< T > reslutList=null;
		mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
		try {
//			for ( Object value:valueList) {
					
		//				Object valueObject=mapper.readValue(values.toString(), classType);
//				Map<String, Object> valueMap;
//				valueMap = mapper.readValue("{\"name\":\"Bob\", \"age\":13}" , Map.class);
//				result.add( getObjectFromValues(key.toString(),valueMap));
//					ControllerGenLogger.printDebug("JSONToObject: result value-" + result.toString());
//			}
		JSONObject jsonObject=StringToJSON(JSONString);
		reslutList=mapper.readValue(jsonObject.toJSONString(), new TypeReference<List<T>>(){});
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
		e.printStackTrace();
		}
		return reslutList;
	}

	public Object getObjectFromValues(String ClassName, Map<String,Object> values) {
		Object result = null;
		Class<?> classType;
		ControllerGenLogger.printDebug("JSONToObject: value-" + values.toString());
		try {
			classType = Class.forName(ClassName);
			result=classType.newInstance();
			for ( String FieldName:values.keySet()) {
					Field field=classType.getDeclaredField(FieldName);
					Object value=field.getType().cast(values.get(FieldName));
					field.setAccessible(true);
					field.set(result, value);
			}
			
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException | IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException | SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}