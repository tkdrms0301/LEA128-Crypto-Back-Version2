package kumoh.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import kumoh.dto.key.KeyInfoDto;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Getter
@Setter
@Component
public class KeyConfig {
    private String publicKey;
    private String secretKey;
    private String symmetricKey;

    private KeyInfoDto publicKeyInfo;
    private KeyInfoDto secretKeyInfo;
    private KeyInfoDto symmetricKeyInfo;
    private FileWriter fw = null;
    private FileReader fr = null;
    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private String KEY = null;
    private String PUBLIC_KEY = "publicKey.txt";
    private String SECRET_KEY = "secretKey.txt";
    private String SYMMETRIC_KEY = "symmetricKey.txt";
    private String PUBLIC_KEY_INFO = "publicKeyInfo.txt";
    private String SECRET_KEY_INFO = "secretKeyInfo.txt";
    private String SYMMETRIC_KEY_INFO = "symmetricKeyInfo.txt";
    private boolean isConfiguration = false;

    public String getPUBLIC_KEY(){
        return KEY + PUBLIC_KEY;
    }

    public String getSECRET_KEY(){
        return KEY + SECRET_KEY;
    }

    public String getSYMMETRIC_KEY(){
        return KEY + SYMMETRIC_KEY;
    }

    public String getPUBLIC_KEY_INFO(){
        return KEY + PUBLIC_KEY_INFO;
    }

    public String getSECRET_KEY_INFO(){
        return KEY + SECRET_KEY_INFO;
    }

    public String getSYMMETRIC_KEY_INFO(){
        return KEY + SYMMETRIC_KEY_INFO;
    }

    public KeyConfig(){
    }

    public void keyConfiguration(){
        if (!isConfiguration){
            publicKeyInfo = new KeyInfoDto();
            secretKeyInfo = new KeyInfoDto();
            symmetricKeyInfo = new KeyInfoDto();
            try{
                publicKeyInfoRead();
                secretKeyInfoRead();
                symmetricKeyInfoRead();

                publicKey = publicKeyRead();
                secretKey = secretKeyRead();
                symmetricKey = symmentricKeyRead();
                isConfiguration = true;
            }catch (IOException e){
                e.printStackTrace();
            }
            publicKeyInfo.setKeyValue(publicKey);
            secretKeyInfo.setKeyValue(secretKey);
            symmetricKeyInfo.setKeyValue(symmetricKey);
        }

    }

    private String keyRead(String dir, String keyFile) throws IOException {

        try {
            Path path = Paths.get(dir + keyFile);

            fr = new FileReader(path.toString());
            br = new BufferedReader(fr);
            return br.readLine();

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(br != null){
                br.close();
            }
            if(fr != null){
                fr.close();
            }
        }
    }

    private void keyInfoRead(String dir, String keyInfoFile, KeyInfoDto keyInfoDto) throws IOException {

        try{
            Path path = Paths.get(dir + keyInfoFile);

            fr = new FileReader(path.toString());
            br = new BufferedReader(fr);
            keyInfoDto.setKeyType(br.readLine());
            keyInfoDto.setCreatedDate(br.readLine());
            keyInfoDto.setExpirationDate(br.readLine());
            keyInfoDto.setValidTerm(br.readLine());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(br != null){
                br.close();
            }
            if(fr != null){
                fr.close();
            }
        }

    }


    private void keyWrite(String dir, String keyFile, String newKey) throws IOException {
        try{
            Path path = Paths.get(dir + keyFile);
            fw = new FileWriter(path.toString(), false);
            bw = new BufferedWriter(fw);
            bw.write(newKey);
            bw.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(br != null){
                br.close();
            }
            if(fr != null){
                fr.close();
            }
        }
    }

    private void keyInfoWrite(String dir, String keyInfoFile, KeyInfoDto keyInfoDto, KeyInfoDto newKeyInfo) throws IOException {
        try{
            Path path = Paths.get(dir + keyInfoFile);
            fw = new FileWriter(path.toString(), false);
            bw = new BufferedWriter(fw);
            bw.write(newKeyInfo.getKeyType());
            bw.newLine();
            bw.write(newKeyInfo.getCreatedDate());
            bw.newLine();
            bw.write(newKeyInfo.getExpirationDate());
            bw.newLine();
            bw.write(newKeyInfo.getValidTerm());
            bw.flush();

            keyInfoDto.setKeyType(newKeyInfo.getKeyType());
            keyInfoDto.setCreatedDate(newKeyInfo.getCreatedDate());
            keyInfoDto.setExpirationDate(newKeyInfo.getExpirationDate());
            keyInfoDto.setValidTerm(newKeyInfo.getValidTerm());
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(br != null){
                br.close();
            }
            if(fr != null){
                fr.close();
            }
        }
    }


