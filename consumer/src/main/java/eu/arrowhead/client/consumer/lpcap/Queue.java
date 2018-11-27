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

public class Queue {
  private long swigCPtr;
  protected boolean swigCMemOwn;

  protected Queue(long cPtr, boolean cMemoryOwn) {
    swigCMemOwn = cMemoryOwn;
    swigCPtr = cPtr;
  }

  protected static long getCPtr(Queue obj) {
    return (obj == null) ? 0 : obj.swigCPtr;
  }

  protected void finalize() {
    delete();
  }

  public synchronized void delete() {
    if (swigCPtr != 0) {
      if (swigCMemOwn) {
        swigCMemOwn = false;
        testeJNI.delete_Queue(swigCPtr);
      }
      swigCPtr = 0;
    }
  }

  public void setCapacity(int value) {
    testeJNI.Queue_capacity_set(swigCPtr, this, value);
  }

  public int getCapacity() {
    return testeJNI.Queue_capacity_get(swigCPtr, this);
  }

  public void setSize(int value) {
    testeJNI.Queue_size_set(swigCPtr, this, value);
  }

  public int getSize() {
    return testeJNI.Queue_size_get(swigCPtr, this);
  }

  public void setFront(int value) {
    testeJNI.Queue_front_set(swigCPtr, this, value);
  }

  public int getFront() {
    return testeJNI.Queue_front_get(swigCPtr, this);
  }

  public void setRear(int value) {
    testeJNI.Queue_rear_set(swigCPtr, this, value);
  }

  public int getRear() {
    return testeJNI.Queue_rear_get(swigCPtr, this);
  }

  public void setElements(SWIGTYPE_p_p_char value) {
    testeJNI.Queue_elements_set(swigCPtr, this, SWIGTYPE_p_p_char.getCPtr(value));
  }

  public SWIGTYPE_p_p_char getElements() {
    long cPtr = testeJNI.Queue_elements_get(swigCPtr, this);
    return (cPtr == 0) ? null : new SWIGTYPE_p_p_char(cPtr, false);
  }

  public Queue() {
    this(testeJNI.new_Queue(), true);
  }

}
