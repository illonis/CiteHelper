package de.illonis.citehelper;

import com.google.common.eventbus.EventBus;

public class CiteEventBus {

	private final EventBus bus = new EventBus();

	// Innere private Klasse, die erst beim Zugriff durch die umgebende Klasse
	// initialisiert wird
	private static final class InstanceHolder {
		// Die Initialisierung von Klassenvariablen geschieht nur einmal
		// und wird vom ClassLoader implizit synchronisiert
		static final CiteEventBus INSTANCE = new CiteEventBus();
	}

	// Verhindere die Erzeugung des Objektes über andere Methoden
	private CiteEventBus() {
	}

	// Eine nicht synchronisierte Zugriffsmethode auf Klassenebene.
	public static CiteEventBus getInstance() {
		return InstanceHolder.INSTANCE;
	}

	public EventBus getBus() {
		return bus;
	}
}
