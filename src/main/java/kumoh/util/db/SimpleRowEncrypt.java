package kumoh.util.db;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import kumoh.config.KeyConfig;
import kumoh.domain.Log;
import kumoh.dto.db.DBConfig;
import kumoh.service.RSAService;
import kumoh.util.LEA128_CTR_encrypt;
import kumoh.util.LEA128_key;
import kumoh.util.log.LogDirBuilder;
import kumoh.util.log.LogFileDir;
import kumoh.util.log.LogUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SimpleRowEncrypt {

    private final RSAService rsaControl;
    private final LEA128_CTR_encrypt encrypt;
    private final LEA128_key key;
    private final KeyConfig keyConfig;

    private DBEncryptionDir fileEncryptionDir;
    private DBConfig dbConfig;

    public SimpleRowEncrypt(RSAService rsaControl, LEA128_CTR_encrypt encrypt, LEA128_key key, KeyConfig keyConfig, DBEncryptionDir fileEncryptionDir, DBConfig dbConfig) {
        this.rsaControl = rsaControl;
        this.encrypt = encrypt;
        this.key = key;
        this.keyConfig = keyConfig;
        this.fileEncryptionDir = fileEncryptionDir;
        this.dbConfig = dbConfig;
    }

    String filename;

    public File rowToFile(String data) {
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyy_MM_dd_HH_mm");
        filename = dbConfig.getConnectDBType() + "_" + dbConfig.getSelectedSchema() + "_" + dbConfig.getSelectedTable() + fourteen_format.format(date_now);

        List<Map<String, Object>> info = new Gson().fromJson(String.valueOf(data),
                new TypeToken<List<Map<String, Object>>>() {
                }.getType());

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(fileEncryptionDir.getDir() + "\\" + filename + ".csv"));

            boolean headerNotRead = true;
            for (Map<String, Object> memberInfo : info) {
                int mapSize = memberInfo.size();
                int mapCount = 1;
                if (headerNotRead) {

                    for (Map.Entry<String, Object> entrySet : memberInfo.entrySet()) {
                        if (entrySet.getKey().equals("DB_ID")) continue;
                        bw.append(entrySet.getKey());
                        if (++mapCount < mapSize) bw.append(',');
                        else bw.append('\n');
                    }
                    headerNotRead = false;
                }
                mapCount = 1;
                for (Map.Entry<String, Object> entrySet : memberInfo.entrySet()) {
                    if (entrySet.getKey().equals("DB_ID")) continue;
                    bw.append(entrySet.getValue().toString());
                    if (++mapCount < mapSize) bw.append(',');
                    else bw.append('\n');
                }

            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.out.println("error : " + e);
        }
        return new File(fileEncryptionDir.getDir() + "\\" + filename + ".csv");
    }

    public File encryptCSVUsingLEA128(File file) {
        String currentPath = fileEncryptionDir.getDir();
        String encryptPath = currentPath + "\\Encoding_" + file.getName();
        try {
            encrypt.file(file, encryptPath, key);
        } catch (IOException e) {
            throw new RuntimeException("Can not find file path");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Can not read file");
        }

        saveLog(file.getName(), System.getProperty("user.name"));
        System.gc();
        new File(fileEncryptionDir.getDir() + "\\" + filename + ".csv").delete();
        return new File(encryptPath);
    }

    public void rowEncrypt(String data) {
        encryptCSVUsingLEA128(rowToFile(data));
    }


    private void saveLog(String title, String authorInfo) {
        String symmetricKey = keyConfig.getSymmetricKey();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime ldt = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), now.getHour(), now.getMinute(), now.getSecond());
        List<Log> newLog = new ArrayList<>();
        String date = ldt.toLocalDate().toString() + "/" + ldt.toLocalTime();
        newLog.add(new Log(symmetricKey, title, date, authorInfo));
        LogDirBuilder ldb = new LogDirBuilder(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), LogFileDir.getDir());
        LogUtils logUtils = new LogUtils(ldb.getDir());
        rsaControl.setLogUtils(logUtils);
        rsaControl.recordKey(newLog);
    }
}
