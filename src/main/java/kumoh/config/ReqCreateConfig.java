package kumoh.config;

import kumoh.util.directory.DirectoryValidator;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class ReqCreateConfig implements DirCreateConfig {
    private final String dirName = "req";
    private final File reqDir = new File(dirName);
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final DirectoryValidator validator;

    public ReqCreateConfig(DirectoryValidator validator){
        this.validator = validator;
        if (!hasDir()){
            createDir();
        }
    }

    @Override
    public boolean hasDir() {
        return reqDir.exists();
    }

    @Override
    public void createDir() {
        if (validator.hasAuthorityInDirectory()){
            reqDir.mkdir();
        }
        if (!validator.hasAuthorityInDirectory()){
            logger.error("createDir where " + dirName);
        }
        logger.info("Make Directory (" + this.getClass().getName() + ")");
    }
}
