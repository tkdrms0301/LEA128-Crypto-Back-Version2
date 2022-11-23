package kumoh.util.db.DBConnects.EachDBConnect;


import kumoh.dto.db.DBConfig;
import kumoh.util.db.DBConnects.DBConnect;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MariaDBConnect extends DBConnect {

    //주소(localhost...), id, pw, db타입(mssql, mysql...) 받음
    public MariaDBConnect(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        try {

            Class.forName("org.mariadb.jdbc.Driver");
            this.dbConfig.setDriverType("mysql");
            this.dbConfig.setUrl("jdbc:" + this.dbConfig.getDriverType() + "://" + this.dbConfig.getDbAddress() + "/");
            this.dbConfig.setConn(DriverManager.getConnection(this.dbConfig.getUrl(), this.dbConfig.getUserId(), this.dbConfig.getPwd()));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
}
