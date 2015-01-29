package com.recipegrace.tailorswift.common;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.scalaide.core.internal.jdt.model.ScalaClassElement;
import org.scalaide.core.internal.jdt.model.ScalaSourceFile;

import com.recipegrace.tailorswift.launchshortcut.JavaLaunchConfigurationUtils;
@SuppressWarnings("restriction")
public class ScalaParsingHelper {

	public static final String WEBSCALDING_JOB_TYPE = "com.twitter.scalding.Job";

	public static ScalaClassElement getWebScaldingJobClass(
			ScalaSourceFile scalaSourceFile) throws JavaModelException,
			CoreException {
	
		IProject project = scalaSourceFile.getCompilationUnit().getJavaProject().getProject();
		for (IJavaElement element : scalaSourceFile
				.getChildren()) {
			if (element instanceof ScalaClassElement) {
				
				ScalaClassElement elt = (ScalaClassElement) element;
				if (isWebScaldingType(project,elt))
					 return elt;
			}
		}
		return null;
	}


	public static boolean isWebScaldingType(
			ScalaSourceFile scalaSourceFile) throws JavaModelException,
			CoreException {
	
		IProject project = scalaSourceFile.getCompilationUnit().getJavaProject().getProject();
		for (IJavaElement element : scalaSourceFile
				.getChildren()) {
			if (element instanceof ScalaClassElement) {
				
				ScalaClassElement elt = (ScalaClassElement) element;
				if (isWebScaldingType(project,elt))
					 return true;
			}
		}
		return false;
	}

	public static boolean isWebScaldingType(IProject project, IType type)
			throws JavaModelException, CoreException {
		boolean isWebScaldingJob = false;
		ITypeHierarchy hierarchy = type
				.newSupertypeHierarchy(new NullProgressMonitor());
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IJavaModel javaModel = JavaCore.create(workspaceRoot);
		IJavaProject javaProject = javaModel.getJavaProject(project.getName());
		IType javaLangApplet = JavaLaunchConfigurationUtils.getMainType(
				WEBSCALDING_JOB_TYPE, javaProject);
		if (hierarchy.contains(javaLangApplet)) {
			isWebScaldingJob = true;
		}
		return isWebScaldingJob;
	}

}
