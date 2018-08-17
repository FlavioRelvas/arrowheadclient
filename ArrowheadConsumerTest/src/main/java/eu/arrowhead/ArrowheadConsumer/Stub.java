/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.ArrowheadConsumer;

import static eu.arrowhead.ArrowheadConsumer.Utility.toPrettyJson;
import eu.arrowhead.ArrowheadConsumer.model.ArrowheadSystem;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.ws.rs.core.Response;
import java.lang.instrument.Instrumentation;
import java.util.List;
import java.util.Map;
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

    public Stub(ArrowheadSystem consumer, ArrowheadSystem provider) {
        this.consumer = consumer;
        this.provider = provider;
    }

    public <T> Response sendRequestM(String uri, String method, T payload) {
        startTime = System.nanoTime();
        System.out.println(toPrettyJson(null, Entity.json(payload)));
        Response r = Utility.sendRequest(uri, method, payload);
        
        
        endTime = System.nanoTime();
        MultivaluedMap<String,String> header = r.getStringHeaders();
        int cl = 0;
        for(Map.Entry<String,List<String>> entry : header.entrySet()){
            for(String s : entry.getValue()){
                System.out.println(s);
                cl+=s.length();
            }
            System.out.println(entry.getKey());
            cl += entry.getKey().length();
        }
        cl +=r.getLength();
        System.out.println("Size " + cl);
        contentLength = r.getLength();
        pool.execute(new CalculationThread(startTime, endTime, contentLength, consumer, provider));
        return r;
    }
}
