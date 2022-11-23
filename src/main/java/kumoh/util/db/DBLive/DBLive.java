package kumoh.util.db.DBLive;



import kumoh.dto.db.DBCryptFilePath;
import kumoh.dto.db.DBListDTO;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBLive {

    protected DBListDTO dbListDTO;
    protected DBCryptFilePath dbCryptFilePath;
    protected Connection conn;

    public void setFilePath(DBCryptFilePath dbCryptFilePath) {
        this.dbCryptFilePath = dbCryptFilePath;

    }

    public File dataToCSV(String filename) {
        ResultSet rs = null;

        try {

            FileWriter fw = new FileWriter(this.dbCryptFilePath.getDownloadPath() + filename + ".csv");
            BufferedWriter bw = new BufferedWriter(fw);

            String query = "SELECT * FROM " + this.dbListDTO.getTableName();

            Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery(query);


            int cols = rs.getMetaData().getColumnCount();

            for (int i = 1; i <= cols; i++) {
                bw.append(rs.getMetaData().getColumnLabel(i));
                if (i < cols) bw.append(',');
                else bw.append('\n');
            }

            rs.absolute(Integer.parseInt(this.dbListDTO.getStartPoint()) - 1);

            while (rs.next()) {

                for (int i = 1; i <= cols; i++) {
                    bw.append(rs.getString(i));
                    if (i < cols) bw.append(',');
                }
                bw.append('\n');
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.out.println("error : " + e);
        }
        return new File(this.dbCryptFilePath.getDownloadPath() + filename + ".csv");
    }

}
