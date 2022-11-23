package kumoh.dto.zip;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ZipDTO {
    private Boolean isEncrypted;
    private String filePath;
}
