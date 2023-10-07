package com.imooc.Utils;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @TODO
 */
@Slf4j
public class AgentSvcTask extends Thread {
	  
	  private static ArrayList<AgentSvcTask> _svcs = new ArrayList<>();
	  
	  private AgentSvcTask next_;
	  
	  private MessageQueue queue_;
	  
	  private long startTime;
	  
	  private boolean isRunning;
	  
	  private long threadId;
	  
	  private Thread thread;
	  
	  public volatile Boolean shutdown;
	  
	  public AgentSvcTask() {
	    setName(getClass().getName());
	    this.shutdown = Boolean.valueOf(false);
	    this.queue_ = new MessageQueue();
	    setDaemon(true);
	    synchronized (_svcs) {
	      _svcs.add(this);
	    } 
	  }
	  
	  public boolean isRunning() {
	    return this.isRunning;
	  }
	  
	  public long getThreadId() {
	    return this.threadId;
	  }
	  
	  public Thread getThread() {
	    return this.thread;
	  }
	  
	  public MessageQueue getQueue() {
	    return this.queue_;
	  }
	  
	  public void shutdown() {
	    this.shutdown = Boolean.valueOf(true);
	  }
	  
	  public long getStartTime() {
	    return this.startTime;
	  }
	  
	  public void run() {
	    this.startTime = System.currentTimeMillis();
	    log.debug("SvcTask[" + getName() + "] begin to running.....");
	    this.isRunning = true;
	    this.thread = Thread.currentThread();
	    this.threadId = Thread.currentThread().getId();
	    try {
	      beforeTask();
	      runTask();
	      afterTask();
	    } catch (Exception exp) {
	      log.error("SvcTask[" + getName() + "] catch unexpection.  " + exp.getMessage(), exp);
	    } finally {
	      this.isRunning = false;
	      this.thread = null;
	      removeFromSvcs();
	    } 
	    log.debug("SvcTask[" + getName() + "] exit.");
	  }
	  
	  protected void removeFromSvcs() {
	    synchronized (_svcs) {
	      _svcs.remove(this);
	    } 
	  }
	  
	  public void init_svc() {}
	  
	  protected void runTask() {
	    long lasttime = getLastTime();
	    try {
	      while (!this.shutdown.booleanValue() && lasttime > 0L) {
	        Thread.sleep(lasttime);
	        loopRun();
	        lasttime = getLastTime();
	      } 
	    } catch (InterruptedException interruptedException) {}
	  }
	  
	  protected void afterTask() {}
	  
	  protected void beforeTask() {}
	  
	  protected void loopRun() {}
	  
	  protected long getLastTime() {
	    return 0L;
	  }
	  
	  public AgentSvcTask getNext() {
	    return this.next_;
	  }
	  
	  public void setNext(AgentSvcTask n) {
	    this.next_ = n;
	  }
	  
	  public Object putq(Object element) {
	    return this.queue_.putq(element);
	  }
	  
	  public Object getq() throws InterruptedException {
	    return this.queue_.getq();
	  }
	  
	  public Object getq(long timeout) throws InterruptedException {
	    return this.queue_.getq(timeout);
	  }
	  
	  public int getQueueSize() {
	    return this.queue_.size();
	  }
	  
	  public synchronized void quit() {
	    this.shutdown = Boolean.valueOf(true);
	    interrupt();
	  }
	  
	  public static String getAllSvcInfo() {
	    StringBuilder sb = new StringBuilder();
	    synchronized (_svcs) {
	      for (int pos = 0; pos < _svcs.size(); pos++) {
	        AgentSvcTask svc = _svcs.get(pos);
	        sb.append("id: " + svc.getId() + " name:" + svc.getName() + " queue:" + svc.getQueueSize() + " " + svc
	            .additionInfo() + "\r\n");
	      } 
	    } 
	    sb.append("\r\n");
	    return sb.toString();
	  }
	  
	  public static String getAllSimpleSvcInfo() {
	    StringBuilder sb = new StringBuilder();
	    synchronized (_svcs) {
	      for (int pos = 0; pos < _svcs.size(); pos++) {
	        AgentSvcTask svc = _svcs.get(pos);
	        String str = svc.additionInfo();
	        if (svc.getQueueSize() != 0 || (str != null && str.length() != 0))
	          sb.append("id: " + svc.getId() + " name:" + svc.getName() + " queue:" + svc.getQueueSize() + " " + svc
	              .additionInfo() + "\r\n"); 
	      } 
	    } 
	    sb.append("\r\n");
	    return sb.toString();
	  }
	  
	  protected String additionInfo() {
	    return "";
	  }
	  
	  public static List<AgentSvcTask> allSvcs() {
	    synchronized (_svcs) {
	      return new ArrayList<>(_svcs);
	    } 
	  }
	  
	  public static void shutdownAll(boolean interruptSvc) {
	    synchronized (_svcs) {
	      for (AgentSvcTask svc : _svcs) {
	        if (svc.isRunning) {
	          svc.shutdown();
	          if (interruptSvc && svc.thread != null)
	            svc.thread.interrupt(); 
	        } 
	      } 
	    } 
	  }
	  
	  public static int countRunSvc() {
	    int count = 0;
	    synchronized (_svcs) {
	      for (AgentSvcTask svc : _svcs) {
	        if (svc.isRunning)
	          count++; 
	      } 
	    } 
	    return count;
	  }
	  
	  public static void waitAllQuit() {
	    waitAllQuit(0L);
	  }
	  
	  public static void waitAllQuit(long waitMilseconds) {
	    List<AgentSvcTask> svcs = allSvcs();
	    for (AgentSvcTask svc : svcs) {
	      if (svc.isRunning)
	        try {
	          if (waitMilseconds > 0L) {
	            svc.thread.join(waitMilseconds);
	            continue;
	          } 
	          svc.thread.join();
	        } catch (InterruptedException interruptedException) {} 
	    } 
	  }
	}

