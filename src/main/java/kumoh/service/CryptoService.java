package kumoh.service;

import kumoh.config.KeyConfig;
import kumoh.config.defaultconfig.ConfigLoader;
import kumoh.config.defaultconfig.FileDecDirInfo;
import kumoh.config.defaultconfig.FileEncDirInfo;
import kumoh.dto.ResponseGetFilePathDto;
import kumoh.domain.Log;
import kumoh.dto.ResponseSetFilePathDto;
import kumoh.dto.connect.ResponseConnectDto;
import kumoh.dto.decrypt.DecryptFilePathDto;
import kumoh.dto.encrypt.EncryptFilePathDto;
import kumoh.dto.multi.*;
import kumoh.util.LEA128_CTR_decrypt;
import kumoh.util.LEA128_CTR_encrypt;
import kumoh.util.LEA128_key;
import kumoh.util.directory.DirectoryValidator;
import kumoh.util.file.FileDirList;
import kumoh.util.fileEncryption.FileDecryptionDir;
import kumoh.util.fileEncryption.FileEncryptionDir;
import kumoh.util.log.LogDirBuilder;
import kumoh.util.log.LogFileDir;
import kumoh.util.log.LogUtils;
import kumoh.util.usb.USBConnectListener;
import kumoh.util.usb.USBHandler;
import kumoh.util.usb.USBLoginListener;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CryptoService {
    private final RSAService rsaControl;
    private final LEA128_key key;
    private final LEA128_CTR_decrypt decrypt;
    private final LEA128_CTR_encrypt encrypt;
    private final KeyConfig keyConfig;

    private final FileEncryptionDir fileEncryptionDir;
    private final FileEncDirInfo fileEncDirInfo;
    private final FileDecryptionDir fileDecryptionDir;
    private final FileDecDirInfo fileDecDirInfo;

    private final FileDirList fileDirList;
    private static String progressPath = "file/";

    private static String filePath = "file/";
    private static String requestFilePath = "req/";

    private final USBHandler handler;

    public static String getProgressPath () {
        return progressPath;
    }

    public static String getRequestFilePath() {
        return requestFilePath;
    }

    public void encryptLEA128(String path, List<String> fileList) throws IOException, NoSuchAlgorithmException{
        for(String filePath : fileList){
            File file = new File(filePath);
            System.out.println(filePath);
            encrypt.file(file, path + "/" + file.getName(), key);
            saveLog(file.getName(), System.getProperty("user.name"));
        }
    }
    public void decryptLEA128(String path, List<String> fileList) throws IOException, NoSuchAlgorithmException{
        for(String filePath : fileList){
            File file = new File(filePath);
            decrypt.file(file, path + "/" + file.getName(), key);
            saveLog(file.getName(), System.getProperty("user.name"));
        }
    }

    public File encryptCSVUsingLEA128(File file){
        String encryptPath = progressPath+"live/"+"Encoding_" +file.getName();
        File dir = new File(progressPath+"live");
        try{
            if(!dir.exists()){
                dir.mkdirs();
            }

            encrypt.file(file, encryptPath , key);
            saveLog(file.getName(), System.getProperty("user.name"));
        } catch (IOException e) {
            throw new RuntimeException("Can not find file path");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not read file");
        }

        saveLog(file.getName(), System.getProperty("user.name"));
        return new File(encryptPath);
    }

    public File encryptFileUsingLEA128(MultipartFile mFile){
        File file = convert(mFile);
        String encryptPath;

        if(null == fileEncryptionDir.getDir()){
            encryptPath = filePath + "/Encoding_" + file.getName();
        }else{
            encryptPath = fileEncryptionDir.getDir() + "/Encoding_" + file.getName();
        }
        try {
            long i = System.currentTimeMillis();
            encrypt.file(file, encryptPath, key);
            System.out.println("파일 암호화 시간 : " + (System.currentTimeMillis() - i));
        } catch (IOException e) {
            throw new RuntimeException("Can not find file path");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not read file");
        }

        saveLog(mFile.getName(), System.getProperty("user.name"));
        return new File(encryptPath);


    }

    public Object encryptObjectUsingLEA128(Object cipher) {
        try {
            Object encObject = encrypt.object(cipher, key);
            saveLog("textEncryption", System.getProperty("user.name"));
            return encObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Object decryptObjectUsingLEA128(byte[] cipher) {
        try {
            Object decObject = decrypt.object(cipher, key);
            saveLog("textDecryption", System.getProperty("user.name"));
            return decObject;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public File decryptFileUsingLEA128(MultipartFile mFile){
        File file = convert(mFile);

        String decryptPath;
        if(null == fileDecryptionDir.getDir()){
            decryptPath = filePath + "/Decoding_" + file.getName();
        }else{
            decryptPath = fileDecryptionDir.getDir() + "/Decoding_" + file.getName();
        }
        try {
            long i = System.currentTimeMillis();

            decrypt.file(file, decryptPath, key);
            System.out.println("파일 복호화 시간 : " + (System.currentTimeMillis() - i));

        } catch (IOException | NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not find file path");
        }

        saveLog(mFile.getName(), System.getProperty("user.name"));

        return new File(decryptPath);
    }

    public ResponseConnectDto isConnected(){
        new USBConnectListener(handler);
        new USBLoginListener(handler);
        handler.validate();
        return new ResponseConnectDto(handler.isConnected(), handler.isLogin());
    }

    public ResponseSetFilePathDto setFilePath(String path){
        DirectoryValidator validator = new DirectoryValidator();
        validator.setDirectory(path);
        if (validator.hasAuthorityInDirectory()){
            filePath = validator.getDirectory();
            return new ResponseSetFilePathDto(true);
        }
        return new ResponseSetFilePathDto(false);
    }

    public ResponseGetFilePathDto getFilePath(){
        return new ResponseGetFilePathDto(filePath);
    }

    public void setKey(String key){
        setKey(key);
    }

    private File convert(MultipartFile mFile) {
        File file = new File(requestFilePath + decodingKor(mFile.getOriginalFilename()));
        try {

            BufferedInputStream in = new BufferedInputStream(mFile.getInputStream());
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(requestFilePath + decodingKor(mFile.getOriginalFilename())));
            byte[] buff = new byte[32 * 1024];
            int len = 0;
            while ((len = in.read(buff)) > 0) //If necessary readLine()
                out.write(buff, 0, len);
            in.close();
            out.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }

    private String decodingKor(String name){
        return URLDecoder.decode(name, StandardCharsets.UTF_8);
    }

    private void saveLog(String title, String authorInfo){
        String symmetricKey = keyConfig.getSymmetricKey();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ldt = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        List<Log> newLog = new ArrayList<>();
        String date = ldt.toLocalDate().toString() + " " + ldt.toLocalTime();
        newLog.add(new Log(symmetricKey, title, date, authorInfo));
        LogDirBuilder ldb = new LogDirBuilder(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), LogFileDir.getDir());
        LogUtils logUtils = new LogUtils(ldb.getDir());
        rsaControl.setLogUtils(logUtils);
        rsaControl.recordKey(newLog);
    }

    public ResponseEntity<?> setFileEncryptPath(EncryptFilePathDto encryptFilePathDto) {
        String dir = encryptFilePathDto.getEncryptedFilePath();
        try {
            fileEncryptionDir.setDir(dir);
            fileEncDirInfo.updateFileEncDir(dir, ConfigLoader.getConfigFile());
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DIRECTORY NOT FOUND");
        } catch (IOException e) {
            throw new RuntimeException("setPath 에러");
        }
        return ResponseEntity.ok("Success!");
    }
    public ResponseEntity<?> getFileEncryptPath() {

        String dir = fileEncryptionDir.getDir();

        return ResponseEntity.ok(new EncryptFilePathDto(dir));
    }

    public ResponseEntity<?> setFileDecryptPath(DecryptFilePathDto decryptFilePathDto) {
        String dir = decryptFilePathDto.getDecryptedFilePath();
        try {
            fileDecryptionDir.setDir(dir);
            fileDecDirInfo.updateFileDecDir(dir, ConfigLoader.getConfigFile());
        } catch (FileNotFoundException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "DIRECTORY NOT FOUND");
        } catch (IOException e) {
            throw new RuntimeException("setPath 에러");
        }
        return ResponseEntity.ok("Success!");
    }
    public ResponseEntity<?> getFileDecryptPath() {

        String dir = fileDecryptionDir.getDir();

        return ResponseEntity.ok(new DecryptFilePathDto(dir));
    }

    public boolean encryptFileByPathUsingLEA128(String path) {
        File file = new File(path);
        if(file.exists()){
            String encryptPath = fileEncryptionDir.getDir() + "/Encoding_" + file.getName();
            try {
                encrypt.file(file, encryptPath, key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            saveLog(file.getName(), "author");
            return true;
        }else{
            return false;
        }
    }

    public boolean decryptFileByPathUsingLEA128(String path) {
        File file = new File(path);
        if(file.exists()){
            String encryptPath = fileDecryptionDir.getDir() + "/Decoding_" + file.getName();
            try {
                decrypt.file(file, encryptPath, key);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
            saveLog(file.getName(), "author");
            return true;
        }else{
            return false;
        }

    }

    public DirListDto fileNameList(String folderPath){
        List<String> newFileNameList = fileDirList.fileDirNameList(folderPath);
        List<FileNameDto> fileNameDtoList = new ArrayList<>();
        for (String fileName : newFileNameList) {
            fileNameDtoList.add(new FileNameDto(fileName));
        }
        return new DirListDto(fileNameDtoList);
    }

    public ResponseEntity<?> encryptMultipleFile(MultiFilePathDTO multipleFilePathDTO) {

        MultiFilePathResponseDTO multipleFilePathResponseDTO = new MultiFilePathResponseDTO();

        for (MultiFileEachDTO multiFileEachDTO : multipleFilePathDTO.getData()) {
            MultiFileEachResponseDTO multiFileEachResponseDTO = new MultiFileEachResponseDTO(multiFileEachDTO.getIndex(), multiFileEachDTO.getFileName());
            if(encryptFileByPathUsingLEA128(multiFileEachDTO.getFileName())){
                multiFileEachResponseDTO.setSuccess(true);
            }else{
                multiFileEachResponseDTO.setSuccess(false);

                System.gc();
                String[] split = multiFileEachResponseDTO.getFileName().split("\\\\");
                String newPath = "";
                for (int i = 0; i < split.length; i++) {
                    if (i == split.length - 1)
                        newPath += File.separator + "Encoding_";
                    else
                        newPath += split[i];
                }

                new File(newPath).delete();
            }

            multipleFilePathResponseDTO.getData().add(multiFileEachResponseDTO);
        }
        return ResponseEntity.ok(multipleFilePathResponseDTO);
    }

    public ResponseEntity<?> decryptMultipleFile(MultiFilePathDTO multiFilePathDTO) {

        MultiFilePathResponseDTO multipleFilePathResponseDTO = new MultiFilePathResponseDTO();

        for (MultiFileEachDTO multiFileEachDTO : multiFilePathDTO.getData()) {
            MultiFileEachResponseDTO multiFileEachResponseDTO = new MultiFileEachResponseDTO(multiFileEachDTO.getIndex(), multiFileEachDTO.getFileName());
            if(decryptFileByPathUsingLEA128(multiFileEachDTO.getFileName())) {
                multiFileEachResponseDTO.setSuccess(true);
            }else{
                multiFileEachResponseDTO.setSuccess(false);

                System.gc();
                String[] split = multiFileEachResponseDTO.getFileName().split("\\\\");
                String newPath = "";
                for (int i = 0; i < split.length; i++) {
                    if (i == split.length - 1)
                        newPath += File.separator + "Decoding_";
                    else
                        newPath += split[i];
                }

                new File(newPath).delete();
            }

            multipleFilePathResponseDTO.getData().add(multiFileEachResponseDTO);
        }
        return ResponseEntity.ok(multipleFilePathResponseDTO);
    }

}