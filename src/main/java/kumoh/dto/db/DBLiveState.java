package kumoh.dto.db;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DBLiveState {
    boolean running;
    LocalDateTime startTime;

    public DBLiveState(boolean running){
        this.running = running;
    }
}
