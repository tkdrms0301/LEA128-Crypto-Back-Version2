package kumoh.util;

import kumoh.config.KeyConfig;
import kumoh.config.Encoding;
import kumoh.service.RSAService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@RequiredArgsConstructor
@Component
public class LEA128_key {
    private String key = null;

    private final KeyConfig keyConfig;

    private final RSAService rsaService;

    public byte[] getKey(){
        try {
            key = keyConfig.symmentricKeyRead();
            key = rsaService.decryptionString(key); // RSA 암호화 된 string -> 복호화
            validation(key);
            return Encoding.hexStringToByteArray(key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void setKey(String key){
        this.key = key;
    }

    private void validation(final String key) throws Exception{
        if(key == null) {
            throw new Exception("Key 가 설정되어 있지 않습니다, Key 파일을 확인해주세요.");
        }
    }
}
