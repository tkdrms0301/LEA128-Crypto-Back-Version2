package kumoh.util.crypto.util;

import java.math.BigDecimal;

public class ConvertBIGDEMICAL implements ObjectConverter {
    private final TypeConvertor<BigDecimal> typeConvertor;

    public ConvertBIGDEMICAL(){
        this.typeConvertor = new TypeConvertor<>();
    }
    @Override
    public Object alterClassType(Object val) {
        BigDecimal bigDecimal = new BigDecimal((int)val);
        return typeConvertor.getData(bigDecimal);
    }
}
