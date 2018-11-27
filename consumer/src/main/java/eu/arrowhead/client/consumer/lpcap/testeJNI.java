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

public class testeJNI {
  public final static native void Queue_capacity_set(long jarg1, Queue jarg1_, int jarg2);
  public final static native int Queue_capacity_get(long jarg1, Queue jarg1_);
  public final static native void Queue_size_set(long jarg1, Queue jarg1_, int jarg2);
  public final static native int Queue_size_get(long jarg1, Queue jarg1_);
  public final static native void Queue_front_set(long jarg1, Queue jarg1_, int jarg2);
  public final static native int Queue_front_get(long jarg1, Queue jarg1_);
  public final static native void Queue_rear_set(long jarg1, Queue jarg1_, int jarg2);
  public final static native int Queue_rear_get(long jarg1, Queue jarg1_);
  public final static native void Queue_elements_set(long jarg1, Queue jarg1_, long jarg2);
  public final static native long Queue_elements_get(long jarg1, Queue jarg1_);
  public final static native long new_Queue();
  public final static native void delete_Queue(long jarg1);
  public final static native void dev_set(String jarg1);
  public final static native String dev_get();
  public final static native void descr_set(long jarg1);
  public final static native long descr_get();
  public final static native void filter_set(String jarg1);
  public final static native String filter_get();
  public final static native long createQueue(int jarg1);
  public final static native void Dequeue(long jarg1, Queue jarg1_);
  public final static native String front(long jarg1, Queue jarg1_);
  public final static native void Enqueue(long jarg1, Queue jarg1_, String jarg2);
  public final static native void handleDev(String jarg1);
  public final static native void handleDescr();
  public final static native void capture(long jarg1, Queue jarg1_);
  public final static native void stop(long jarg1);
  public final static native void capture1(long jarg1, Queue jarg1_);
}
