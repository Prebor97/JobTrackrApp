package com.jobtrackr.schedule;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@WebListener
public class AppInitializer implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(AppInitializer.class);
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        try {
            SchedulerUtil.startScheduler();
            logger.info("Scheduler started.");
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to start scheduler: " + e.getMessage(), e);
        }
    }
}



