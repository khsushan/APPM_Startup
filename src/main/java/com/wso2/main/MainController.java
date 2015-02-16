package com.wso2.main;

import com.wso2.appm.ClaimManagementServiceClient;
import com.wso2.appm.LoginAdminServiceClient;
import com.wso2.appm.RemoteUserStoreManagerServiceClient;
import com.wso2.bean.AppCreateRequest;
import com.wso2.http.HttpHandler;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created by ushan on 2/11/15.
 */
public class MainController {

    private static final Log log = LogFactory.getLog(MainController.class);

    public static  void  main(String[] args){
        BasicConfigurator.configure();
        publishApplication();
    }


    private static  void publishApplication(){
        System.setProperty("javax.net.ssl.trustStore", "/home/ushan/wso2/APPM/LATEST_BUILD/wso2appm-1.0.0-SNAPSHOT/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");
        String backEndUrl = "https://localhost:9443";
        LoginAdminServiceClient login = null;

        try {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            login = new LoginAdminServiceClient(backEndUrl);
            String session = login.authenticate("admin", "admin");
            ClaimManagementServiceClient claimManagementServiceClient =  new ClaimManagementServiceClient(session,backEndUrl);
            RemoteUserStoreManagerServiceClient remoteUserStoreManagerServiceClient =  new RemoteUserStoreManagerServiceClient(session,backEndUrl);
            //plan-your-trip
            log.info("Add claim mapping");
            claimManagementServiceClient.addClaim("FrequentFlyerID", "http://wso2.org/ffid", true);
            claimManagementServiceClient.addClaim("zipcode", "http://wso2.org/zipcode", true);
            claimManagementServiceClient.addClaim("Credit card number", "http://wso2.org/claims/card_number",true);
            claimManagementServiceClient.addClaim("zipcode","http://wso2.org/zipcode",true);
            claimManagementServiceClient.addClaim("zipcode","http://wso2.org/zipcode",true);

            //filldata
            log.info("Updating claim values");
            remoteUserStoreManagerServiceClient.updateClaims("admin","http://wso2.org/ffid","12345151");
            remoteUserStoreManagerServiceClient.updateClaims("admin","http://wso2.org/claims/streetaddress","21/5");


            //publish application
            log.info("Creating application DTO");
            AppCreateRequest appCreateRequest = new AppCreateRequest();
            appCreateRequest.setOverview_name("travelWebapp");
            appCreateRequest.setOverview_context("//travel");
            appCreateRequest.setOverview_version("1.0.0");
            appCreateRequest.setOverview_transports("http");
            appCreateRequest.setOverview_webAppUrl("http://localhost:8080/plan-your-trip");

            HttpHandler httpHandler = new HttpHandler();
            try {
                String session_id =  httpHandler.doPost(backEndUrl + "/publisher/api/authenticate", "username=admin&password=admin&action=login", "");
                log.info("Session id is : "+session_id);
                String payload = appCreateRequest.generateRequestParameters();
                log.info("Payload is : "+session_id);
                String resPonce =   httpHandler.doPost(backEndUrl + "/publisher/asset/webapp", payload, session_id);
            } catch (IOException e) {
                log.error(e.getMessage());
                //e.printStackTrace();
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            publishApplication();
           // axisFault.printStackTrace();
        } catch (RemoteException e) {
            //e.printStackTrace();
            log.error(e.getMessage());
        } catch (LoginAuthenticationExceptionException e) {
            log.error(e.getMessage());
            //e.printStackTrace();
        } catch (ClaimManagementServiceException e) {
            log.error(e.getMessage());
            //e.printStackTrace();
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException e) {
            log.error(e.getMessage());
           // e.printStackTrace();
        }

    }




}
