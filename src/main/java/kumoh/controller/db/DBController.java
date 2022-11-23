package kumoh.controller.db;

import kumoh.dto.db.*;
import kumoh.service.DBService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class DBController {

    @Autowired
    private  DBService dbService;


    @PostMapping("/dbConnect/init")
    public ResponseEntity<List<SchemaDTO>> dbConnectInit(@RequestBody DBConfig dbConfig) {
        return new ResponseEntity<>(dbService.dbConnection(dbConfig), HttpStatus.CREATED);
    }

    @PostMapping("/dbConnect/schema")
    public ResponseEntity<List<TableDTO>> selectSchema(@RequestBody SchemaName schemaName) {
        return new ResponseEntity<>(dbService.chooseSchemaFindTable(schemaName), HttpStatus.CREATED);
    }

    @PostMapping("/dbConnect/table")
    public ResponseEntity<DBFieldData> selectTable(@RequestBody TableName tableName) {
        return new ResponseEntity<>(dbService.chooseTableFindData(tableName), HttpStatus.CREATED);
    }


    @GetMapping("/api/list/db")
    public ResponseEntity<DBStartList> findDBList() {
        return new ResponseEntity<>(dbService.readDBList(), HttpStatus.CREATED);
    }


    @PostMapping("/api/add/db")
    public ResponseEntity<DBStartList> addDBList(@RequestBody DBStartList dbListDTOS) {
        return new ResponseEntity<>(dbService.addDBList(dbListDTOS.getDbStartList()), HttpStatus.CREATED);
    }


    @PostMapping("/api/list/db/selectList")
    public ResponseEntity<?> chooseDBList(@RequestBody DBStartList dbListDTOS) {
        return dbService.selectDBList(dbListDTOS.getDbStartList());
    }

    @PostMapping("/api/simple-encrypt/db")
    public ResponseEntity<?> simpleEncrypt() {
        return dbService.simpleEncrypt();
    }

    @PostMapping("/api/row-encrypt/db")
    public ResponseEntity<?> rowEncrypt(@RequestBody String data) {
        return dbService.rowEncrypt(data);
    }


    @PostMapping("/startLive/db")
    public ResponseEntity<?> startDBLive() {
        return dbService.runDBEncrypt();
    }


    @PostMapping("/stopLive/db")
    public ResponseEntity<?> stopDBLive() {
        return dbService.stopDBEncrypt();
    }


    @GetMapping("/stateLive/db")
    public ResponseEntity<DBLiveState> getDBRunState() {
        return new ResponseEntity<>(dbService.getDBRunState(), HttpStatus.CREATED);
    }

    @PutMapping("/db-encrypt-file/path")
    public ResponseEntity<?> setFileEncryptPath(@RequestBody DBCryptFilePath dbCryptFilePath) {
        return dbService.setFileEncryptPath(dbCryptFilePath);
    }
    @GetMapping("/db-encrypt-file/path")
    public ResponseEntity<?> getFileEncryptPath() {
        return dbService.getFileEncryptPath();
    }

}