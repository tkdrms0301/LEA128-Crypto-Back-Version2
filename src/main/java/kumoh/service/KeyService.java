package kumoh.service;

import kumoh.config.KeyConfig;
import kumoh.domain.Log;
import kumoh.dto.key.KeyInfoDto;
import kumoh.dto.key.RSAKeyDto;
import kumoh.util.RSA.RSA;
import kumoh.util.file.FileIO;
import kumoh.util.fileEncryption.FileDecryptionDir;
import kumoh.util.fileEncryption.FileEncryptionDir;
import kumoh.util.log.LogFileDir;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RequiredArgsConstructor
@Service
@ToString
public class KeyService {
    private final KeyConfig keyConfig;
    private final CryptoService cryptoService;
    private final RSAService rsaService;
    private final RSA rsa;
    private final FileIO fileIO;

    private final int KEY_LENGTH = 32;
    private final char[] KEY_CHACRCTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D',
            'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'W', 'X', 'Y', 'Z'};
    private final static String PUBLIC_KEY_TYPE = "공개키";
    private final static String SECRET_KEY_TYPE = "비밀키";
    private final static String SYMMETRIC_KEY_TYPE = "대칭키";
    private final static String VALID_TERM = "2년";
    private FileEncryptionDir fileEncryptionDir;
    private FileDecryptionDir fileDecryptionDir;
    private LogFileDir logFileDir;

