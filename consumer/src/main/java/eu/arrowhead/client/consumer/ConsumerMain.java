/*
 *  Copyright (c) 2018 AITIA International Inc.
 *
 *  This work is part of the Productive 4.0 innovation project, which receives grants from the
 *  European Commissions H2020 research and innovation programme, ECSEL Joint Undertaking
 *  (project no. 737459), the free state of Saxony, the German Federal Ministry of Education and
 *  national funding authorities from involved countries.

 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, you can obtain one at http://mozilla.org/MPL/2.0/.
 *
 * This work was partially supported by National Funds through FCT/MCTES (Portuguese Foundation
 * for Science and Technology), within the CISTER Research Unit (CEC/04234) and also by
 * Grant nr. 737459 Call H2020-ECSEL-2016-2-IA-two-stage 
 * ISEP/CISTER, Polytechnic Institute of Porto.
 * Luis Lino Ferreira (llf@isep.ipp.pt), Flávio Relvas (flaviofrelvas@gmail.com),
 * Michele Albano (mialb@isep.ipp.pt), Rafael Teles Da Rocha (rtdrh@isep.ipp.pt)
 */
package eu.arrowhead.client.consumer;

import eu.arrowhead.client.common.can_be_modified.model.TemperatureReadout;
import eu.arrowhead.client.common.no_need_to_modify.Utility;
import eu.arrowhead.client.common.no_need_to_modify.exception.ArrowheadException;
import eu.arrowhead.client.common.no_need_to_modify.misc.TypeSafeProperties;
import eu.arrowhead.client.common.no_need_to_modify.model.ArrowheadService;
import eu.arrowhead.client.common.no_need_to_modify.model.ArrowheadSystem;
import eu.arrowhead.client.common.no_need_to_modify.model.OrchestrationResponse;
import eu.arrowhead.client.common.no_need_to_modify.model.ServiceRequestForm;
import eu.arrowhead.client.consumer.lpcap.CaptureThread;
import eu.arrowhead.client.consumer.lpcap.Queue;
import eu.arrowhead.client.consumer.lpcap.teste;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLContext;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.jersey.SslConfigurator;

public class ConsumerMain {

    static {
        System.load("/media/flavio/Data/Documentos/PESTI/CISTER/Repositories/arrowhead github/arrowheadclient/consumer/src/main/java/eu/arrowhead/client/consumer/lpcap/module.so");
    }

    private static boolean isSecure;
    private static String orchestratorUrl;
    private static final TypeSafeProperties props = Utility.getProp("app.properties");
    private static Stub s;
    static boolean flag = true;

    private static int POOL_SIZE = 5;
    private static final int MAX_QUEUE_SIZE = 100;
    private static final String NETWORK_INTEFACE = "enp4s0";
    public static InetAddress ip = null;

