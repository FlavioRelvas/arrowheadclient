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

package eu.arrowhead.client.consumer.lpcap;

public class teste {
  public static void setDev(String value) {
    testeJNI.dev_set(value);
  }

  public static String getDev() {
    return testeJNI.dev_get();
  }

  public static void setDescr(SWIGTYPE_p_pcap_t value) {
    testeJNI.descr_set(SWIGTYPE_p_pcap_t.getCPtr(value));
  }

  public static SWIGTYPE_p_pcap_t getDescr() {
    long cPtr = testeJNI.descr_get();
    return (cPtr == 0) ? null : new SWIGTYPE_p_pcap_t(cPtr, false);
  }

  public static void setFilter(String value) {
    testeJNI.filter_set(value);
  }

  public static String getFilter() {
    return testeJNI.filter_get();
  }

  public static Queue createQueue(int maxElements) {
    long cPtr = testeJNI.createQueue(maxElements);
    return (cPtr == 0) ? null : new Queue(cPtr, false);
  }

  public static void Dequeue(Queue Q) {
    testeJNI.Dequeue(Queue.getCPtr(Q), Q);
  }

  public static String front(Queue Q) {
    return testeJNI.front(Queue.getCPtr(Q), Q);
  }

  public static void Enqueue(Queue Q, String element) {
    testeJNI.Enqueue(Queue.getCPtr(Q), Q, element);
  }

  public static void handleDev(String netif) {
    testeJNI.handleDev(netif);
  }

  public static void handleDescr() {
    testeJNI.handleDescr();
  }

  public static void capture(Queue q) {
    testeJNI.capture(Queue.getCPtr(q), q);
  }

  public static void stop(SWIGTYPE_p_pcap_t descr) {
    testeJNI.stop(SWIGTYPE_p_pcap_t.getCPtr(descr));
  }

  public static void capture1(Queue q) {
    testeJNI.capture1(Queue.getCPtr(q), q);
  }

}
