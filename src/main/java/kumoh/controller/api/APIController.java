package kumoh.controller.api;

import kumoh.config.USBConnect;
import kumoh.dto.api.APIRequestDTO;
import kumoh.dto.auth.ResponseAuthDto;
import kumoh.service.APIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api")
public class APIController {

    private final APIService apiService;
    private final USBConnect usbConnect;

    @PostMapping("/add")
    public ResponseEntity<?> add(@RequestBody APIRequestDTO dto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return apiService.replaceAPI(dto);
    }

    @GetMapping("/list")
    public ResponseEntity<?> getList(){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return apiService.findAllAPI();
    }
}