    public KeyInfoDto newPublicKeyInfo(){
        KeyInfoDto keyInfoDto = new KeyInfoDto();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        LocalDateTime expirationDate = LocalDateTime.of(now.getYear() + 2, now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        String createdDateValue = createdDate.toLocalDate().toString() + "/" + createdDate.toLocalTime();
        String expirationDateValue = expirationDate.toLocalDate().toString() + "/" + expirationDate.toLocalTime();

        keyInfoDto.setKeyType("공개키");
        keyInfoDto.setExpirationDate(expirationDateValue);
        keyInfoDto.setCreatedDate(createdDateValue);
        keyInfoDto.setValidTerm("2년");
        return keyInfoDto;
    }

    public KeyInfoDto newSymmetricKeyInfo(){
        KeyInfoDto keyInfoDto = new KeyInfoDto();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        LocalDateTime expirationDate = LocalDateTime.of(now.getYear() + 2, now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        String createdDateValue = createdDate.toLocalDate().toString() + "/" + createdDate.toLocalTime();
        String expirationDateValue = expirationDate.toLocalDate().toString() + "/" + expirationDate.toLocalTime();

        keyInfoDto.setKeyType("대칭키");
        keyInfoDto.setExpirationDate(expirationDateValue);
        keyInfoDto.setCreatedDate(createdDateValue);
        keyInfoDto.setValidTerm("2년");
        return keyInfoDto;
    }

    public KeyInfoDto newSecretKeyInfo(){
        KeyInfoDto keyInfoDto = new KeyInfoDto();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime createdDate = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        LocalDateTime expirationDate = LocalDateTime.of(now.getYear() + 2, now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        String createdDateValue = createdDate.toLocalDate().toString() + "/" + createdDate.toLocalTime();
        String expirationDateValue = expirationDate.toLocalDate().toString() + "/" + expirationDate.toLocalTime();

        keyInfoDto.setKeyType("비밀키");
        keyInfoDto.setExpirationDate(expirationDateValue);
        keyInfoDto.setCreatedDate(createdDateValue);
        keyInfoDto.setValidTerm("2년");
        return keyInfoDto;
    }


    public String publicKeyRead() throws IOException {
        return keyRead(KEY, PUBLIC_KEY);
    }

    public String secretKeyRead() throws IOException {
        return keyRead(KEY, SECRET_KEY);
    }

    public String symmentricKeyRead() throws IOException {
        String key =  keyRead(KEY, SYMMETRIC_KEY);
        return key;
    }

    public void publicKeyInfoRead() throws IOException {
        keyInfoRead(KEY, PUBLIC_KEY_INFO, publicKeyInfo);
    }

    public void secretKeyInfoRead() throws IOException {
        keyInfoRead(KEY, SECRET_KEY_INFO, secretKeyInfo);
    }

    public void symmetricKeyInfoRead() throws IOException {
        keyInfoRead(KEY, SYMMETRIC_KEY_INFO, symmetricKeyInfo);
    }

    public void publicKeyWrite(String newKey) throws IOException {
        keyWrite(KEY, PUBLIC_KEY, newKey);
        setPublicKey(newKey);
    }

    public void secretKeyWrite(String newKey) throws IOException {
        keyWrite(KEY, SECRET_KEY, newKey);
        setSecretKey(newKey);
    }

    public void symmentricKeyWrite(String newKey) throws IOException{
        keyWrite(KEY, SYMMETRIC_KEY, newKey);
        setSymmetricKey(newKey);
    }

    public void publicKeyInfoWrite(KeyInfoDto newKeyInfo) throws IOException {
        keyInfoWrite(KEY, PUBLIC_KEY_INFO, publicKeyInfo, newKeyInfo);
    }

    public void secretKeyInfoWrite(KeyInfoDto newKeyInfo) throws IOException {
        keyInfoWrite(KEY, SECRET_KEY_INFO, secretKeyInfo, newKeyInfo);
    }

    public void symmetricKeyInfoWrite(KeyInfoDto newKeyInfo) throws IOException {
        keyInfoWrite(KEY, SYMMETRIC_KEY_INFO, symmetricKeyInfo, newKeyInfo);
    }
}
