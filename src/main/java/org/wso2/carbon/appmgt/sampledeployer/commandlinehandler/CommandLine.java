package org.wso2.carbon.appmgt.sampledeployer.commandlinehandler;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by ushan on 3/20/15.
 */
public class CommandLine {


    public static String executeCommand(String[] command) {

        StringBuffer output = new StringBuffer();

        Process p;
        try {
            p = Runtime.getRuntime().exec(command);
            p.waitFor();
            BufferedReader reader =
                    new BufferedReader(new InputStreamReader(p.getInputStream()));

            String line = "";
            while ((line = reader.readLine())!= null) {
                output.append(line + "\n");
            }
            System.out.println(line);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return output.toString();

    }


}
