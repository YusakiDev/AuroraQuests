package gg.auroramc.quests.api.quest;

import gg.auroramc.quests.AuroraQuests;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class QuestRollerScheduler {
    private final QuestPool pool;
    private Scheduler scheduler;
    private JobDetail job;
    private Trigger trigger;
    @Getter
    private boolean valid = false;

    public QuestRollerScheduler(QuestPool pool) {
        this.pool = pool;
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
            var config = pool.getConfig();
            String cronExpression = config.getResetFrequency();

            this.job = JobBuilder.newJob(QuestRollJob.class)
                    .withIdentity(pool.getId() + "-QuestRollJob")
                    .build();

            this.trigger = TriggerBuilder.newTrigger()
                    .withIdentity(pool.getId() + "-QuestRollTrigger")
                    .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                    .build();

            scheduler.scheduleJob(job, trigger);
            valid = true;
        } catch (SchedulerException e) {
            AuroraQuests.logger().severe("Failed to start scheduler: " + e.getMessage());
        }
    }

    public Date getNextRollDate() {
        return trigger.getNextFireTime();
    }

    public Date getPreviousRollDate() {
        return trigger.getPreviousFireTime();
    }

    public class QuestRollJob implements Job {
        @Override
        public void execute(JobExecutionContext context) {
            Bukkit.getAsyncScheduler().runDelayed(AuroraQuests.getInstance(), (task) -> {
                var players = new ArrayList<>(Bukkit.getOnlinePlayers());

                for (var player : players) {
                    pool.reRollQuests(player, true);
                }
            }, 100, TimeUnit.MILLISECONDS);
        }
    }

    public void shutdown() {
        if (scheduler == null) return;
        if (job == null) return;
        if (trigger == null) return;
        try {
            scheduler.deleteJob(job.getKey());
        } catch (SchedulerException e) {
            AuroraQuests.logger().severe("Failed to remove job from quest poll (" + pool.getId() + ") scheduler: " + e.getMessage());
        }
    }
}
