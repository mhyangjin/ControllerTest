package com.codeJ.ControllerTest.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import com.codeJ.ControllerTest.Generator.testWebGenerator;
import com.codeJ.ControllerTest.comm.ControllerGenLogger;

/** This is Configuration.
 * @author julu1 <julu1 @ naver.com >
 * @version 0.1.0
 */
@Configuration
public class ControllerTestGenConfig {
	@Value("${MVCJen.CodeJ.com}") 
	String mvcViewLocation;
	
	@Bean
	public MappingJackson2JsonView jsonView() {
		return new MappingJackson2JsonView();
	}
	
	@Bean(name="webGen")
	public testWebGenerator webGen() {
		ControllerGenLogger.printDebug("mvcViewLocation:"+ mvcViewLocation);
		if ( mvcViewLocation == null)
			return new testWebGenerator().setPath("src/main/resources/",false);
		else
			return new testWebGenerator().setPath(mvcViewLocation,true);
	}
}
