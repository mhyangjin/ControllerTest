package kr.co.codeJ.ControllerTest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import java.nio.charset.Charset;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;

import kr.co.codeJ.ControllerTest.Generator.RequestMethodDefine;
import kr.co.codeJ.ControllerTest.comm.JSONUtil;
import kr.co.codeJ.ControllerTest.comm.MVCData;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Service
public class MockMVCService {
	private static Logger logger = LoggerFactory.getLogger(MockMVCService.class);
	
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
		String ControllerName = mvcData.getControllerName();
		if (! ControllerName.startsWith("/"))
			ControllerName = "/" + ControllerName;
		logger.trace("callTest {}",mvcData.getMethod());
		RequestMethodDefine ReqMethod=  RequestMethodDefine.valueOf(mvcData.getMethod().toUpperCase());
		switch (ReqMethod) {
		case GET:
			requestBuilder = get(ControllerName);
			break;
		case POST:
			requestBuilder = post(ControllerName);
			break;
		case DELETE:
			requestBuilder = delete(ControllerName);
			break;
		case PUT:
			requestBuilder = put(ControllerName);
			break;
		case ANY:
			requestBuilder = get(ControllerName);
			break;
		default:
		}
		
		//parameter JSONString으로 들어올 경우, model인 경우임
		if ( mvcData.getJSonClassString().length  > 0 ) {
			logger.trace("getJSonClassString... {}",mvcData.getJSonClassString());
			
				for( String jsonString:mvcData.getJSonClassString()) {
//					String testString="{\r\n\"com.codeJ.BasicFrame.commModel.authorityGroup.AuthorityGroupDTO\":[\r\n { \r\n \"groupDesc\" : \"administrator\",  \"groupCode\" : \"ADMIN\"\r\n} \r\n]";
//					logger.trace("getJSonClassString... {}",testString);
					requestBuilder= requestBuilder.content(jsonString);		
				}
				requestBuilder=	requestBuilder.contentType(contentType);
				
								
		}
		//parameter 에 INPUT type으로 들어온 인자가 있을 경우
		if (mvcData.getRequestString().length > 0  )
		{
			MultiValueMap paramMap =  jsonUtil.JSonStringtoMap(mvcData.getRequestString());
			requestBuilder = requestBuilder.params(paramMap);
		}

		try {
			
			logger.trace("call controller... {}",ControllerName);
			result = this.mockMvc.perform(requestBuilder).andReturn();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}