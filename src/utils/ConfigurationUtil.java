package utils;

import java.io.*;
import java.util.Properties;

public class ConfigurationUtil {

    public static final String CONFIGFILE = "/etc/transfertool/properties/transfertool.conf";

    public static boolean isConfigPresent() {
        File confFile = new File(CONFIGFILE);
        return confFile.exists();
    }

    public static void generateConf(){
        try{
            String[] defaultConf = {
                    "#Default file, autogenerated",
                    "#transferTool is casesensitive, bad formating or incorrect fields will end in a fatal error",
                    "",
                    "#[KEYS]",
                    "PrivateKeyPath:/etc/transfertool/keys/private.key",
                    "PublicKeyPath:/etc/transfertool/keys/public.key",
                    "KeyBytes:2048",
                    "AlgorithmKeyParGenerator:RSA",
                    "AlgorithmEncriptor:RSA/ECB/OAEPWithSHA-256AndMGF1Padding",
                    "AlgorithmDecriptor:RSA/ECB/OAEPPadding",
                    "OAEPParameterMdName:SHA-256",
                    "OAEPParameterMgfName:MGF1",
                    "MGF1ParameterMdName:SHA-1",
                    "StandardCharset:UTF_8",
                    "",
                    "",
                    "#[LISTENER]",
                    "",
                    "",
                    ""
            };
            File confFile = new File(CONFIGFILE);

            if (confFile.getParentFile() != null) {
                confFile.getParentFile().mkdirs();
            }
            confFile.createNewFile();

            BufferedWriter publicConfOS = new BufferedWriter(new FileWriter(CONFIGFILE));
            for (String s: defaultConf) publicConfOS.write(s + "\n");
            publicConfOS.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

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

    public static String getProperty(String key){
        try(BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE))){
            String line;
            while ((line = in.readLine()) != null) {
                if(line.isEmpty() || line.substring(0, 1).equals("#")) continue;
                String[] parts = line.split(":");
                if (parts[0].equals(key)) return parts[1];
            }
            return null;
        }catch (IOException e){
//            e.printStackTrace();
            return null;
        }
    }

    public static String getPropertyOrDefault(String key, String defaultValue){
        try(BufferedReader in = new BufferedReader(new FileReader(CONFIGFILE))){
            String line;
            while ((line = in.readLine()) != null) {
                if(line.isEmpty() || line.substring(0, 1).equals("#")) continue;
                String[] parts = line.split(":");
                if (parts[0].equals(key)) return parts[1];
            }
            return defaultValue;
        }catch (IOException e){
//            e.printStackTrace();
            return defaultValue;
        }
    }
}
