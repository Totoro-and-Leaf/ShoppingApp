package com.fyp.shoping.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class PostApi {

    public Map<String, Object> fire(String uri, Object requestObject) {

        Map<String, Object> response = new HashMap<>();
        String resp = "";
        try {
            URL obj = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");

            con.setRequestProperty("Accept", "application/json");
            con.setRequestProperty("Content-Type", "application/json; utf-8");

            ObjectMapper mapper1 = new ObjectMapper();
            String json = "";
            if (requestObject != null) ;
            json = mapper1.writeValueAsString(requestObject);

            con.setDoOutput(true);

            try (OutputStream os = con.getOutputStream()) {
                byte[] input = json.getBytes("utf-8");
                os.write(input, 0, input.length);
                os.flush();
            }

            int responseCode = con.getResponseCode();
            System.out.println("POST Response Code :: " + responseCode);
            try (BufferedReader br = new BufferedReader(
                    new InputStreamReader(con.getInputStream(), "utf-8"))) {
                StringBuilder rawResponse = new StringBuilder();
                String responseLine = null;
                while ((responseLine = br.readLine()) != null) {
                    rawResponse.append(responseLine.trim());
                }
                resp = rawResponse.toString();
            } catch (Exception e){
                e.printStackTrace();
            }

            System.out.println("Success  " + resp);

            ObjectMapper mapper = new ObjectMapper();
            response = mapper.readValue(resp, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("ERROR", e.getMessage());
        }
        return response;
    }
}
