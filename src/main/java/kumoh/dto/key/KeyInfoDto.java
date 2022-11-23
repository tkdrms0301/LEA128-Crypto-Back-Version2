package kumoh.dto.key;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KeyInfoDto implements Cloneable {
    private String keyType;
    private String keyValue;
    private String createdDate;
    private String expirationDate;
    private String validTerm;

    @Override
    public KeyInfoDto clone() {
        try {
            KeyInfoDto clone = (KeyInfoDto) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
