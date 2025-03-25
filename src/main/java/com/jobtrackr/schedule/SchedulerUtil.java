package com.jobtrackr.schedule;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchedulerUtil {
    private static final Logger logger = LoggerFactory.getLogger(SchedulerUtil.class);

    public static void startScheduler() throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(EmailReminderJob.class)
                .withIdentity("emailReminderJob", "group1")
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("emailReminderTrigger", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?"))
                .build();

        scheduler.scheduleJob(job, trigger);

        scheduler.start();
        logger.info("Scheduler started.");
    }
}
