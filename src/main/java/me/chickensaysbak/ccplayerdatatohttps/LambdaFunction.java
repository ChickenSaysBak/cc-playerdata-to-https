package me.chickensaysbak.ccplayerdatatohttps;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class LambdaFunction implements RequestHandler<Map<String, Object>, Map<String, Object>> {

    public final String ENDPOINT = "http://cc-playerdata-app.us-east-2.elasticbeanstalk.com";

    @Override
    public Map<String, Object> handleRequest(Map<String, Object> input, Context context) {

        Map<String, Object> response = new HashMap<>();
        String path = (String) input.getOrDefault("rawPath", "");

        if (!path.startsWith("/api/playerdata")) {
            response.put("statusCode", 400);
            response.put("body", "Requests must come from the following base path: /api/playerdata");
            return response;
        }

        String queryString = (String) input.getOrDefault("rawQueryString", "");
        String params = path + (!queryString.isEmpty() ? "?" + queryString : "");

        try {

            URL url = new URL(ENDPOINT + params);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            StringBuilder content = new StringBuilder();

            String inputLine;
            while ((inputLine = in.readLine()) != null) content.append(inputLine);
            in.close();

            response.put("statusCode", con.getResponseCode());
            response.put("body", content);
            response.put("headers", Map.of("Access-Control-Allow-Origin", "*"));
            con.disconnect();

        } catch (IOException e) {
            response.put("statusCode", 500);
            response.put("body", e.toString());
        }

        return response;

    }

}
