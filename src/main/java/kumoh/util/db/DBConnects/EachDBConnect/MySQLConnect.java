package kumoh.util.db.DBConnects.EachDBConnect;



import kumoh.dto.db.DBConfig;
import kumoh.util.db.DBConnects.DBConnect;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLConnect extends DBConnect {
    public MySQLConnect(DBConfig dbConfig) {
        this.dbConfig = dbConfig;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
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
