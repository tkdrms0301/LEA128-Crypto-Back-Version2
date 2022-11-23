package kumoh.util.crypto.util;

import static kumoh.util.crypto.util.DataTypeDeclaration.setTypeInJava;

public class DataTypeTranslation {

    public DataTypeTranslation(){
        DataTypeDeclaration.setTypeInJava();
        DataTypeDeclaration.setClassWithByte(DataTypeDescription.CLASSTYPE);
    }

    public Object alterClassType(Object val, Byte key){
        return DataTypeDescription.CLASSTYPE.get(key).alterClassType(val);
    }

    public static byte getTypeInJava(Object type){
        DataTypeDeclaration.setTypeInJava();
        return DataTypeDescription.JAVATYPE.get(type.getClass());
    }

}
