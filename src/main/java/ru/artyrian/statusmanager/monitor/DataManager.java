package ru.artyrian.statusmanager.monitor;

import org.apache.log4j.Logger;

/**
 * Created by artyrian on 1/31/2015.
 */
public class DataManager {
    private static final Object monitor = new Object();
    private static boolean ready = false;

    private static DataManager dataManager = null;

    private static final Logger logger = Logger.getLogger(DataManager.class);


    private DataManager() {

    }

    public static DataManager getInstance() {
        if (dataManager == null) {
            dataManager = new DataManager();
        }
        return dataManager;
    }

    public void prepareData() {
        synchronized (monitor) {
            logger.debug("Data prepared");
            ready = true;
            monitor.notifyAll();
        }
    }

    public boolean waitingDataReady() {
        synchronized (monitor) {
            logger.debug("Waiting for data...");
            while (!ready) {
                try {
                    monitor.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            // continue execution and sending data
            logger.debug("data ready to send...");
            ready = false;
        }

        return true;
    }

    public boolean isMonitorWorking() {
        return true;
    }
}
