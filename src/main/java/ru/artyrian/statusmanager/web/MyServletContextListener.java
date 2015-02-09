package ru.artyrian.statusmanager.web;

import org.apache.log4j.Logger;
import ru.artyrian.statusmanager.service.TaskExecutor;
import ru.artyrian.statusmanager.service.KeyValuesMap;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.ResourceBundle;

public class MyServletContextListener implements ServletContextListener {
    private final static Logger logger = Logger.getLogger(MyServletContextListener.class);

    private static final ResourceBundle props = ResourceBundle.getBundle(KeyValuesMap.AUTH_PROPS_NAME);
    private final TaskExecutor taskExecutor = TaskExecutor.getInstance();

    @Override
    public void contextInitialized(ServletContextEvent contextEvent) {
        /* Do Startup stuff. */
        logger.debug("startup stuff");
        taskExecutor.startTask(Integer.parseInt(props.getString(KeyValuesMap.SECONDS_PERIOD_UPDATE)));
    }

    @Override
    public void contextDestroyed(ServletContextEvent contextEvent) {
        /* Do Shutdown stuff. */
        logger.debug("shutdown stuff");
        taskExecutor.stopTask();
    }

}
