package kumoh.dto.encrypt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseEncryptFilePathDto {
    private boolean isEncrypted;
    private String encryptedFilePath;
}
