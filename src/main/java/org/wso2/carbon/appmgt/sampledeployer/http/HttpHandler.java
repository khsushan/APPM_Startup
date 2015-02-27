package org.wso2.carbon.appmgt.sampledeployer.http;

import org.wso2.carbon.appmgt.sampledeployer.bean.MobileApplicationBean;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static org.apache.http.impl.client.HttpClients.createDefault;

/**
 * Created by ushan on 2/11/15.
 */
public class HttpHandler {

    private final String USER_AGENT = "Mozilla/5.0";


    public String doPost(String backEnd, String payload, String your_session_id, String contentType)
            throws IOException {
        URL obj = new URL(backEnd);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();
        //add reuqest header
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        if (!your_session_id.equals("")) {
            con.setRequestProperty(
                    "Cookie", "JSESSIONID=" + your_session_id);
        }
        con.setRequestProperty("Content-Type", contentType);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(payload);
        wr.flush();
        wr.close();
        int responseCode = con.getResponseCode();
        if (responseCode == 200) {
            System.out.println("Session message" + con.getResponseMessage());
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            if (your_session_id.equals("")) {
                System.out.println(response.toString());
                String session_id = response.substring((response.lastIndexOf(":") + 3), (response.lastIndexOf("}") - 2));
                return session_id;
            }
            return response.toString();
        }
        return null;
    }

    public String RestPutClient(String url, String cookie) {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        StringBuilder result = new StringBuilder();
        try {
            HttpPut putRequest = new HttpPut(url);
            putRequest.addHeader("Content-Type", "application/x-www-form-urlencoded");
            putRequest.addHeader("Accept-Language", "en-US,en;q=0.5");
            putRequest.addHeader("Cookie", "JSESSIONID=" + cookie);
            putRequest.addHeader("Accept-Encoding", "gzip, deflate");
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

    public String postMultiData(String url, String method, MobileApplicationBean mobileApplicationBean, String cookie)
            throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        httpPost.addHeader("Cookie", "JSESSIONID=" + cookie);
        MultipartEntityBuilder reqEntity;
        if (method.equals("upload")) {
            System.out.println("Upload app data");
            reqEntity = MultipartEntityBuilder.create();
            FileBody fileBody = new FileBody(new File(mobileApplicationBean.getApkFile()));
            reqEntity.addPart("file", fileBody);
        } else {
            System.out.println("Send app data");
            reqEntity = MultipartEntityBuilder.create();
            reqEntity.addPart("version", new StringBody(mobileApplicationBean.getVersion(),
                    ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("provider", new StringBody(mobileApplicationBean.getMarkettype(),
                    ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("markettype", new StringBody(mobileApplicationBean.getMarkettype(),
                    ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("platform", new StringBody(mobileApplicationBean.getPlatform(),
                    ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("name", new StringBody(mobileApplicationBean.getName(), ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("description", new StringBody(mobileApplicationBean.getDescription(),
                    ContentType.MULTIPART_FORM_DATA));
            FileBody bannerImageFile = new FileBody(new File(mobileApplicationBean.getBannerFilePath()));
            reqEntity.addPart("bannerFile", bannerImageFile);
            FileBody iconImageFile = new FileBody(new File(mobileApplicationBean.getIconFile()));
            reqEntity.addPart("iconFile", iconImageFile);
            FileBody screenShot1 = new FileBody(new File(mobileApplicationBean.getScreenShot1File()));
            reqEntity.addPart("screenshot1File", screenShot1);
            FileBody screenShot2 = new FileBody(new File(mobileApplicationBean.getScreenShot2File()));
            reqEntity.addPart("screenshot2File", screenShot2);
            FileBody screenShot3 = new FileBody(new File(mobileApplicationBean.getScreenShot3File()));
            reqEntity.addPart("screenshot3File", screenShot3);
            reqEntity.addPart("addNewAssetButton", new StringBody("Submit", ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("mobileapp", new StringBody(mobileApplicationBean.getMobileapp(),
                    ContentType.MULTIPART_FORM_DATA));
            reqEntity.addPart("appmeta", new StringBody(mobileApplicationBean.getAppmeta(),
                    ContentType.MULTIPART_FORM_DATA));
        }
        final HttpEntity entity = reqEntity.build();
        httpPost.setEntity(entity);
        System.out.println("Requesting : " + httpPost.getAllHeaders());
        ResponseHandler<String> responseHandler = new BasicResponseHandler();
        String responseBody = httpClient.execute(httpPost, responseHandler);
        System.out.println("responseBody : " + responseBody);
        if (!method.equals("upload")) {
            String id_part = responseBody.split(",")[2].split(":")[1];
            return id_part.substring(2, (id_part.length() - 2));
        }
        return responseBody;
    }

    public String postData_C(String cookie, String url) throws IOException {
        HttpClient httpclient = createDefault();
        HttpPost httppost = new HttpPost(url);
        httppost.addHeader("Cookie", "JSESSIONID=" + cookie);
        // Request parameters and other properties.
        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
        params.add(new BasicNameValuePair("anonymousAccessToUrlPattern", "false"));
        params.add(new BasicNameValuePair("policyGroupName", "test"));
        params.add(new BasicNameValuePair("throttlingTier", "Unlimited"));
        httppost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
         //Execute and get the response.
        HttpResponse response = httpclient.execute(httppost);
        HttpEntity entity = response.getEntity();
        return  entity.getContent().toString();
    }

}
