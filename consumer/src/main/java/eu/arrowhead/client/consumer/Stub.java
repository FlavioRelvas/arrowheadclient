/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.client.consumer;


import eu.arrowhead.client.common.no_need_to_modify.Utility;
import eu.arrowhead.client.common.no_need_to_modify.model.ArrowheadSystem;
import eu.arrowhead.client.consumer.lpcap.CaptureThread;
import eu.arrowhead.client.consumer.lpcap.Queue;
import eu.arrowhead.client.consumer.lpcap.teste;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.Response;
import java.lang.instrument.Instrumentation;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MultivaluedMap;

/**
 *
 * @author Flavio
 */
public class Stub {

    // will static fields be bad here because of delay due to calculations?
    private long startTime;
    private long endTime;
    private int contentLength;
    private ArrowheadSystem consumer;
    private ArrowheadSystem provider;
    private static int POOL_SIZE = 5;
    public static ExecutorService pool = Executors.newFixedThreadPool(POOL_SIZE);
    public static final String MONITOR_URI = "http://127.0.0.1:8456/monitor/";
    private Instrumentation i;
    private final int MAX_QUEUE_SIZE = 100;
    private final String NETWORK_INTEFACE = "enp4s0";

    private Queue q = teste.createQueue(100);

    public Stub(ArrowheadSystem consumer, ArrowheadSystem provider) {
        //System.load("/home/flavio/NetBeansProjects/arrowhead-m3-funcional-clients/ArrowheadConsumerTest/src/main/java/eu/arrowhead/ArrowheadConsumer/lpcap/module.so");
        this.consumer = consumer;
        this.provider = provider;
        try {
            InetAddress ip = null;
            NetworkInterface ninf = NetworkInterface.getByName(NETWORK_INTEFACE);
            Enumeration<InetAddress> addresses = ninf.getInetAddresses();
            while (addresses.hasMoreElements()) {
                ip = addresses.nextElement();
                if (ip.getClass() == Inet4Address.class) {
                    break;
                }
            }
            if (ip != null) {
                teste.handleDev(NETWORK_INTEFACE);
                teste.handleDescr();
                teste.setFilter("(dst host " + provider.getAddress() + " && port " + provider.getPort() + ") || (dst host " + ip.toString().replace("/", "") + " && (src host " + provider.getAddress() + " && port " + provider.getPort() + "))");
                System.out.println("FILTER: " + teste.getFilter());
                pool.execute(new CaptureThread(q));
            }
        } catch (SocketException ex) {
            Logger.getLogger(Stub.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public <T> Response sendRequestM(String uri, String method, T payload) {
        startTime = System.nanoTime();
        Instant st = Instant.now();
        System.out.println(Utility.toPrettyJson(null, Entity.json(payload)));
        Response r = Utility.sendRequest(uri, method, payload);
        endTime = System.nanoTime();
        Instant et = Instant.now();

        MultivaluedMap<String, String> header = r.getStringHeaders();
        int cl = 0;
        for (Map.Entry<String, List<String>> entry : header.entrySet()) {
            for (String s : entry.getValue()) {
                System.out.println(s);
                cl += s.length();
            }
            System.out.println(entry.getKey());
            cl += entry.getKey().length();
        }
        cl += r.getLength();
        System.out.println("Size " + cl);
        contentLength = r.getLength();
        System.out.println("ST: " + st.toString() + "ET: " + et.toString());
        pool.execute(new CalculationThread(startTime, endTime, q, st, et, consumer, provider));
        return r;
    }
}
