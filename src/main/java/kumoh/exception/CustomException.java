package kumoh.exception;

import kumoh.dto.error.DecryptErrorDto;
import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private boolean isDecryption = false;

    public CustomException(){

    }

    public DecryptErrorDto getErrorDto() {
        return new DecryptErrorDto(isDecryption);
    }
}
