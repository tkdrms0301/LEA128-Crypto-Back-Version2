package kumoh.service.impl;

import kumoh.config.defaultconfig.APISavedInfo;
import kumoh.domain.Api;
import kumoh.dto.api.APIRequestDTO;
import kumoh.service.APIService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class APIServiceImpl implements APIService {

    private final APISavedInfo apiSavedInfo;
    @Override
    public ResponseEntity<?> replaceAPI(APIRequestDTO dto) {
        try {

            apiSavedInfo.updateApiList(dto.getApiList());
        } catch (IOException e) {
            return ResponseEntity.ok().body(new ArrayList<Api>());
        }
        return ResponseEntity.ok(APIRequestDTO.builder().apiList(APISavedInfo.getApiList()).build());
    }

    @Override
    public ResponseEntity<?> findAllAPI() {
        return ResponseEntity.ok(APISavedInfo.getApiList());
    }

}
