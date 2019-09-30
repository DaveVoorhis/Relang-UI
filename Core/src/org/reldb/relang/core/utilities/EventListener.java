package org.reldb.relang.core.utilities;

@FunctionalInterface
public interface EventListener<Event> {
	public void notify(Event event);
}
