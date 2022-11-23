package kumoh.config.defaultconfig;

import kumoh.util.fileEncryption.FileDecryptionDir;
import kumoh.util.txt.MappingHashMap;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

@RequiredArgsConstructor
@Component
public class FileDecDirInfo {

    private final FileDecryptionDir fileDecDir;
    private final MappingHashMap mappingHashMap;
    void loadFileDecDir(LinkedHashMap<String, String> configInfo) throws IOException {
        if(configInfo.containsKey("dec-dir"))
            fileDecDir.setDir(configInfo.get("dec-dir"));
    }

    void saveFileDecDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.saveHashMap("dec-dir", dirInfo, configFile);
    }

    public void updateFileDecDir(String dirInfo, File configFile) throws IOException {
        mappingHashMap.updateHashMap("dec-dir", dirInfo, configFile);
    }
}
