package kumoh.dto.encrypt;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class EncryptFilePathDto {
    private String encryptedFilePath;
}
