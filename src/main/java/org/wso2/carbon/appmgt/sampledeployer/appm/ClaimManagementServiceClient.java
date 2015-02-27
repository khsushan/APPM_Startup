package org.wso2.carbon.appmgt.sampledeployer.appm;

import org.apache.axis2.AxisFault;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceException;
import org.wso2.carbon.claim.mgt.stub.ClaimManagementServiceStub;
import org.wso2.carbon.claim.mgt.stub.dto.ClaimDTO;
import org.wso2.carbon.claim.mgt.stub.dto.ClaimMappingDTO;
import java.rmi.RemoteException;

/**
 * Created by ushan on 2/10/15.
 */
public class ClaimManagementServiceClient {

    private ClaimManagementServiceStub claimManagementServiceStub;

    public ClaimManagementServiceClient(String cookie, String url) throws AxisFault {

        String serviceURL = url + "/services/ClaimManagementService";
        claimManagementServiceStub = new ClaimManagementServiceStub(serviceURL);
        ServiceClient svcClient = claimManagementServiceStub._getServiceClient();
        Options option;
        option = svcClient.getOptions();
        option.setManageSession(true);
        option.setProperty(org.apache.axis2.transport.http.HTTPConstants.COOKIE_STRING, cookie);
    }

    public void addClaim(String description, String claimURI, boolean isRequired) throws RemoteException,
            ClaimManagementServiceException {
        ClaimDTO claimDTO = new ClaimDTO();
        claimDTO.setDialectURI("http://wso2.org/claims");
        claimDTO.setClaimUri(claimURI);
        claimDTO.setDisplayTag(description);
        claimDTO.setDescription(description);
        claimDTO.setSupportedByDefault(true);
        claimDTO.setReadOnly(false);
        claimDTO.setRequired(isRequired);
        claimDTO.setDisplayOrder(0);
        ClaimMappingDTO claimMappingDTO = new ClaimMappingDTO();
        claimMappingDTO.setClaim(claimDTO);
        claimMappingDTO.setMappedAttribute(description);
        claimManagementServiceStub.addNewClaimMapping(claimMappingDTO);
    }


}
