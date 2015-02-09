package ru.artyrian.statusmanager.web;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.artyrian.statusmanager.service.TaskExecutor;
import ru.artyrian.statusmanager.api.BitbucketApiBuilderV1;
import ru.artyrian.statusmanager.monitor.DataManager;
import ru.artyrian.statusmanager.service.KeyValuesMap;
import ru.artyrian.statusmanager.service.UpdateService;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

@Controller
@RequestMapping("/rest/api")
public class RestApi {
    private static final Logger logger = Logger.getLogger(RestApi.class);


    private static final ResourceBundle props = ResourceBundle.getBundle(KeyValuesMap.AUTH_PROPS_NAME);

    private UpdateService updateService = UpdateService.getInstance();

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/result")
    public Object jsonResult() {
        Map resultMap = updateService.resultMap();
        JSONObject jsonMap = new JSONObject(resultMap);

        return jsonMap.toString();
    }

    @RequestMapping(method = RequestMethod.GET, value = "/streamdata")
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/event-stream");
        response.setCharacterEncoding("UTF-8");

        PrintWriter writer = response.getWriter();

        if (!updateService.resultMap().isEmpty()) {
            writer.write("data: ready \n\n");
            writer.flush();
        }

        if (DataManager.getInstance().waitingDataReady()) {
            writer.write("data: ready \n\n");
            writer.flush();
        }

        writer.close();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/settings")
    public Object settings() {
        Map settingsMap = new LinkedHashMap();
        settingsMap.put("secondsPeriodUpdate", props.getString(KeyValuesMap.SECONDS_PERIOD_UPDATE));
        settingsMap.put("isJobStarted", TaskExecutor.getInstance().isStarted());
        JSONObject jsonMap = new JSONObject(settingsMap);

        return jsonMap.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/assignees")
    public Object assignees() {
        JSONArray jsonArray = new JSONArray();
        for (String assignee : updateService.getAssignees()) {
            jsonArray.put(assignee);
        }

        return jsonArray.toString();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.GET, value = "/priorities")
    public Object priorities() {
        JSONArray jsonArray = new JSONArray();
        for (String priority : BitbucketApiBuilderV1.PRIORITIES) {
            jsonArray.put(priority);
        }

        return jsonArray.toString();
    }


    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/start")
    public void start() {
        Integer secondsUpdatePeriod = Integer.parseInt(props.getString(KeyValuesMap.SECONDS_PERIOD_UPDATE));
        TaskExecutor.getInstance().startTask(secondsUpdatePeriod);
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/stop")
    public void stop() {
        TaskExecutor.getInstance().stopTask();
    }

    @ResponseBody
    @RequestMapping(method = RequestMethod.POST, value = "/manual")
    public void manual() {
        TaskExecutor.getInstance().manual();
    }
}