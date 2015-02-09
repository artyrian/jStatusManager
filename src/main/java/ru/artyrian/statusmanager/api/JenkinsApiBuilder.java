package ru.artyrian.statusmanager.api;

/**
 * Created by artyrian on 12/25/2014.
 */
public class JenkinsApiBuilder {
    private final String serverUrl;
    private final String job;

    public JenkinsApiBuilder(String serverUrl, String job) {
        this.serverUrl = serverUrl;
        this.job = job;
    }

    public String getLastBuild() {
        return serverUrl + "/job/" + job + "/lastBuild/api/json";
    }
}
