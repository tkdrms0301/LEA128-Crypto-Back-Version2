package kumoh.config.defaultconfig;

import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@Component
@RequiredArgsConstructor
public class DownloadDirInfo {

    private static String downloadDir = "";

    private static final String DOWNLOAD_DIR_KEY = "downloadDir";

    private final MappingHashMap mappingHashMap;
    private static final String PATH_SPLIT = System.getProperty("os.name").contains("Win")?"\\":"/";
    void loadDownloadDir(LinkedHashMap<String, String> configInfo) {
        if (configInfo.containsKey(DOWNLOAD_DIR_KEY)){
            downloadDir = configInfo.get(DOWNLOAD_DIR_KEY);
            if(downloadDir.lastIndexOf(PATH_SPLIT) != (downloadDir.length()-1)){
                downloadDir += PATH_SPLIT;
            }
        }
    }

    void saveDownloadDir(String dirInfo, File configFile) throws IOException {
        String dirInfoTemp = dirInfo;
        if(dirInfoTemp.lastIndexOf(PATH_SPLIT) != (dirInfoTemp.length()-1)){
            dirInfoTemp += PATH_SPLIT;
        }
        System.out.println();
        mappingHashMap.saveHashMap(DOWNLOAD_DIR_KEY, dirInfoTemp, configFile);
    }

    public void updateDownloadDir(String dirInfo) throws IOException {
        updateDownloadDir(dirInfo, ConfigLoader.getConfigFile());
    }

    private void updateDownloadDir(String dirInfo, File configFile) throws IOException {
        String dirInfoTemp = dirInfo;
        if(dirInfoTemp.lastIndexOf(PATH_SPLIT) != (dirInfoTemp.length()-1)){
            dirInfoTemp += PATH_SPLIT;
        }

        mappingHashMap.updateHashMap(DOWNLOAD_DIR_KEY, dirInfoTemp, configFile);
        System.out.println(System.getProperty("os.name"));
        System.out.println(dirInfoTemp);
        downloadDir = dirInfoTemp;
    }

    public static String getDownloadDir() {
        return downloadDir;
    }
}
