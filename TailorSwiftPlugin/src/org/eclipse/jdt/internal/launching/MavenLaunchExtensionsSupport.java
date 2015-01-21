package org.eclipse.jdt.internal.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.ISourceLookupParticipant;
import org.eclipse.jdt.launching.sourcelookup.containers.JavaSourceLookupParticipant;
import org.eclipse.osgi.util.NLS;
import org.eclipse.m2e.actions.MavenLaunchConstants;
import org.eclipse.m2e.internal.launch.IMavenLaunchParticipant;
import org.eclipse.m2e.internal.launch.MavenLaunchParticipantInfo;
import org.eclipse.m2e.internal.launch.MavenRuntimeLaunchSupport.VMArguments;
import org.eclipse.m2e.internal.launch.MavenSourceLocator;


/**
 * @since 1.5
 */
public class MavenLaunchExtensionsSupport {


  private final List<IMavenLaunchParticipant> participants;

  private MavenLaunchExtensionsSupport(List<IMavenLaunchParticipant> participants) {
    this.participants = participants;
  }

  public void configureSourceLookup(ILaunchConfiguration configuration, ILaunch launch, IProgressMonitor monitor) {
    if(launch.getSourceLocator() instanceof MavenSourceLocator) {
      final MavenSourceLocator sourceLocator = (MavenSourceLocator) launch.getSourceLocator();
      for(IMavenLaunchParticipant participant : participants) {
        List<ISourceLookupParticipant> sourceLookupParticipants = participant.getSourceLookupParticipants(
            configuration, launch, monitor);
        if(sourceLookupParticipants != null && !sourceLookupParticipants.isEmpty()) {
          sourceLocator.addParticipants(sourceLookupParticipants
              .toArray(new ISourceLookupParticipant[sourceLookupParticipants.size()]));
        }
      }
      sourceLocator.addParticipants(new ISourceLookupParticipant[] {new JavaSourceLookupParticipant()});
    } else {
 //     log.warn(NLS.bind(Messages.MavenLaynchDelegate_unsupported_source_locator, launch.getSourceLocator().getClass()
  //        .getCanonicalName()));
    }
  }

  public static MavenLaunchExtensionsSupport create(ILaunchConfiguration configuration, ILaunch launch)
      throws CoreException {
    @SuppressWarnings("unchecked")
    Set<String> disabledExtensions = configuration.getAttribute(MavenLaunchConstants.ATTR_DISABLED_EXTENSIONS,
        Collections.EMPTY_SET);

    List<IMavenLaunchParticipant> participants = new ArrayList<IMavenLaunchParticipant>();

    for(MavenLaunchParticipantInfo info : MavenLaunchParticipantInfo.readParticipantsInfo()) {
      if(!disabledExtensions.contains(info.getId()) && info.getModes().contains(launch.getLaunchMode())) {
        try {
          participants.add(info.createParticipant());
        } catch(CoreException e) {
     //     log.debug("Problem with external extension point", e);
        }
      }
    }

    return new MavenLaunchExtensionsSupport(participants);
  }

  public void appendProgramArguments(StringBuilder arguments, ILaunchConfiguration configuration, ILaunch launch,
      IProgressMonitor monitor) {
    for(IMavenLaunchParticipant participant : participants) {
      String extensionArguments = participant.getProgramArguments(configuration, launch, monitor);
      if(extensionArguments != null) {
        arguments.append(" ").append(extensionArguments);
      }
    }
  }

  public void appendVMArguments(VMArguments arguments, ILaunchConfiguration configuration, ILaunch launch,
      IProgressMonitor monitor) {
    for(IMavenLaunchParticipant participant : participants) {
      arguments.append(participant.getVMArguments(configuration, launch, monitor));
    }
  }

}
