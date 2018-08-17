/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.ArrowheadConsumer.lpcap;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapBpfProgram;
import org.jnetpcap.PcapHandler;
import org.jnetpcap.PcapIf;

/**
 *
 * @author Flavio
 */
public class lpcap {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        //find network interfaces
        StringBuilder errbuf = new StringBuilder();
        List<PcapIf> ifs = new ArrayList<PcapIf>(); // Will hold list of devices
        int statusCode = Pcap.findAllDevs(ifs, errbuf);
        if (statusCode != Pcap.OK) {
            System.out.println("Error occured: " + errbuf.toString());
            return;
        }

        //open network interface for live capture
        int snaplen = 2048; // Truncate packet at this size
        int promiscous = Pcap.MODE_PROMISCUOUS;
        int timeout = 60 * 1000; // In milliseconds
        Pcap pcap
                = Pcap.openLive("any", snaplen, promiscous, timeout, errbuf);

        //compile and apply filter
        PcapBpfProgram filter = new PcapBpfProgram();
        String expression = "port 23";
        int optimize = 0; // 1 means true, 0 means false
        int netmask = 0;

        int r = pcap.compile(filter, expression, optimize, netmask);
        if (r != Pcap.OK) {
            System.out.println("Filter error: " + pcap.getErr());
        }
        pcap.setFilter(filter);

        //dispatcher to receive packets
        PcapHandler handler = new PcapHandler() {

            @Override
            public void nextPacket(Object userData, long caplen, int len, int seconds,
                    int usecs, ByteBuffer buffer) {

                PrintStream out = (PrintStream) userData;
                out.println("Packet captured on: " + new Date(seconds * 1000).toString());
            }
        };

        int cnt = 10; // Capture packet count
        PrintStream out = System.out; // Our custom object to send into the handler

        pcap.loop(cnt, handler, out); // Each packet will be dispatched to the handler

        pcap.close();
    }

}
