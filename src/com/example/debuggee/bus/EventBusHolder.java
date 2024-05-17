package com.example.debuggee.bus;

import java.util.LinkedList;
import java.util.Queue;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.Activator;
import org.palladiosimulator.addon.slingshot.debuggereventsystems.EventDebugSystem;
import org.palladiosimulator.analyzer.slingshot.common.events.DESEvent;
import org.palladiosimulator.analyzer.slingshot.debugger.translator.DebuggingEnabledEventBusFactory;
import org.palladiosimulator.analyzer.slingshot.eventdriver.Bus;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.PostIntercept;
import org.palladiosimulator.analyzer.slingshot.eventdriver.entity.interceptors.InterceptorInformation;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.InterceptionResult;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import com.example.debuggee.events.FirstEvent;

import umontreal.ssj.simevents.Event;
import umontreal.ssj.simevents.Simulator;

public final class EventBusHolder {
	
	private final Logger LOGGER = LogManager.getLogger(EventBusHolder.class);

	private final Bus eventBus;
	private final Simulator simulator = new Simulator();

	private int cumulativeEvents = 0;
	private boolean isAcceptingEvents = false;
	private boolean isRunning = false;
	
	private Queue<DESEvent> queuedEvent = new LinkedList<>();
	
	public EventBusHolder() {
		eventBus = DebuggingEnabledEventBusFactory.createBus("test");
		init();
	}

	//@Override
	public void init() {
		simulator.init();
		isAcceptingEvents = true;
		eventBus.register(this);
	}

	//@Override
	public void scheduleEvent(final DESEvent event) {
		if (!isAcceptingEvents) {
			return;
		}

		if (event.time() > 0) {
			this.scheduleEventAt(event, event.time());
			return;
		}

		final Event simulationEvent = new SSJEvent(event);
		LOGGER.debug("Schedule event " + event.getName() + " with delay " + event.delay());
		simulationEvent.schedule(event.delay() + 1.0);
	}

	public void scheduleEventAt(final DESEvent event, final double simulationTime) {
		if (!isAcceptingEvents) {
			return;
		}

		final Event simulationEvent = new SSJEvent(event);
		simulationEvent.setTime(simulationTime + event.delay());
		simulator.getEventList().add(simulationEvent);
	}

	public void start() {
		simulator.start();
		isRunning = true;
		eventBus.acceptEvents(true);
	}

	public void stop() {
		simulator.stop();
		isRunning = false;
		eventBus.acceptEvents(false);
		isAcceptingEvents = false;
	}

	public void register(final Object reg) {
		this.eventBus.register(reg);
	}
	
	public void post(final DESEvent event) {
//		if (event instanceof DESEvent desEvent) {
//			scheduleEvent(desEvent);
//		} else {
//			//eventBus.post(event);
//		}
		
		scheduleEvent(event);
		
		if (!isRunning) {
			start();
		}
	}

	private final class SSJEvent extends Event {

		private final DESEvent event;

		private SSJEvent(final DESEvent correspondingEvent) {
			super(simulator);
			event = correspondingEvent;
		}

		@Override
		public void actions() {
			if (this.simulator().isStopped()) {
				return;
			}

			LOGGER.info("Even dispatched at " + this.simulator().time() + ": " + event.getName() + "(" + event.getId() + ")");

			event.setTime(this.simulator().time());
			eventBus.post(event);
			cumulativeEvents++;
		}

	}
	
	@PostIntercept
	public InterceptionResult republish(final InterceptorInformation inf, final Object event, final Result<?> result) {
		while (!queuedEvent.isEmpty()) {
			final DESEvent ev = queuedEvent.poll();
			post(ev);
		}
		
		result.getResultEvents().stream()
			.filter(DESEvent.class::isInstance)
			.map(DESEvent.class::cast)
			.forEach(ev -> {
				if (inf.getEventType().equals(FirstEvent.class) && inf.getName().equals("onFirstEvent")) {
					queuedEvent.add(ev);
					return;
				}
				post(ev);
			});
		
		
		
		return InterceptionResult.success();
	}
}
