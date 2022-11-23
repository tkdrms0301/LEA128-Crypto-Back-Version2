package kumoh.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kumoh.config.KeyConfig;
import kumoh.config.defaultconfig.ConfigLoader;
import kumoh.config.defaultconfig.DBEncDirInfo;
import kumoh.dto.db.*;
import kumoh.util.LEA128_CTR_encrypt;
import kumoh.util.LEA128_key;
import kumoh.util.db.BackGroundDBEncrypt;
import kumoh.util.db.DBConnects.DBConnect;
import kumoh.util.db.DBConnects.EachDBConnect.MariaDBConnect;
import kumoh.util.db.DBConnects.EachDBConnect.MsSQLConnect;
import kumoh.util.db.DBConnects.EachDBConnect.MySQLConnect;
import kumoh.util.db.DBConnects.EachDBConnect.OracleConnect;
import kumoh.util.db.DBEncryptionDir;
import kumoh.util.db.DBListManage.DBList;
import kumoh.util.db.SimpleRowEncrypt;
import kumoh.util.db.ThreeCycle.LiveDBThreadList;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DBService {

    private final DBEncryptionDir fileEncryptionDir;
    private final DBEncDirInfo fileEncDirInfo;

    private final RSAService rsaControl;
    private final LEA128_CTR_encrypt encrypt;
    private final LEA128_key key;
    private final KeyConfig keyConfig;

    DBConfig dbConfig;
    DBList dbList;
    DBConnect dbConnect;
    List<DBListDTO> dbListDTOS;
    SimpleRowEncrypt simpleRowEncrypt;

    private final LiveDBThreadList threadList;

    private BackGroundDBEncrypt backGroundDBEncrypt;

    public List<SchemaDTO> dbConnection(DBConfig dbConfig) {
        this.dbConfig = dbConfig;

        if (this.dbConfig.getConnectDBType().equals("MariaDB")) {
            dbConnect = new MariaDBConnect(dbConfig);
        } else if (this.dbConfig.getConnectDBType().equals("MySQL")) {
            dbConnect = new MySQLConnect(dbConfig);
        } else if (this.dbConfig.getConnectDBType().equals("MsSQL")) {
            dbConnect = new MsSQLConnect(dbConfig);
        } else if (this.dbConfig.getConnectDBType().equals("Oracle")) {
            dbConnect = new OracleConnect(dbConfig);
        }
        return dbConnect.findAllSchema();
    }

    public List<TableDTO> chooseSchemaFindTable(SchemaName schemaName) {
        if (!this.dbConfig.getConnectDBType().equals("Oracle")) {
            dbConnect.chooseSchema(schemaName.getSchema());
        }
        return dbConnect.findAllTable();
    }

    public DBFieldData chooseTableFindData(TableName tableName) {
        dbConnect.chooseTable(tableName.getTableName());
        return dbConnect.findAllData();
    }

    public ResponseEntity<?> selectDBList(List<DBListDTO> dbListDTOS) {
        this.dbListDTOS = dbListDTOS;
        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
    }

    public DBStartList readDBList() {
        dbList = new DBList(rsaControl);
        return new DBStartList(dbList.readFileToDBList());
    }

    public DBStartList addDBList(List<DBListDTO> dbListDTOS) {
        dbList = new DBList(rsaControl);
        dbList.toLiveDBList(dbListDTOS);
        return new DBStartList(dbList.readFileToDBList());
    }

    public ResponseEntity<?> simpleEncrypt() {
        threadList.encryptDB(dbListDTOS, new DBCryptFilePath(fileEncryptionDir.getDir() + "\\"));
        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
    }

    public ResponseEntity<?> rowEncrypt(String data) {
        Map<String, Object> info = new Gson().fromJson(String.valueOf(data),
                new TypeToken<Map<String, Object>>() {
                }.getType());

        simpleRowEncrypt = new SimpleRowEncrypt(rsaControl,encrypt, key,keyConfig, fileEncryptionDir, dbConnect.getDbConfig());
        simpleRowEncrypt.rowEncrypt(info.get("data").toString());
        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
    }

//    public ResponseEntity<?> runDBEncrypt(){
//        threadList.liveDBRun(dbListDTOS, new DBCryptFilePath(fileEncryptionDir.getDir() + "\\"));
//        threadList.encDBZip();
//        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
//    }
//
//    public ResponseEntity<?> stopDBEncrypt() {
//        threadList.liveDBStop();
//        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
//    }

    public ResponseEntity<?> runDBEncrypt() {
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileNameTime = fourteen_format.format(date_now);

        backGroundDBEncrypt = new BackGroundDBEncrypt(threadList, fileEncryptionDir, dbListDTOS, fileNameTime);
        Thread t = new Thread(backGroundDBEncrypt);
        t.start();
        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
    }

    public ResponseEntity<?> stopDBEncrypt() {
        backGroundDBEncrypt.stopDBEncrypt();
        return ResponseEntity.ok(DBListResponseDTO.builder().isValid(true).build());
    }

    public DBLiveState getDBRunState() {
        return threadList.getDbLiveState();
    }


    public ResponseEntity<?> setFileEncryptPath(DBCryptFilePath dbCryptFilePath) {
        String dir = dbCryptFilePath.getDownloadPath();
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

        return ResponseEntity.ok(new DBCryptFilePath(dir));
    }
}
