package kumoh.config.defaultconfig;

import kumoh.util.log.LogFileDir;
import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
@Component
public class LogDirInfo{

    private final LogFileDir logFileDir;
    private final MappingHashMap mappingHashMap;
    private static final String PATH_SPLIT = System.getProperty("os.name").contains("win")?"\\":"/";
    void loadLogDir(LinkedHashMap<String, String> configInfo) throws IOException {

        if(configInfo.containsKey("dir")) {
            String dirInfoTemp = configInfo.get("dir");

            if(dirInfoTemp.lastIndexOf(PATH_SPLIT) != dirInfoTemp.length()-1)
                dirInfoTemp += PATH_SPLIT;

            logFileDir.setDir(dirInfoTemp);
        }

    }

    void saveLogDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.saveHashMap("dir", dirInfo, configFile);
    }

    public void updateLogDir(String dirInfo, File configFile) throws IOException {
        String dirInfoTemp = dirInfo;

        if(dirInfoTemp.lastIndexOf(PATH_SPLIT) != dirInfoTemp.length()-1)
            dirInfoTemp += PATH_SPLIT;

        logFileDir.setDir(dirInfoTemp);
        mappingHashMap.updateHashMap("dir", dirInfoTemp, configFile);
    }
}
