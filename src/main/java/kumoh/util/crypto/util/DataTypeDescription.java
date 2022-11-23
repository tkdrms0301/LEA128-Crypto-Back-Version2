package kumoh.util.crypto.util;

import java.util.HashMap;
import java.util.Map;

public class DataTypeDescription {
    // 0x01 ~ 0x20 : 문자형 데이터타입 (추가할 거 있으면 추가)
    public static final byte CHAR = 0x01;
    public static final byte VARCHAR = 0x02;
    public static final byte TINYTEXT = 0x03;
    public static final byte TEXT = 0x04;
    public static final byte MEDIUMTEXT = 0x05;
    public static final byte LONGTEXT = 0x06;
    public static final byte JSON = 0x07;
    public static final byte STRING = 0x08;

    // 0x20 ~ 0x40 : 숫자형 데이터타입
    public static final byte TINYINT = 0x20;
    public static final byte SMALLINT = 0x21;
    public static final byte MEDIUMINT = 0x22;
    public static final byte INT = 0x23;
    public static final byte BIGINT = 0x24;
    public static final byte FLOAT = 0x25;
    public static final byte DEMICAL = 0x26;
    public static final byte DOUBLE = 0x27;
    public static final byte NUMERIC = 0x28;
    public static final byte REAL = 0x29;
    public static final byte SHORT = 0x30;
    public static final byte LONG = 0x31;

    // 0x40 ~ 0x50 : 날짜형 데이터타입
    public static final byte DATE = 0x40;
    public static final byte TIME = 0x41;
    public static final byte DATETIME = 0x42;
    public static final byte TIMESTAMP = 0x43;
    public static final byte YEAR = 0x44;

    // 0x50 ~ 0x60 : 이진 데이터타입
    public static final byte BINARY = 0x50;
    public static final byte BYTE = 0x51;
    public static final byte VARBINARY = 0x52;
    public static final byte TINYBLOB = 0x53;
    public static final byte BLOB = 0x54;
    public static final byte MEDIUMBLOB = 0x55;
    public static final byte LONGBLOB = 0x56;
    public static final byte BOOLEAN = 0x57;

    // 0x60 ~ 0x70 : 기타 타입
    public static final byte FILE = 0x60;

    public static final Map<Class, Byte> JAVATYPE = new HashMap<>();
    public static final Map<Byte, ObjectConverter> CLASSTYPE = new HashMap<>();







    public static void setTypeInJava(Object type, byte typeName){
        JAVATYPE.put(type.getClass(), typeName);
    }


}