    public static ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);

    public static Queue q;

    private static ArrowheadSystem consumer;
    private static ArrowheadSystem provider;

    private static final Logger log = Logger.getLogger(ConsumerMain.class.getName());

    public static void main(String[] args) {

        //Prints the working directory for extra information. Working directory should always contain a config folder with the app.properties file!
        System.out.println("Working directory: " + System.getProperty("user.dir"));

        //Compile the URL for the orchestration request.
        getOrchestratorUrl(args);

        //Start a timer, to measure the speed of the Core Systems and the provider application system.
        long startTime = System.currentTimeMillis();

        //Compile the payload, that needs to be sent to the Orchestrator - THIS METHOD SHOULD BE MODIFIED ACCORDING TO YOUR NEEDS
        ServiceRequestForm srf = compileSRF();

        //Sending the orchestration request and parsing the response
        String providerUrl = sendOrchestrationRequest(srf);
        try {
            NetworkInterface ninf = NetworkInterface.getByName(NETWORK_INTEFACE);
            Enumeration<InetAddress> addresses = ninf.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                if (ip.getClass() == Inet4Address.class) {
                    break;
                }
            }

            pool = Executors.newFixedThreadPool(POOL_SIZE);
            if (ip != null) {
                teste.handleDev(NETWORK_INTEFACE);
                teste.handleDescr();
                teste.setFilter("(dst host " + provider.getAddress() + " && port " + provider.getPort() + ") || (dst host " + ip.toString().replace("/", "") + " && (src host " + provider.getAddress() + " && port " + provider.getPort() + "))");
                System.out.println("FILTER: " + teste.getFilter());

            }
        } catch (SocketException ex) {

        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                while (flag) {
                    try {
                        //Connect to the provider, consuming its service - THIS METHOD SHOULD BE MODIFIED ACCORDING TO YOUR USE CASE
                        consumeService(providerUrl);

                        //Printing out the elapsed time during the orchestration and service consumption
                        //long endTime = System.currentTimeMillis();
                        //System.out.println("Orchestration and Service consumption response time: " + Long.toString(endTime - startTime));
                        //flag = false;
                        Thread.sleep(300);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConsumerMain.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });
        t.start();

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String input = "";
        try {
            while (!input.equals("stop")) {
                input = br.readLine();
            }
            br.close();
            flag = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            t.join();
        } catch (InterruptedException ex) {
            Logger.getLogger(ConsumerMain.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Releasing resources");
        releaseResources();

    }

    //Compiles the payload for the orchestration request
    private static ServiceRequestForm compileSRF() {
        /*
      ArrowheadSystem: systemName, (address, port, authenticationInfo)
      Since this Consumer skeleton will not receive HTTP requests (does not provide any services on its own),
      the address, port and authenticationInfo fields can be set to anything.
      SystemName can be an arbitrarily chosen name, which makes sense for the use case.
         */

        String systemName = props.getProperty("system_name");

        ArrowheadSystem consumer = new ArrowheadSystem(systemName, "localhost", 0, "null");
        consumer.setId(props.getIntProperty("system_id", 0));

        ConsumerMain.consumer = consumer;

        //You can put any additional metadata you look for in a Service here (key-value pairs)
        Map<String, String> metadata = new HashMap<>();
        metadata.put("unit", "watt");
        if (isSecure) {
            //This is a mandatory metadata when using TLS, do not delete it
            metadata.put("security", "token");
        }
        /*
      ArrowheadService: serviceDefinition (name), interfaces, metadata
      Interfaces: supported message formats (e.g. JSON, XML, JSON-SenML), a potential provider has to have at least 1 match,
      so the communication between consumer and provider can be facilitated.
         */
        ArrowheadService service = new ArrowheadService("Power", Collections.singletonList("JSON"), metadata);

        //Some of the orchestrationFlags the consumer can use, to influence the orchestration process
        Map<String, Boolean> orchestrationFlags = new HashMap<>();
        //When true, the orchestration store will not be queried for "hard coded" consumer-provider connections
        orchestrationFlags.put("overrideStore", true);
        //When true, the Service Registry will ping every potential provider, to see if they are alive/available on the network
        orchestrationFlags.put("pingProviders", false);
        //When true, the Service Registry will only providers with the same exact metadata map as the consumer
        orchestrationFlags.put("metadataSearch", true);
        //When true, the Orchestrator can turn to the Gatekeeper to initiate interCloud orchestration, if the Local Cloud had no adequate provider
        //orchestrationFlags.put("enableInterCloud", true);
        orchestrationFlags.put("enableQoS", true);

        Map<String, String> requestQoS = new HashMap<>();
        requestQoS.put("bandwidth", props.getProperty("bandwidth"));

        //Build the complete service request form from the pieces, and return it
        ServiceRequestForm srf = new ServiceRequestForm.Builder(consumer).requestedService(service).orchestrationFlags(orchestrationFlags).requestedQoS(requestQoS).build();
        System.out.println("Service Request payload: " + Utility.toPrettyJson(null, srf));
        return srf;
    }

    private static void consumeService(String providerUrl) {
        /*
      Sending request to the provider, to the acquired URL. The method type and payload should be known beforehand.
      If needed, compile the request payload here, before sending the request.
      Supported method types at the moment: GET, POST, PUT, DELETE
         */
        Response getResponse = s.sendRequestM(providerUrl, "GET", null);
        if (getResponse == null) {
            return;
        }
        /*
      Parsing the response from the provider here. This code prints an error message, if the answer is not in the expected JSON format, but custom
      error handling can also be implemented here. For example the Orchestrator will send back a JSON with the structure of the eu.arrowhead.client
      .common.exception.ErrorMessage class, and the errors from the Orchestrator are parsed this way.
         */
        TemperatureReadout readout = new TemperatureReadout();
        try {
            readout = getResponse.readEntity(TemperatureReadout.class);
            System.out.println("Provider Response payload: " + Utility.toPrettyJson(null, readout));
        } catch (RuntimeException e) {
            e.printStackTrace();
            System.out.println("Provider did not send the power readout in SenML format.");
        }
        if (readout.getE().get(0) == null) {
            System.out.println("Provider did not send any MeasurementEntry.");
        } else {
            System.out.println("The device power consumption is " + readout.getE().get(0).getV() + " watts.");
            JLabel label = new JLabel("The indoor temperature is " + readout.getE().get(0).getV() + " degrees celsius.");
            label.setFont(new Font("Arial", Font.BOLD, 18));
            //JOptionPane.showMessageDialog(null, label, "Provider Response", JOptionPane.INFORMATION_MESSAGE);
        }

    }

    /*
      Methods that should be modified to your use case ↑
   ----------------------------------------------------------------------------------------------------------------------------------
      Methods that do not need to be modified ↓
     */
    //DO NOT MODIFY - Gets the correct URL where the orchestration requests needs to be sent (from app.properties config file + command line argument)
    private static void getOrchestratorUrl(String[] args) {
        String orchAddress = props.getProperty("orch_address", "0.0.0.0");
        int orchInsecurePort = props.getIntProperty("orch_insecure_port", 8440);
        int orchSecurePort = props.getIntProperty("orch_secure_port", 8441);

        for (String arg : args) {
            if (arg.equals("-tls")) {
                isSecure = true;
                SslConfigurator sslConfig = SslConfigurator.newInstance().trustStoreFile(props.getProperty("truststore"))
                        .trustStorePassword(props.getProperty("truststorepass"))
                        .keyStoreFile(props.getProperty("keystore")).keyStorePassword(props.getProperty("keystorepass"))
                        .keyPassword(props.getProperty("keypass"));
                SSLContext sslContext = sslConfig.createSSLContext();
                Utility.setSSLContext(sslContext);
                break;
            }
        }

        if (isSecure) {
            orchestratorUrl = Utility.getUri(orchAddress, orchSecurePort, "orchestrator/orchestration", true, false);
        } else {
            orchestratorUrl = Utility.getUri(orchAddress, orchInsecurePort, "orchestrator/orchestration", false, false);
        }
    }

    /* NO NEED TO MODIFY (for basic functionality)
     Sends the orchestration request to the Orchestrator, and compiles the URL for the first provider received from the OrchestrationResponse */
    private static String sendOrchestrationRequest(ServiceRequestForm srf) {
        //Sending a POST request to the orchestrator (URL, method, payload)
        Response postResponse = Utility.sendRequest(orchestratorUrl, "POST", srf);
        //Parsing the orchestrator response
        OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
        System.out.println("Orchestration Response payload: " + Utility.toPrettyJson(null, orchResponse));
        if (orchResponse.getResponse().isEmpty()) {
            throw new ArrowheadException("Orchestrator returned with 0 Orchestration Forms!");
        }

        //Getting the first provider from the response
        ArrowheadSystem provider = orchResponse.getResponse().get(0).getProvider();
        ConsumerMain.provider = provider;
        s = new Stub(srf.getRequesterSystem(), provider);
        String serviceURI = orchResponse.getResponse().get(0).getServiceURI();
        //Compiling the URL for the provider
        UriBuilder ub = UriBuilder.fromPath("").host(provider.getAddress()).scheme("http");
        if (serviceURI != null) {
            ub.path(serviceURI);
        }
        if (provider.getPort() > 0) {
            ub.port(provider.getPort());
        }
        if (orchResponse.getResponse().get(0).getService().getServiceMetadata().containsKey("security")) {
            ub.scheme("https");
            ub.queryParam("token", orchResponse.getResponse().get(0).getAuthorizationToken());
            ub.queryParam("signature", orchResponse.getResponse().get(0).getSignature());
        }
        System.out.println("Received provider system URL: " + ub.toString());
        return ub.toString();
    }

    private static void releaseResources() {
        ConsumerMain.pool.shutdownNow();
        String systemName = props.getProperty("system_name");

        ArrowheadSystem consumer = new ArrowheadSystem(systemName, "localhost", 0, "null");
        consumer.setId(props.getIntProperty("system_id", 0));

        ArrowheadService service = new ArrowheadService("InsecureQoSManager", Collections.singletonList("JSON"), null);

        //Build the complete service request form from the pieces, and return it
        Map<String, Boolean> orchestrationFlags = new HashMap<>();
        //When true, the orchestration store will not be queried for "hard coded" consumer-provider connections
        orchestrationFlags.put("overrideStore", true);

        ServiceRequestForm srf = new ServiceRequestForm.Builder(consumer).requestedService(service).orchestrationFlags(orchestrationFlags).build();
        System.out.println("Service Request payload: " + Utility.toPrettyJson(null, srf));

        Response postResponse = Utility.sendRequest(orchestratorUrl, "POST", srf);
        //Parsing the orchestrator response
        OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
        System.out.println("Orchestration Response payload: " + Utility.toPrettyJson(null, orchResponse));
        if (orchResponse.getResponse().isEmpty()) {
            throw new ArrowheadException("Orchestrator returned with 0 Orchestration Forms!");
        }

        //Getting the first provider from the response
        ArrowheadSystem provider = orchResponse.getResponse().get(0).getProvider();
        String providerUrl = Utility.getUri(provider.getAddress(), provider.getPort(), orchResponse.getResponse().get(0).getServiceURI() + "/release", false, false);

        Response releaseResponse = Utility.sendRequest(providerUrl, "POST", consumer);
        if (releaseResponse.getStatusInfo().getFamily() != Response.Status.Family.SUCCESSFUL) {
            throw new ArrowheadException("Unable to release resources.");
        }
    }

}
