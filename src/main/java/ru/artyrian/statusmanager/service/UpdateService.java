package ru.artyrian.statusmanager.service;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.artyrian.statusmanager.api.BitbucketApiBuilderV1;
import ru.artyrian.statusmanager.api.BitbucketApiBuilderV2;
import ru.artyrian.statusmanager.api.JenkinsApiBuilder;
import ru.artyrian.statusmanager.crawler.JsonCrawler;
import ru.artyrian.statusmanager.monitor.DataManager;

import java.util.*;

/**
 * Created by artyrian on 12/25/2014.
 */
public class UpdateService {
    private static final Logger logger = Logger.getLogger(UpdateService.class);

    private static final String AUTH_PROPS_NAME = "stat";
    private static final ResourceBundle props = ResourceBundle.getBundle(AUTH_PROPS_NAME);

    private final BitbucketApiBuilderV1 bitbucketApiBuilderV1;
    private final BitbucketApiBuilderV2 bitbucketApiBuilderV2;
    private final JenkinsApiBuilder jenkinsApiBuilder;
    private final JsonCrawler bitbucketJsonCrawler;
    private final JsonCrawler bitbucketV2JsonCrawler;
    private final JsonCrawler jenkinsJsonCrawler;

    private final String[] ASSIGNEE_NICKNAMES;

    private final Map<String, Object> resultMap;

    private static UpdateService updateService = null;

    private UpdateService() {
        bitbucketApiBuilderV1 = new BitbucketApiBuilderV1(
            props.getString("bibucket.ownerRepository"),
            props.getString("bitbucket.repositoryName"));

        bitbucketApiBuilderV2 = new BitbucketApiBuilderV2(
                props.getString("bibucket.ownerRepository"),
                props.getString("bitbucket.repositoryName"));


        jenkinsApiBuilder = new JenkinsApiBuilder(
            props.getString("jenkins.url"),
            props.getString("jenkins.job"));


        bitbucketJsonCrawler = new JsonCrawler(
            props.getString("bitbucket.username"),
            props.getString("bitbucket.password"));

        bitbucketV2JsonCrawler = new JsonCrawler(
                props.getString("bitbucket.username"),
                props.getString("bitbucket.password"));

        jenkinsJsonCrawler = new JsonCrawler(
            props.getString("jenkins.username"),
            props.getString("jenkins.password"));

        ASSIGNEE_NICKNAMES = props.getString("bitbucket.assignees").split(",");

        resultMap = new LinkedHashMap<String, Object>();

        //timerTask(Integer.parseInt(props.getString("execute.secondsPeriodUpdate")));
    }

    public static UpdateService getInstance() {
        if (updateService == null) {
            updateService = new UpdateService();
        }
        return updateService;
    }

    public Map<String, Object> resultMap() {
        return resultMap;
    }

    public String[] getAssignees() {
        return ASSIGNEE_NICKNAMES;
    }

    public synchronized void update() {
        logger.info("start update");

        long startUpdateTime = new Date().getTime();
        resultMap.put(KeyValuesMap.START_UPDATE_TIME, startUpdateTime);

        try {

            Map<String, Map> priorityIssuesMap = new LinkedHashMap<>();
            for (String priority : BitbucketApiBuilderV1.PRIORITIES) {
                Map<String, Integer> issuesMap = new LinkedHashMap<String, Integer>();

                for (String nickname : ASSIGNEE_NICKNAMES) {
                    JSONObject jsonObject = bitbucketJsonCrawler.getJsonObject(bitbucketApiBuilderV1.getOpenIssues(nickname, priority));
                    issuesMap.put(nickname, (Integer) jsonObject.get("count"));
                }

//                JSONObject jsonObject = bitbucketJsonCrawler.getJsonObject(bitbucketApiBuilderV1.getOpenIssues(null, priority));
//                issuesMap.put("all", (Integer) jsonObject.get("count"));

                priorityIssuesMap.put(priority, issuesMap);
            }
            resultMap.put(KeyValuesMap.OPEN_ISSUES_MAP, priorityIssuesMap);


            JSONObject lastBuildJsonObject = jenkinsJsonCrawler.getJsonObject(jenkinsApiBuilder.getLastBuild());
            if (lastBuildJsonObject != null) {
                String statusLastBuild = (String) lastBuildJsonObject.get("result");
                resultMap.put(KeyValuesMap.LAST_BUILD_STATUS, statusLastBuild);
            }

            JSONObject lastCommitValuesJsonObject = bitbucketV2JsonCrawler.getJsonObject(bitbucketApiBuilderV2.getLastCommit());
            JSONArray jsonCommitsValues = (JSONArray) lastCommitValuesJsonObject.get("values");
            if (jsonCommitsValues != null && !jsonCommitsValues.isNull(0)) {
                JSONObject lastCommitJsonObject = (JSONObject) jsonCommitsValues.get(0);

                JSONObject authorJson = (JSONObject) lastCommitJsonObject.get("author");
                JSONObject userJson = (JSONObject) authorJson.get("user");
                String lastCommitUser = userJson.get("username").toString();
                resultMap.put("lastCommitUser", lastCommitUser);

                String lastCommitMessage = (String) lastCommitJsonObject.get("message");
                resultMap.put("lastCommitMessage", lastCommitMessage);

                resultMap.put("lastCommitDate", lastCommitJsonObject.get("date").toString());
            }


        } catch (JSONException e) {
            logger.error("Cannot read property or something else in json object.", e);
            e.printStackTrace();
        }

        long endUpdateTime = new Date().getTime();
        resultMap.put("endUpdateTime", endUpdateTime);
        resultMap.put(KeyValuesMap.DURATION_UPDATE_TIME, endUpdateTime - startUpdateTime);

        logger.info("end update");

        DataManager.getInstance().prepareData();
    }
}
