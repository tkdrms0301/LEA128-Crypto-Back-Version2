package kumoh.util.db.ThreeCycle;


import kumoh.config.KeyConfig;
import kumoh.config.defaultconfig.DBEncDirInfo;
import kumoh.domain.Log;
import kumoh.dto.db.DBCryptFilePath;
import kumoh.dto.db.DBListDTO;
import kumoh.service.RSAService;
import kumoh.util.LEA128_CTR_encrypt;
import kumoh.util.LEA128_key;
import kumoh.util.db.DBEncryptionDir;
import kumoh.util.db.DBLive.DBLive;
import kumoh.util.db.DBLive.EachDBLive.MariaDBLive;
import kumoh.util.db.DBLive.EachDBLive.MsSQLLive;
import kumoh.util.db.DBLive.EachDBLive.MySQLLive;
import kumoh.util.db.DBLive.EachDBLive.OracleLive;
import kumoh.util.log.LogDirBuilder;
import kumoh.util.log.LogFileDir;
import kumoh.util.log.LogUtils;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
public class LiveThread{

    private final DBEncryptionDir fileEncryptionDir;
    private final DBEncDirInfo fileEncDirInfo;

    private final RSAService rsaControl;
    private final LEA128_CTR_encrypt encrypt;
    private final LEA128_key key;
    private final KeyConfig keyConfig;

    private DBListDTO dto;
    private DBCryptFilePath dbCryptFilePath;


    public LiveThread(DBListDTO dbListDTO, DBCryptFilePath dbCryptFilePath, DBEncryptionDir fileEncryptionDir, DBEncDirInfo fileEncDirInfo, RSAService rsaControl,LEA128_CTR_encrypt encrypt, LEA128_key key, KeyConfig keyConfig)
    {
        dto = dbListDTO;
        this.dbCryptFilePath = dbCryptFilePath;

        this.fileEncryptionDir = fileEncryptionDir;
        this.fileEncDirInfo = fileEncDirInfo;
        this.rsaControl = rsaControl;
        this.encrypt =encrypt;
        this.key = key;
        this.keyConfig = keyConfig;

    }


    public void runlive(){
        encryptCSVUsingLEA128(dbToCSV());
    }

    public File dbToCSV(){
        DBLive dbLive = null;
        if (dto.getDbmsName() .equals("MariaDB")){
            dbLive = new MariaDBLive(dto);
        }else if (dto.getDbmsName() .equals("MySQL")){
            dbLive = new MySQLLive(dto);
        }else if (dto.getDbmsName() .equals("MsSQL")){
            dbLive = new MsSQLLive(dto);
        }else if (dto.getDbmsName() .equals("Oracle")){
            dbLive = new OracleLive(dto);
        }
        dbLive.setFilePath(dbCryptFilePath);

        String filename = dto.getDbmsName()+"_" + dto.getSchemaName()+"_" + dto.getTableName()+"_" + dto.getStartPoint();

        return dbLive.dataToCSV(filename);
    }

    public synchronized File encryptCSVUsingLEA128(File file){
        String currentPath = fileEncryptionDir.getDir();
        String encryptPath = currentPath + "\\Encoding_" + file.getName();
        try{
            encrypt.file(file, encryptPath , key);
        } catch (IOException e) {
            throw new RuntimeException("Can not find file path");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not read file");
        }

        saveLog(file.getName(), System.getProperty("user.name"));

        System.gc();
        file.delete();
        return new File(encryptPath);
    }


    private void saveLog(String title, String authorInfo){
        String symmetricKey = keyConfig.getSymmetricKey();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ldt = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        List<Log> newLog = new ArrayList<>();
        String date = ldt.toLocalDate().toString() + "/" + ldt.toLocalTime();
        newLog.add(new Log(symmetricKey, title, date, authorInfo));
        LogDirBuilder ldb = new LogDirBuilder(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), LogFileDir.getDir());
        LogUtils logUtils = new LogUtils(ldb.getDir());
        rsaControl.setLogUtils(logUtils);
        rsaControl.recordKey(newLog);
    }
}
