package kumoh.util.hash;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileHash implements Hash{
    private String hashValue;


    @Override
    public String getHash(){
        return hashValue;
    }

    @Override
    public int getHashLength(){
        return hashValue.length();
    }

    @Override
    public void setHash(String path, String type) {
        MessageDigest messageDigest = getMessageDigest(type); // MD5 or SHA-512
        StringBuffer stringBuffer = new StringBuffer();;
        try(FileInputStream fileInputStream = getFileInputStream(path)) {
            readStreamAndUpdateDigest(fileInputStream, messageDigest);
            byte[] mdBytes = messageDigest.digest();

            for (byte mdByte : mdBytes) {
                stringBuffer.append(Integer.toString((mdByte & 0xff) + 0x100, 16)).substring(1);
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
        hashValue = stringBuffer.toString();
    }

    private MessageDigest getMessageDigest(String alg){
        try {
            return MessageDigest.getInstance(alg);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
    private FileInputStream getFileInputStream(String path){
        try {
            return new FileInputStream(path);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void readStreamAndUpdateDigest(FileInputStream fileInputStream, MessageDigest messageDigest){
        int nRead = 0;
        byte[] dataBytes = new byte[1024];


        while(true) {
            try {
                if ((nRead = fileInputStream.read(dataBytes)) == -1) break;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            messageDigest.update(dataBytes, 0, nRead);
        }
    }
}
