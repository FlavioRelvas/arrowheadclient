/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.client.consumer;


import eu.arrowhead.client.common.no_need_to_modify.Utility;
import eu.arrowhead.client.common.no_need_to_modify.model.AddLogForm;
import eu.arrowhead.client.common.no_need_to_modify.model.ArrowheadSystem;
import eu.arrowhead.client.consumer.lpcap.Queue;
import eu.arrowhead.client.consumer.lpcap.teste;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Instant;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import javax.ws.rs.core.Response;

/**
 *
 * @author Flavio
 */
public class CalculationThread implements Runnable {

    private long startTime;
    private long endTime;
    private int contentLength = 0;
    private Queue q;
    private String producerIp;
    private ArrowheadSystem consumer;
    private ArrowheadSystem provider;
    private Instant st, et;

    public CalculationThread(long startTime, long endTime, Queue q, Instant st, Instant et, ArrowheadSystem consumer, ArrowheadSystem provider) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.q = q;
        this.st = st;
        this.et = et;
        this.consumer = consumer;
        this.provider = provider;
        this.producerIp = provider.getAddress();
    }

    @Override
    public void run() {
        try {
            Future<String> bandFuture = calculateBandwidth();
            Future<String> lossFuture = calculatePacketLoss(producerIp);
            Thread.sleep(1000);
            String bandwidth = bandFuture.get();
            String packetLoss = lossFuture.get();
            //System.out.println("StartTime:" + startTime * 0.0000000001);
            //System.out.println("EndTime:" + endTime * 0.0000000001);
            String duration = String.format("%.2f ms", (endTime - startTime) * 0.0000001);
            System.out.println("Duration: " + duration);
            System.out.println("Bandwidth: " + bandwidth);
            System.out.println("Packet Loss: " + packetLoss);
            Stub.pool.shutdown();
            HashMap<String, String> parameters = new HashMap();
            parameters.put("bandwidth", bandwidth);
            parameters.put("packet loss", packetLoss);
            parameters.put("duration", duration);

            AddLogForm adf = new AddLogForm(consumer, provider, parameters);
            System.out.println(Utility.toPrettyJson(null, adf));
            Response postResponse = Utility.sendRequest(Stub.MONITOR_URI + "newlog", "PUT", adf);
            //OrchestrationResponse orchResponse = postResponse.readEntity(OrchestrationResponse.class);
            if ((postResponse.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL)) {
                System.out.println("Log added successfully");
            }
        } catch (InterruptedException | ExecutionException ex) {
            System.out.println(ex);
        }
    }

    private Future<String> calculateBandwidth() {
        Future<String> f = Stub.pool.submit(() -> {
            Thread.sleep(1000);
            teste.stop(teste.getDescr());
            System.out.println("SIZE: " + q.getSize());
            while (q.getSize() > 0) {
                System.out.println("Packet: " + teste.front(q));
                contentLength += Integer.parseInt(teste.front(q).split(",")[2].trim());
                teste.Dequeue(q);
            }
            return String.format("%.2fb/s", contentLength / ((endTime - startTime) * Math.pow(10, -9)));
        });
        return f;
    }

    private Future<String> calculatePacketLoss(String ip) {
        Future<String> f = Stub.pool.submit(() -> {
            try {
                if (!ip.equals("127.0.0.1")) {
                    if (System.getProperty("os.name").toLowerCase().contains("win")) {
                        String lost = new String();
                        String delay = new String();
                        Process p = Runtime.getRuntime().exec("ping -n 4 " + ip);
                        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String str = new String();
                        while ((str = buf.readLine()) != null) {
                            if (str.contains("Packets")) {
                                int i = str.indexOf("(");
                                int j = str.indexOf("%");
                                lost = str.substring(i + 1, j + 1);
                            }
                        }
                        return lost;
                    } else {
                        String lost = new String();
                        String delay = new String();
                        Process p = Runtime.getRuntime().exec("ping -c 4" + ip);
                        p.waitFor();
                        BufferedReader buf = new BufferedReader(new InputStreamReader(p.getInputStream()));
                        String str = new String();
                        while ((str = buf.readLine()) != null) {
                            if (str.contains("packet loss")) {
                                int i = str.indexOf("received");
                                int j = str.indexOf("%");
                                lost = str.substring(i + 10, j + 1);
                            }
                        }
                        return lost;
                    }
                } else {
                    return "0% (is localhost)";
                }
            } catch (IOException ex) {
                System.out.println(ex);
                return "error";
            }
        });
        return f;
    }

}
