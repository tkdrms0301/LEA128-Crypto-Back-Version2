package kumoh.dto.key;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class RSAKeyDto {
    String publicKey;
    String secretKey;
}
