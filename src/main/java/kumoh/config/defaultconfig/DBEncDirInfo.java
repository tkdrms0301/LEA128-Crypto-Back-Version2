package kumoh.config.defaultconfig;

import kumoh.util.db.DBEncryptionDir;
import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
@Component
public class DBEncDirInfo {

    private final DBEncryptionDir fileEncDir;
    private final MappingHashMap mappingHashMap;

    void loadFileEncDir(LinkedHashMap<String, String> configInfo) throws IOException {
        if(configInfo.containsKey("db-enc-dir"))
            fileEncDir.setDir(configInfo.get("db-enc-dir"));
    }

    void saveFileEncDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.saveHashMap("db-enc-dir", dirInfo, configFile);
    }

    public void updateFileEncDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.updateHashMap("db-enc-dir", dirInfo, configFile);
    }
}
