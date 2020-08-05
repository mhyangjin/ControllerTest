package com.codeJ.MVCTestGen;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;


@Controller
public class MockMVCController {
	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	MockMVCService mvcService;
	
	@GetMapping("/Mock")
	public String main(Model model) {
		ControllerGenLogger.printDebug("main called!");
		return "main";
	}
	
//	 @MessageMapping("/mvcTest")
//	 @SendTo("/subscribe/mvcTest")
//	  public void mvcTest(@RequestParam Map param) throws Exception {
//		 String ControllerName=(String) param.get("controller");
//	
//		 MvcResult result=mvcService.callTest(param);
//		 
//		 
//		 ModelAndView mv=new ModelAndView("jsonView");
//		 mv.addAllObjects(result.getModelAndView().getModelMap());
//
//		 Map<String,Object> returns=new HashMap<>();
//		 returns.put("controller", ControllerName);
//		 returns.put("Result",  result.getResponse().getStatus());
//		 returns.put("ERR",  result.getResponse().getErrorMessage());
//		 returns.put("mvcResult",mv);
//		 template.convertAndSend("/subscribe/mvcTest",returns);
//	}
	 
	 @MessageMapping("/mvcTest")
	 @SendTo("/subscribe/mvcTest")
	  public void mvcTest(MVCData mvcData) throws Exception {
		 ControllerGenLogger.printDebug("mvcTest: INIT!"); 
//		 ControllerGenLogger.printDebug("mvcTest: getRequestString:" + mvcData.getRequestString().length); 
//		 ControllerGenLogger.printDebug("mvcTest: JSonClassString:" + mvcData.JSonClassString.length); 
//		 ControllerGenLogger.printDebug("mvcTest: JSonClassString:" + mvcData.toString()); 
		 String ControllerName=mvcData.getControllerName();
	
		 MvcResult result=mvcService.callTest(mvcData);
		 		 
		 ModelAndView mv=new ModelAndView("jsonView");
		 Map<String,Object> returns=new HashMap();
		 returns.put("controller", ControllerName);
		 returns.put("Result",  result.getResponse().getStatus());
		 returns.put("ERR",  result.getResponse().getContentAsString());
		 ControllerGenLogger.printDebug("ERR:" +  result.getResponse().getContentAsString());
		 if (HttpStatus.OK.value() == result.getResponse().getStatus() ) {
			 mv.addAllObjects(result.getModelAndView().getModelMap());
			 returns.put("mvcResult",mv);
		 }
		 template.convertAndSend("/subscribe/mvcTest",returns);
	}
	 

}
