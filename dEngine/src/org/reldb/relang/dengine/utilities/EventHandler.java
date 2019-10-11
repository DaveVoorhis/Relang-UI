package org.reldb.relang.dengine.utilities;

import java.util.Vector;

public class EventHandler<Event> {

	private Vector<EventListener<Event>> listeners = new Vector<>();
	
	public void addListener(EventListener<Event> listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EventListener<Event> listener) {
		listeners.remove(listener);
	}

	public void fire(Event event) {
		listeners.forEach(listener -> listener.notify(event));
	}

}
