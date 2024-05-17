package com.example.debuggee;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.example.debuggee.bus.EventBusHolder;
import com.example.debuggee.events.FirstEvent;

public class Main implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		final EventBusHolder bus = new EventBusHolder();
		
		/* Register event handlers */
		bus.register(new DebugMe());
		bus.register(new DebugMeAlso());
		
		/* Post first event */
		bus.post(new FirstEvent());
		
		System.out.println("Execution Ended");
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// Nothing to do
	}

}


