package com.codeJ.ControllerTest.Generator;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

public enum RequestMethodDefine {
	POST("post"),
	GET("get"),
	DELETE("delete"),
	PUT("put"),
	ANY("any");
	
	private String value;
	
	RequestMethodDefine (String value) {
		this.value = value;
	}
	public String getValue() {
		return value;
	}
}
