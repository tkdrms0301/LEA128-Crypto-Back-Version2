package kumoh.controller.log;

import kumoh.config.USBConnect;
import kumoh.dto.auth.ResponseAuthDto;
import kumoh.dto.log.LogPathDTO;
import kumoh.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log")
@Validated
public class LogController {

    private final LogService logServiceImpl;
    private final USBConnect usbConnect;

    @GetMapping("/path")
    public ResponseEntity<?> getPath() {
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.getPath();
    }

    @PutMapping("/path")
    public ResponseEntity<?> setPath(@RequestBody LogPathDTO logPathDTO) {
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.setPath(logPathDTO);
    }

    @GetMapping("/dir")
    public ResponseEntity<?> getDirAboutYear(){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.getDir(null, null, null);
    }

    @GetMapping("/dir/{year}/{month}/{day}")
    public ResponseEntity<?> getLog(@PathVariable @NotNull Integer year, @PathVariable @NotNull Integer month, @PathVariable @NotNull Integer day){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.getLog(year,month,day);
    }

    @GetMapping("/dir/{year}")
    public ResponseEntity<?> getDirWithYear(@PathVariable @NotNull Integer year){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.getDir(year, null, null);
    }

    @GetMapping("/dir/{year}/{month}")
    public ResponseEntity<?> getDirWithYearAndMonth(@PathVariable @NotNull Integer year, @PathVariable @NotNull Integer month){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceImpl.getDir(year, month, null);
    }

}
