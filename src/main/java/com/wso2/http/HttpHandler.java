package com.wso2.http;

import org.json.simple.JSONObject;

import javax.json.Json;
import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ProtocolException;
import java.net.URL;

/**
 * Created by ushan on 2/11/15.
 */
public class HttpHandler {

    private final String USER_AGENT = "Mozilla/5.0";


    public String doPost(String backEnd, String payload, String your_session_id) throws IOException {

        URL obj = new URL(backEnd);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        if(!your_session_id.equals("")){
            con.setRequestProperty(
                    "Cookie", "JSESSIONID=" + your_session_id);
        }
        con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");

        //String urlParameters = ;

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();


        int responseCode = con.getResponseCode();
        if(responseCode == 200){
            System.out.println("Session message"+  con.getResponseMessage());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if(your_session_id.equals("")){
                System.out.println(response.toString());
                String session_id  =  response.substring((response.lastIndexOf(":")+3),(response.lastIndexOf("}")-2));
                return session_id;
            }

            return  response.toString();


        }
        /*System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);*/


        return  null;
        //print result


    }
}
