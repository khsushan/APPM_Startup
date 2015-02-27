package org.wso2.carbon.appmgt.sampledeployer.main;

import org.apache.axis2.AxisFault;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.BasicConfigurator;
import org.wso2.carbon.appmgt.sampledeployer.appm.ClaimManagementServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.LoginAdminServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.RemoteUserStoreManagerServiceClient;
import org.wso2.carbon.appmgt.sampledeployer.appm.WSRegistryService_Client;
import org.wso2.carbon.appmgt.sampledeployer.bean.AppCreateRequest;
import org.wso2.carbon.appmgt.sampledeployer.bean.MobileApplicationBean;
import org.wso2.carbon.appmgt.sampledeployer.http.HttpHandler;
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
    private static String tomcatPort = "8080";
    private static String wampPort = "80";
    private static String appmPath = "/home/ushan/wso2/APPM/APPM(2_22)/wso2appm-1.0.0-SNAPSHOT";
    private static String username = "admin";
    private static String password = "admin";


    public static void main(String[] args) {
        BasicConfigurator.configure();
        tomcatPort = args[0];
        wampPort = args[1];
        appmPath = args[2];
        username = args[3];
        password = args[4];
        configure();
    }

    private static void configure() {
        System.setProperty("javax.net.ssl.trustStore", appmPath + "/repository/resources/security/wso2carbon.jks");
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
            String normal_session = login.authenticate(username, password);
            ClaimManagementServiceClient claimManagementServiceClient =
                    new ClaimManagementServiceClient(normal_session, backEndUrl);
            RemoteUserStoreManagerServiceClient remoteUserStoreManagerServiceClient =
                    new RemoteUserStoreManagerServiceClient(normal_session, backEndUrl);
            //plan-your-trip
            log.info("Add claim mapping");
            claimManagementServiceClient.addClaim("FrequentFlyerID", "http://wso2.org/ffid", true);
            claimManagementServiceClient.addClaim("zipcode", "http://wso2.org/zipcode", true);
            claimManagementServiceClient.addClaim("Credit card number", "http://wso2.org/claims/card_number", true);
            claimManagementServiceClient.addClaim("Credit cArd Holder Name", "http://wso2.org/claims/card_holder"
                    , true);
            claimManagementServiceClient.addClaim("Credit card expiration date", "http://wso2.org/claims/expiration_date"
                    , true);

            //filldata
            log.info("Updating claim values");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/ffid", "12345151");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/streetaddress", "21/5");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/zipcode", "GL");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_number"
                    , "001012676878");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/card_holder", "Admin");
            remoteUserStoreManagerServiceClient.updateClaims("admin", "http://wso2.org/claims/expiration_date"
                    , "31/12/2015");
            //publish application
            log.info("Creating application DTO");
            try {
                String appm_session = httpHandler.doPost(backEndUrl + "/publisher/api/authenticate",
                        "username="+username+"&password="+password+"&action=login", ""
                        , "application/x-www-form-urlencoded");
                AppCreateRequest appCreateRequest = new AppCreateRequest();
                String policyIDResponce = httpHandler.doPost(backEndUrl + "/publisher/api/entitlement/policy/partial" +
                                "/policyGroup/save", "anonymousAccessToUrlPattern=false&policyGroupName" +
                                "=test&throttlingTier=Unlimited&objPartialMappings=[]&policyGroupDesc=null&userRoles=null",
                        appm_session, "application/x-www-form-urlencoded; charset=UTF-8");
                System.out.println("Policy id res is : " + policyIDResponce);
                String splitValue = policyIDResponce.split(":")[3];
                String policyId = splitValue.substring(1, (splitValue.length() - 2)).trim();
                System.out.println("Policy id is : " + policyId);
                appCreateRequest.setUritemplate_policyGroupIds("[" + policyId + "]");
                appCreateRequest.setUritemplate_policyGroupId4(policyId);
                appCreateRequest.setUritemplate_policyGroupId3(policyId);
                appCreateRequest.setUritemplate_policyGroupId2(policyId);
                appCreateRequest.setUritemplate_policyGroupId1(policyId);
                appCreateRequest.setUritemplate_policyGroupId0(policyId);

                //publishing travelWebapp
                appCreateRequest.setOverview_name("travelWebapp");
                appCreateRequest.setOverview_displayName("travelWebapp");
                appCreateRequest.setOverview_context("/travel6");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("http://localhost:" + tomcatPort + "/plan-your-trip");
                String payload = appCreateRequest.generateRequestParameters();
                System.out.println(payload);
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        appm_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                //publishing travel booking application
                appCreateRequest.setOverview_name("TravelBooking");
                appCreateRequest.setOverview_displayName("TravelBooking");
                appCreateRequest.setOverview_context("/travelBooking");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("http://localhost:" + tomcatPort + "/travel-booking-1.0");
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        appm_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                //publishing notifi webapplication
                appCreateRequest.setOverview_name("notifi");
                appCreateRequest.setOverview_context("/notifi");
                appCreateRequest.setOverview_displayName("notifi");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("http://localhost/notifi/index.php");
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session,
                        appm_session, appCreateRequest, "application/x-www-form-urlencoded", "");
                //ExchangeR
                appCreateRequest.setOverview_name("ExchangeR");
                appCreateRequest.setOverview_displayName("ExchangeR");
                appCreateRequest.setOverview_context("/exchangeR");
                appCreateRequest.setOverview_version("1.0.0");
                appCreateRequest.setOverview_transports("http");
                appCreateRequest.setOverview_webAppUrl("file://" + appmPath +
                        "/repository/resources/mobileapps/ExchangeR/Source/index.html");
                appCreateRequest.setImages_thumbnail(appmPath +
                        "/repository/resources/mobileapps/ExchangeR/Resources/icon.png");
                appCreateRequest.setImages_banner(appmPath +
                        "/repository/resources/mobileapps/ExchangeR/Resources/banner.png");
                publishApplication(appCreateRequest.getOverview_name(), "webapp", normal_session, appm_session,
                        appCreateRequest, "application/x-www-form-urlencoded", "");

                //publish mobile application clean calc
                MobileApplicationBean mobileApplicationBean = new MobileApplicationBean();
                mobileApplicationBean.setApkFile(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/CleanCalc.apk");
                String appMeta = httpHandler.postMultiData(backEndUrl + "/publisher/api/mobileapp/upload",
                        "upload", mobileApplicationBean, appm_session);
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Enterprise");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("CleanCalc");
                mobileApplicationBean.setDescription("this is a clean calucultor");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/mobileapps/" +
                        "CleanCalc/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen2.png");
                mobileApplicationBean.setScreenShot3File(appmPath + "/repository/resources/mobileapps/CleanCalc" +
                        "/Resources/screen3.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                mobileApplicationBean.setAppmeta(appMeta);
                String ID = null;
                ID = httpHandler.postMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none",
                        mobileApplicationBean, appm_session);
                System.out.println("ID Clean Clac : " + ID);
                publishApplication("", "mobileapp", normal_session, appm_session, null, "", ID);

                //wso2Con
                mobileApplicationBean.setAppmeta("{\"package\":\"com.wso2.wso2con.mobile\",\"version\":\"1.0.0\"}");
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Market");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("Wso2Con");
                mobileApplicationBean.setDescription("WSO2Con mobile app provides the agenda and meeting tool for " +
                        "WSO2Con. Get the app to follow the agenda, find out about the sessions and speakers, provide " +
                        "feedback on the sessions, and get more information on the venue and sponsors. Join us at " +
                        "WSO2Con, where we will place emerging technology, best practices, and WSO2 product features " +
                        "in the perspective of accelerating development and building a connected business. We hope you " +
                        "enjoy WSO2Con!");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/WSO2Con" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps" +
                        "/WSO2Con/Resources/screen2.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                ID = httpHandler.postMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none"
                        , mobileApplicationBean, appm_session);
                System.out.println(" ID WSO2 Con : " + ID);
                publishApplication("", "mobileapp", normal_session, appm_session, null, "", ID);

                //MyTrack
                mobileApplicationBean.setAppmeta("{\"package\":\"com.google.android.maps.mytracks\",\"version\":\"1.0" +
                        ".0\"}");
                mobileApplicationBean.setVersion("1.0.0");
                mobileApplicationBean.setProvider("1WSO2Mobile");
                mobileApplicationBean.setMarkettype("Market");
                mobileApplicationBean.setPlatform("android");
                mobileApplicationBean.setName("MyTracks");
                mobileApplicationBean.setDescription("My Tracks records your path, speed, distance, and elevation while " +
                        "you walk, run, bike, or do anything else outdoors. While recording, you can view your data live" +
                        ", annotate your path, and hear periodic voice announcements of your progress.\n" +
                        "With My Tracks, you can sync and share your tracks via Google Drive. For sharing, you can share" +
                        " tracks with friends, see the tracks your friends have shared with you, or make tracks public " +
                        "and share their URLs via Google+, Facebook, Twitter, etc. In addition to Google Drive, you can " +
                        "also export your tracks to Google My Maps, Google Spreadsheets, or external storage.\n" +
                        "My Tracks also supports Android Wear. For watches with GPS, My Tracks can perform GPS recording" +
                        " of tracks without a phone and sync tracks to the phone. For watches without GPS, you can see " +
                        "your current distance and time, and control the recording of your tracks from your wrist.");
                mobileApplicationBean.setBannerFilePath(appmPath + "/repository/resources/" +
                        "mobileapps/MyTracks/Resources/banner.png");
                mobileApplicationBean.setIconFile(appmPath + "/repository/resources/mobileapps" +
                        "/MyTracks/Resources/icon.png");
                mobileApplicationBean.setScreenShot1File(appmPath + "/repository/resources/mobileapps/MyTracks" +
                        "/Resources/screen1.png");
                mobileApplicationBean.setScreenShot2File(appmPath + "/repository/resources/mobileapps/MyTracks" +
                        "/Resources/screen2.png");
                mobileApplicationBean.setMobileapp("mobileapp");
                ID = httpHandler.postMultiData(backEndUrl + "/publisher/api/asset/mobileapp", "none"
                        , mobileApplicationBean, appm_session);
                publishApplication("", "mobileapp", normal_session, appm_session, null, "", ID);
            } catch (IOException e) {
                //log.error(e.getMessage());
                e.printStackTrace();
            }
        } catch (AxisFault axisFault) {
            //log.error(axisFault.getMessage());
            //axisFault.printStackTrace();
            configure();
            // axisFault.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
            //log.error(e.getMessage());
        } catch (LoginAuthenticationExceptionException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (ClaimManagementServiceException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (RemoteUserStoreManagerServiceUserStoreExceptionException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (RegistryException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }

    }

    private static void publishApplication(String applicationName, String applicationType, String session, String
            appm_session_id, AppCreateRequest appCreateRequest, String contentType, String id)
            throws RegistryException, IOException {
        String UUID = "";
        String appPath = "";

        if (!applicationType.equals("mobileapp")) {
            String payload = appCreateRequest.generateRequestParameters();
            String resPonce = httpHandler.doPost(backEndUrl + "/publisher/asset/" + applicationType, payload
                    , appm_session_id, contentType);
            System.out.println("resPonce Creating application " + resPonce);
            String jsonPayload = "{\"provider\":\"wso2is-5.0.0\",\"logout_url\":\"\",\"claims\":[\"http://wso2." +
                    "org/claims/role\"],\"app_name\":\"" + appCreateRequest.getOverview_name() + "\",\"app_verison\":\""
                    + appCreateRequest.getOverview_version() + "\",\"app_transport\":\"http\",\"app_context\":\""
                    + appCreateRequest.getOverview_context() + "\",\"app_provider\":\"admin\",\"app_allowAnonymous\":\"f" +
                    "alse\"}";
            resPonce = httpHandler.doPost(backEndUrl + "/publisher/api/sso/addConfig", jsonPayload, appm_session_id
                    , "application/json; charset=UTF-8");
            System.out.println("resPonce SSO Config " + resPonce);
            wsRegistryService_client = new WSRegistryService_Client(backEndUrl, session);
            appPath = "/_system/governance/apimgt/applicationdata/provider/admin/" + applicationName + "/1.0.0/"
                    + applicationType;
            UUID = wsRegistryService_client.getUUID(appPath);
        } else {
            UUID = id;
        }
        //publishing application
        httpHandler.RestPutClient(backEndUrl + "/publisher/api/lifecycle/Submit/" + applicationType + "/" + UUID
                , appm_session_id);
        httpHandler.RestPutClient(backEndUrl + "/publisher/api/lifecycle/Approve/" + applicationType + "/" + UUID
                , appm_session_id);
        if (applicationType.equals("mobileapp")) {
            httpHandler.RestPutClient(backEndUrl + "/publisher/api/lifecycle/Publish/" + applicationType + "/" + UUID
                    , appm_session_id);
        }
    }


}
