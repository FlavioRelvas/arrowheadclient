/*
 * Copyright (c) 2018 AITIA International Inc.
 *
 * This work is part of the Productive 4.0 innovation project, which receives grants from the
 * European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 * (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 * national funding authorities from involved countries.
 */
package eu.arrowhead.ArrowheadConsumer;

import eu.arrowhead.ArrowheadConsumer.model.ArrowheadService;
import eu.arrowhead.ArrowheadConsumer.model.ArrowheadSystem;
import eu.arrowhead.ArrowheadConsumer.model.OrchestrationResponse;
import eu.arrowhead.ArrowheadConsumer.model.ServiceRequestForm;
import eu.arrowhead.ArrowheadConsumer.model.TemperatureReadout;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

public class ConsumerMain {

    private static boolean isSecure = false;
    private static final String ORCH_URI = Utility.getProp().getProperty("orch_uri", "http://0.0.0.0:8440/orchestrator/orchestration");

    public static void main(String[] args) {
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        if (ORCH_URI.startsWith("https")) {
            Utility.isUrlValid(ORCH_URI, true);
        } else {
            Utility.isUrlValid(ORCH_URI, false);
        }

        for (int i = 0; i < args.length; ++i) {
            if (args[i].equals("-m")) {
                ++i;
                switch (args[i]) {
                    case "insecure":
                        isSecure = false;
                        break;
                    case "secure":
                        isSecure = true;
                        break;
                    default:
                        throw new AssertionError("Unknown security level: " + args[i]);
                }
            }
        }

        //Payload compiling
        ServiceRequestForm srf = compileSRF();
        System.out.println("Service Request payload: " + Utility.toPrettyJson(null, srf));

        //for (int i = 0; i < 1000; i++) {
        //Sending request to the orchestrator, parsing the response
        long orchStartTime = System.nanoTime();
        Response postResponse = Utility.sendRequest(ORCH_URI, "POST", srf);
        OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
        long orchTime = System.nanoTime();

        System.out.println("Orchestration Response payload: " + Utility.toPrettyJson(null, orchResponse));

        ArrowheadSystem provider = orchResponse.getResponse().get(0).getProvider();
        String serviceURI = orchResponse.getResponse().get(0).getServiceURI();
        UriBuilder ub = UriBuilder.fromPath("").host(provider.getAddress()).path(serviceURI).scheme("http");
        if (provider.getPort() > 0) {
            ub.port(provider.getPort());
        }
        if (orchResponse.getResponse().get(0).getService().getServiceMetadata().containsKey("security")) {
            ub.scheme("https");
            ub.queryParam("token", orchResponse.getResponse().get(0).getAuthorizationToken());
            ub.queryParam("signature", orchResponse.getResponse().get(0).getSignature());
        }
        System.out.println("Received provider system URL: " + ub.toString());

        // for (int j = 0; j < 1000; j++) {
        //Sending request to the provider, parsing the answer
        //long servStartTime = System.nanoTime();
        Stub s = new Stub(srf.getRequesterSystem(), provider);

        Response getResponse = s.sendRequestM(ub.toString(), "GET", null);
        
        //System.out.println("Producer Response payload: " + Utility.toPrettyJson(null, getResponse));
        System.out.println("Response length:" + getResponse.getLength());
        TemperatureReadout readout = new TemperatureReadout();
        try {
            readout = getResponse.readEntity(TemperatureReadout.class);
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Provider did not send the power consumption readout in SenML format.");
        }
        if (readout.getE().get(0) == null) {
            System.out.println("Provider did not send any MeasurementEntry.");
        } else {
            //long endTime = System.nanoTime();
            //String consumerTime = Long.toString(endTime - servStartTime);
            //Utility.logData("consumerTime", consumerTime);
            System.out.println("The power consumption is " + readout.getE().get(0).getV() + " watts.");
            System.out.println("Orchestration response time: " + Long.toString(orchTime - orchStartTime));
            //System.out.println("Service consumption response time: " + Long.toString(endTime - servStartTime));
        }
        //   }
        Utility.logData("orchTime", Long.toString(orchTime - orchStartTime));
    }

    private static ServiceRequestForm compileSRF() {
        ArrowheadSystem consumer = new ArrowheadSystem("client2", "0.0.0.0", 0, "null");
        consumer.setId(30);
        
        Map<String, String> metadata = new HashMap<>();
        metadata.put("unit", "watt");
        if (isSecure) {
            metadata.put("security", "token");
        }
        ArrowheadService service = new ArrowheadService("Power", Collections.singletonList("json"), metadata);

        Map<String, Boolean> orchestrationFlags = new HashMap<>();
        orchestrationFlags.put("overrideStore", true);
        orchestrationFlags.put("pingProviders", false);
        orchestrationFlags.put("metadataSearch", true);
        orchestrationFlags.put("enableQoS", true);

        Map<String, String> requestQoS = new HashMap<>();
        requestQoS.put("bandwidth", "0.2B/s");

        return new ServiceRequestForm.Builder(consumer).requestedService(service).orchestrationFlags(orchestrationFlags).requestedQoS(requestQoS).build();
    }
}
