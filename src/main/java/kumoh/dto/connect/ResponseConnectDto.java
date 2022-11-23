package kumoh.dto.connect;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ResponseConnectDto {
    private Boolean isConnected;
    private Boolean isLogin;
}
