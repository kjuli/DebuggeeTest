package com.example.debuggee.events;

import java.util.Objects;

import org.palladiosimulator.analyzer.slingshot.common.events.AbstractSimulationEvent;

public class SecondEvent extends AbstractSimulationEvent {

	/** The reason why this event has happened. */
	private final String reason;
	
	/**
	 * Creates an instance with the specified reason.
	 * 
	 * @param reason A non-null string (can be empty)
	 */
	public SecondEvent(final String reason) {
		this.reason = Objects.requireNonNull(reason);
	}
	
	public String reason() {
		return reason;
	}
	
}
