package com.recipegrace.tailorswift.common;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.scalaide.core.internal.jdt.model.ScalaClassElement;
import org.scalaide.core.internal.jdt.model.ScalaSourceFile;
import org.scalaide.core.internal.project.ScalaProject;

import scala.collection.JavaConversions;

import com.recipegrace.tailorswift.launchshortcut.JavaLaunchConfigurationUtils;
@SuppressWarnings("restriction")
public class ScalaParsingHelper {

	public static final String WEBSCALDING_JOB_TYPE = "com.twitter.scalding.Job";

	public static ScalaClassElement getOneWebScaldingJobClass(
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

	public static Set<IType> getAllWebScaldingJobClass(
			ScalaSourceFile scalaSourceFile) throws JavaModelException,
			CoreException {
		final Set<IType> result= new HashSet<IType>();
		IProject project = scalaSourceFile.getCompilationUnit().getJavaProject().getProject();
		for (IJavaElement element : scalaSourceFile
				.getChildren()) {
			if (element instanceof ScalaClassElement) {
				
				ScalaClassElement elt = (ScalaClassElement) element;
				if (isWebScaldingType(project,elt))
					  result.add(elt);
			}
		}
		return result;
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
	public static IType[] findWebScaldingJobClasses(IRunnableContext context, final String projectName) throws InvocationTargetException, InterruptedException, JavaModelException {
		final Set<IType> result= new HashSet<IType>();
				
		IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) throws InterruptedException {
					pm.beginTask("Searching for webscalding job classes", 1); 
					try {
	
						if(projectName.length()>0) {
						extractWebScaldingClassesFromProject(projectName,result);
						}
						else {
							IProject[] projects= ResourcesPlugin.getWorkspace().getRoot().getProjects();
							for(IProject project: projects){
								extractWebScaldingClassesFromProject(project.getName(), result);
							}
						}
					} catch (CoreException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {
						pm.done();
					}
				}

				protected void extractWebScaldingClassesFromProject(
						final String projectName, final Set<IType> result)
						throws JavaModelException, CoreException {
					IProject project =ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
					ScalaProject scalaProject = new ScalaProject(project);
					Collection<IFile> files=   JavaConversions.asJavaCollection(scalaProject.allSourceFiles().toList());
					for( IFile file: files ) {
						ScalaSourceFile sourceFile = ScalaSourceFile.createFromPath(file.getFullPath().toOSString()).get();	
						Set<IType> scalaClasses = getAllWebScaldingJobClass(sourceFile);
						result.addAll(scalaClasses);
						}
				}
			};
			context.run(true, true, runnable);			
		
		return (IType[]) result.toArray(new IType[result.size()]) ; 
	}
	
	
}
