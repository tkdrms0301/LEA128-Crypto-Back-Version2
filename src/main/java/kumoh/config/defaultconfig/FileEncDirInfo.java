package kumoh.config.defaultconfig;

import kumoh.util.fileEncryption.FileEncryptionDir;
import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
@Component
public class FileEncDirInfo {

    private final FileEncryptionDir fileEncDir;
    private final MappingHashMap mappingHashMap;

    void loadFileEncDir(LinkedHashMap<String, String> configInfo) throws IOException {
        if(configInfo.containsKey("enc-dir"))
            fileEncDir.setDir(configInfo.get("enc-dir"));
    }

    void saveFileEncDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.saveHashMap("enc-dir", dirInfo, configFile);
    }

    public void updateFileEncDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.updateHashMap("enc-dir", dirInfo, configFile);
    }
}
