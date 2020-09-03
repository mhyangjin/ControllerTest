package com.codeJ.ControllerTest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.web.servlet.ModelAndView;
import com.codeJ.ControllerTest.comm.MVCData;


@Controller
public class MockMVCController {
	private static Logger logger = LoggerFactory.getLogger(MockMVCController.class);
	@Autowired
	private SimpMessagingTemplate template;
	@Autowired
	MockMVCService mvcService;
	
	@GetMapping("/Mock")
	public String main(Model model) {
		logger.debug("main called!");
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
	 
	 @MessageMapping("mvcTest")
	 @SendTo("/subscribe/mvcTest")
	  public void mvcTest(MVCData mvcData) throws Exception {
		 logger.debug("mvcTest: INIT!"); 
//		 logger.debug("mvcTest: getRequestString:{}", mvcData.getRequestString().length); 
//		 logger.debug("mvcTest: JSonClassString:{}", mvcData.JSonClassString.length); 
//		 logger.debug("mvcTest: JSonClassString:{}", mvcData.toString()); 
		 String ControllerName=mvcData.getControllerName();
	
		 MvcResult result=mvcService.callTest(mvcData);
		 		 
		 ModelAndView mv=new ModelAndView("jsonView");
		 Map<String,Object> returns=new HashMap();
		 returns.put("controller", ControllerName);
		 returns.put("Result",  result.getResponse().getStatus());
		 returns.put("ERR",  result.getResponse().getContentAsString());
		 
		 logger.debug("Result:{}",result.getResponse().getStatus());
		 logger.debug("ERR:{}", result.getResponse().getContentAsString());
		 
		 if (HttpStatus.OK.value() == result.getResponse().getStatus() ) {
			 ModelAndView modelview=result.getModelAndView();
			 if(modelview !=null) {
				 mv.addAllObjects(modelview.getModelMap());
				 returns.put("mvcResult",mv);
			 }
			 else {
				 String resultString=result.getResponse().getContentAsString();
				 logger.debug("mvcTest: resultStsring:{}",resultString); 
				 returns.put("mvcResult",resultString);
			 }
			
		 }
		 template.convertAndSend("/subscribe/mvcTest",returns);
	}
}