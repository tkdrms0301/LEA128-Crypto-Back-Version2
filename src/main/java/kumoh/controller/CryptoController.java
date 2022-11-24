package kumoh.controller;

import kumoh.config.USBConnect;
import kumoh.dto.auth.ResponseAuthDto;
import kumoh.dto.ResponseDto;
import kumoh.dto.ResponseSetFilePathDto;
import kumoh.dto.decrypt.DecryptFilePathDto;
import kumoh.dto.decrypt.ResponseDecryptFilePathDto;
import kumoh.dto.decrypt.ResponseDecryptObjectPathDto;
import kumoh.dto.encrypt.EncryptFilePathDto;
import kumoh.dto.encrypt.ResponseEncryptFilePathDto;
import kumoh.dto.encrypt.ResponseEncryptObjectPathDto;
import kumoh.dto.request.RequestDecryptObjectDto;
import kumoh.dto.request.RequestEncryptObjectDto;
import kumoh.service.CryptoService;
import kumoh.util.zip.FileScanner;
import kumoh.util.zip.ZipConvertor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@RequiredArgsConstructor
@RestController
public class CryptoController {
    private final CryptoService cryptoService;
    private final USBConnect usbConnect;


    @GetMapping("/test/decrypt/{cipher}")
    public ResponseDto<Object> decryptCipher(@PathVariable byte[] cipher) {
        return new ResponseDto<>(HttpStatus.OK, cryptoService.decryptObjectUsingLEA128(cipher));
    }

    @GetMapping("/test/key/{key}")
    public ResponseDto<?> changeKey(@PathVariable String key){
        cryptoService.setKey(key);
        return new ResponseDto<>(HttpStatus.OK, "done");
    }

    @PostMapping("/encrypt-direct")
    public ResponseEntity<?> encryptObject(@RequestBody RequestEncryptObjectDto dto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        Object cipherText = cryptoService.encryptObjectUsingLEA128(dto.getPlainText());
        return new ResponseEntity<>(new ResponseEncryptObjectPathDto(true, cipherText), HttpStatus.CREATED);
    }

    @PostMapping("/decrypt-direct")
    public ResponseEntity<?> decryptObject(@RequestBody RequestDecryptObjectDto dto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        Object plainText = cryptoService.decryptObjectUsingLEA128(dto.getCipherText());
        return new ResponseEntity<>(new ResponseDecryptObjectPathDto(true, plainText), HttpStatus.CREATED);
    }

    @PostMapping("/encrypt-file")
    public ResponseEntity<?> encryptFilePath(@RequestParam("file")MultipartFile mFile) {
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }

        File encFile = cryptoService.encryptFileUsingLEA128(mFile);

        return new ResponseEntity<>(new ResponseEncryptFilePathDto(true, encFile.getPath()), HttpStatus.CREATED);
    }

    @PostMapping("/decrypt-file")
    public ResponseEntity<?> decryptFilePath(@RequestParam("file")MultipartFile mFile) {
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        File decFile = cryptoService.decryptFileUsingLEA128(mFile);
        return new ResponseEntity<>(new ResponseDecryptFilePathDto(true, decFile.getPath()), HttpStatus.CREATED);
    }

    @GetMapping("/connect")
    public ResponseEntity<?> isConnected(){
        return ResponseEntity.ok().body(cryptoService.isConnected());
    }

    @GetMapping("/decrypt-file/path")
    public ResponseEntity<?> getFilePath(){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return ResponseEntity.ok().body(cryptoService.getFileDecryptPath());
    }

    @PostMapping("/decrypt-file/path")
    public ResponseEntity<?> setFilePath(@RequestBody DecryptFilePathDto decryptFilePathDto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseSetFilePathDto(false));
        }
        return ResponseEntity.ok().body(cryptoService.setFileDecryptPath(decryptFilePathDto));
    }

    @GetMapping("/encrypt-file/path")
    public ResponseEntity<?> getEncryptFilePath(){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseAuthDto(false));
        }
        return ResponseEntity.ok().body(cryptoService.getFileEncryptPath());
    }

    @PostMapping("/encrypt-file/path")
    public ResponseEntity<?> setEncryptFilePath(@RequestBody EncryptFilePathDto encryptFilePathDto){
        if (!usbConnect.isLogin()) {
            return ResponseEntity.ok().body(new ResponseSetFilePathDto(false));
        }
        return ResponseEntity.ok().body(cryptoService.setFileEncryptPath(encryptFilePathDto));
    }

    public ResponseEntity<?> getZipFile(HttpServletResponse response) throws IOException {
        ZipConvertor zipConvertor = new ZipConvertor(new FileScanner("path")); // path : zip 으로 묶을 파일 디렉토리
        File zipFile = zipConvertor.convert("zipName.zip","path/"); // zip 이름
        setFileTransferHeader(response, zipFile.getName());

        return new ResponseEntity<>(IOUtils.toByteArray(new FileInputStream(zipFile)), HttpStatus.CREATED);
    }


    private void setFileTransferHeader(HttpServletResponse response, String filename){
        response.setHeader("Content-Disposition", "attachment; filename=\"" + URLEncoder.encode(filename, StandardCharsets.UTF_8) + "\";");
        response.setHeader("Content-Transfer-Encoding", "binary");
    }

}
