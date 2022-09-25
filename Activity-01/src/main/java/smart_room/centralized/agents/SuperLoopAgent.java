package smart_room.centralized.agents;

import smart_room.centralized.SinglelBoardSimulator;

public class SuperLoopAgent extends Thread {

    final private double threshold;
    final private SinglelBoardSimulator board = new SinglelBoardSimulator();
    private Status status = Status.OFF;

    public SuperLoopAgent(double threshold) {
        this.threshold = threshold;
        board.init();
    }

    @Override
    public void run() {
        while (true) {
            // sense
            final boolean presenceDetected = board.presenceDetected();
            final double luminosity = board.getLuminosity();

            // decide what to do and act
            switch (status) {
                case ON:
                    if (!presenceDetected || luminosity > threshold) {
                        board.off();
                        status = Status.OFF;
                    }
                    break;
                case OFF:
                    if (presenceDetected && luminosity < threshold) {
                        board.on();
                        status = Status.ON;
                    }
                    break;
            }

            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
