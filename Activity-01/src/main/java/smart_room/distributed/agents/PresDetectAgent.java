package smart_room.distributed.agents;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.distributed.PresDetectSensorSimulator;

public class PresDetectAgent extends AbstractVerticle {

    private static final String SENSOR_ID = "MyPIR";
    final private PresDetectSensorSimulator sensor;

    public PresDetectAgent() {
        sensor = new PresDetectSensorSimulator(SENSOR_ID);
        sensor.init();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(1883, "broker.mqtt-dashboard.com", c ->
            sensor.register(ev ->
                client.publish(Topic.PRESENCE_EVENT.name(),
                    Buffer.buffer(String.valueOf(sensor.presenceDetected())),
                    MqttQoS.AT_LEAST_ONCE,
                    false,
                    false))
        );
    }
}
