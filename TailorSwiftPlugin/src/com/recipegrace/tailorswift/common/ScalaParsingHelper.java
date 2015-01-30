package com.recipegrace.tailorswift.common;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
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
	public static IType[] findWebScaldingJobClasses(IRunnableContext context, final Object[] elements) throws InvocationTargetException, InterruptedException, JavaModelException {
		final Set<IType> result= new HashSet<IType>();
	
		if (elements.length > 0) {
			
			IProject project =ResourcesPlugin.getWorkspace().getRoot().getProjects(0)[0];
			ScalaProject scalaProject = new ScalaProject(project);
			
	      	 
	      	Collection<IFile> files=   JavaConversions.asJavaCollection(scalaProject.allSourceFiles().toList());
	      	
	      	ScalaSourceFile sourceFile = ScalaSourceFile.createFromPath(files.iterator().next().getLocation().toOSString()).get();
	      	  
			
/*		IRunnableWithProgress runnable= new IRunnableWithProgress() {
				public void run(IProgressMonitor pm) throws InterruptedException {
					int nElements= elements.length;
					pm.beginTask("Searching for webscalding job classes", nElements); 
					try {
	
					   result = sourceFile.getAllTypes();
					} finally {
						pm.done();
					}
				}
			};
			context.run(true, true, runnable);			
		}
		return (IType[]) result.toArray(new IType[result.size()]) ; */
		return sourceFile.getAllTypes();
		}
		return null;
	}
	public static Set<IType>  collectWebScaldingJobTypes(IProgressMonitor monitor, IJavaProject project) {
		IType[] types;
		Set<IType> result = new HashSet<IType>(5);
		try {
			IType javaLangApplet = JavaLaunchConfigurationUtils.getMainType(
					WEBSCALDING_JOB_TYPE, project);
			ITypeHierarchy hierarchy = javaLangApplet.newTypeHierarchy(project, new SubProgressMonitor(monitor, 1));
			types = hierarchy.getAllTypes();
			int length = types.length;
			if (length != 0) {
				for (int i = 0; i < length; i++) {
					if (!types[i].isBinary()) {
						result.add(types[i]);
					}
				}
			}
		} catch(JavaModelException jme) {
			jme.printStackTrace();
		} catch(CoreException e) {
		}
		monitor.done();
		return result;
	}
	private static Object computeScope(Object element) {
        if (element instanceof IJavaElement) {
            return element;
        }
		if (element instanceof IAdaptable) {
			element = ((IAdaptable)element).getAdapter(IResource.class);
		}
		if (element instanceof IResource) {
			IJavaElement javaElement = JavaCore.create((IResource)element);
			if (javaElement != null && !javaElement.exists()) {
				// do not consider the resource - corresponding java element does not exist
				element = null;
			} else {
			    element= javaElement;
            }
            
		}
		return element;
	}

	
	public static void collectTypes(Object element, IProgressMonitor monitor, Set<IType>  result) throws JavaModelException/*, InvocationTargetException*/ {
		element= computeScope(element);
		while(element instanceof IMember) {
			if(element instanceof IType) {
				if (isSubClassOfScaldingJob(monitor, (IType)element)) {
					result.add((IType)element);
					monitor.done();
					return;
				}
			}
			element= ((IJavaElement)element).getParent();
		}
		if (element instanceof ICompilationUnit) {
			ICompilationUnit cu= (ICompilationUnit)element;
			IType[] types= cu.getAllTypes();
			for (int i= 0; i < types.length; i++) {
				if (isSubClassOfScaldingJob(monitor, types[i])) {
					result.add(types[i]);
				}
			}
		} else if (element instanceof IClassFile) {
			IType type = ((IClassFile)element).getType();
			if (isSubClassOfScaldingJob(monitor, type)) {
				result.add(type);
			}
		} else if (element instanceof IJavaElement) {
			IJavaElement parent = (IJavaElement) element;
			List<IType> found= searchSubclasssesOfWebScaldingJob(monitor, (IJavaElement)element);
			// filter within the parent element
			Iterator<IType> iterator = found.iterator();
			while (iterator.hasNext()) {
				IJavaElement target = (IJavaElement) iterator.next();
				IJavaElement child = target;
				while (child != null) {
					if (child.equals(parent)) {
						result.add((IType)target);
						break;
					}
					child = child.getParent();
				}
			}
		}
		monitor.done();
	}

	private static List<IType> searchSubclasssesOfWebScaldingJob(IProgressMonitor pm, IJavaElement javaElement) {
		return new ArrayList<IType>(collectWebScaldingJobTypes(pm, javaElement.getJavaProject()));
	}
	
	private static boolean isSubClassOfScaldingJob(IProgressMonitor pm, IType type) {
		return collectWebScaldingJobTypes(pm, type.getJavaProject()).contains(type);
	}
	
}
