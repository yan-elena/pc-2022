package smart_room.centralized.agents;

public class Main {

    private static final double T = 0.5;

    public static void main(String[] args) {
        SuperLoopAgent agent = new SuperLoopAgent(T);
//        EventLoopAgent agent = new EventLoopAgent(T);
        agent.start();
    }
}