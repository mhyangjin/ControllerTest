package com.codeJ.ControllerTest.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import com.codeJ.ControllerTest.Generator.testWebGenerator;

/** This is Configuration.
 * @author julu1 <julu1 @ naver.com >
 * @version 0.1.0
 */
@Configuration
public class ControllerTestGenConfig {
	private static Logger logger = LoggerFactory.getLogger(ControllerTestGenConfig.class);
	@Value("${ControllerTest.CodeJ.com}") 
	String mvcViewLocation;
	
	@Bean
	public MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	
	@Bean(name="webGen")
	public testWebGenerator webGen() {
		logger.debug("mvcViewLocation:{}",mvcViewLocation);
		if ( mvcViewLocation == null)
			return new testWebGenerator().setPath("src/main/resources/",false);
		else
			return new testWebGenerator().setPath(mvcViewLocation,true);
	}
}
