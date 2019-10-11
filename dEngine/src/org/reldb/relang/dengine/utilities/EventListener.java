package org.reldb.relang.dengine.utilities;

@FunctionalInterface
public interface EventListener<Event> {
	public void notify(Event event);
}
