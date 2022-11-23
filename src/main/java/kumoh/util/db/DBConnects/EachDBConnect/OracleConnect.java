package kumoh.util.db.DBConnects.EachDBConnect;



import kumoh.dto.db.DBConfig;
import kumoh.dto.db.SchemaDTO;
import kumoh.util.db.DBConnects.DBConnect;

import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OracleConnect extends DBConnect {
    public OracleConnect(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        this.dbConfig.setSelectedSchema(dbConfig.getUserId().toUpperCase());
        //오라클은 스키마명 == 접속한 유저 아이디
        try {

            Class.forName("oracle.jdbc.driver.OracleDriver");
            this.dbConfig.setDriverType("oracle");
            this.dbConfig.setUrl("jdbc:" + this.dbConfig.getDriverType() + ":thin:@" + this.dbConfig.getDbAddress()); //ex)address == localhost:1521:xe  || host:port:sid(service_name)
            this.dbConfig.setConn(DriverManager.getConnection(this.dbConfig.getUrl(), this.dbConfig.getUserId(), this.dbConfig.getPwd()));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }

    @Override
    public List<SchemaDTO> findAllSchema() {
        schemaList = new ArrayList<>();
        schemaList.add(new SchemaDTO(this.dbConfig.getSelectedSchema()));
        return schemaList;
    }
}
