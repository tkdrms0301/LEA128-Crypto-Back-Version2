package kumoh.dto.live;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LiveStatusResponseDTO {
    private Boolean isValid;
}
