package org.wso2.carbon.appmgt.sampledeployer.main;

import org.w3c.dom.Node;
import org.wso2.carbon.appmgt.sampledeployer.xmlhandler.XMLHandler;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;

/**
 * Created by ushan on 3/13/15.
 */
public class ConfigureStatisticsMain {


    private static String appmPath = "/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT";
    private static String bamPath = "";
    private static String offcet ="3";
    private static String ipAddress ="localhost";
    private static int tcpPort = 7611;


    public static void main(String[] args) {
        appmPath = args[0];
        bamPath = args[1];
        offcet = args[2];
        ipAddress =  args[3];
        tcpPort += Integer.parseInt(offcet);
        statisticsConfiguration();
    }

    private static void statisticsConfiguration(){
        try {
            XMLHandler.configuredAPPMXML(appmPath + "/repository/conf/app-manager.xml",tcpPort+"",ipAddress);
            Node node = XMLHandler.configuredDataSourceXML(appmPath + "/repository/conf/datasources/master-datasources.xml"
                    ,"jdbc:h2:"+bamPath + "/repository/database/APIMGTSTATS_DB;AUTO_SERVER=TRUE");
            XMLHandler.updateOffset(bamPath + "/repository/conf/carbon.xml", offcet);
            ///home/ushan/Shell_Script_Test/BAM/wso2bam-2.5.0/repository/conf/datasources/bam-datasources.xml
            XMLHandler.updateBamDatasource(bamPath + "/repository/conf/datasources/bam-datasources.xml", node);
            //XMLHandler.updateHectorConfigXML(bamPath + "/repository/conf/etc/hector-config.xml", "localhost:9160");
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
    }

}
