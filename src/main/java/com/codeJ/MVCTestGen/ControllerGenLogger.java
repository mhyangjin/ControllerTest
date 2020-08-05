package com.codeJ.MVCTestGen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerGenLogger {
	private static Logger logger=LoggerFactory.getLogger(ControllerGenLogger.class);
	public static void printTrace(String msg) {
		logger.trace(msg);
	}
	
	public static void printDebug(String msg) {
		logger.debug(msg);
	}
		/**
	 * print info.
	 */
	public static void printInfo(String msg) {
		logger.info(msg);
	}

	public static void printWarn(String msg) {
		logger.warn(msg);
	}
	/**
	 *print error
	 */
	public static void printError(String msg) {
		logger.error(msg);
	}
}
