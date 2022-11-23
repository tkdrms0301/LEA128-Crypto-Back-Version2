package kumoh.service;

import kumoh.config.KeyConfig;
import kumoh.domain.Log;
import kumoh.dto.db.DBListDTO;
import kumoh.dto.key.KeyInfoDto;
import kumoh.util.RSA.RSA;
import kumoh.util.file.FileIO;
import kumoh.util.log.LogUtils;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class RSAService {
    private final KeyConfig keyConfig;
    private final FileIO fileIO;
    private LogUtils logUtils;
    private final RSA rsa;
    private FileWriter fw = null;
    private FileReader fr = null;
    private BufferedWriter bw = null;
    private BufferedReader br = null;

    public RSAService(KeyConfig keyConfig, RSA rsa, LogUtils logUtils,FileIO fileIO){
        this.keyConfig = keyConfig;
        this.rsa = rsa;
        this.fileIO = fileIO;
        this.logUtils = logUtils;
    }
    public void setLogUtils(LogUtils logUtils){
        this.logUtils = logUtils;
    }

    public boolean keyValidation() {
        String originKey = keyConfig.getSymmetricKey(); // 현재 사용중인 키 읽어들임
        String logSymmetricKey = null;
        List<Log> logList = logUtils.getLog();
        if(!logList.isEmpty())
            logSymmetricKey = rsa.decryption(logList.get(logList.size()-1).getSymmetricKey()); // 마지막 index의 대칭키만 검사
        else{
            System.out.println("로그 기록 없음");
        }
        if (originKey.equals(logSymmetricKey)) {
            System.out.println(logSymmetricKey + " 키 유효");
            return true;
        }else{
            System.out.println(logSymmetricKey + " 키 무효");
            return false;
        }
    }

    public String encryptingString(String charSequence) {
        return rsa.encryption(charSequence);
    }

    public String decryptionString(String charSequence) {
        return rsa.decryption(charSequence);

    }

    public List<Log> readLog(){
        List<Log> logs = null;
        try {
            if(!logUtils.logRead().isEmpty()){
                logs = logUtils.getLog().stream().map(this::getLog).toList();
            }else System.out.println("로그 기록 없음");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return logs;
    }

    public void recordKey(List<Log> newLog){
        List<Log> existingLog = new ArrayList<>();
        // 기존 로그 get
        try {
            if(!logUtils.logRead().isEmpty())
                existingLog = logUtils.getLog();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //기존 로그 파일에 새로운 로그내용 덮어쓰기
        for(Log log : newLog){
            String newSymmetricKey = log.getSymmetricKey();
            String newEncryptedData = rsa.encryption(log.getTitle());
            String newDate = rsa.encryption(log.getRecordDate());
            String newAuthorInfo = rsa.encryption(log.getAuthorInfo());


            existingLog.add(new Log(newSymmetricKey, newEncryptedData, newDate, newAuthorInfo));
        }
        try {
            logUtils.logWrite(existingLog);
            // 암호문 출력
            /*for(Log log : logConfig.getLog()){
                System.out.println(log.getSymmetricKey());
                System.out.println(log.getEncryptedData());
                System.out.println(log.getRecordDate());
                System.out.println(log.getAuthorInfo());
            }*/
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Log getLog(Log encryptedLog){
        Log decryptedLog = new Log();
        decryptedLog.setSymmetricKey(rsa.decryption(encryptedLog.getSymmetricKey()));
        decryptedLog.setTitle(rsa.decryption(encryptedLog.getTitle()));
        decryptedLog.setRecordDate(rsa.decryption(encryptedLog.getRecordDate()));
        decryptedLog.setAuthorInfo(rsa.decryption(encryptedLog.getAuthorInfo()));
        return decryptedLog;
    }

    public void encryptLogFile(String path, List<Log> logLines){
        try{
            for(Log logLine : logLines){
                logLine.setSymmetricKey(rsa.encryption(logLine.getSymmetricKey()));
                logLine.setTitle(rsa.encryption(logLine.getTitle()));
                logLine.setRecordDate(rsa.encryption(logLine.getRecordDate()));
                logLine.setAuthorInfo(rsa.encryption((logLine.getAuthorInfo())));
            }
            fileIO.writeLogFile(path, logLines);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Log> decryptLogFile(String path){
        List<Log> logLines = null;
        try{
            logLines = fileIO.readLogFile(path);
            for(Log logLine : logLines){
                logLine.setSymmetricKey(rsa.decryption(logLine.getSymmetricKey()));
                logLine.setTitle(rsa.decryption(logLine.getTitle()));
                logLine.setRecordDate(rsa.decryption(logLine.getRecordDate()));
                logLine.setAuthorInfo(rsa.decryption((logLine.getAuthorInfo())));
            }
            return logLines;
        }catch (IOException e){
            logLines = new ArrayList<>();
            e.printStackTrace();
        }
        return logLines;
    }

    public DBListDTO encryptDBList(DBListDTO dbListDTO){
        DBListDTO encrypteddbListDTO = new DBListDTO();
        encrypteddbListDTO.setDbmsName(rsa.encryption(dbListDTO.getDbmsName()));
        encrypteddbListDTO.setUserID(rsa.encryption(dbListDTO.getUserID()));
        encrypteddbListDTO.setUserPW(rsa.encryption(dbListDTO.getUserPW()));
        encrypteddbListDTO.setSchemaName(rsa.encryption(dbListDTO.getSchemaName()));
        encrypteddbListDTO.setTableName(rsa.encryption(dbListDTO.getTableName()));
        encrypteddbListDTO.setStartPoint(rsa.encryption(dbListDTO.getStartPoint()));
        encrypteddbListDTO.setDbAddress(rsa.encryption(dbListDTO.getDbAddress()));
        return encrypteddbListDTO;
    }
    public DBListDTO decryptDBList(DBListDTO dbListDTO){
        // 복호화
        DBListDTO decrypteddbListDTO =  new DBListDTO();
        decrypteddbListDTO.setDbmsName(rsa.decryption(dbListDTO.getDbmsName()));
        decrypteddbListDTO.setUserID(rsa.decryption(dbListDTO.getUserID()));
        decrypteddbListDTO.setUserPW(rsa.decryption(dbListDTO.getUserPW()));
        decrypteddbListDTO.setSchemaName(rsa.decryption(dbListDTO.getSchemaName()));
        decrypteddbListDTO.setTableName(rsa.decryption(dbListDTO.getTableName()));
        decrypteddbListDTO.setStartPoint(rsa.decryption(dbListDTO.getStartPoint()));
        decrypteddbListDTO.setDbAddress(rsa.decryption(dbListDTO.getDbAddress()));
        return decrypteddbListDTO;
    }

    public void encryptKeyInfo(KeyInfoDto keyInfoDto){
        if(keyInfoDto.getKeyType() != null)
            keyInfoDto.setKeyType(rsa.encryption(keyInfoDto.getKeyType()));
        if(keyInfoDto.getCreatedDate() != null)
            keyInfoDto.setCreatedDate(rsa.encryption(keyInfoDto.getCreatedDate()));
        if(keyInfoDto.getExpirationDate() != null)
            keyInfoDto.setExpirationDate(rsa.encryption(keyInfoDto.getExpirationDate()));
        if(keyInfoDto.getValidTerm() != null)
            keyInfoDto.setValidTerm(rsa.encryption(keyInfoDto.getValidTerm()));
    }

    public void decryptKeyInfo(KeyInfoDto keyInfoDto){
        if(keyInfoDto.getKeyType() != null)
            keyInfoDto.setKeyType(rsa.decryption(keyInfoDto.getKeyType()));
        if(keyInfoDto.getCreatedDate() != null)
            keyInfoDto.setCreatedDate(rsa.decryption(keyInfoDto.getCreatedDate()));
        if(keyInfoDto.getExpirationDate() != null)
            keyInfoDto.setExpirationDate(rsa.decryption(keyInfoDto.getExpirationDate()));
        if(keyInfoDto.getValidTerm() != null)
            keyInfoDto.setValidTerm(rsa.decryption(keyInfoDto.getValidTerm()));
    }

    public List<String> encryptStringList(List<String> lines){
        List<String> copyLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++){
            System.out.println("lines = " + lines.get(i));
            copyLines.add(rsa.encryption(lines.get(i)));
        }
        lines = copyLines;
        return lines;
    }

    public List<String> decryptionStringList(List<String> lines){
        List<String> copyLines = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++){
            System.out.println("lines = " + lines.get(i));
            copyLines.add(rsa.decryption(lines.get(i)));
        }
        lines = copyLines;
        return lines;
    }
}
