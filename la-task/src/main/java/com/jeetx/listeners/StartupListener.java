package com.jeetx.listeners;
import javax.servlet.ServletContext;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

import com.jeetx.common.redis.JedisClient;
import com.jeetx.util.LogUtil;

/**
 * 1 => StartupListener.setApplicationContext
 * 2 => StartupListener.setServletContext
 * 3 => StartupListener.afterPropertiesSet
 * 4.1 => MyApplicationListener.onApplicationEvent
 * 4.2 => MyApplicationListener.onApplicationEvent
 * 4.1 => MyApplicationListener.onApplicationEvent
 */
@Component
public class StartupListener implements ApplicationContextAware, ServletContextAware,
        InitializingBean, ApplicationListener<ContextRefreshedEvent> {
	@Autowired JedisClient jedisClient;

	@Value("${initModule2Privilege}")
	private String initModule2Privilege;
 
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        LogUtil.info("1 => StartupListener.setApplicationContext");
    }

    public void setServletContext(ServletContext context) {
        //LogUtil.info("2 => StartupListener.setServletContext");
    }
 
    public void afterPropertiesSet() throws Exception {
        //LogUtil.info("3 => StartupListener.afterPropertiesSet");
    }
 
    public void onApplicationEvent(ContextRefreshedEvent evt) {
        //LogUtil.info("4.1 => MyApplicationListener.onApplicationEvent");
        if (evt.getApplicationContext().getParent() == null) {
            //LogUtil.info("4.2 => MyApplicationListener.onApplicationEvent");
        }
    }

}
