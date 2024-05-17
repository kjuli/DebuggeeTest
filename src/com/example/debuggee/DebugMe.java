package com.example.debuggee;

import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.Subscribe;
import org.palladiosimulator.analyzer.slingshot.eventdriver.annotations.eventcontract.OnEvent;
import org.palladiosimulator.analyzer.slingshot.eventdriver.returntypes.Result;

import com.example.debuggee.bus.EventBusHolder;
import com.example.debuggee.events.FirstEvent;
import com.example.debuggee.events.SecondEvent;
import com.example.debuggee.events.ThirdEvent;

@OnEvent(when = FirstEvent.class, then = SecondEvent.class)
@OnEvent(when = SecondEvent.class, then = { SecondEvent.class, ThirdEvent.class })
@OnEvent(when = ThirdEvent.class, then = {})
public class DebugMe {
	
	private static final String MYSTERIOUS_REASON = "mysterious reason";
	private static final String RIGHT_REASON = "right reason";
	
	@Subscribe
	public Result<SecondEvent> onFirstEvent(final FirstEvent event) {
		System.out.println("First event caught in DebugMe");
		return Result.of(new SecondEvent(MYSTERIOUS_REASON));
	}
	
	@Subscribe
	public Result<?> onSecondEvent(final SecondEvent event) {
		final String reason = event.reason();
		System.out.println("Reason is: " + reason);
		
		if (MYSTERIOUS_REASON.equals(reason)) {
			return Result.of(new SecondEvent(RIGHT_REASON));
		} else if (RIGHT_REASON.equals(reason)) {
			return Result.of(new ThirdEvent());
		} else {
			throw new IllegalArgumentException("Only mysterious reason and right "
					+ "reason are allowed, but it is " + reason);
		}
	}
	
	@Subscribe
	public void onThirdEvent(final ThirdEvent event) {
		System.out.println("Third event reached in DebugMe class");
	}
}


