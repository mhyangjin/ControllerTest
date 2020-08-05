package com.codeJ.MVCTestGen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;

@Component
public class testWebGenerator {
	@Autowired
	WebApplicationContext context;
	@Autowired
	JSONUtil jsonUtil;
	
	private  String jspPath="src//main//webapp//WEB-INF//jsp";
	private final String jspHeader= "<%@ page language=\"java\" contentType=\"text/html; charset=UTF-8\"\r\n" + 
			"    pageEncoding=\"UTF-8\"%>\r\n" + 
			"<!DOCTYPE html>\r\n" + 
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
			"</head>\r\n" + 
			"<% String path=request.getContextPath();\r\n" + 
			"%>"; 
	public testWebGenerator setPath(String jspLocation) {
		this.jspPath=jspLocation;
		return this;
	}
	public void makeTestPage() {
		File writeFile = new File ("." + "//main.jsp");
		BufferedWriter bufferWriter =null;
		try {
			bufferWriter = new BufferedWriter( new OutputStreamWriter( new FileOutputStream(writeFile),"UTF8"));
			bufferWriter.write(jspHeader);
			bufferWriter.write("<body>\r\n");
			
			String[] names=context.getBeanNamesForAnnotation(Controller.class);
			ControllerGenLogger.printInfo("-----------Controlleranme size:" + names.length);
//			Map<String,Object> controllers= gatherController();
//			if (true){
//			ControllerGenLogger.printInfo("Controller size:" + controllers.size());
//			return;
//			}
			int i=1;
			
//			for(Object object:controllers.values()) {
			for (String name:names) {
				if ( ! name.contains("mainframe")) continue;
				ControllerGenLogger.printInfo("Controller List:" + name);
				Class controllClass;
				try {
					controllClass = Class.forName(name);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
//				Object object= context.getBean(name);
				List<Method> methods= gatherAnotatedMethod(controllClass);
				if( methods.size() == 0) continue;
				bufferWriter.write("<H1>" + i++ + ".   "+ name + "</H1>\r\n");
				StringBuffer formString= makeRequestFrom(methods);
				bufferWriter.write(formString.toString());
				
//				for(Method method:methods) {
//					bufferWriter.write(method.toString());
//					List<Parameter> requestParams=getAnnoatedParams(method);
//					if ( requestParams.size() > 0) {
//						StringBuffer parameterString=makeInputParams(requestParams);
//						bufferWriter.write(parameterString.toString());
//					}
//				}
				
//				StringBuffer methodString=getwriteStrings(methods);
//				bufferWriter.write(methodString.toString());
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
	
	private Map<String,Object> gatherController() {
		Map<String,Object> controllers=context.getBeansWithAnnotation(Controller.class);
		return controllers;
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
		
	private StringBuffer makeRequestFrom(List<Method> methods) {
		StringBuffer writeString= new StringBuffer();
		int i=1;
		for(Method method:methods) {
			ControllerTestGenerator testGen=method.getAnnotation(ControllerTestGenerator.class);
			//test를 위해 하나만 display 한다.
//			if ( !"reaAlldData".equals(testGen.MethodName())) continue;
			writeString.append("<dl><dd>" + i++);
			writeString.append(".  ");
			writeString.append(testGen.MethodName());
			writeString.append("</dd></dl>\r\n");
			writeString.append("<dl><dd><fieldset style = \"width:90%\">\r\n"); 
			writeString.append("<div id=\"" +testGen.MethodName() +"_"+i + "\">\r\n");
			writeString.append("<form accept-charset=\"UTF-8\">\r\n"); 
			
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
			
			writeString.append("<input type=\"button\" id=\"" + testGen.MethodName() +"\"" + " onclick=sendMVCTest(this) value=\"전송\"/>  \r\n" );
			writeString.append("<input type=\"reset\" value=\"취소\"/><br>\r\n");
			StringBuffer inputString= makeResultArea(testGen.MethodName());
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
