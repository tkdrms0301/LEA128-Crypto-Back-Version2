package kumoh.config.defaultconfig;


import kumoh.service.CryptoService;
import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;


@RequiredArgsConstructor
@Component
public class ConfigLoader{

    private static final String DEFAULT_DIR = "config/";
    private static final String DEFAULT_LOG_DIR = "Log/";
    private static final File CONFIG_FILE = new File("config/config.txt");
    private static final String DEFAULT_FILE_ENCRYPTION_DIR = "FileEncryption";

    private static final String DEFAULT_FILE_DECRYPTION_DIR = "FileDecryption";

    private static final String DEFAULT_FILE_DB_LIVE_DIR = "DBLive";
    private static final String CSV_DOWNLOAD_PATH = CryptoService.getRequestFilePath()+"live";
    private final LogDirInfo logDirInfo;
    private final DownloadDirInfo downloadDirInfo;

    private final FileEncDirInfo fileEncDirInfo;
    private final FileDecDirInfo fileDecDirInfo;
    private final DBEncDirInfo dbEncDirInfo;
    private static LinkedHashMap<String, String> configData;

    private final MappingHashMap mappingHashMap;

    public static boolean ONE_TIME = false;
    public void onApplicationEvent() {
        if(!ONE_TIME){
            try {
                if(CONFIG_FILE.exists())
                    loadFile();
                else{
                    makeFile();
                    loadFile();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ONE_TIME = true;
        }

    }

    public static File getConfigFile() {
        return CONFIG_FILE;
    }

    public static LinkedHashMap<String, String> getLinkedHashMap() {return configData;}

    private void makeFile() throws IOException {
        new File(DEFAULT_DIR).mkdirs();
        CONFIG_FILE.createNewFile();

        logDirInfo.saveLogDir(DEFAULT_LOG_DIR, CONFIG_FILE);
        downloadDirInfo.saveDownloadDir(CSV_DOWNLOAD_PATH, CONFIG_FILE);
        fileEncDirInfo.saveFileEncDir(DEFAULT_FILE_ENCRYPTION_DIR, CONFIG_FILE);
        fileDecDirInfo.saveFileDecDir(DEFAULT_FILE_DECRYPTION_DIR, CONFIG_FILE);
        dbEncDirInfo.saveFileEncDir(DEFAULT_FILE_DB_LIVE_DIR, CONFIG_FILE);
    }

    private void loadFile() throws IOException {
        loadConfigData();

        logDirInfo.loadLogDir(configData);
        downloadDirInfo.loadDownloadDir(configData);
        fileEncDirInfo.loadFileEncDir(configData);
        fileDecDirInfo.loadFileDecDir(configData);
        dbEncDirInfo.loadFileEncDir(configData);
        APISavedInfo.loadApiList(configData);
    }

    private void loadConfigData() throws IOException {
            configData = mappingHashMap.readHashMap(CONFIG_FILE);
    }

    public static String getDefaultDir(){
        return DEFAULT_DIR;
    }
}
 