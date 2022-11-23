package kumoh.dto.api;


import lombok.*;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class APISelectResponseDTO {
    private Map<String, String> list;
}
