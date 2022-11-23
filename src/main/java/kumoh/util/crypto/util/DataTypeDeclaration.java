package kumoh.util.crypto.util;

import java.io.File;
import java.math.BigDecimal;
import java.util.Map;

import static kumoh.util.crypto.util.DataTypeDescription.*;

public class DataTypeDeclaration {
    public static void setTypeInJava(Map<Class, Byte> JAVATYPE){
        JAVATYPE.put(Character.class, CHAR);
        JAVATYPE.put(String.class, VARCHAR);
        JAVATYPE.put(String.class, TINYTEXT);
        JAVATYPE.put(String.class, TEXT);
        JAVATYPE.put(String.class, MEDIUMTEXT);
        JAVATYPE.put(String.class, LONGTEXT);
        JAVATYPE.put(String.class, JSON);

        JAVATYPE.put(Integer.class, TINYINT);
        JAVATYPE.put(Integer.class, SMALLINT);
        JAVATYPE.put(Integer.class, MEDIUMINT);
        JAVATYPE.put(Integer.class, INT);
        JAVATYPE.put(Long.class, BIGINT);



        JAVATYPE.put(Boolean.class, BOOLEAN);
        JAVATYPE.put(Short.class, SHORT);

        JAVATYPE.put(Long.class, LONG);
        JAVATYPE.put(Float.class, FLOAT);
        JAVATYPE.put(Double.class, DOUBLE);

        JAVATYPE.put(String.class, STRING);
        JAVATYPE.put(BigDecimal.class, DEMICAL);
        JAVATYPE.put(File.class, FILE);
    }

    public static void setTypeInJava(){
        JAVATYPE.put(Boolean.class, BOOLEAN);
        JAVATYPE.put(Short.class, SHORT);
        JAVATYPE.put(Integer.class, INT);
        JAVATYPE.put(Long.class, LONG);
        JAVATYPE.put(Float.class, FLOAT);
        JAVATYPE.put(Double.class, DOUBLE);
        JAVATYPE.put(Character.class, CHAR);
        JAVATYPE.put(String.class, STRING);
        JAVATYPE.put(BigDecimal.class, DEMICAL);
        JAVATYPE.put(File.class, FILE);
    }


    public static void setClassWithByte(Map<Byte, ObjectConverter> CLASSTYPE){
        //CLASSTYPE.put(CHAR, new TypeConvertor<Character>());
        CLASSTYPE.put(DEMICAL, new ConvertBIGDEMICAL()); // 나중에 하나씩 추가
//        CLASSTYPE.put(VARCHAR, String.class);
//        CLASSTYPE.put(TINYTEXT, String.class);
//        CLASSTYPE.put(TEXT, String.class);
//        CLASSTYPE.put(MEDIUMTEXT, String.class);
//        CLASSTYPE.put(LONGTEXT, String.class);
//        CLASSTYPE.put(JSON, String.class);
//
//        CLASSTYPE.put(TINYINT, Integer.class);
//        CLASSTYPE.put(SMALLINT, Integer.class);
//        CLASSTYPE.put(MEDIUMINT, Integer.class);
//        CLASSTYPE.put(INT, Integer.class);
//        CLASSTYPE.put(BIGINT, Long.class);
//        CLASSTYPE.put(FLOAT, Float.class);
//        CLASSTYPE.put(DEMICAL, BigDecimal.class);
//        CLASSTYPE.put(DOUBLE, Double.class);
//        CLASSTYPE.put(NUMERIC, Long.class);
//        CLASSTYPE.put(REAL, Short.class);
//        CLASSTYPE.put(SHORT, Short.class);
//        CLASSTYPE.put(LONG, Long.class);
//
//        CLASSTYPE.put(DATE, Date.class);
//        CLASSTYPE.put(TIME, Time.class);
//        CLASSTYPE.put(DATETIME, LocalDateTime.class);
//        CLASSTYPE.put(TIMESTAMP, Timestamp.class);
//        CLASSTYPE.put(YEAR, Date.class);
//
//
//        CLASSTYPE.put(BINARY, Byte[].class);
//        CLASSTYPE.put(BYTE, Byte.class);
//        CLASSTYPE.put(VARBINARY, Byte[].class);
//        CLASSTYPE.put(TINYBLOB, Byte[].class);
//        CLASSTYPE.put(BLOB, Byte[].class);
//        CLASSTYPE.put(MEDIUMBLOB, Byte[].class);
//        CLASSTYPE.put(LONGBLOB, Byte[].class);
//        CLASSTYPE.put(BOOLEAN, Boolean.class);




    }
}
