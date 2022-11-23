package kumoh.util.crypto;

import kumoh.util.crypto.padding.PKCS5Padding;
import kumoh.util.crypto.symm.LEA;
import kumoh.util.crypto.util.DataTypeTranslation;

import java.io.*;
import java.nio.file.Files;

public class LEA128 {
    public static BlockCipherMode cipher;
    public static byte[] key;
    public static byte[] byteIV;

    public static int len = 0; // 블록 갯수
    public static int byteBlock = 1024 * 1024 * 512; // 읽어들일 바이트 길이

    private static byte[] ct1;
    private static byte[] pt1;

    private static byte type;

    public LEA128(){
        cipher = new LEA.CTR();
        key = Seed_CBC.key;
        byteIV = new byte[] { (byte) 0x26, (byte) 0x8D, (byte) 0x66, (byte) 0xA7, (byte) 0x35, (byte) 0xA8, (byte) 0x1A, (byte) 0x81, (byte) 0x6F, (byte) 0xBA, (byte) 0xD9, (byte) 0xFA, (byte) 0x36, (byte) 0x16, (byte) 0x25, (byte) 0x01 };
    }

    private void encryptSetting(){
        cipher.init(BlockCipher.Mode.ENCRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
    }

    private void decryptSetting(){
        cipher.init(BlockCipher.Mode.DECRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
    }

    public byte[] convertObjectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }

    public Object convertBytesToObject(byte[] bytes)
            throws IOException, ClassNotFoundException {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        }
    }



    public byte[] encryptObject(Object plain) throws IOException {
        byte[] test1 = convertObjectToBytes(plain);
        for (int i = 0; i < test1.length; i++) {
            System.out.print(test1[i]);
        }
        System.out.println();
        encryptSetting();
        byte[] plainBytes = new byte[convertObjectToBytes(plain).length + 1];
        plainBytes[0] = DataTypeTranslation.getTypeInJava(plain); // 첫 바이트에 타입 구분자 삽입
        System.arraycopy(convertObjectToBytes(plain), 0, plainBytes, 1, convertObjectToBytes(plain).length);
        System.out.println(plainBytes[0]);
        ct1 = encrypt(plainBytes);
        return ct1;
    }

    public Object decryptObject(byte[] cipherBytes) throws IOException, ClassNotFoundException {
        decryptSetting();

        pt1 = decrypt(cipherBytes); // pt1[0] : 타입 구분자
        type = pt1[0]; // 타입 구분자 추출
        byte[] temp = pt1;
        pt1 = new byte[temp.length - 1]; // 타입 구분자 제거
        System.arraycopy(temp, 1, pt1, 0, pt1.length);

        return convertBytesToObject(pt1);
    }

    // byte 배열만큼 암호화
    // 매개변수 : byte 배열
    // return : 암호화된 byte 배열
    public byte[] encrypt(byte[] plainBytes){
        return cipher.doFinal(plainBytes);
    }

    // byte 배열만큼 복호화
    // 매개변수 : byte 배열
    // return : 복호화된 byte 배열
    public byte[] decrypt(byte[] baos){
        return cipher.doFinal(baos);
    }


    // 파일 끊어 읽기
    // 매개변수 : file의 BufferedInputStream, block size
    // return : size 만큼의 ByteArrayOutputStream
    public ByteArrayOutputStream readFromByte(BufferedInputStream bis, long size) {
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
    public void encryptFile(File file, String encryptionPath) throws IOException {
        File wFile = new File(encryptionPath);

        wFile.createNewFile();
        OutputStream out = new FileOutputStream(wFile);
        encryptSetting();

        int totalSize = 0;

        // 첫 바이트에 타입 구분자 삽입
        out.write(DataTypeTranslation.getTypeInJava(file));


        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); // inputStream 상속받은 모든 객체
            int dividByte = byteBlock; // Block 자체는 1024 * 1024 * 512 (16바이트로 나누는 것은 encrypt 과정에서 진행)
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
    public byte decryptFile(File file, String decryptionPath) throws IOException {

        File wFile = new File(decryptionPath);

        wFile.createNewFile();
        OutputStream out = new FileOutputStream(wFile);
        decryptSetting();

        int decryptClearLen = 0;
        byte type = -1;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); // inputStream 상속받은 모든 객체
            int dividByte = byteBlock; // 32Byte (암호화가 진행되면 16byte -> 32byte이기 때문)
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
