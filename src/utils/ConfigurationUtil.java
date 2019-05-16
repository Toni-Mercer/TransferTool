package utils;

import java.io.*;
import java.util.Properties;

/**
 * @author san
 * @version 0.0.1
 *
 * license MIT <https://mit-license.org/>
 */
public class ConfigurationUtil {

    private static final String CONFIGFILE = "/etc/transfertool/properties/transfertool.conf";

    /**
     * check if file exists
     * @return boolean
     */
    public static boolean isConfigPresent() {
        File confFile = new File(CONFIGFILE);
        return confFile.exists();
    }

    /**
     * generates the conf file on CONFIGFILE path
     */
    public static void generateConf(){
        try{
            String[] defaultConf = {
                    "#Default file, autogenerated",
                    "#transferTool is case sensitive, bad formatting or incorrect fields will end in a fatal error",
                    "",
                    "#[KEYS] using TransferTool to encrypt & decrypt messages(same key required or will end with useless data)",
                    "PrivateKeyPath:/etc/transfertool/keys/private.key",
                    "PublicKeyPath:/etc/transfertool/keys/public.key",
                    "KeyBytes:2048",
                    "AlgorithmKeyParGenerator:RSA",
                    "AlgorithmEncrypt:RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
                    "AlgorithmDecrypt:RSA/ECB/OAEPPadding",
                    "OAEPParameterMdName:SHA-256",
                    "OAEPParameterMgfName:MGF1",
                    "MGF1ParameterMdName:SHA-1",
                    "StandardCharset:UTF_8",
                    "",
                    "",
                    "#[LISTENER]  @deprecated",
                    "ListenerPort:9990",
                    "#Server can refuse unencrypted transfers:[Mandatory], allow them [Optional] or refuse them [Free]",
                    "Encryption:Mandatory",
                    "#Incoming connections can be [Verbose](the user accepts it), [All](non host filters) or [Filtered](only trusted hosts)",
                    "IncomingConnections:Filtered",
                    "#REGEXP to filter allowed ips default only lo & C class are allowed",
                    "#Custom regexps are allowed",
                    "#lo->  127.0.0.0   – 127.255.255.255   127.0.0.0 /8    - (^127\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))",
                    "#A->   10.0.0.0    – 10.255.255.255    10.0.0.0 /8     - (^10\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))",
                    "#B->   172.16.0.0  – 172.31.255.255    172.16.0.0 /12  - ((^172\\.1[6-9]\\.)|(^172\\.2[0-9]\\.)|(^172\\.3[0-1]\\.))(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})",
                    "#C->   192.168.0.0 – 192.168.255.255   192.168.0.0 /16 - (^192\\.168\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))",
                    "TrustedHosts:(^127\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))|(^192\\.168\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2})\\.(([1-2]([0-5]?[0-5]))|[0-9]{1,2}))",
                    "",
                    "",
                    "#[SENDER via SSH and FTPS]",
                    "SSHRemotePort:22",
                    "FTPSRemotePort:21",
                    "Debugging:OFF",
                    "Method:ssh",
                    "#StrictHostKeyChecking [yes], [no], [default]",
                    "StrictHostKeyChecking:default"
            };
            File confFile = new File(CONFIGFILE);

            if (confFile.getParentFile() != null) if (confFile.getParentFile().mkdirs()) System.err.println("Run manually:\n $ sudo mkdir /etc/transfertool/ && sudo chmod 777 /etc/transfertool/ -R");
            if (!confFile.createNewFile()) System.err.println("Unable to write file, check permissions.");

            BufferedWriter publicConfOS = new BufferedWriter(new FileWriter(CONFIGFILE));
            for (String s: defaultConf) publicConfOS.write(s + "\n");
            publicConfOS.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * get all params in properties object
     * @return Properties object
     */
    public static Properties getParams(){
        try(BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE))){
            Properties properties = new Properties();
            String line;
            while ((line = in.readLine()) != null) {
                if(line.isEmpty() || line.substring(0, 1).equals("#")) continue;
                String[] parts = line.split(":");
                properties.setProperty(parts[0].trim(),parts[1].trim());
            }
            return properties;
        }catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * get a property with the key name
     * @param key property name
     * @return property value or null if not found
     */
    public static String getProperty(String key){
        try(BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE))){
            return propertyReader(in,key);
        }catch (IOException e){
//            e.printStackTrace();
            return null;
        }
    }

    /**
     * get property value if exists or default value
     * @param key property name
     * @param defaultValue property value if not found
     * @return value or default value if property is missing
     */
    public static String getPropertyOrDefault(String key, String defaultValue){
        try(BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE))){
            String value = propertyReader(in, key);
            if (value != null) return value;
            else return defaultValue;
        }catch (IOException e){
//            e.printStackTrace();
            return defaultValue;
        }
    }

    /**
     * file reader & property getter
     * @param in BufferedReader, file to read
     * @param key property name
     * @return value if key is present or null
     * @throws IOException if file is missing or bad formatted
     */
    private static String propertyReader(BufferedReader in, String key) throws IOException{
        String line;
        while ((line = in.readLine()) != null) {
            if(line.isEmpty() || line.substring(0, 1).equals("#")) continue;
            String[] parts = line.split(":");
            if (parts[0].equals(key)) return parts[1].trim();
        }
        return null;
    }
}
