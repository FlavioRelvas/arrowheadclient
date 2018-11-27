/*
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

import eu.arrowhead.client.common.no_need_to_modify.Utility;
import eu.arrowhead.client.common.no_need_to_modify.exception.UnavailableServerException;
import eu.arrowhead.client.common.no_need_to_modify.model.ArrowheadSystem;
import static eu.arrowhead.client.consumer.ConsumerMain.pool;
import static eu.arrowhead.client.consumer.ConsumerMain.q;
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

    private static final Logger log = Logger.getLogger(Stub.class.getName());

    public static final String MONITOR_URI = "http://127.0.0.1:8456/monitor/";
    private Instrumentation i;

    public Stub(ArrowheadSystem consumer, ArrowheadSystem provider) {
        //System.load("/home/flavio/NetBeansProjects/arrowhead-m3-funcional-clients/ArrowheadConsumerTest/src/main/java/eu/arrowhead/ArrowheadConsumer/lpcap/module.so");
        this.consumer = consumer;
        this.provider = provider;

    }

    public <T> Response sendRequestM(String uri, String method, T payload) {
        q = teste.createQueue(1000);
        pool.execute(new CaptureThread(q));
        startTime = System.nanoTime();
        Instant st = Instant.now();
        System.out.println(Utility.toPrettyJson(null, Entity.json(payload)));
        try {
            Response r = Utility.sendRequest(uri, method, payload);
            endTime = System.nanoTime();
            Instant et = Instant.now();

            MultivaluedMap<String, String> header = r.getStringHeaders();
            int cl = 0;
            for (Map.Entry<String, List<String>> entry : header.entrySet()) {
                for (String s : entry.getValue()) {
                    //System.out.println(s);
                    cl += s.length();
                }
                //System.out.println(entry.getKey());
                cl += entry.getKey().length();
            }
            cl += r.getLength();
            //System.out.println("Size " + cl);
            contentLength = r.getLength();
            //System.out.println("ST: " + st.toString() + "ET: " + et.toString());
            teste.stop(teste.getDescr());
            ConsumerMain.pool.execute(new CalculationThread(startTime, endTime, ConsumerMain.q, st, et, consumer, provider));
            return r;
        } catch (UnavailableServerException ex) {
            log.info("Could not get any response from server. Message:" + ex.getMessage());
            //ex.printStackTrace();
            System.out.println("Could not get any response from server.");
        }
        teste.stop(teste.getDescr());
        return null;
    }
}
