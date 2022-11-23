
package kumoh.util.db.DBConnects;

import kumoh.dto.db.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Setter
@NoArgsConstructor

public abstract class DBConnect {

    protected DBConfig dbConfig;


    protected List<SchemaDTO> schemaList = null;


    protected List<TableDTO> tableList = null;

    protected List<DBColumnDTO> columns = null;


    public List<SchemaDTO> findAllSchema() {
        ResultSet rs = null;
        schemaList = new ArrayList<>();

        try {
            DatabaseMetaData dbmd = dbConfig.getConn().getMetaData();
            rs = dbmd.getCatalogs();

            while (rs.next()) {
                schemaList.add(new SchemaDTO(rs.getString(1)));
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
        return schemaList;
    }

    public void chooseSchema(String schemaName) {
        dbConfig.setSelectedSchema(schemaName);
        includeSchemaConn();
    }


    protected void includeSchemaConn() {

        try {
            dbConfig.getConn().close();
            dbConfig.setConn(null);
            dbConfig.setUrl("jdbc:" + dbConfig.getDriverType() + "://" + dbConfig.getDbAddress() + "/" + dbConfig.getSelectedSchema());
            dbConfig.setConn(DriverManager.getConnection(dbConfig.getUrl(), dbConfig.getUserId(), dbConfig.getPwd()));
        } catch (SQLException e) {
            System.out.println("error : " + e);
        }
    }

    public void printSchemaList() {
        System.out.println("====================Schema====================");
        for (int i = 0; i < schemaList.size(); i++) {
            System.out.println(i + 1 + ". " + schemaList.get(i).getSchemaName());
        }
        System.out.println("========================================");
    }


    public List<TableDTO> findAllTable() {
        ResultSet rs = null;
        this.tableList = new ArrayList<>();

        try {
            rs = dbConfig.getConn().getMetaData().getTables(dbConfig.getSelectedSchema(), null, null, new String[]{"TABLE"});
            while (rs.next()) {
                tableList.add(new TableDTO(rs.getString("TABLE_NAME")));
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

        return this.tableList;
    }


    public void printTableList() {
        System.out.println("====================Table====================");
        for (int i = 0; i < tableList.size(); i++) {
            System.out.println(i + 1 + ". " + tableList.get(i).getTableName());
        }
        System.out.println("========================================");
    }

    public void chooseTable(String tableName) {
        dbConfig.setSelectedTable(tableName);
    }


    public List<DBColumnDTO> findAllColumns() {
        ResultSet rs = null;
        this.columns = new ArrayList<>();

        try {
            rs = dbConfig.getConn().getMetaData().getColumns(null, dbConfig.getSelectedSchema(), dbConfig.getSelectedTable(), null);
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

    public void printColumnList() {
        for (int i = 0; i < columns.size(); i++) {
            System.out.println(columns.get(i).getColumnName());
        }
    }

    public DBFieldData findAllData() {
        findAllColumns();

        ResultSet rs = null;
        Statement stmt = null;
        DBFieldData totalResult = null;
        List<Map<String, String>> dataResult = null;
        List<DBColumnName> columnResult = null;

        try {

            String query = "SELECT * FROM " + dbConfig.getSelectedTable();

            stmt = dbConfig.getConn().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt.executeQuery(query);

            ResultSetMetaData md = rs.getMetaData();
            int numCols = md.getColumnCount();
            List<String> colNames = IntStream.range(0, numCols).mapToObj(i -> {
                        try {
                            return md.getColumnName(i + 1);
                        } catch (SQLException e) {
                            e.printStackTrace();
                            return "?";
                        }
                    })
                    .collect(Collectors.toList());

            columnResult = new ArrayList<>();
            DBColumnName idColumn = new DBColumnName("id","ID");
            columnResult.add(idColumn);
            String columnName = "";
            for (int i = 0; i < columns.size(); i++){

                if (columns.get(i).getColumnName().equals("ID") || columns.get(i).getColumnName().equals("id") || columns.get(i).getColumnName().equals("Id")){
                    columnName = "DB_ID";
                }else{
                    columnName = columns.get(i).getColumnName();
                }
                DBColumnName dbColumnName = new DBColumnName(columnName, columnName.toUpperCase());
                columnResult.add(dbColumnName);
            }

            dataResult = new ArrayList<>();


            int muiId = 0;
            while (rs.next()) {

                ResultSet finalRs = rs;
                HashMap<String,String> hashMap = new LinkedHashMap<>();
                hashMap.put("id",Integer.toString(++muiId));
                String s = "";
                String originColumnName = "";
                for (int i = 0; i <colNames.size(); i++){
                    try {
                        if (colNames.get(i).equals("ID") || colNames.get(i).equals("id")||colNames.get(i).equals("Id")){
                            s = "DB_ID";
                        }else{
                            s = colNames.get(i);
                        }
                        originColumnName = colNames.get(i);
                        hashMap.put(s,finalRs.getObject(originColumnName).toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                dataResult.add(hashMap);
            }

        } catch (
                SQLException e) {
            System.out.println("error : " + e);
        } finally {
            try {
                if (dbConfig.getConn() != null && !rs.isClosed()) {
                    rs.close();
                }
                if (dbConfig.getConn() != null && !stmt.isClosed()) {
                    rs.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
        totalResult = new DBFieldData(columnResult,dataResult);

        return totalResult;
    }
}
