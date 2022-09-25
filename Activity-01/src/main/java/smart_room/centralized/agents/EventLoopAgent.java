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
    private Status status = Status.OFF;

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
                switch (status) {
                    case ON:
                        if ((ev instanceof LightLevelChanged && ((LightLevelChanged) ev).getNewLevel() > threshold)
                                || (ev instanceof PresenceNoMoreDetected)) {
                            board.off();
                            status = Status.OFF;
                        }
                        break;
                    case OFF:
                        if ((ev instanceof LightLevelChanged && (board.presenceDetected() && ((LightLevelChanged) ev).getNewLevel() < threshold))
                                || (ev instanceof PresenceDetected && board.getLuminosity() < threshold)) {
                            board.on();
                            status = Status.ON;
                        }
                        break;
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
