package com.wso2.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;


import javax.activation.MimeType;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;

/**
 * Created by ushan on 2/11/15.
 */
public class HttpHandler {

    private final String USER_AGENT = "Mozilla/5.0";


    public String doPost(String backEnd, String payload, String your_session_id,String contentType) throws IOException {

        URL obj = new URL(backEnd);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
        //con.setRequestProperty("User-Agent", USER_AGENT);
        if(!your_session_id.equals("")){
            con.setRequestProperty(
                    "Cookie", "JSESSIONID=" + your_session_id);
        }
        con.setRequestProperty("Content-Type", contentType);
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

    public String RestPutClient(String url,String cookie) {
        // example url : http://localhost:9898/data/1d3n71f13r.json
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StringBuilder result = new StringBuilder();
        try {
            HttpPut putRequest = new HttpPut(url);
            putRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
            putRequest.addHeader("Accept-Language", "en-US,en;q=0.5");
            putRequest.addHeader("Cookie","JSESSIONID=" + cookie);
            putRequest.addHeader("Accept-Encoding","gzip, deflate");
            HttpResponse response = httpClient.execute(putRequest);
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatusLine().getStatusCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));
            String output;
            while ((output = br.readLine()) != null) {
                result.append(output);
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.toString();
    }


    public String postMultiData(String url,String method,String appMeta,String cookie) throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost =  new HttpPost(url);
        //httpPost.addHeader("Content-Type",contentType);
        //httpPost.addHeader("Accept","application/json, text/javascript, */*; q=0.01");
        //httpPost.addHeader("Accept-Encoding","gzip, deflate");
        //httpPost.addHeader("Accept-Language","en-US,en;q=0.5");
        httpPost.addHeader("Cookie", "JSESSIONID=" + cookie);

        MultipartEntityBuilder reqEntity;
        if(method.equals("upload")) {
            System.out.println("Upload app data");
            reqEntity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(new File("/home/ushan/wso2/APPM/LATEST_BUILD/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/CleanCalc.apk"));

            reqEntity.addPart("file", fileBody);
        }else{
            System.out.println("Send app data");
            reqEntity = MultipartEntityBuilder.create();
            reqEntity.addPart("version",new StringBody("1.2.3",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("provider",new StringBody("1WSO2Mobile",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("markettype",new StringBody("Enterprise",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("platform",new StringBody("android",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("name",new StringBody("myTest",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("description",new StringBody("this is a clean calucultor",ContentType.MULTIPART_FORM_DATA));
            FileBody bannerImageFile = new FileBody(new File("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/banner.png"));
            reqEntity.addPart("bannerFile",bannerImageFile);
            FileBody iconImageFile = new FileBody(new File("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/icon.png"));
            reqEntity.addPart("iconFile",iconImageFile);
            FileBody screenShot1 = new FileBody(new File("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/screen1.png"));
            reqEntity.addPart("screenshot1File",screenShot1);
            FileBody screenShot2= new FileBody(new File("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/screen2.png"));
            reqEntity.addPart("screenshot2File",screenShot2);
            FileBody screenShot3 = new FileBody(new File("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/repository/resources/mobileapps/CleanCalc/Resources/screen3.png"));
            reqEntity.addPart("screenshot3File",screenShot3);


            reqEntity.addPart("addNewAssetButton",new StringBody("Submit",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("mobileapp",new StringBody("mobileapp",ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("appmeta",new StringBody(appMeta,ContentType.MULTIPART_FORM_DATA));
        }
        final HttpEntity entity = reqEntity.build();
        httpPost.setEntity(entity);
        System.out.println("Requesting : " + httpPost.getAllHeaders());




        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpClient.execute(httpPost, responseHandler);
        System.out.println("responseBody : " + responseBody);
        int statusCode = -1;
        /*String responseBody = null;

        try {
            HttpResponse httpResponse = httpClient.execute(httpPost);

            StatusLine status = httpResponse.getStatusLine();
            statusCode = status.getStatusCode();
            responseBody = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");
            System.out.println("Status code : "+ status);
            System.out.println("Responce body : "+ responseBody);
            if (statusCode >= 300) {

            } else {

            }
        } catch (IOException e) {

        }*/
        return  responseBody;

    }
}
