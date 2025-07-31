package com.sddevops.jenkins_maven.eclipse;

import java.util.logging.Logger; // <-- Required import

/**
 * Hello world!
 */
public class App {
	// Create a Logger instance
	private static final Logger logger = Logger.getLogger(App.class.getName());

	public static void main(String[] args) {
		logger.info("Hello World!"); // Replaces System.out.println
		logger.info("Test"); // Replaces System.out.println
	}

	// Trigger CI build test
}
