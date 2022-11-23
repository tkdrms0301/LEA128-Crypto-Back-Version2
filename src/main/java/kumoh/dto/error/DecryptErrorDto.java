package kumoh.dto.error;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DecryptErrorDto {
    private Boolean isDecryption;
}
