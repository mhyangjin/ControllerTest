package com.codeJ.ControllerTest.Generator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;
import com.codeJ.ControllerTest.comm.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class testWebGenerator {
	private static Logger logger = LoggerFactory.getLogger(testWebGenerator.class);
	@Autowired
	WebApplicationContext context;
	@Autowired
	JSONUtil jsonUtil;
	boolean fromConfig;
	
	private  String jspPath="src//main//webapp//WEB-INF//jsp";
	private final String jspHeader= "<!DOCTYPE html>\r\n" + 
			"<html>\r\n" + 
			"<head>\r\n" + 
			"    <meta http-equiv=\"Cache-Control\" content=\"no-cache, no-store, must-revalidate\" />\r\n" + 
			"    <meta http-equiv=\"Pragma\" content=\"no-cache\" />\r\n" + 
			"    <meta http-equiv=\"Expires\" content=\"0\" />" + 
			"<title>This is Controller Test made by CodeJ project</title>\r\n" + 
			"	<link href=\"/webjars/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\"/>\r\n" +			  
			"    <script src=\"/webjars/sockjs-client/1.1.2/sockjs.min.js\"></script>\r\n" + 
			"    <script src=\"/webjars/stomp-websocket/2.3.3/stomp.min.js\"></script>\r\n" + 
			"    <script src=\"/webjars/jquery/3.3.1/jquery.min.js\"></script>\r\n" + 
			"    <script src=\"/webSocketClient.js\"></script>\r\n" + 
			"</head>\r\n" ; 
	public testWebGenerator setPath(String jspLocation, boolean fromConfig) {
		this.jspPath=jspLocation;
		this.fromConfig = fromConfig;
		return this;
	}
	public void makeTestPage() {
		
		try {
			copyJsFile();
		} catch ( IOException e) {
			e.printStackTrace();
		}
		File writeFile=null;
		if (fromConfig)
			writeFile = new File (jspPath + "main.jsp");
		else
			writeFile = new File (jspPath + "templates//main.html");
			
		BufferedWriter bufferWriter =null;
		try {
			bufferWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(writeFile),"UTF8"));
			if (fromConfig)
				bufferWriter.write("<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"\r\n" + 
						"    pageEncoding=\"UTF-8\"%>");
			bufferWriter.write(jspHeader);
			bufferWriter.write("<body>\r\n");
			
			Map<String, Object >controllers=context.getBeansWithAnnotation(Controller.class);
			logger.info("-----------Controlleranme size:{}", controllers.size());

			int i=1;

			for(Object object:controllers.values()) {
				logger.info("Controller List:{}",object.getClass().getSimpleName());
				
				Class controllClass = object.getClass();	
				RequestMapping requstmappingAnno=(RequestMapping)controllClass.getAnnotation(RequestMapping.class);
				String requesstMappingStr="";
				if ( requstmappingAnno!= null)
					requesstMappingStr=requstmappingAnno.value()[0] + "/";
				
				List<Method> methods= gatherAnotatedMethod(controllClass);
				if( methods.size() == 0) continue;
				
				bufferWriter.write("<H1>" + i++ + ".   "+  requesstMappingStr+ object.getClass().getSimpleName() + "</H1>\r\n");
				StringBuffer formString= makeRequestFrom(requesstMappingStr, methods);
				bufferWriter.write(formString.toString());

			}
			bufferWriter.write("</body>\r\n");
			bufferWriter.write("</html>\r\n");
			bufferWriter.flush();
			bufferWriter.close();
		} catch ( IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void copyJsFile() throws FileNotFoundException, IOException {
		InputStream srcFile=this.getClass().getResourceAsStream("..//Ui//webSocketClient.js");
		File destFile= null;
//		if (fromConfig)
//			destFile = new File ( jspPath + "webSocketClient.js");
//		else
			destFile = new File (  "src//main//resources//static//webSocketClient.js");
			
		
		FileReader inFile=null;
		BufferedReader bufferedIn =null;
		FileWriter outFile=null;
		BufferedWriter bufferedOut=null;
		
		try {
			bufferedIn = new BufferedReader(new InputStreamReader(srcFile));
			outFile = new FileWriter(destFile);
			bufferedOut = new BufferedWriter(outFile);
			String line="";
			while ((line = bufferedIn.readLine()) != null) {	
				bufferedOut.write(line);
				bufferedOut.newLine();
			}
	
		} finally
		{
			if ( bufferedIn != null ) bufferedIn.close();
			if ( inFile != null ) inFile.close();
			if ( bufferedOut != null ) bufferedOut.close();
			if ( outFile != null ) outFile.close();
		}
	}
	
	private List<Method> gatherAnotatedMethod(Class coltrollerClass) {
		List<Method> controllerMethods=new ArrayList<>();
		Method[] methods=coltrollerClass.getDeclaredMethods();
		for(Method method:methods) {
			ControllerTestGenerator testGen=method.getAnnotation(ControllerTestGenerator.class);
		if ( testGen != null)
			controllerMethods.add(method);
		}
		return controllerMethods;
	}
	
	private List<Parameter> getAnnoatedParams(Method method) {
		List<Parameter> requestParameters= new ArrayList<>();
		Parameter[] parameters=method.getParameters();
		for(Parameter parameter:parameters) {
			RequestParam requestparam=parameter.getAnnotation(RequestParam.class);
			if ( requestparam != null) {
				requestParameters.add(parameter);
			}
		}
		return requestParameters;
	}
	
	private List<Parameter> getArguments(Method method) {
		List<Parameter> requestParameters= new ArrayList<>();
		Parameter[] parameters=method.getParameters();
		for(Parameter parameter:parameters) {
			RequestParam requestparam=parameter.getAnnotation(RequestParam.class); //Annotation이 있는 것은 제외
			if ( requestparam == null) {
				requestParameters.add(parameter);
			}
		}
		return requestParameters;
	}
	
	private List<String> getMethodAnnotations(Method method) {
		List<String> methodAnntations=new ArrayList<>();
		
		Annotation[] annotations=method.getAnnotations();
		for (Annotation annotation:annotations) {
			if( GetMapping.class.equals(annotation.annotationType()))
			{
				GetMapping getmapp=GetMapping.class.cast(annotation);
				methodAnntations.add("GET");
				methodAnntations.addAll(Arrays.asList( getmapp.path()));
			}
			if( PostMapping.class.equals(annotation.annotationType()))
			{
				PostMapping postMapping=PostMapping.class.cast(annotation);
				methodAnntations.add("POST");
				methodAnntations.addAll(Arrays.asList( postMapping.path()));
			}
			if( PutMapping.class.equals(annotation.annotationType()))
			{
				PutMapping putMapping=PutMapping.class.cast(annotation);
				methodAnntations.add("PUT");
				methodAnntations.addAll(Arrays.asList( putMapping.path()));
			}
			if( DeleteMapping.class.equals(annotation.annotationType()))
			{
				DeleteMapping depeteMapping=DeleteMapping.class.cast(annotation);
				methodAnntations.add("DELETE");
				methodAnntations.addAll(Arrays.asList( depeteMapping.path()));
			}
			if( RequestMapping.class.equals(annotation.annotationType()))
			{
				RequestMapping requestMapping=RequestMapping.class.cast(annotation);
				RequestMethod[] methods=requestMapping.method();
				if (methods.length ==1 )
					methodAnntations.add(methods[0].name().toString());
				else
					methodAnntations.add("ANY");
				methodAnntations.addAll(Arrays.asList( requestMapping.path()));
			}
		}
		
		return methodAnntations;
	}
	
	private StringBuffer makeRequestFrom(String requesstMappingStr, List<Method> methods) {
		StringBuffer writeString= new StringBuffer();
		int i=1;
		for(Method method:methods) {
			List<String> methodAnns=getMethodAnnotations(method);
			
			//test를 위해 하나만 display 한다.
//			if ( !"reaAlldData".equals(testGen.MethodName())) continue;
			writeString.append("<dl><dd>" + i++);
			writeString.append(".  ");
			writeString.append( method.getName()  );
			writeString.append("</dd></dl>\r\n");
			writeString.append("<dl><dd><fieldset style = \"width:90%\">\r\n");
			writeString.append("<div id=\"" + method.getName() +"_"+i + "\">\r\n");
			if ( methodAnns.get(0).equals("ANY")) //method가 ANY이면 세팅 안함
				writeString.append("<form accept-charset=\"UTF-8\">\r\n"); 
			else
				writeString.append("<form accept-charset=\"UTF-8\" " + "name=\"" + methodAnns.get(0) +"\" " + "method=\"" + methodAnns.get(0) +"\">\r\n");
			List<Parameter> requestParams=getAnnoatedParams(method);
			if ( requestParams.size() > 0 )  { //RequestParams가 있는 경우 input From 작성 
				StringBuffer inputString= makeInputParams(requestParams,"INPUT");
				writeString.append(inputString);
			}
			else	//Annotation이 붙지 않은 argument일 경우
			{
				requestParams.clear();
				requestParams=getArguments(method);
				StringBuffer inputString= makeInputParams(requestParams,"JSON");
				writeString.append(inputString);
				
			}
			
			writeString.append("<input type=\"button\" id=\""+ requesstMappingStr  + method.getName() +"\"" + " onclick=sendMVCTest(this) value=\"전송\"/>  \r\n" );
			writeString.append("<input type=\"reset\" value=\"취소\"/><br>\r\n");
			StringBuffer inputString= makeResultArea(requesstMappingStr + method.getName());
			writeString.append(inputString);
			writeString.append("</form>\r\n</div>\r\n</fieldset></dd></dl>\r\n");
			writeString.append("\r\n");
		}
		return writeString;
	}

	private StringBuffer makeInputParams( List<Parameter> requestParameters,String InputType) {
		StringBuffer writeString= new StringBuffer();
		if ( InputType.equals("INPUT")) {
			for ( Parameter parameter:requestParameters) {
		
				RequestParam requestAnnotation = parameter.getAnnotation(RequestParam.class);
				writeString.append("<p>"+ requestAnnotation.value() + " : ");
				writeString.append("<input id=\"" + requestAnnotation.value()+ "\" type=\"text\" name=\"");
				writeString.append(requestAnnotation.value() + "\" size=\"50\" /></p>\r\n");
			}
		}	
		if  ( InputType.equals("JSON")) {
			for ( Parameter parameter:requestParameters) {
				String JsonString="";
				try {
					JsonString = jsonUtil.classToJSONString(parameter.getType());
				} catch (JsonProcessingException e) {}
				//writeString.append("<% var typeString=JSON.stringify(" + parameter.getType().getSimpleName() + "); %>\r\n");
				writeString.append("<p><pre>" + parameter.getType().getSimpleName() + " : " + JsonString +"</pre></p>\r\n");
				writeString.append("<textarea id=\"" + parameter.getType().getSimpleName()+ "\"  rows=\"20\" cols=\"100\" name=\"");
				writeString.append(parameter.getType().getName() + "\">"+ JsonString + "</textarea><br>\r\n");
			}
		
		}
		return writeString;
	}
	private StringBuffer makeResultArea(String MethodName ) {
		StringBuffer writeString= new StringBuffer();
		writeString.append("Result\r\n" + 
		"                <text id=\""+ MethodName + "_result\">\r\n" + 
		"                </text>\r\n" + 
		"\r\n");
		
		return writeString;
	}
}