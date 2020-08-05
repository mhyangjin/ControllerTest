package com.codeJ.MVCTestGen;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)//, classes= {DBCoreDevConfigClass.class})
@Service
public class MockMVCService {
	@Autowired
    private SimpMessagingTemplate template;
	@Autowired
	private WebApplicationContext context;
	@Autowired
	JSONUtil jsonUtil;
	
	private MockMvc mockMvc;
	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
									MediaType.APPLICATION_JSON.getSubtype(),
									Charset.forName("utf8"));
	
	@Before
	public void setup() {
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build(); 
	}
	
//    JsonMapper jsonMapper = new JsonMapper();
	public boolean callTest(String to, String text){
//		  ControllerGenLogger.printDebug("MockMVCService : publish");
		boolean ret = false;
		try {
			this.template.convertAndSend("/topic/"+to);
			ret = true;
		} catch (Exception ex) {
		}
		return ret;
	} 
	  
	@SuppressWarnings("rawtypes")
	public MvcResult callTest(MVCData mvcData) {
		MvcResult result=null;
		MockHttpServletRequestBuilder requestBuilder=null;
		  
		mockMvc = MockMvcBuilders.webAppContextSetup(context).build(); 
		String ControllerName ="/" + mvcData.getControllerName();
		
		requestBuilder = get(ControllerName);
		
		//parameter JSONString으로 들어올 경우, model인 경우임
		if ( mvcData.getJSonClassString().length  > 0 ) {
				for( String jsonString:mvcData.getJSonClassString())
					requestBuilder= requestBuilder.content(jsonString);					
		}
		//parameter 에 INPUT type으로 들어온 인자가 있을 경우
		if (mvcData.getRequestString().length > 0  )
		{
			MultiValueMap paramMap =  jsonUtil.JSonStringtoMap(mvcData.getRequestString());
			requestBuilder = requestBuilder.params(paramMap);
		}

		try {
			result = this.mockMvc.perform(requestBuilder).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
