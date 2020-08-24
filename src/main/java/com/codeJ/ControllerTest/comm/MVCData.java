package com.codeJ.ControllerTest.comm;

import org.springframework.lang.Nullable;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;

@Data
@JsonSerialize
public class MVCData {
	public String ControllerName;
	public String Method;
	@Nullable
	public String[] JSonClassString;
	@Nullable
	public String[] RequestString;
}