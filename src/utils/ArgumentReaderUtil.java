package utils;

import exceptions.WrongArgumentException;

import java.lang.reflect.Parameter;
import java.util.Properties;

public class ArgumentReaderUtil {

    public static Properties getParams(String[] args) throws WrongArgumentException {
        Properties properties = new Properties();

        //-u user
        //-p port
        //-R host
        //-r recursive
        //-s strict mode ------------------
        //-t generates new dir on remote host
        //-v verbose
        //-d debugging
        //-W host:port
        //-J [user@]host[:remoteFile]
        //-fl files local
        //-fr files remote

        if (args.length < 4) {
            System.out.println("Invalid arguments.");
            System.exit(0);
        }

        //load default parameters
        properties.put("Debugging", "1");

        //read arguments and overwrite def parameters
        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R")) properties.put("host",args[++a]);
            else if (args[a].equals("-p")) properties.put("port",args[++a]);
            else if (args[a].equals("-fl")) properties.put("fileLocal",args[++a]);
            else if (args[a].equals("-fr")) properties.put("fileRemote",args[++a]);
            else if (args[a].equals("-W")){
                String[] hpCommand = args[++a].split(":");
                if (hpCommand.length != 2) throw new WrongArgumentException("Wrong parameter on arguments:\n\t-W host[:port]");
                properties.put("host",hpCommand[0]);
                properties.put("port",hpCommand[1]);
            }else if (args[a].equals("-J")){
                String[] hpCommand = args[++a].split("[@:]");
                if (hpCommand.length != 3) throw new WrongArgumentException("Wrong parameter on arguments:\n\t-J [user@]host[:port]");
                properties.put("user",hpCommand[0]);
                properties.put("host",hpCommand[1]);
                properties.put("port",hpCommand[2]);
            }else if (args[a].equals("-u")) properties.put("user",args[++a]);
            else if (args[a].equals("-r"))  properties.put("recursive", "1");
            else if (args[a].equals("-v"))  properties.put("verbose", "1");
            else if (args[a].equals("-d"))  properties.put("Debugging", "1");
            else{
                properties.put("Debugging","0");
                properties.put("recursive", "0");
                properties.put("verbose", "0");
                if (a == 0) properties.put("fileLocal",args[a]);
                else if (a == 1){
                    String[] hpCommand = args[a].split("[@:]");
                    if (hpCommand.length != 3) throw new WrongArgumentException("Wrong default parameters on arguments:\n\t[user@]host[:path]");
                    properties.put("user",hpCommand[0]);
                    properties.put("host",hpCommand[1]);
                    properties.put("fileRemote",hpCommand[2]);
                }else if (a == 2) properties.put("port",args[a]);
                else throw new WrongArgumentException("Unknown default parameters path [path] user@host[:path] port");
            }
        }

        if (properties.getProperty("Debugging").equals("1")) properties.forEach((k, v) -> System.out.println("ArgumentReaderUtil -> " + k + ":" + v));

        return properties;
    }
}
