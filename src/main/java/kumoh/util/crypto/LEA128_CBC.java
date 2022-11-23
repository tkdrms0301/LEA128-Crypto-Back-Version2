package kumoh.util.crypto;

import kumoh.util.crypto.padding.PKCS5Padding;
import kumoh.util.crypto.symm.LEA;
import kumoh.util.crypto.util.DataTypeTranslation;

import java.io.*;
import java.nio.file.Files;

public class LEA128_CBC {
    public static BlockCipherMode cipher;
    public static byte[] key;
    public static byte[] byteIV;

    public static int len = 0; // 블록 갯수

    public LEA128_CBC(){
        cipher = new LEA.CBC();
        key = Seed_CBC.key;
        byteIV = new byte[] { (byte) 0x26, (byte) 0x8D, (byte) 0x66, (byte) 0xA7, (byte) 0x35, (byte) 0xA8, (byte) 0x1A, (byte) 0x81, (byte) 0x6F, (byte) 0xBA, (byte) 0xD9, (byte) 0xFA, (byte) 0x36, (byte) 0x16, (byte) 0x25, (byte) 0x01 };
    }


    
    // byte 배열만큼 암호화
    // 매개변수 : byte 배열
    // return : 암호화된 byte 배열
    public static byte[] encrypt(byte[] plainBytes){
        cipher.init(BlockCipher.Mode.ENCRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
        return cipher.doFinal(plainBytes);
    }

    // byte 배열만큼 복호화
    // 매개변수 : byte 배열
    // return : 복호화된 byte 배열
    public static byte[] decrypt(byte[] baos){
        cipher.init(BlockCipher.Mode.DECRYPT, key, byteIV); // cipher 초기화 (LEA 는 무조건 해야하는듯)
        cipher.setPadding(new PKCS5Padding(16));
        return cipher.doFinal(baos);
    }

    
    // 파일 끊어 읽기
    // 매개변수 : file의 BufferedInputStream, block size
    // return : size 만큼의 ByteArrayOutputStream 
    public static ByteArrayOutputStream readFromByte(BufferedInputStream bis, long size) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            for(int b = 0; (b = bis.read()) != -1;) {
                baos.write(b);

                if(baos.size()+1>size)
                    break;
            }
        }catch (Exception e) {
            e.getMessage();
        }
        return baos;
    }



    // 파일 암호화
    // 매개변수 : File, encrypt file 저장경로
    // return : 없음
    public static void encryptFile(File file, String encryptionPath) throws IOException {
        File wFile = new File(encryptionPath);

        wFile.createNewFile();
        OutputStream out = new FileOutputStream(wFile);

        int totalSize = 0;

        // 첫 바이트에 타입 구분자 삽입
        out.write(DataTypeTranslation.getTypeInJava(file));


        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); // inputStream 상속받은 모든 객체
            int dividByte = 16; // 16Byte
            boolean isTheEnd = true;
            boolean firstRound = true;

            do {
                ByteArrayOutputStream baos = readFromByte(bis, dividByte);
                if (baos.size() == 0) // 더이상 읽어들일게 없으면 break
                    break;
                if (firstRound && (baos.size() < 16)) // 첫번째 라운드에 size 가 16바이트 미만이면 아래 if 문에서 처리
                    break;
                totalSize += baos.size();
                out.write(encrypt(baos.toByteArray())); // 16바이트 블록 암호문 블록 삽입
                len++;
                firstRound = false;
            }while (isTheEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (totalSize == 0){ // 16 바이트 이하는 추가 패딩 작업 이후 패딩된 블록으로 암호화
            byte[] bytes16 = new byte[16];
            byte[] plainBytesUnder16 = Files.readAllBytes(file.toPath());
            int plainBytesUnder16Index = 0;

            for (int i = 0; i < bytes16.length; i++) {
                if (plainBytesUnder16Index < plainBytesUnder16.length)
                    bytes16[i] = plainBytesUnder16[plainBytesUnder16Index++];
            }
            byte[] encryptBytes16 = encrypt(bytes16);
            out.write(encryptBytes16);
        }

        out.close();

    }

    // 파일 복호화
    // 매개변수 : File, Decrypt 될 파일 경로
    // return : 첫번째 바이트 구분자 바이트
    public static byte decryptFile(File file, String decryptionPath) throws IOException {

        File wFile = new File(decryptionPath);

        wFile.createNewFile();
        OutputStream out = new FileOutputStream(wFile);

        int decryptClearLen = 0;
        byte type = -1;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); // inputStream 상속받은 모든 객체
            int dividByte = 32; // 32Byte (암호화가 진행되면 16byte -> 32byte이기 때문)
            boolean isTheEnd = true;
            type = (byte)bis.read(); // 첫 암호화 타입 바이트 제거
            do {
                ByteArrayOutputStream baos = readFromByte(bis, dividByte);

                if (baos.size() == 0)
                    break;

                if (decryptClearLen < len){
                    out.write(decrypt(baos.toByteArray())); // 16바이트 블록 암호문 블록 삽입
                }
                else{
                    byte[] lastRound = decrypt(baos.toByteArray());
                    for (int i = 0; i < lastRound.length; i++) {
                        if (lastRound[i] == 0) // 0 = padding 값
                            break;
                        out.write(lastRound[i]);
                    }
                }
                decryptClearLen++;


            }while (isTheEnd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        len = 0; // 복호화 이후 블록 갯수 초기화

        out.close();
        return type;
    }

}
