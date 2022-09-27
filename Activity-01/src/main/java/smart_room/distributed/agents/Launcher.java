package smart_room.distributed.agents;

import io.vertx.core.Vertx;

public class Launcher {

    private static final double T = 0.5;

    public static void main(String[] args) {
        final Vertx vertx = Vertx.vertx();
        final LumSensorAgent lumSensorAgent = new LumSensorAgent();
        final PresDetectAgent presDetectAgent = new PresDetectAgent();
        final LightAgent lightAgent = new LightAgent(T);

        vertx.deployVerticle(lightAgent);
        vertx.deployVerticle(lumSensorAgent);
        vertx.deployVerticle(presDetectAgent);
    }
}
