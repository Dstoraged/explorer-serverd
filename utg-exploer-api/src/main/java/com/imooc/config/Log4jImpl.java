//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.imooc.config;

import org.apache.ibatis.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Log4jImpl implements Log {
    private static final String FQCN = Log4jImpl.class.getName();
    private final Logger log;

    public Log4jImpl(String clazz) {
        this.log = Logger.getLogger(clazz);
    }

    public boolean isDebugEnabled() {
        return true;
    }

    public boolean isTraceEnabled() {
        return this.log.isTraceEnabled();
    }

    public void error(String s, Throwable e) {
        this.log.log(FQCN, Level.ERROR, s, e);
    }

    public void error(String s) {
        this.log.log(FQCN, Level.ERROR, s, (Throwable)null);
    }

    public void debug(String s) {
//        if(s!=null&&(s.startsWith("==>")||s.startsWith("<=="))){
//            this.log.log(FQCN, Level.INFO, s, (Throwable)null);
//        }else{
            this.log.log(FQCN, Level.DEBUG, s, (Throwable)null);
//        }
    }

    public void trace(String s) {
        this.log.log(FQCN, Level.TRACE, s, (Throwable)null);
    }

    public void warn(String s) {
        this.log.log(FQCN, Level.WARN, s, (Throwable)null);
    }
}
