package kumoh.service;

import kumoh.dto.live.LiveDTO;
import kumoh.dto.live.LiveExcelDownloadRequestDTO;
import org.springframework.http.ResponseEntity;

public interface LiveCSVService {

    ResponseEntity<?> getStartLiveState();

    ResponseEntity<?> startLiveOn(LiveDTO liveDTO);

    ResponseEntity<?> getCSVDownloadPath();

    ResponseEntity<?> setCSVDownloadPath(LiveExcelDownloadRequestDTO dto);
}
