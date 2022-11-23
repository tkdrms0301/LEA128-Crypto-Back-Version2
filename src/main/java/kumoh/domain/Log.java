package kumoh.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
@NoArgsConstructor
public class Log {
    private String symmetricKey;
    private String title;
    private String recordDate;
    private String authorInfo;

}
