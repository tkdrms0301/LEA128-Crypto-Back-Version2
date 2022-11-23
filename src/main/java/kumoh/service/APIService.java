package kumoh.service;

import kumoh.dto.api.APIRequestDTO;
import org.springframework.http.ResponseEntity;

public interface APIService {

    ResponseEntity<?> replaceAPI(APIRequestDTO dto);
    ResponseEntity<?> findAllAPI();
}
