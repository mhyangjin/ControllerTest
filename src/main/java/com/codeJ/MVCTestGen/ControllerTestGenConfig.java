package com.codeJ.MVCTestGen;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

/** This is Configuration.
 * @author julu1 <julu1 @ naver.com >
 * @version 0.1.0
 */
@Configuration
public class ControllerTestGenConfig {
//	@Value("${spring.mvc.view.prefix}") 
	String mvcViewLocation;
	
	@Bean
	public MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	
	@Bean(name="webGen")
	public testWebGenerator webGen() {
		ControllerGenLogger.printDebug("mvcViewLocation:" + mvcViewLocation);
		return new testWebGenerator().setPath("src/main/webapp/"+mvcViewLocation);
	}
}
