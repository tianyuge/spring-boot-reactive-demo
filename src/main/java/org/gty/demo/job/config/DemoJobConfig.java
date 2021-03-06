package org.gty.demo.job.config;

import org.gty.demo.constant.SystemConstants;
import org.gty.demo.job.DemoJob;
import org.quartz.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.TimeZone;

@Configuration
public class DemoJobConfig {

    @Bean
    @Nonnull
    public JobDetail demoJobDetail() {
        return JobBuilder.newJob(DemoJob.class)
            .withIdentity("demoJob")
            .usingJobData("info", "job demonstration.")
            .storeDurably()
            .build();
    }

    @Bean
    @Nonnull
    public Trigger demoJobTrigger(@Nonnull final JobDetail demoJobDetail) {
        Objects.requireNonNull(demoJobDetail, "demoJobDetail must not be null");

        final var scheduleBuilder
            = SimpleScheduleBuilder.simpleSchedule()
            .withIntervalInSeconds(15)
            .repeatForever();

        final var cronScheduleBuilder = CronScheduleBuilder
            .dailyAtHourAndMinute(1, 0)
            .inTimeZone(TimeZone.getTimeZone(SystemConstants.defaultTimeZone))
            .build();

        return TriggerBuilder.newTrigger()
            .forJob(demoJobDetail)
            .withIdentity("demoJobTrigger")
            .withSchedule(scheduleBuilder)
            .build();
    }
}
