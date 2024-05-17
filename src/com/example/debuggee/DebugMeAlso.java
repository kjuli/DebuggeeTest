package com.example.debuggee;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;

import com.example.debuggee.events.FirstEvent;

@OnEvent(when = FirstEvent.class, then = {})
public class DebugMeAlso {

	@Subscribe
	public void anotherFirstEvent(final FirstEvent firstEvent) {
		System.out.println("This is printed to the console by another event handler.");
	}
	
}
