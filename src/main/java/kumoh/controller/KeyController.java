package kumoh.controller;

import kumoh.config.KeyConfig;
import kumoh.dto.ResponseDto;
import kumoh.dto.key.InputKey;
import kumoh.dto.key.KeyInfoDto;
import kumoh.dto.key.RSAKeyDto;
import kumoh.service.CryptoService;
import kumoh.service.KeyService;
import kumoh.util.RSA.RSA;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@RestController
public class KeyController {
    @Autowired
    KeyService keyService;
    @Autowired
    private CryptoService cryptoService;
    @Autowired
    private RSA rsa;
    @Autowired
    private KeyConfig keyConfig;

    @GetMapping("/key-create")
    public ResponseDto<Boolean> keyCreate(){
        try{
            rsa.RSAKeyCreate();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }
        return new ResponseDto<Boolean>(HttpStatus.OK, true);
    }

    // 키 확인
    @GetMapping("/key-check")
    public ResponseDto<List<KeyInfoDto>> keyCheck(){
        return new ResponseDto<>(HttpStatus.OK, keyService.checkKey());
    }

    // 공개키, 비밀키 쌍 생성
    @GetMapping("/key-create/rsa")
    public ResponseDto<Boolean> keyCreateRSA(){
        try{
            keyService.createRSAKey();
        } catch (IOException e) {
            return new ResponseDto<>(HttpStatus.OK, false);
        }
        return new ResponseDto<>(HttpStatus.OK, true);
    }

    // 난수 대칭키 생성
    @GetMapping("/key-create/lea")
    public ResponseDto<Boolean> keyCreateLEA(){
        return new ResponseDto<>(HttpStatus.OK, keyService.createRandomLEAKey());
    }

    // 입력값 대칭키 생성
    @PostMapping("/key-create/input-lea")
    public ResponseDto<Boolean> keyCreateInputLEA(@RequestBody InputKey inputKey){
        return new ResponseDto<>(HttpStatus.OK, keyService.createInputLEAKey(inputKey.getInput()));
    }
}
