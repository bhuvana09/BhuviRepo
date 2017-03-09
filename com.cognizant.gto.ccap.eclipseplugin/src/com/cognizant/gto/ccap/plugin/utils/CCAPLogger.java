/**
 *
 */

package com.cognizant.gto.ccap.plugin.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 221013
 */
public class CCAPLogger {

	private Logger logger = null;

	protected CCAPLogger(Logger logger) {

		this.logger = logger;
	}

	public static CCAPLogger getLogger(Class<?> clazz) {

		return new CCAPLogger(LoggerFactory.getLogger(clazz));
	}

	/**
	 *
	 * @param message
	 * @param t
	 */
	public void debug(String message, Throwable t) {

		logger.debug(message, t);
	}

	/**
	 *
	 * @param message
	 */
	public void debug(String message) {

		logger.debug(message);
	}

	/**
	 *
	 * @param message
	 * @param t
	 */
	public void error(String message, Throwable t) {

		logger.error(message, t);
	}

	/**
	 *
	 * @param message
	 */
	public void error(String message) {

		logger.error(message);
	}

	public void error(Throwable t) {

		logger.error("", t);
	}

	/**
	 *
	 * @param message
	 * @param t
	 */
	public void info(String message, Throwable t) {

		logger.info(message, t);
	}

	/**
	 *
	 * @param message
	 */
	public void info(String message) {

		logger.info(message);
	}

	public void info(Throwable t) {
		logger.info("", t);
	}

	/**
	 *
	 * @param message
	 * @param t
	 */
	public void warn(String message, Throwable t) {

		logger.warn(message, t);
	}

	/**
	 *
	 * @param message
	 * @param t
	 */
	public void warn(Throwable t) {
		logger.warn("", t);
	}

	/**
	 *
	 * @param message
	 */
	public void warn(String message) {

		logger.warn(message);
	}

}
