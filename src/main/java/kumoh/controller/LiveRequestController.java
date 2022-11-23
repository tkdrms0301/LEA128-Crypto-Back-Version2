package kumoh.controller;

import kumoh.config.USBConnect;
import kumoh.dto.auth.ResponseAuthDto;
import kumoh.dto.live.LiveDTO;
import kumoh.dto.live.LiveExcelDownloadRequestDTO;
import kumoh.service.LiveCSVService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class LiveRequestController {

    private final LiveCSVService liveCSVService;
    private final USBConnect usbConnect;

    @GetMapping("/stateLive")
    public ResponseEntity<?> getStateLive() {
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return liveCSVService.getStartLiveState();
    }
    @PostMapping("/startLive")
    public ResponseEntity<?> startLive(@RequestBody LiveDTO liveDTO){
        if (!usbConnect.isLogin()) {
            liveCSVService.startLiveOn(liveDTO);
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return liveCSVService.startLiveOn(liveDTO);
    }

    @PostMapping("/api/path")
    public ResponseEntity<?> setCSVDownloadPath(@RequestBody LiveExcelDownloadRequestDTO dto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return liveCSVService.setCSVDownloadPath(dto);
    }

    @GetMapping("/api/path")
    public ResponseEntity<?> getCSVDownloadPath(){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return liveCSVService.getCSVDownloadPath();
    }
}