    public String hash(String value) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] hash = digest.digest(value.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hash);
    }
    private String bytesToHex(byte[] hash) {
        StringBuffer stringBuffer = new StringBuffer();
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) {
                stringBuffer.append('0');
            }
            stringBuffer.append(hex);
        }
        return stringBuffer.toString();
    }

    public String createKey(){
        Random random = new Random(System.currentTimeMillis());
        String newKey = "";
        String hashValue = String.valueOf(System.currentTimeMillis());
        for(int i = 0; i < KEY_LENGTH; i++){
            hashValue = hash(hashValue);
            newKey += hashValue.charAt(random.nextInt(hashValue.length() - 1));
        }
        return newKey;
    }

    public String inputKeyCheck(String input){
        if(input.length() < KEY_LENGTH){
            Random random = new Random(System.currentTimeMillis());
            String hashValue = String.valueOf(System.currentTimeMillis());
            for(int i = input.length(); i < KEY_LENGTH; i++){
                hashValue = hash(hashValue);
                input += hashValue.charAt(random.nextInt(hashValue.length() - 1));
            }
        }else if(input.length() > KEY_LENGTH){
            input = input.substring(0, KEY_LENGTH);
        }
        return input;
    }

    public List<String> fileList(String path){
        List<String> fileList = new ArrayList<>();
        if(!path.substring(path.length() - 1).equals("\\")){
            path += "\\";
        }
        recursivefileList(path, fileList);
        return fileList;

    }

    private void recursivefileList(String path, List<String> fileList){
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                recursivefileList(file.getPath(), fileList);
            } else {
                fileList.add(file.getPath());
            }
        }
    }

    public List<KeyInfoDto> checkKey(){
        KeyInfoDto publicKeyInfo = keyConfig.getPublicKeyInfo().clone();
        KeyInfoDto secretKeyInfo = keyConfig.getSecretKeyInfo().clone();
        KeyInfoDto symmetricKeyInfo = keyConfig.getSymmetricKeyInfo().clone();
        rsaService.decryptKeyInfo(publicKeyInfo);
        rsaService.decryptKeyInfo(secretKeyInfo);
        rsaService.decryptKeyInfo(symmetricKeyInfo);

        // 대칭키 RSA복호화
        String result = rsa.decryption(keyConfig.getSymmetricKey());
        symmetricKeyInfo.setKeyValue(result);

        // 키정보 리스트 반환
        List<KeyInfoDto> keyInfoDtoList = new ArrayList<>();
        keyInfoDtoList.add(publicKeyInfo);
        keyInfoDtoList.add(secretKeyInfo);
        keyInfoDtoList.add(symmetricKeyInfo);
        return keyInfoDtoList;
    }

    public void createRSAKey() throws IOException {
        // 기존 RSA키로 복호화
        // 로그파일, 대칭키, API관련 설정파일, DB관련 설정 파일읽기

        String logPath = LogFileDir.getDir();
        List<String> fileList =  fileIO.fileList(logPath);
        for(String filePath : fileList){
            System.out.println("filePath = " + filePath);
            List<Log> logs = rsaService.decryptLogFile(filePath);

            // 테스트
            for(Log log : logs){
                System.out.println("log.toString() = " + log.toString());
            }

            fileIO.writeLogFile(filePath, logs);
        }

        String symmetricKeyInfoPath = keyConfig.getKEY() + "symmetricKeyInfo.txt";
        List<String> fileLines = fileIO.readFile(symmetricKeyInfoPath);
        fileLines = rsaService.decryptionStringList(fileLines);
        fileIO.writeFile(symmetricKeyInfoPath, fileLines);

        // DB 설정파일, JSON 설정파일, 키 파일 같은 파일마다
//        String filePath = "파일 경로";
//        List<String> fileLines = fileIO.readFile(filePath);
//        for(String fileLine : fileLines){
//            fileLine = rsa.decryption(fileLine);
//            System.out.println("fileLine = " + fileLine);
//        }
//        fileIO.writeFile(filePath, fileLines);

        // DB 정보 파일
//        DBList dbList = new DBList(rsaService);
//        List<DBListDTO> dbListDTOList = dbList.readFileToDBList();


        System.out.println("getSymmetricKey : " + keyConfig.getSymmetricKey());
        String symmetricKey = rsa.decryption(keyConfig.getSymmetricKey());

        try{
            rsa.RSAKeyCreate();
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }

        // DB 정보 파일 쓰기
//        dbList.toLiveDBList(dbListDTOList);

        System.out.println("decrypt getSymmetricKey : " + symmetricKey);
        // 공개키, 비밀키 키파일에 파일쓰기
        keyConfig.symmentricKeyWrite(rsa.encryption(symmetricKey));


        // symmetricKeyInfo 정보 쓰기
        fileLines = fileIO.readFile(symmetricKeyInfoPath);
        fileLines = rsaService.encryptStringList(fileLines);
        KeyInfoDto syKeyInfoDto = new KeyInfoDto(fileLines.get(0), null ,fileLines.get(1), fileLines.get(2), fileLines.get(3));
//        fileIO.writeFile(symmetricKeyInfoPath, fileLines);
        keyConfig.symmetricKeyInfoWrite(syKeyInfoDto);

        // 키정보 파일에 쓰기
        KeyInfoDto publicKeyInfoDto = keyConfig.newPublicKeyInfo();
        rsaService.encryptKeyInfo(publicKeyInfoDto);
        keyConfig.publicKeyInfoWrite(publicKeyInfoDto);
        KeyInfoDto secretKeyInfo = keyConfig.newSecretKeyInfo();
        rsaService.encryptKeyInfo(secretKeyInfo);
        keyConfig.secretKeyInfoWrite(secretKeyInfo);

        // 새로운 RSA키로 암호화
        for(String filePath : fileList) {
            try {
                System.out.println("filePath = " + filePath);
                List<Log> logs = fileIO.readLogFile(filePath);
                for (Log log : logs){
                    System.out.println("log.toString() = " + log.toString());
                }
                rsaService.encryptLogFile(filePath, logs);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        // DB 설정파일, JSON 설정파일, 키 파일 같은 파일마다
//        List<String> fileLines = fileIO.readFile(filePath);
//        for(String fileLine : fileLines){
//            fileLine = rsa.encryption(fileLine);
//            System.out.println("fileLine = " + fileLine);
//        }
//        fileIO.writeFile(filePath, fileLines);
    }

    public boolean createRandomLEAKey()  {
        // 파일 탐색
        String encryptionPath = fileEncryptionDir.getDir();
        // 임시 폴더 경로
        String decryptionPath = fileEncryptionDir.getDir() + "\\CryptoKeyTemp" + LocalDate.now();
        // 파일 리스트
        List<String> encryptFileList = fileList(encryptionPath);
        String result = "";
        if(encryptFileList.size() == 0){
            // 파일 없는 경우에는 진행하지 않음 -> 키만 생성
            result = rsa.encryption(createKey());
            System.out.println("result = " + result);
            KeyInfoDto symmetricKeyInfo = keyConfig.newSymmetricKeyInfo();
            rsaService.encryptKeyInfo(symmetricKeyInfo);
            try {
                keyConfig.symmentricKeyWrite(result);
                keyConfig.symmetricKeyInfoWrite(symmetricKeyInfo);
            } catch (IOException e) {
                System.out.println("생성된 대칭키 암호화 에러");
                return false;
            }
        }else{
            File folder = new File(decryptionPath);
            // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
            if (!folder.exists()) {
                try{
                    folder.mkdir(); //폴더 생성합니다.
                    System.out.println("폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    System.out.println("폴더 생성 에러");
                }
            }else {
                System.out.println("이미 폴더가 생성되어 있습니다.");
            }
            // 기존 파일들 복호화
            try {
                cryptoService.decryptLEA128(decryptionPath, encryptFileList);
            }catch (IOException e){
                System.out.println("Can not find file path");
                return false;
            }catch (NoSuchAlgorithmException e){
                System.out.println("Can not read file");
                return false;
            }
            // 암호화된 파일들 삭제
            System.gc();
            for(String file : encryptFileList){
                File deleteFile = new File(file);
                System.out.println("deleteFile.delete() = " + deleteFile.delete());
            }
            // 키생성
            result = rsa.encryption(createKey());
            System.out.println("result = " + result);
            KeyInfoDto symmetricKeyInfo = keyConfig.newSymmetricKeyInfo();
            rsaService.encryptKeyInfo(symmetricKeyInfo);
            try {
                keyConfig.symmentricKeyWrite(result);
                keyConfig.symmetricKeyInfoWrite(symmetricKeyInfo);
            } catch (IOException e) {
                System.out.println("생성된 대칭키 암호화 에러");
                throw new RuntimeException(e);
            }
            // 기존에 대칭키로 복호화한 파일 새 대칭키로 암호화
            List<String> decryptFileList = fileList(decryptionPath);
            try{
                cryptoService.encryptLEA128(encryptionPath, decryptFileList);
            }catch (IOException e){
                System.out.println("Can not find file path");
                return false;
            }catch (NoSuchAlgorithmException e){
                System.out.println("Can not read file");
                return false;
            }
            System.gc();
            for(String file : decryptFileList){
                File deleteFile = new File(file);
                System.out.println("deleteFile.delete() = " + deleteFile.delete());
            }
            System.out.println("folder = " + folder.delete());
        }
        return true;
    }
    public boolean createInputLEAKey(String input){
        // 파일 탐색
        String encryptionPath = fileEncryptionDir.getDir();
        // 임시 폴더 경로
        String decryptionPath = fileEncryptionDir.getDir() + "\\CryptoKeyTemp" + LocalDate.now();
        // 파일 리스트
        List<String> encryptFileList = fileList(encryptionPath);
        String result = "";
        if(input.length() != 32) return false;

        if(encryptFileList.size() == 0){
            // 파일 없는 경우에는 진행하지 않음 -> 키만 생성
            String newSymmetricKey = inputKeyCheck(input);
            System.out.println(newSymmetricKey);
            result = rsa.encryption(newSymmetricKey);
            System.out.println("result = " + result);
            KeyInfoDto symmetricKeyInfo = keyConfig.newSymmetricKeyInfo();
            rsaService.encryptKeyInfo(symmetricKeyInfo);
            try {
                keyConfig.symmentricKeyWrite(result);
                keyConfig.symmetricKeyInfoWrite(symmetricKeyInfo);
            } catch (IOException e) {
                System.out.println("생성된 대칭키 암호화 에러");
                return false;
            }
        }else{
            File folder = new File(decryptionPath);
            // 해당 디렉토리가 없을경우 디렉토리를 생성합니다.
            if (!folder.exists()) {
                try{
                    folder.mkdir(); //폴더 생성합니다.
                    System.out.println("폴더가 생성되었습니다.");
                }
                catch(Exception e){
                    System.out.println("폴더 생성 에러");
                }
            }else {
                System.out.println("이미 폴더가 생성되어 있습니다.");
            }
            // 기존 파일들 복호화
            try {
                cryptoService.decryptLEA128(decryptionPath, encryptFileList);
            }catch (IOException e){
                System.out.println("Can not find file path");
                return false;
            }catch (NoSuchAlgorithmException e){
                System.out.println("Can not read file");
                return false;
            }
            // 암호화된 파일들 삭제
            System.gc();
            for(String file : encryptFileList){
                File deleteFile = new File(file);
                System.out.println("deleteFile.delete() = " + deleteFile.delete());
            }
            // 키생성
            String newSymmetricKey = inputKeyCheck(input);
            System.out.println(newSymmetricKey);
            result = rsa.encryption(newSymmetricKey);
            System.out.println("result = " + result);
            KeyInfoDto symmetricKeyInfo = keyConfig.newSymmetricKeyInfo();
            rsaService.encryptKeyInfo(symmetricKeyInfo);
            try {
                keyConfig.symmentricKeyWrite(result);
                keyConfig.symmetricKeyInfoWrite(symmetricKeyInfo);
            } catch (IOException e) {
                System.out.println("생성된 대칭키 암호화 에러");
                return false;
            }
            // 기존에 대칭키로 복호화한 파일 새 대칭키로 암호화
            List<String> decryptFileList = fileList(decryptionPath);
            try{
                cryptoService.encryptLEA128(encryptionPath, decryptFileList);
            }catch (IOException e){
                System.out.println("Can not find file path");
                return false;
            }catch (NoSuchAlgorithmException e){
                System.out.println("Can not read file");
                return false;
            }
            System.gc();
            for(String file : decryptFileList){
                File deleteFile = new File(file);
                System.out.println("deleteFile.delete() = " + deleteFile.delete());
            }
            System.out.println("folder = " + folder.delete());
        }
        return true;
    }
}