package kumoh.dto.multi;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class MultiFilePathResponseDTO {
    private List<MultiFileEachResponseDTO> data;

    public MultiFilePathResponseDTO() {
        this.data = new ArrayList<>();
    }
}
