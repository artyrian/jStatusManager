package ru.artyrian.statusmanager.crawler;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by artyrian on 12/24/2014.
 */
public class JsonCrawler {
    final static Logger logger = Logger.getLogger(JsonCrawler.class);

    private final String authStringEnc;

    public JsonCrawler(String username, String password) {
        String authString = username + ":" + password;
        authStringEnc = DatatypeConverter.printBase64Binary(authString.getBytes());
        logger.debug("Base64 encoded auth string: " + authStringEnc);
    }

    public JSONObject getJsonObject(String urlPath) {
        logger.debug("url request to:" + urlPath);
        JSONObject jsonObject = null;
        try {
            URL url = new URL(urlPath);
            URLConnection urlConnection = url.openConnection();
            urlConnection.setRequestProperty("Authorization", "Basic " + authStringEnc);
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");

            BufferedReader input = new BufferedReader(isr);

            StringBuffer stringBuffer = new StringBuffer();
            String line;
            while ((line = input.readLine()) != null) {
                stringBuffer.append(line);
            }

            jsonObject = new JSONObject(stringBuffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonObject;
    }
}
