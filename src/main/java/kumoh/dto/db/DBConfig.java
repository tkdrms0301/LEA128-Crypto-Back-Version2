package kumoh.dto.db;

import lombok.*;

import java.sql.Connection;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class DBConfig {
    private Connection conn;
    private String connectDBType;    //maria, mysql, mssql, oracle...
    private String driverType;       //mysql, oracle, ...
    private String dbAddress;        //localhost:3306, ...
    private String userId;
    private String pwd;
    private String url;

    private String selectedSchema;
    private String selectedTable;

    public DBConfig(String connectDBType, String dbAddress, String userId, String pwd){
        this.connectDBType = connectDBType;
        this.dbAddress = dbAddress;
        this.userId = userId;
        this.pwd = pwd;
    }
}
