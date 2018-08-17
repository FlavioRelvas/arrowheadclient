/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.arrowhead.client.consumer.lpcap;

/**
 *
 * @author root
 */
public class CaptureThread implements Runnable {

    Queue q;

    public CaptureThread(Queue q) {
        this.q = q;
    }

    @Override
    public void run() {
        teste.capture(q);
    }

}
