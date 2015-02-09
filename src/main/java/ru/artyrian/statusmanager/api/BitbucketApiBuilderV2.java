package ru.artyrian.statusmanager.api;

/**
 * Created by artyrian on 12/25/2014.
 */
public class BitbucketApiBuilderV2 {
    private static final String URL_API_V_1 = "https://bitbucket.org/api/2.0/";

    private final String REQUEST_PATH;

    public BitbucketApiBuilderV2(String username, String repository) {
        REQUEST_PATH = URL_API_V_1 + "repositories/" + username + "/" + repository;
    }

    public String getLastCommit() {
        StringBuilder requestBuilder = new StringBuilder(REQUEST_PATH + "/commits?pagelen=1");
        return  requestBuilder.toString();
    }
}
