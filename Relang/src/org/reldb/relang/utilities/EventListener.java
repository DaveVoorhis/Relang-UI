package org.reldb.relang.utilities;

@FunctionalInterface
public interface EventListener<Event> {
	public void notify(Event event);
}
