package kumoh.service.impl;

import kumoh.async.StartEncryptCSV;
import kumoh.async.StartLiveCSV;
import kumoh.config.AsyncConfig;
import kumoh.config.USBConnect;
import kumoh.config.defaultconfig.APISavedInfo;
import kumoh.config.defaultconfig.DownloadDirInfo;
import kumoh.domain.Api;
import kumoh.dto.live.LiveDTO;
import kumoh.dto.live.LiveExcelDownloadRequestDTO;
import kumoh.dto.live.LiveExcelDownloadResponseDTO;
import kumoh.dto.live.LiveStatusResponseDTO;
import kumoh.service.CryptoService;
import kumoh.service.LiveCSVService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LiveCSVServiceImpl implements LiveCSVService {

    private final static List<Api> API_LIST = APISavedInfo.getApiList();
    private final StartLiveCSV startLiveCSV;
    private final AsyncConfig asyncConfig;
    private final StartEncryptCSV encryptCSV;
    private final DownloadDirInfo downloadDirInfo;

    private final USBConnect usbConnect;

    @Override
    public ResponseEntity<?> getStartLiveState() {
        LiveDTO liveDTO = new LiveDTO();
        liveDTO.setRunning(asyncConfig.getStartTime() != null);
        liveDTO.setStartTime(asyncConfig.getStartTime());
        return ResponseEntity.ok(liveDTO);
    }

    @Override
    public ResponseEntity<?> startLiveOn(LiveDTO liveDTO) {
        LiveStatusResponseDTO dto = new LiveStatusResponseDTO();


        if( usbConnect.isLogin() && liveDTO.getRunning() && (asyncConfig.getStartTime() == null)){
            dto.setIsValid(true);
            asyncConfig.start(liveDTO.getStartTime());
            startLiveCSV.start();
            try {
                startLiveCSV.apiRequestAPIListStart(API_LIST, "id", DownloadDirInfo.getDownloadDir());

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }else if(usbConnect.isLogin() && liveDTO.getRunning() && asyncConfig.getStartTime() != null ){
            dto.setIsValid(false);
        }else {
            liveDTO.setStartTime(null);
            startLiveCSV.end();
            dto.setIsValid(false);

        }
        return ResponseEntity.ok(dto);
    }

    @Override
    public ResponseEntity<?> getCSVDownloadPath() {
        return ResponseEntity.ok(LiveExcelDownloadRequestDTO.builder().downloadPath(DownloadDirInfo.getDownloadDir()).build());
    }

    @Override
    public ResponseEntity<?> setCSVDownloadPath(LiveExcelDownloadRequestDTO dto) {
        try {
            downloadDirInfo.updateDownloadDir(dto.getDownloadPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return ResponseEntity.ok(LiveExcelDownloadResponseDTO.builder().isValid(true).build());
    }

    private String getPath(Api api) {
        return CryptoService.getProgressPath()+"live/"+api.getTableName()+".csv";//cryptoService.getProgressPath()
    }

}
