package kumoh.util.db.DBLive.EachDBLive;




import kumoh.dto.db.DBListDTO;
import kumoh.util.db.DBLive.DBLive;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLLive extends DBLive {
    public MySQLLive(DBListDTO dbListDTO) {
        this.dbListDTO = dbListDTO;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url ="jdbc:mysql://" + this.dbListDTO.getDbAddress() + "/" + this.dbListDTO.getSchemaName();

            this.conn = DriverManager.getConnection(url, this.dbListDTO.getUserID(), this.dbListDTO.getUserPW());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
}
