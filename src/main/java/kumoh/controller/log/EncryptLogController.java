package kumoh.controller.log;

import kumoh.config.USBConnect;
import kumoh.dto.auth.ResponseAuthDto;
import kumoh.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequiredArgsConstructor
@RequestMapping("/log/EN")
@Validated
public class EncryptLogController {

    private final LogService logServiceENImpl;
    private final USBConnect usbConnect;

    @GetMapping("/dir/{year}/{month}/{day}")
    public ResponseEntity<?> getLog(@PathVariable @NotNull Integer year, @PathVariable @NotNull Integer month, @PathVariable @NotNull Integer day){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return logServiceENImpl.getLog(year, month, day);
    }


}
