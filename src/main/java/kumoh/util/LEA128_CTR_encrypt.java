package kumoh.util;

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
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.function.Predicate;

import static java.lang.System.in;
import static org.apache.tomcat.util.codec.binary.Base64.*;


@Component
public class LEA128_CTR_encrypt {
    public static BlockCipherMode cipher;
    public static byte[] byteIV;

    public static int len = 0; // 블록 갯수
    public static int byteBlock = 1024 * 1024 * 5; // 읽어들일 바이트 길이

    private static byte[] ct1;


    private static CRC crc;


    public LEA128_CTR_encrypt(){
        cipher = new LEA.CTR();
        byteIV = new byte[] { (byte) 0x26, (byte) 0x8D, (byte) 0x66, (byte) 0xA7, (byte) 0x35, (byte) 0xA8, (byte) 0x1A, (byte) 0x81, (byte) 0x6F, (byte) 0xBA, (byte) 0xD9, (byte) 0xFA, (byte) 0x36, (byte) 0x16, (byte) 0x25, (byte) 0x01 };
        crc = new CRC();
    }

    private void validation(final String key) {
        try{
            Optional.ofNullable(key)
                    .filter(Predicate.not(String::isBlank))
                    .filter(Predicate.not(s -> s.length() != 16))
                    .orElseThrow(IllegalArgumentException::new);
        }catch (IllegalArgumentException e){
            throw new IllegalArgumentException("Key should Non-Blank and must be 16 length");
        }
    }

    private void encryptSetting(byte[] key){
        cipher.init(BlockCipher.Mode.ENCRYPT, key, byteIV);
        cipher.setPadding(new PKCS5Padding(16));
    }

    public byte[] convertObjectToBytes(Object obj) throws IOException {
        byte[] data;
        try (   ByteArrayOutputStream boas = new ByteArrayOutputStream();
                ObjectOutputStream ois = new ObjectOutputStream(boas)) {
            ois.writeObject(obj);
            data = boas.toByteArray();
        }catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return data;
    }



    public byte[] object(Object plain, LEA128_key key) throws IOException {
        encryptSetting(key.getKey());
        byte[] plainBytes = new byte[convertObjectToBytes(plain).length + 1];
        plainBytes[0] = DataTypeTranslation.getTypeInJava(plain); // 첫 바이트에 타입 구분자 삽입
        System.arraycopy(convertObjectToBytes(plain), 0, plainBytes, 1, convertObjectToBytes(plain).length);
        ct1 = encrypt(plainBytes);
        return ct1;
    }

    // byte 배열만큼 암호화
    // 매개변수 : byte 배열
    // return : 암호화된 byte 배열
    private byte[] encrypt(byte[] plainBytes){
        return cipher.doFinal(plainBytes);
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
    public void file(File file, String encryptionPath, LEA128_key key) throws IOException, NoSuchAlgorithmException {
        long nowTime = System.currentTimeMillis(); // 성능측정

        Hash hash = new FileHash();
        hash.setHash(file.getAbsolutePath(), "MD5");
        File wFile = new File(encryptionPath);

        int totalSize = 0;
        wFile.createNewFile();
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(wFile))) {
            encryptSetting(key.getKey());

            totalSize = 0;

            // 첫 바이트에 타입 구분자 삽입
            out.write(DataTypeTranslation.getTypeInJava(file));
            byte[] hashBytes = convertObjectToBytes(hash.getHash());
            out.write(hashBytes.length);
            out.write(hashBytes);

            // inputStream 상속받은 모든 객체
            try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file))) {

                int dividByte = byteBlock; // Block 자체는 1024 * 1024 * 5 (16바이트로 나누는 것은 encrypt 과정에서 진행)
                boolean isTheEnd = true;
                boolean firstRound = true;
                BufferedInputStream buif = null;

                do {
                    try (ByteArrayOutputStream baos = readFromByte(bis, dividByte)) {

                        if (baos.size() == 0) // 더이상 읽어들일게 없으면 break
                            break;
                        if (firstRound && (baos.size() < 16)) // 첫번째 라운드에 size 가 16바이트 미만이면 아래 if 문에서 처리
                            break;
                        totalSize += baos.size();
                        buif = new BufferedInputStream(new ByteArrayInputStream(Base64.encodeBase64(encrypt(baos.toByteArray()))));
                        len++;
                        firstRound = false;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                } while (isTheEnd);


                byte[] buff = new byte[ 64 * 1024 ];
                int lenn = 0;
                while ((lenn = buif.read(buff)) > 0) //If necessary readLine()
                    out.write(buff, 0, lenn);

                bis.close();
                buif.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (totalSize == 0) { // 16 바이트 이하는 추가 패딩 작업 이후 패딩된 블록으로 암호화
                byte[] bytes16 = new byte[16];
                byte[] plainBytesUnder16 = Files.readAllBytes(file.toPath());
                int plainBytesUnder16Index = 0;

                for (int i = 0; i < bytes16.length; i++) {
                    if (plainBytesUnder16Index < plainBytesUnder16.length)
                        bytes16[i] = plainBytesUnder16[plainBytesUnder16Index++];
                }
                byte[] encryptBytes16 = encodeBase64(encrypt(bytes16));


                out.write(encryptBytes16);
            }

            System.out.println("파일 암호화 시간 : " +file.getName()+ (System.currentTimeMillis() - nowTime));

        }catch (IOException e){
            e.printStackTrace();
        }finally {
            System.gc();
        }
        //crc.addCRCValueWithFile(wFile);
    }

}
