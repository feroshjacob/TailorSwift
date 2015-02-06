package com.recipegrace.exampleproject.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import tailorswift.Activator;

/**
* Sample integration test. In Eclipse, right-click > Run As > JUnit-Plugin. <br/>
* In Maven CLI, run "mvn integration-test".
*/
public class ActivatorTest {

	@Test
	public void veryStupidTest() {
		assertEquals("TailorSwiftPlugin",Activator.PLUGIN_ID);
	//	assertTrue("Plugin should be started", Activator.getDefault().started);
	}
}  