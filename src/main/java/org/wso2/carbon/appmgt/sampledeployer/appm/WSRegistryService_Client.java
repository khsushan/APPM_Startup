package org.wso2.carbon.appmgt.sampledeployer.appm;

import org.wso2.carbon.registry.core.Registry;
import org.wso2.carbon.registry.core.Resource;
import org.wso2.carbon.registry.core.exceptions.RegistryException;
import org.wso2.carbon.registry.ws.client.registry.WSRegistryServiceClient;

/**
 * Created by ushan on 2/17/15.
 */
public class WSRegistryService_Client {
    private Registry wsRegistryServiceClient;

    public WSRegistryService_Client(String backEndUrl, String cookie) throws RegistryException {
        wsRegistryServiceClient = new WSRegistryServiceClient(backEndUrl + "/services/", cookie);
    }

    public String getUUID(String path) throws RegistryException {
        Resource resource = wsRegistryServiceClient.get(path);
        return resource.getUUID();

    }


}
