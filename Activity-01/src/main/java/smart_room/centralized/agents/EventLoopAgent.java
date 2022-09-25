package smart_room.centralized.agents;

import smart_room.Event;
import smart_room.centralized.LightLevelChanged;
import smart_room.centralized.PresenceDetected;
import smart_room.centralized.PresenceNoMoreDetected;
import smart_room.centralized.SinglelBoardSimulator;

import java.util.LinkedList;

public class EventLoopAgent extends Thread {

    final private double threshold;
    final private SinglelBoardSimulator board = new SinglelBoardSimulator();
    final private LinkedList<Event> eventQueue = new LinkedList<>();

    public EventLoopAgent(double threshold) {
        this.threshold = threshold;
        board.init();
        // register for events
        board.register(eventQueue::add);
    }

    @Override
    public void run() {
        while (true) {
            // wait for event
            if (!eventQueue.isEmpty()) {
                final Event ev = eventQueue.removeFirst();

                // select handler and execute
                if (ev instanceof LightLevelChanged) {
                    if (board.presenceDetected() && ((LightLevelChanged) ev).getNewLevel() < threshold) {
                        board.on();
                    } else {
                        board.off();
                    }
                } else if (ev instanceof PresenceDetected) {
                    if (board.getLuminosity() < threshold) {
                        board.on();
                    }
                } else if (ev instanceof PresenceNoMoreDetected) {
                    board.off();
                }
            }
            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
