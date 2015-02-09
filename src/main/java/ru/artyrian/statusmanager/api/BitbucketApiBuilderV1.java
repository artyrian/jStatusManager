package ru.artyrian.statusmanager.api;

/**
 * Created by artyrian on 12/25/2014.
 */
public class BitbucketApiBuilderV1 {
    public static final String NEW_STATUS = "new";
    public static final String OPEN_STATUS = "open";

    public static final String CRITICAL_PRIORITY = "critical";
    public static final String MAJOR_PRIORITY = "major";
    public static final String ALL_PRIORITY = "all";

    public static final String ALL_ASSIGNEES = "__all";

    private static final String URL_API_V_1 = "https://bitbucket.org/api/1.0/";
    private static final String[] OPEN_STATUSES = {NEW_STATUS, OPEN_STATUS};
    public static final String[] PRIORITIES = {MAJOR_PRIORITY, CRITICAL_PRIORITY, ALL_PRIORITY};
    private static final String OPEN_ISSUES_GET_PARAM;

    private final String REQUEST_PATH;

    static {
        StringBuilder openIssuesGetParam = new StringBuilder();
        for (int i = 0; i < OPEN_STATUSES.length; i++) {
            openIssuesGetParam.append("&status=" + OPEN_STATUSES[i]);
        }

        OPEN_ISSUES_GET_PARAM = openIssuesGetParam.toString();
    }


    public BitbucketApiBuilderV1(String username, String repository) {
        REQUEST_PATH = URL_API_V_1 + "repositories/" + username + "/" + repository;
    }

    public String getOpenIssues(String responsible, String priority) {
        StringBuilder requestBuilder = new StringBuilder(REQUEST_PATH + "/issues?limit=0" + OPEN_ISSUES_GET_PARAM);
        if (responsible != null && !responsible.equals(ALL_ASSIGNEES)) {
            requestBuilder.append("&responsible=" + responsible);
        }
        if (priority != null && !priority.equals(ALL_PRIORITY)) {
            requestBuilder.append("&priority=" + priority);
        }

        return requestBuilder.toString();
    }
}
