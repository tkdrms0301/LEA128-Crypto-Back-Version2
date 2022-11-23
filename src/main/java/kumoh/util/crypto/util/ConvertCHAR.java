package kumoh.util.crypto.util;

public class ConvertCHAR implements ObjectConverter {
    private final TypeConvertor<Character> typeConvertor;
    public ConvertCHAR(){
        this.typeConvertor = new TypeConvertor<>();
    }

    @Override
    public Object alterClassType(Object val) {
        Character character = val.toString().charAt(0);
        return typeConvertor.getData(character);
    }
}
