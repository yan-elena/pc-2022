package smart_room.distributed.agents;

import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.buffer.Buffer;
import io.vertx.mqtt.MqttClient;
import smart_room.Event;
import smart_room.distributed.LuminositySensorSimulator;

public class LumSensorAgent extends AbstractVerticle {

    private static final String SENSOR_ID = "MyLightSensor";
    final private LuminositySensorSimulator sensor;


    public LumSensorAgent() {
        sensor = new LuminositySensorSimulator(SENSOR_ID);
        sensor.init();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(1883, "broker.mqtt-dashboard.com", c ->
            sensor.register((Event ev) ->
                client.publish(Topic.LIGHT_LEVEL.name(),
                    Buffer.buffer(String.valueOf(sensor.getLuminosity())),
                    MqttQoS.AT_LEAST_ONCE,
                    false,
                    false))
        );
    }
}
