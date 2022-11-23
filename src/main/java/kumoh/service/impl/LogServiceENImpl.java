package kumoh.service.impl;

import kumoh.config.defaultconfig.ConfigLoader;
import kumoh.config.defaultconfig.LogDirInfo;
import kumoh.domain.Log;
import kumoh.dto.log.LogDirResponseDTO;
import kumoh.dto.log.LogPathDTO;
import kumoh.dto.log.LogResponseDTO;
import kumoh.service.LogService;
import kumoh.service.RSAService;
import kumoh.util.log.LogDirBuilder;
import kumoh.util.log.LogFileDir;
import kumoh.util.log.LogMapping;
import kumoh.util.log.LogUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogServiceENImpl implements LogService {


    private final LogMapping logMapping;
    private final LogFileDir logFileDir;
    private final LogDirInfo logDirInfo;

    private final RSAService rsaService;
    @Override
    public ResponseEntity<?> getPath() {

        String dir = LogFileDir.getDir();

        return ResponseEntity.ok(new LogPathDTO(dir));
    }

    @Override
    public ResponseEntity<?> setPath(LogPathDTO logPathDTO) {
        String dir = logPathDTO.getLogPath();
        try {
            logFileDir.setDir(dir);
            logDirInfo.updateLogDir(dir, ConfigLoader.getConfigFile()); //설정파일에 logdir이 변경되었음을 알리고 업데이트함.
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DIRECTORY NOT FOUND");
        } catch (IOException e) {
            throw new RuntimeException("setPath 에러");
        }
        return ResponseEntity.ok("Success!");
    }

    @Override
    public ResponseEntity<?> getLog(Integer year, Integer month, Integer day) {
        LogDirBuilder ldb = new LogDirBuilder(year, month, day, LogFileDir.getDir());
        LogUtils logUtils = new LogUtils(ldb.getDir());
        rsaService.setLogUtils(logUtils);
        List<Log> logs = rsaService.readLog();

        return ResponseEntity.ok(new LogResponseDTO(logs));
    }

    @Override
    public ResponseEntity<?> getDir(Integer year, Integer month, Integer day) {
        LogDirBuilder ldb = new LogDirBuilder(year, month, day, LogFileDir.getDir());
        List<String> childDirsName;
        try {
            List<File> childDirsFile = logFileDir.getChildDirs(ldb.getDir());
            childDirsName = childDirsFile.stream().map(File::getName).toList();
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DIRECTORY NOT FOUND");
        }

        return ResponseEntity.ok(new LogDirResponseDTO(true, childDirsName));
    }
}
