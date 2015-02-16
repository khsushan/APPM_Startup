package com.wso2.appm;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceStub;
import org.wso2.carbon.um.ws.api.stub.RemoteUserStoreManagerServiceUserStoreExceptionException;

import java.rmi.RemoteException;

/**
 * Created by ushan on 2/11/15.
 */
public class RemoteUserStoreManagerServiceClient {

    private RemoteUserStoreManagerServiceStub userStoreManagerStub;

    public RemoteUserStoreManagerServiceClient(String cookie,String url) throws AxisFault {

        String serviceURL = url+"/services/RemoteUserStoreManagerService";
        String username = "admin";
        String password = "admin";


        userStoreManagerStub = new RemoteUserStoreManagerServiceStub(serviceURL);
        ServiceClient svcClient = userStoreManagerStub._getServiceClient();
        Options option;

        option = svcClient.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);

    }

    public void updateClaims(String username,String claimURI,String data) throws RemoteException, RemoteUserStoreManagerServiceUserStoreExceptionException {
        userStoreManagerStub.setUserClaimValue(username, claimURI,data, "default");
    }
}
