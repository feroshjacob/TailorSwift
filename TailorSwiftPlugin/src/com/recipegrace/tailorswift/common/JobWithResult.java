package com.recipegrace.tailorswift.common;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;

public abstract class JobWithResult extends Job {


	protected IOUtils command = new IOUtils();
	public JobWithResult(final String jobName) {
		super(jobName);
		setUser(true);
		addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (event.getResult().isOK())
					command.openInfo("Job completed successfully", jobName, IStatus.INFO);
				else
					command.openInfo("Job failed, check error log for details", jobName, IStatus.ERROR);
			}
		});

	}



}
