package smart_room.distributed.agents;

import io.vertx.core.AbstractVerticle;
import io.vertx.mqtt.MqttClient;
import smart_room.distributed.LightDeviceSimulator;

import java.util.Map;

public class LightAgent extends AbstractVerticle {

    private static final String LIGHT_ID = "MyLight";
    final private LightDeviceSimulator lightDevice;
    final private double threshold;
    private double lumLevel = 0.0;
    private boolean presenceDetected = false;

    public LightAgent(double threshold) {
        this.threshold = threshold;
        lightDevice = new LightDeviceSimulator(LIGHT_ID);
        lightDevice.init();
    }

    @Override
    public void start() {
        MqttClient client = MqttClient.create(vertx);

        client.connect(1883, "broker.mqtt-dashboard.com", c ->
            client.publishHandler(s -> {
                final Topic topic = Topic.valueOf(s.topicName());
                switch (topic) {
                    case PRESENCE_EVENT:
                        presenceDetected = Boolean.parseBoolean(s.payload().toString());
                        break;
                    case LIGHT_LEVEL:
                        lumLevel = Double.parseDouble(s.payload().toString());
                        break;
                }
                checkLightState();
            }).subscribe(Map.of(Topic.PRESENCE_EVENT.name(), 2, Topic.LIGHT_LEVEL.name(), 2)));
    }

    private void checkLightState() {
        if (presenceDetected)  {
            if (lumLevel < threshold) {
                lightDevice.on();
            } else {
                lightDevice.off();
            }
        } else {
            lightDevice.off();
        }
    }
}
