package kumoh.config;

public class Encoding {
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String byteArrayToHexaString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();

        for (byte data : bytes) {
            builder.append(String.format("%02X", data));
        }

        return builder.toString();
    }
}
