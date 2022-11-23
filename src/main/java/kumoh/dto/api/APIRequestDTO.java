package kumoh.dto.api;

import kumoh.domain.Api;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class APIRequestDTO {
    private List<Api> apiList;
}
