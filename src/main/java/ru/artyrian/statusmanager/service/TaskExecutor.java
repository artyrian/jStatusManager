package ru.artyrian.statusmanager.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by artyrian on 12/25/2014.
 */
public class TaskExecutor {
    private static final Logger logger = Logger.getLogger(TaskExecutor.class);

    private ScheduledExecutorService ses;
    private static TaskExecutor taskExecutor;

    private Runnable manualRunnableUpdate;

    private AtomicBoolean started = new AtomicBoolean(false);
    private DateTime lastUpdateTime;

    private TaskExecutor() {
        lastUpdateTime = DateTime.now();

        manualRunnableUpdate = new Runnable() {
            @Override
            public void run() {
                UpdateService.getInstance().update();
                lastUpdateTime = DateTime.now();
            }
        };
    }

    public static TaskExecutor getInstance() {
        if (taskExecutor == null) {
            taskExecutor = new TaskExecutor();
        }
        return taskExecutor;
    }


    public void startTask(int secondsPeriod) {
        ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(new Runnable() {
            public void run() {
                manualRunnableUpdate.run();
            }
        }, 0, secondsPeriod, TimeUnit.SECONDS);

        started.set(true);
    }

    public void stopTask() {
        if (ses != null) {
            ses.shutdownNow();
            started.set(false);
        }
    }

    public void manual() {
        if (!lastUpdateTime.plusSeconds(30).isAfterNow()) {
            Thread thread = new Thread(manualRunnableUpdate);
            thread.start();
        }
    }

    public AtomicBoolean isStarted() {
        return started;
    }
}
