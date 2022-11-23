package kumoh.util.db.DBLive.EachDBLive;




import kumoh.dto.db.DBListDTO;
import kumoh.util.db.DBLive.DBLive;

import java.sql.DriverManager;
import java.sql.SQLException;

public class OracleLive extends DBLive {
    public OracleLive(DBListDTO dbListDTO) {
        this.dbListDTO = dbListDTO;

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url ="jdbc:oracle:thin:@" + this.dbListDTO.getDbAddress();

            this.conn = DriverManager.getConnection(url, this.dbListDTO.getUserID(), this.dbListDTO.getUserPW());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
}
