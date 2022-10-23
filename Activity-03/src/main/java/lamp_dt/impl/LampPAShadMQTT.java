package lamp_dt.impl;

import io.vertx.mqtt.MqttClient;
import lamp_thing.impl.LampThingAPI;
import io.netty.handler.codec.mqtt.MqttQoS;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;

/**
 * Lamp Physical Asset shadowing module, based on MQTT
 * 
 * @author aricci
 *
 */
public class LampPAShadMQTT {

    private MqttClient client;	
	private String host;
	private int port;
	private String shadowingTopicFromPAtoDT;
	private String shadowingTopicFromDTtoPA;
	private String LampThingDTId;
	private Vertx vertx;
    private final int qos = 1;
	
	private LampThingAPI model;
	
	public LampPAShadMQTT(LampThingAPI model, String LampThingDTId, String host, int port, Vertx vertx) throws Exception {
		this.host = host;
		this.port = port;
		this.model = model;
		this.vertx = vertx;
		this. LampThingDTId =  LampThingDTId;
	}
	
	public void setupAdapter(Promise<Void> promise) {
		Future<JsonObject> tdf = this.model.getTD();
    	tdf.onComplete(td -> {
    		try {
    			String thingId = td.result().getString("id");

		        this.shadowingTopicFromPAtoDT = "dt/" +  LampThingDTId +"/shadowing";
		        this.shadowingTopicFromDTtoPA = "pa/" +  thingId +"/shadowing";
	
		       	client = MqttClient.create(vertx);
		    	client.connect(port, host, c -> {
					log("MQTT PA shadowing module connected - in topic: " + shadowingTopicFromDTtoPA + " - out topic: " + shadowingTopicFromPAtoDT);
		    
					// this handler is for managing shadowing messages 
					// towards the PA from the DT
					client.publishHandler(s -> {
		    			// System.out.println("There are new message in topic: " + s.topicName());
		    			// System.out.println("Content(as string) of the message: " + s.payload().toString());
		    			// System.out.println("QoS: " + s.qosLevel());
				    	this.vertx.eventBus().publish("shadowing", new String(s.payload().toString()));    	
					})
					.subscribe(shadowingTopicFromDTtoPA, qos);
		    	
		    	
		    	});   
		    	    		
		    	// this.getVertx().eventBus().consumer("requests", this::handleRequests);
		        /* handler to process MQTT msgs on the event-loop */ 
		        vertx.eventBus().consumer("shadowing", this::handleShadowing);   

		        // in the thing part, I subscribe the model, and I sending the message to the other module on the dt
				this.model.subscribe(ev -> {
					JsonObject syncMsg = new JsonObject();
					syncMsg.put("msg", "sync-event");
					syncMsg.put("syncData",  ev);
					sendMsg(shadowingTopicFromPAtoDT,syncMsg); //sending msg for a topic
				});
		        promise.complete();
	        } catch (Exception ex) {
	        	ex.printStackTrace();
	        	promise.fail(ex.getMessage());
	        }
    	});
	}

	/*
	private void handleRequests(Message<String> msg) {
		log("Processing new request: " + msg.body());
    	JsonObject request = new JsonObject(msg.body());
    	String reqType = request.getString("request");
    	String consumerReplyTopic = request.getString("replyTopic");
    	long reqId = request.getLong("reqId");
    	
		JsonObject reply = new JsonObject();
		reply.put("reqId", reqId);
		reply.put("request", reqType);
		
		if (reqType.equals("readState")) {    	
    		reply.put("reply", "ok");
    		Future<String> status = this.getModel().getState();
    		status.onSuccess(res -> {
    			reply.put("state", res);
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("readTD")) {    	
    		reply.put("reply", "ok");
    		Future<JsonObject> td = this.getModel().getTD();
    		td.onSuccess(res -> {
    			reply.put("td", res);
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("on")) {
    		Future<Void> fut = this.getModel().on();
    		fut.onSuccess(ret -> {
	    		reply.put("reply", "ok");
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else if (reqType.equals("off")) {
    		Future<Void> fut = this.getModel().off();
    		fut.onSuccess(ret -> {
	    		reply.put("reply", "ok");
    			sendMsg(consumerReplyTopic, reply);		
    		});
    	} else {
    		reply.put("reply", "error");
			sendMsg(consumerReplyTopic, reply);		
    	}
	}
	*/
	
	/*
	 * Messages from the DT, and then update the digital twin
	 */	
	private void handleShadowing(Message<String> msg) {
		log("Processing new shadowing: " + msg.body());
		JsonObject obj = new JsonObject(msg.body());
		String ev = obj.getString("type");
		if (ev.equals("syncRequest")) {
			JsonObject syncMsg = new JsonObject();
			syncMsg.put("msg", "sync-state");
			JsonObject data = new JsonObject();			
			syncMsg.put("syncData",  data);
			model
				.getState()
				.onComplete(st -> {
					data.put("state", st.result());
					data.put("timestamp", System.currentTimeMillis());
					sendMsg(shadowingTopicFromPAtoDT,syncMsg);
				});
		} else {
			log("unknown shadowing event: " + ev);
		}    	
	}	
	
	
	private void sendMsg(String topic, JsonObject msg) {
		client.publish(topic,
				  Buffer.buffer(msg.encode()),
				  MqttQoS.AT_LEAST_ONCE,
				  false,
				  false);
	}

	protected void log(String msg) {
		System.out.println("[LampPAShadMQTT]["+System.currentTimeMillis()+"] " + msg);
	}
	
	void notifyRequest(String req) {
    	vertx.eventBus().publish("requests", req);    	
	}
}
