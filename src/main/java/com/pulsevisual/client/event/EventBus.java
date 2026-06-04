package com.pulsevisual.client.event;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class EventBus {
    private static final List<EventListener> listeners = new ArrayList<>();

    public static void subscribe(EventListener listener) {
        listeners.add(listener);
    }

    public static void unsubscribe(EventListener listener) {
        listeners.remove(listener);
    }

    public static void publish(Event event) {
        for (EventListener listener : listeners) {
            listener.onEvent(event);
        }
    }

    public interface EventListener {
        void onEvent(Event event);
    }

    public interface Event {
    }
}
