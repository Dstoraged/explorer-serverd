package com.imooc.Utils;

import java.util.LinkedList;

public class MessageQueue {
  private LinkedList<Object> items = new LinkedList();
  
  public Object putq(Object element) {
    synchronized (this) {
      this.items.add(element);
      notifyAll();
      return element;
    } 
  }
  
  public Object putqInTop(Object element) {
    synchronized (this) {
      if (this.items.size() > 0) {
        this.items.add(0, element);
      } else {
        this.items.add(element);
      } 
      notifyAll();
      return element;
    } 
  }
  
  public Object getq() throws InterruptedException {
    synchronized (this) {
      while (empty())
        wait(10000L); 
      return this.items.removeFirst();
    } 
  }
  
  public Object tryGetq() {
    synchronized (this) {
      if (empty())
        return null; 
      return this.items.removeFirst();
    } 
  }
  
  public Object getq(long timeout) throws InterruptedException {
    synchronized (this) {
      if (empty())
        wait(timeout); 
      if (empty())
        return null; 
      return this.items.removeFirst();
    } 
  }
  
  public int size() {
    return this.items.size();
  }
  
  public boolean empty() {
    return (size() == 0);
  }
  
  public void clear() {
    this.items.clear();
  }
  
  public void removeAll() {
    synchronized (this) {
      this.items.clear();
    } 
  }
  
  public Object[] getAll() {
    synchronized (this) {
      return this.items.toArray();
    } 
  }
}