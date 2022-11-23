package kumoh.util.db.DBLive.EachDBLive;



import kumoh.dto.db.DBListDTO;
import kumoh.util.db.DBLive.DBLive;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MsSQLLive extends DBLive {
    public MsSQLLive(DBListDTO dbListDTO) {
        this.dbListDTO = dbListDTO;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // "jdbc:sqlserver://localhost:50225;database=largeDB;"
            String url ="jdbc:sqlserver://" + this.dbListDTO.getDbAddress() + ";database=" + this.dbListDTO.getSchemaName() + ";encrypt=false";

            this.conn = DriverManager.getConnection(url, this.dbListDTO.getUserID(), this.dbListDTO.getUserPW());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
}
