package smart_room.centralized.agents;

import smart_room.centralized.SinglelBoardSimulator;

public class SuperLoopAgent extends Thread {

    final private double threshold;
    final private SinglelBoardSimulator board = new SinglelBoardSimulator();

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
            if (presenceDetected)  {
                if (luminosity < threshold) {
                    board.on();
                } else {
                    board.off();
                }
            } else {
                board.off();
            }

            try {
                sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
