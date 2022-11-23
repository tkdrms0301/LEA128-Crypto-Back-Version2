package kumoh.dto.db;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DBListDTO {
    private String dbmsName;
    private String userID;
    private String userPW;
    private String schemaName;
    private String tableName;
    private String startPoint;
    private String dbAddress;
}
