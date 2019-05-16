package sftpsender;

import com.jcraft.jsch.*;
import pojos.SSH2User;
import utils.ArgumentReaderUtil;
import utils.PathFinderUtil;
import utils.ScannerUtil;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Properties;

/**
 * TransferTool
 *
 * @version openjdk version "10.0.2" 2018-07-17
 *
 * @author   Toni <tonimercer300@gmail.com>
 * license   MIT <https://mit-license.org/>
 */
public class SFTPSender extends Thread {

    private Properties properties;

    public SFTPSender(Properties properties){
        this.properties = properties;
    }

    @Override
    public void run(){

        //checking required arguments
        String[] requiredProperties = {"user", "port", "host", "fileLocal"};

        if (!ArgumentReaderUtil.isValid(properties, requiredProperties)){
            System.err.println("Missing required arguments");
            System.exit(0);
        }

        //assignation
        String user, port, host, fileLocal;
        boolean debugging = properties.getProperty("Debugging").equals("ON");
        user = properties.getProperty("user");
        port = properties.getProperty("port");
        host = properties.getProperty("host");
        fileLocal = properties.getProperty("fileLocal");


        //getting paths array; cleaning it
        ArrayList<Path> paths = null;
        try{
            paths = PathFinderUtil.getCorrectFormat(Path.of(fileLocal), properties);
        }catch (IOException pe){
            if (debugging) pe.printStackTrace();
            else System.err.println("Error on local path");
            System.exit(0);
        }

        try{
            JSch jsch=new JSch();
            Session session = jsch.getSession(user, host, Integer.parseInt(port));
            Properties strict = new Properties();
            if (properties.getProperty("StrictHostKeyChecking").equals("no")) strict.put("StrictHostKeyChecking", "no");
            else if (properties.getProperty("StrictHostKeyChecking").equals("yes")) strict.put("StrictHostKeyChecking", "yes");
            else if (debugging) System.out.println("StrictHostKeyChecking disabled");
            session.setConfig(strict);
            UserInfo ui = new SSH2User(debugging);
            session.setUserInfo(ui);

            try{
                session.connect();
            }catch(final JSchException jex){
                if (debugging) jex.printStackTrace();
                else System.err.println("Incomplete connection");
                System.exit(-1);
            }

            System.out.println("connected");
            session.disconnect();

        }catch (JSchException e){
            if (debugging)e.printStackTrace();
            else System.err.println("Transfer failed");
            System.exit(-1);
        }
    }
}
