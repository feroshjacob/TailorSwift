package com.recipegrace.tailorswift.propertytester;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.scalaide.core.internal.jdt.model.ScalaSourceFile;

import com.recipegrace.tailorswift.common.ScalaParsingHelper;

public class WebScaldingJobTester extends PropertyTester {

	public WebScaldingJobTester() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
	
		if(property.equals("isWebScaldingJob") && receiver instanceof ScalaSourceFile) {
			ScalaSourceFile source = (ScalaSourceFile)receiver;
			 try {
				boolean result= ScalaParsingHelper.isWebScaldingType(source);
				return result;
			} catch (CoreException e) {
				
					e.printStackTrace();
			}
			
		}
		
		return false;
	}

}
	