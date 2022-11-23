package kumoh.dto.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DBFieldData {
    private List<DBColumnName> fieldHeader;
    private List<Map<String, String>> data;
}
