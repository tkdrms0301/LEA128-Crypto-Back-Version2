package kumoh.service;

import kumoh.dto.log.LogPathDTO;
import org.springframework.http.ResponseEntity;

public interface LogService {

    ResponseEntity<?> getPath();

    ResponseEntity<?> setPath(LogPathDTO logPathDTO);

    ResponseEntity<?>  getLog(Integer year, Integer month, Integer day);

    ResponseEntity<?> getDir(Integer year, Integer month, Integer day);
}
