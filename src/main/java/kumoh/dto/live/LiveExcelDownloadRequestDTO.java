package kumoh.dto.live;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Builder
public class LiveExcelDownloadRequestDTO {
    private String downloadPath;
}
