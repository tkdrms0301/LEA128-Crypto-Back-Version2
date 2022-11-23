package kumoh.config;

import kumoh.util.directory.DirectoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class FileCreateConfig implements DirCreateConfig {
    private final String dirName = "file";
    private final File fileDir = new File(dirName);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryValidator validator;

    public FileCreateConfig(DirectoryValidator validator){
        this.validator = validator;
        if (!hasDir()){
            createDir();
        }
    }

    @Override
    public boolean hasDir() {
        return fileDir.exists();
    }

    @Override
    public void createDir() {
        if (validator.hasAuthorityInDirectory()){
            fileDir.mkdir();
        }
        if (!validator.hasAuthorityInDirectory()){
            logger.error("createDir where " + dirName);
        }
        logger.info("Make Directory (" + this.getClass().getName() + ")");
    }
}
