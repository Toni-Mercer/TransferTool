package utils;

import exceptions.WrongArgumentException;
import java.util.Properties;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class ArgumentReaderUtil {

    /**
     * gets an array
     * @param args user input as array
     * @return Properties with the arguments inserted
     * @throws WrongArgumentException if arguments are invalid or bad filled
     */
    public static Properties getParams(String[] args) throws WrongArgumentException {

        //-ssh [null] <Use SSH scp>
        //-sftp [null] <Use SFTP>
        //-cluster [null] <exec/shell on multiple SSH connections simultaneously>
        //-shell [null] <Get shell>

        //-u [user] <SSH/SFTP/Shell/Cluster User>
        //-p [port] <SSH/SFTP/Shell/Cluster Port>
        //-R [host] <SSH/SFTP/Shell/Cluster url/ip>
        //-r [null] <Recursive>
        //-s [null] <strict mode on>
        //-w [null] <strict mode off>
        //-t [null] <generates new dir on remote host>
        //-v [null] <Verbose>
        //-d [null] <Debugging>
        //-W [host:]port <SSH/SFTP/Shell/Cluster User:Port>
        //-J [user@]host[:port] <SSH/SFTP/Shell/Cluster User@host:port>
        //-fl [path] local <Local directory>
        //-fr [path] <Remote directory>
        //-up [path]:[path] <Upload local file path to remote path>
        //-gt [path]:[path] <Download remote file path to local file path>
        // -h [null] <Help message>


        if (args.length < 1) {
            System.err.println("Null arguments");
            System.exit(0);
        }

        //check conf file
        if (!ConfigurationUtil.isConfigPresent())ConfigurationUtil.generateConf();

        //load default parameters
        Properties properties = ConfigurationUtil.getParams();
        if (properties == null) properties = new Properties();

        //read arguments and overwrite def parameters
        for (int a = 0; a < args.length; a++){
            if (args[a].equals("-R") || args[a].equals("--host")) properties.put("host",args[++a]);
            else if (args[a].equals("-p") || args[a].equals("--port")) properties.put("port",args[++a]);
            else if (args[a].equals("-fl")) properties.put("fileLocal",args[++a]);
            else if (args[a].equals("-fr")) properties.put("fileRemote",args[++a]);
            else if (args[a].equals("-ssh")) properties.put("Method", "ssh");
            else if (args[a].equals("-sftp")) properties.put("Method", "sftp");
            else if (args[a].equals("-cluster")) properties.put("Method", "cluster");
            else if (args[a].equals("-shell")) properties.put("Method", "shell");
            else if (args[a].equals("-h") || args[a].equals("--help")) printHelp();
            else if (args[a].equals("-s")) properties.put("StrictHostKeyChecking", "yes");
            else if (args[a].equals("-w")) properties.put("StrictHostKeyChecking", "no");
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
            }else if (args[a].equals("-u") || args[a].equals("--user")) properties.put("user",args[++a]);
            else if (args[a].equals("-r") || args[a].equals("--recursive"))  properties.put("Recursive", "1");
            else if (args[a].equals("-v") || args[a].equals("--verbose"))  properties.put("Verbose", "1");
            else if (args[a].equals("-d") || args[a].equals("--debugging"))  properties.put("Debugging", "ON");
            else{
                properties.put("Debugging","OFF");
                properties.put("Recursive", "0");
                properties.put("Verbose", "0");
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

        if (properties.getProperty("Debugging").equals("ON")) properties.forEach((k, v) -> ConsolePrinterUtil.printDebugging(ArgumentReaderUtil.class,k + ":" + v, Thread.currentThread().getStackTrace()[1].getLineNumber()));

        return properties;
    }

    private static void printHelp() {
        String message = "\n\n" +
                "\t\t ______________         ______________ \n" +
                "\t\t|_____    _____|       |_____    _____|\n" +
                "\t\t      |  |                   |  |      \n" +
                "\t\t      |  |                   |  |      \n" +
                "\t\t      |  |                   |  |      \n" +
                "\t\t      |  |                   |  |      \n" +
                "\t\t      |  |                   |  |      \n" +
                "\t\t      |__|                   |__|      \n" +
                "\n\n\t\t TransferTool by San-Ny\n\n\nAvailable parameters:\n\n" +
                "Method to transfer files:\n" +
                "\t-ssh [null] <Use SSH scp>\n" +
                "\t-sftp [null] <Use SFTP>\n" +
                "\t-cluster [null] <exec/shell on multiple SSH connections simultaneously>\n" +
                "\t-shell [null] <Get shell>\n" +
                "\n" +
                "Available arguments, usage depends of method:\n" +
                "\t--command\t\t\t\t[parameters]\t\t<description>\n" +
                "\t-u\tor --user\t\t\t[user]\t\t\t\t<SSH/SFTP/Shell/Cluster User>\n" +
                "\t-p\tor --port\t\t\t[port]\t\t\t\t<SSH/SFTP/Shell/Cluster Port>\n" +
                "\t-R\tor --host\t\t\t[host]\t\t\t\t<SSH/SFTP/Shell/Cluster url/ip>\n" +
                "\t-r\tor --recursive\t\t\t\t\t\t\t<Recursive>\n" +
                "\t-s\tor --strict\t\t\t\t\t\t\t\t<strict mode on>\n" +
                "\t-w\tor --unstrict\t\t\t\t\t\t\t<strict mode off>\n" +
                "\t-t\t\t\t\t\t\t\t\t\t\t\t<generates new dir on remote host>\n" +
                "\t-v\tor --verbose\t\t\t\t\t\t\t<Verbose>\n" +
                "\t-d\tor --debugging\t\t\t\t\t\t\t<Debugging>\n" +
                "\t-W\t\t\t\t\t\t[host:]port\t\t\t<SSH/SFTP/Shell/Cluster User:Port>\n" +
                "\t-J\t\t\t\t\t\t[user@]host[:port]\t<SSH/SFTP/Shell/Cluster User@host:port>\n" +
                "\t-fl\tor --filelocal\t\t[path]\t\t\t\t<Local directory>\n" +
                "\t-fr\tor --fileremote\t\t[path]\t\t\t\t<Remote directory>\n" +
                "\t-up\tor --upload\t\t\t[path]:[path]\t\t<Upload local file path to remote path>\n" +
                "\t-gt\tor --download\t\t[path]:[path]\t\t<Download remote file path to local file path>\n" +
                "\t-h\tor --help\t\t\t\t\t\t\t\t<Help message>";

        ConsolePrinterUtil.println(message);
        System.exit(0);
    }

    /**
     * method will get properties and the name of required parameters
     * @param properties propertied collected from arguments and config file
     * @param requiredValues Array of required values
     * @return return if properties document is valid or not
     */
    public static boolean isValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (!properties.containsKey(s)) return false;
        return true;
    }

    /**
     * check if one of the requested parameters is present
     * @param properties properties to check
     * @param requiredValues array with keys to check
     * @return return true if present else false
     */
    public static boolean isOneValid(Properties properties, String[] requiredValues){
        for(String s : requiredValues) if (properties.containsKey(s)) return true;
        return false;
    }
}
