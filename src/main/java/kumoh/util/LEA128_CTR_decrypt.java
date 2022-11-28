package kumoh.util;

import kumoh.exception.CustomException;
import kumoh.util.crc.CRC;
import kumoh.util.crypto.BlockCipher;
import kumoh.util.crypto.BlockCipherMode;
import kumoh.util.crypto.padding.PKCS5Padding;
import kumoh.util.crypto.symm.LEA;
import kumoh.util.crypto.util.DataTypeTranslation;
import kumoh.util.hash.FileHash;
import kumoh.util.hash.Hash;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.NoSuchAlgorithmException;

@Component
public class LEA128_CTR_decrypt {
    public static BlockCipherMode cipher;
    public static byte[] byteIV;

    public static int len = 0; // 블록 갯수
    public static int byteBlock = 1024 * 1024 * 30; // 읽어들일 바이트 길이

    private DataTypeTranslation dataTypeTranslation;

    private static byte[] pt1;

    private Byte type;

    private CRC crc;

    public LEA128_CTR_decrypt(){
        cipher = new LEA.CTR();
        byteIV = new byte[] { (byte) 0x26, (byte) 0x8D, (byte) 0x66, (byte) 0xA7, (byte) 0x35, (byte) 0xA8, (byte) 0x1A, (byte) 0x81, (byte) 0x6F, (byte) 0xBA, (byte) 0xD9, (byte) 0xFA, (byte) 0x36, (byte) 0x16, (byte) 0x25, (byte) 0x01 };
        dataTypeTranslation = new DataTypeTranslation();
        crc = new CRC();
    }

    private void decryptSetting(byte[] key){
        cipher.init(BlockCipher.Mode.DECRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
    }

    private Object convertBytesToObject(byte[] bytes)
            throws IOException, ClassNotFoundException {
        InputStream is = new ByteArrayInputStream(bytes);
        try (ObjectInputStream ois = new ObjectInputStream(is)) {
            return ois.readObject();
        }
    }

    private byte[] convertObjectToBytes(Object obj) throws IOException {
        ByteArrayOutputStream boas = new ByteArrayOutputStream();
        try (ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            return boas.toByteArray();
        }
    }


    public Object object(byte[] cipherBytes, LEA128_key key) throws IOException, ClassNotFoundException {
        decryptSetting(key.getKey());

        pt1 = decrypt(cipherBytes); // pt1[0] : 타입 구분자
        type = pt1[0]; // 타입 구분자 추출
        byte[] temp = pt1;
        pt1 = new byte[temp.length - 1]; // 타입 구분자 제거
        System.arraycopy(temp, 1, pt1, 0, pt1.length);

        return convertBytesToObject(pt1);
    }


    // byte 배열만큼 복호화
    // 매개변수 : byte 배열
    // return : 복호화된 byte 배열
    private byte[] decrypt(byte[] baos){
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



    // 파일 복호화
    // 매개변수 : File, Decrypt 될 파일 경로
    public byte file(File file, String decryptionPath, LEA128_key key) throws IOException, NoSuchAlgorithmException {
        long nowTime = System.currentTimeMillis(); // 성능측정

        File wFile = new File(decryptionPath);
        len = (int)(file.length() / (1024 * 1024 * 30)) + 1;
        wFile.createNewFile();
        OutputStream out = new FileOutputStream(wFile);
        decryptSetting(key.getKey());

        int decryptClearLen = 0;
        byte type = -1;
        int length = -1;
        byte[] hashBytes = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)); // inputStream 상속받은 모든 객체
            int dividByte = byteBlock; // 32Byte (암호화가 진행되면 16byte -> 32byte이기 때문)
            boolean isTheEnd = true;
            type = (byte)bis.read(); // 첫 암호화 타입 바이트 제거
            length = bis.read(); // 두번째는 해쉬 길이 값

            if (length != -1){
                hashBytes = new byte[length];
                for (int i = 0; i < length; i++) {
                    hashBytes[i] = (byte)bis.read();
                }
            }

            do {
                ByteArrayOutputStream baos = readFromByte(bis, dividByte);

                if (baos.size() == 0)
                    break;

                if (decryptClearLen < len){
                    out.write(decrypt(Base64.decodeBase64(baos.toByteArray()))); // 16바이트 블록 암호문 블록 삽입
                }
                else{
                    byte[] lastRound = decrypt(Base64.decodeBase64(baos.toByteArray()));
                    for (int i = 0; i < lastRound.length; i++) {
                        if (lastRound[i] == 0) // 0 = padding 값
                            break;
                        out.write(lastRound[i]);
                    }
                }
                decryptClearLen++;


            }while (isTheEnd);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e){
            throw new CustomException();
        }finally {
            System.gc();
        }

        len = 0; // 복호화 이후 블록 갯수 초기화

        out.close();
        checkHash(wFile, hashBytes);
        System.out.println("파일 복호화 시간 : " +file.getName() + " " + (System.currentTimeMillis() - nowTime));
        return type;
    }

    private void checkHash(File wFile, byte[] hashValue) throws IOException {
        Hash hash = new FileHash();
        hash.setHash(wFile.getAbsolutePath(), "MD5");
        byte[] decrypteHashValue = convertObjectToBytes(hash.getHash());
        for (int i = 0; i < hashValue.length; i++) {
            if (hashValue[i] != decrypteHashValue[i])
                throw new RuntimeException("해쉬 검사에 실패하였습니다.");
        }
    }

    public Byte getType(){
        return this.type;
    }



}
