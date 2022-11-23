package kumoh.util.db.DBConnects.EachDBConnect;


import kumoh.dto.db.DBColumnDTO;
import kumoh.dto.db.DBConfig;
import kumoh.dto.db.TableDTO;
import kumoh.util.db.DBConnects.DBConnect;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

//시도해봐야함. 수정 요함
public class MsSQLConnect extends DBConnect {
    public MsSQLConnect(DBConfig dbConfig) {
        this.dbConfig = dbConfig;
        try {

            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            this.dbConfig.setDriverType("sqlserver");
            // "jdbc:sqlserver://localhost:50225;database=largeDB;"
            this.dbConfig.setUrl("jdbc:" + this.dbConfig.getDriverType() + "://" + this.dbConfig.getDbAddress() + ";encrypt=false");
            this.dbConfig.setConn(DriverManager.getConnection(this.dbConfig.getUrl(), this.dbConfig.getUserId(), this.dbConfig.getPwd()));

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
    @Override
    protected void includeSchemaConn() {

        try {

            dbConfig.getConn().close();
            dbConfig.setConn(null);
            dbConfig.setUrl("jdbc:" + this.dbConfig.getDriverType() + "://" + this.dbConfig.getDbAddress() + ";database=" + this.dbConfig.getSelectedSchema() + ";encrypt=false");
            dbConfig.setConn(DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUserId(), dbConfig.getPwd()));
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }
    @Override
    public List<TableDTO> findAllTable() {
        ResultSet rs = null;
        this.tableList = new ArrayList<>();

        try {
            rs = dbConfig.getConn().getMetaData().getTables(dbConfig.getSelectedSchema(), null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tableList.add(new TableDTO(rs.getString("TABLE_NAME")));
            }
        }catch (SQLException e){
            System.out.println("error : " + e);

        }finally {
            try {
                if (dbConfig.getConn() != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return this.tableList;
    }

    @Override
    public List<DBColumnDTO> findAllColumns() {
        ResultSet rs = null;
        this.columns = new ArrayList<>();

        try {
            rs = dbConfig.getConn().getMetaData().getColumns(dbConfig.getSelectedSchema(), null, dbConfig.getSelectedTable(), null);

            while (rs.next()) {
                columns.add(new DBColumnDTO(rs.getString("COLUMN_NAME")));
            }
        } catch (SQLException e) {
            System.out.println("error : " + e);
        } finally {
            try {
                if (dbConfig.getConn() != null && !rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return this.columns;
    }

}
