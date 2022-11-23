package kumoh.dto.multi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MultiFileEachResponseDTO {
    int index;
    String fileName;
    boolean success;

    public MultiFileEachResponseDTO(int index, String fileName) {
        this.index = index;
        this.fileName = fileName;
        this.success = false;
    }
}
