package com.imooc.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;

/**
 * Spring
 */
@Configuration
public class SpringContextUtils implements ApplicationContextAware {
    private static ApplicationContext applicationContext;
    private static Object lock=new Object();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static <T> T getBean(String beanName) {
        return (T) applicationContext.getBean(beanName);
    }

    public static <T> T getBean(Class<T> cls) {
        return applicationContext.getBean(cls);
    }
    
    public static String getProperty(String key){
    	return applicationContext.getEnvironment().getProperty(key);
    }

}
