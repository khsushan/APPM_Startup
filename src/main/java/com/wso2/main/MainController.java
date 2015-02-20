package com.wso2.main;

import com.sun.tools.xjc.reader.xmlschema.bindinfo.BIConversion;
import com.wso2.appm.ClaimManagementServiceClient;
import com.wso2.appm.LoginAdminServiceClient;
import com.wso2.appm.RemoteUserStoreManagerServiceClient;
import com.wso2.appm.WSRegistryService_Client;
import com.wso2.bean.AppCreateRequest;
import com.wso2.http.HttpHandler;
import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.wso2.carbon.authenticator.stub.LoginAuthenticationExceptionException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Created by ushan on 2/11/15.
 */
public class MainController {

    private static final Log log = LogFactory.getLog(MainController.class);
    private static WSRegistryService_Client wsRegistryService_client;
    private static String backEndUrl = "https://localhost:9443";
    private static HttpHandler httpHandler = new HttpHandler();
    private static String  tomcatPort;
    private static String  wampPort;
    private  static String appmPath;

    public static void main(String[] args) {
        BasicConfigurator.configure();
        tomcatPort = args[0];
        wampPort = args[1];
        appmPath = args[2];
        //log.info("Args 1 "+args[0]);
        //log.info("Args 2 "+args[1]);
        configure();

    }


    private static void configure() {
        System.setProperty("javax.net.ssl.trustStore", appmPath+"/repository/resources/security/wso2carbon.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "wso2carbon");
        System.setProperty("javax.net.ssl.trustStoreType", "JKS");

        LoginAdminServiceClient login = null;

        try {
            try {
                Thread.sleep(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            login = new LoginAdminServiceClient(backEndUrl);
            String normal_session = login.authenticate("admin", "admin");
            ClaimManagementServiceClient claimManagementServiceClient =  new ClaimManagementServiceClient(normal_session,backEndUrl);
            RemoteUserStoreManagerServiceClient remoteUserStoreManagerServiceClient =  new RemoteUserStoreManagerServiceClient(normal_session,backEndUrl);
            //plan-your-trip
            log.info("Add claim mapping");
            claimManagementServiceClient.addClaim("FrequentFlyerID", "http://wso2.org/ffid", true);
            claimManagementServiceClient.addClaim("zipcode", "http://wso2.org/zipcode", true);
            claimManagementServiceClient.addClaim("Credit card number", "http://wso2.org/claims/card_number",true);
            claimManagementServiceClient.addClaim("Credit cArd Holder Name","http://wso2.org/claims/card_holder",true);
            claimManagementServiceClient.addClaim("Credit card expiration date","http://wso2.org/claims/expiration_date",true);

            //filldata
            log.info("Updating claim values");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/ffid", "12345151");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/streetaddress", "21/5");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/zipcode", "GL");
            remoteUserStoreManagerServiceClient.updateClaims("admin","http://wso2.org/claims/card_number", "001012676878");
            remoteUserStoreManagerServiceClient.updateClaims("admin","http://wso2.org/claims/card_holder","Admin");
            remoteUserStoreManagerServiceClient.updateClaims("admin","http://wso2.org/claims/expiration_date","31/12/2015");
            //publish application
            log.info("Creating application DTO");
            try {
                String appm_session = httpHandler.doPost(backEndUrl + "/publisher/api/authenticate", "username=admin&password=admin&action=login", "","application/x-www-form-urlencoded");
                AppCreateRequest appCreateRequest = new AppCreateRequest();
                //publishing travelWebapp
                appCreateRequest.setOverview_name("travelWebapp");
                appCreateRequest.setOverview_context("/travel");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("http://localhost:"+tomcatPort+"/plan-your-trip");
                String payload = appCreateRequest.generateRequestParameters();
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session, appm_session, payload,"application/x-www-form-urlencoded");
                //publishing travel booking application
                appCreateRequest.setOverview_name("TravelBooking");
                appCreateRequest.setOverview_context("/travelBooking");
                appCreateRequest.setOverview_version("1.0.1");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("http://localhost:"+tomcatPort+"/travelBooking");
                payload = appCreateRequest.generateRequestParameters();
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session, appm_session, payload,"application/x-www-form-urlencoded");
                //publishing  CleanCalc mobile application

                //appCreateRequest.set
                //String responceBody = httpHandler.postMultiData(backEndUrl+"/publisher/api/mobileapp/upload","upload","",appm_session);
                //httpHandler.postMultiData(backEndUrl+"/publisher/api/asset/mobileapp","none",responceBody,appm_session);
            } catch (IOException e) {
                log.error(e.getMessage());
                //e.printStackTrace();
            }
        } catch (AxisFault axisFault) {
            log.error(axisFault.getMessage());
            configure();
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
            e.printStackTrace();
        } catch (RegistryException e) {
            e.printStackTrace();
        }

    }


    private static void publishApplication(String applicationName, String applicationType, String session, String appm_session_id, String payload,String contentType) throws RegistryException, IOException {
        String resPonce = httpHandler.doPost(backEndUrl + "/publisher/asset/" + applicationType, payload, appm_session_id,contentType);
        wsRegistryService_client = new WSRegistryService_Client(backEndUrl, session);
        String appPath = "/_system/governance/apimgt/applicationdata/provider/admin/" + applicationName + "/1.0.0/" + applicationType;
        String UUID = wsRegistryService_client.getUUID(appPath);
        //publishing application
        httpHandler.RestPutClient(backEndUrl + "/publisher/api/lifecycle/Submit/"+applicationType+"/" + UUID, appm_session_id);
        httpHandler.RestPutClient(backEndUrl + "/publisher/api/lifecycle/Approve/"+applicationType+"/" + UUID, appm_session_id);

    }


}
