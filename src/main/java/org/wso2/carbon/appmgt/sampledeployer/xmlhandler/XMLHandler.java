package org.wso2.carbon.appmgt.sampledeployer.xmlhandler;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

/**
 * Created by ushan on 3/10/15.
 */
public class XMLHandler {

    public XMLHandler(){

    }

    public static void configuredAPPMXML(String xmlPath,String port,String ipAddress) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlPath.trim());
        Node apiUsageTrackingNode= doc.getElementsByTagName("APIUsageTracking").item(0);
        if(apiUsageTrackingNode instanceof Element) {
            Element docElement = (Element)apiUsageTrackingNode;
            Node enabled = docElement.getElementsByTagName("Enabled").item(0);
            enabled.setTextContent("true");
            System.out.println("done");
            Node bamServerURl = docElement.getElementsByTagName("BAMServerURL").item(0);
            bamServerURl.setTextContent("tcp://"+ipAddress+":"+port);
        }

        Node dataSourceName = doc.createElement("DataSourceName");
        dataSourceName.appendChild(doc.createTextNode("jdbc/WSO2AM_STATS_DB"));
        apiUsageTrackingNode.appendChild(dataSourceName);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlPath).toURI().getPath());
        transformer.transform(source, result);
    }

    public static Node configuredDataSourceXML(String xmlPath,String url) throws ParserConfigurationException, IOException, SAXException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlPath.trim());
        Node datasourceNode = doc.getElementsByTagName("datasource").item(2);
        Node definitionNode = ((Element)datasourceNode).getElementsByTagName("definition").item(0);
        Node configurationNode = ((Element)definitionNode).getElementsByTagName("configuration").item(0);
        Node urlNode = ((Element)configurationNode).getElementsByTagName("url").item(0);
        urlNode.setTextContent(url);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlPath).toURI().getPath());
        transformer.transform(source, result);
        return  datasourceNode;

    }

    public  static void updateOffset(String carbonXMLPath,String offset) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(carbonXMLPath.trim());
        Node portNode = doc.getElementsByTagName("Ports").item(0);
        Node definitionNode = ((Element)portNode).getElementsByTagName("Offset").item(0);
        definitionNode.setTextContent(offset);

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(carbonXMLPath).toURI().getPath());
        transformer.transform(source, result);
    }

    public  static void updateBamDatasource(String xmlFilePath,Node node,String port) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFilePath.trim());
        Node datasources = doc.getElementsByTagName("datasources").item(0);
        Node datasource=doc.importNode(node,true);
        datasources.appendChild(datasource);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath).toURI().getPath());
        transformer.transform(source, result);
    }

    public static  void updateHectorConfigXML(String xmlFilePath,String url) throws IOException, SAXException, ParserConfigurationException, TransformerException {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.parse(xmlFilePath.trim());
        Node hectorConfigurationXML = doc.getElementsByTagName("HectorConfiguration").item(0);
        Node definitionNode = ((Element)hectorConfigurationXML).getElementsByTagName("Cluster").item(0);
        Node configurationNode = ((Element)definitionNode).getElementsByTagName("Nodes").item(0);
        configurationNode.setTextContent(url);
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(xmlFilePath).toURI().getPath());
        transformer.transform(source, result);
    }

//    public static void main(String[] args) {
//        try {
//            //configuredAPPMXML("/home/ushan/Shell_Script_Test/APPM
//            // /wso2appm-1.0.0-SNAPSHOT/repository/conf/app-manager.xml", "APIUsageTracking");
//            Node dataSourceNode = configuredDataSourceXML("/home/ushan/Shell_Script_Test/APPM/wso2appm-1.0.0-SNAPSHOT/" +
//                            "repository/conf/datasources/master-datasources.xml","/home/ushan/wso2/wso2bam-2.4.1" +
//                            "/repository/database/APIMGTSTATS_DB;AUTO_SERVER=TRUE");
//
//            updateBamDatasource("/home/ushan/wso2/wso2bam-2.4.1/repository/conf/datasources/bam-datasources.xml"
//                    ,dataSourceNode,"jdbc:cassandra://localhost:9163/EVENT_KS");
//            //updateOffset("/home/ushan/wso2/wso2bam-2.4.1/repository/conf/carbon.xml","2");
//
//             updateHectorConfigXML("/home/ushan/wso2/wso2bam-2.4.1/repository/conf/etc/hector-config.xml"
//                     ,"localhost:9163");
//            System.out.println("done");
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (SAXException e) {
//            e.printStackTrace();
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }
//
//    }


//


    /*public static void main(String args[]) {
        try {

            Process p = Runtime.getRuntime().exec(new String[]{"bash","-c","sh  ~/wso2/APPM/New_Pack/21st/wso2appm-1.0.0-SNAPSHOT/bin/wso2server.sh"});

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line=null;

            while((line=input.readLine()) != null) {
                System.out.println(line);
            }
            int exitVal = p.waitFor();
            System.out.println("Exited with error code "+exitVal);

            String command= "/usr/bin/xterm";
            Runtime rt = Runtime.getRuntime();
            Process pr = rt.exec(command);

        } catch(Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
        }
        System.out.println(getGeneratedTrackingID());
    }*/



}
