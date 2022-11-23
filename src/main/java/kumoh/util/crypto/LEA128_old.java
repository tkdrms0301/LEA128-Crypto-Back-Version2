package kumoh.util.crypto;

import kumoh.util.crypto.padding.PKCS5Padding;
import kumoh.util.crypto.symm.LEA;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;

public class LEA128_old {

    private static byte[] byteIV;
    private static byte[] key;
    private static BlockCipherMode cipher;

    private static byte[] ct1;

    public LEA128_old(){
        cipher = new LEA.CBC();
        byteIV = new byte[] { (byte) 0x26, (byte) 0x8D, (byte) 0x66, (byte) 0xA7, (byte) 0x35, (byte) 0xA8, (byte) 0x1A, (byte) 0x81, (byte) 0x6F, (byte) 0xBA, (byte) 0xD9, (byte) 0xFA, (byte) 0x36, (byte) 0x16, (byte) 0x25, (byte) 0x01 };
        key = Seed_CBC.key;
    }

    public void encrypt(File file, String encryptionPath) throws IOException {
        byte[] plainBytes = Files.readAllBytes(file.toPath());

        cipher.init(BlockCipher.Mode.ENCRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
        ct1 = cipher.doFinal(plainBytes);

        makeFile(ct1, encryptionPath);
    }

    public void decrypt(File file, String decryptionPath) throws IOException {
        byte[] cipherBytes = Files.readAllBytes(file.toPath());

        cipher.init(BlockCipher.Mode.DECRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
        byte[] pt1 = cipher.doFinal(ct1);

        makeFile(pt1, decryptionPath);
    }

    private void makeFile(byte[] data, String path){
        try {
            OutputStream output = new FileOutputStream(path);
            output.write(data);

        } catch (Exception e) {
            e.getStackTrace();
        }
    }

}
