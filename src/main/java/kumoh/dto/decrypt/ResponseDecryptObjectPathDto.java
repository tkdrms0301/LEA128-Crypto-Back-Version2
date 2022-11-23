package kumoh.dto.decrypt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseDecryptObjectPathDto {
    private boolean isDecrypted;
    private Object plainText;
}
