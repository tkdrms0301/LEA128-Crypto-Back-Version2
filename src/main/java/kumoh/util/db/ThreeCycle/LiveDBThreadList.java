package kumoh.util.db.ThreeCycle;

import kumoh.config.KeyConfig;
import kumoh.config.defaultconfig.DBEncDirInfo;
import kumoh.dto.db.DBCryptFilePath;
import kumoh.dto.db.DBListDTO;
import kumoh.dto.db.DBLiveState;
import kumoh.service.RSAService;
import kumoh.util.LEA128_CTR_encrypt;
import kumoh.util.LEA128_key;
import kumoh.util.db.DBEncryptionDir;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RequiredArgsConstructor
@Getter
@Setter
@Component
public class LiveDBThreadList {

    private final DBEncryptionDir fileEncryptionDir;
    private final DBEncDirInfo fileEncDirInfo;

    private final RSAService rsaControl;
    private final LEA128_CTR_encrypt encrypt;
    private final LEA128_key key;
    private final KeyConfig keyConfig;

    List<LiveThread> liveThreads = new ArrayList<>();
    DBLiveState dbLiveState = new DBLiveState(false);
    List<Thread> threadList = new ArrayList<>();
    boolean zipRun;
    List<DBListDTO> dbListDTOS;

    boolean runningLive;
    public void liveDBRun(List<DBListDTO> dbListDTOS, String fileName) {
        runningLive = true;
        DBCryptFilePath dbCryptFilePath = new DBCryptFilePath(fileEncryptionDir.getDir() + "\\");
        dbLiveState = new DBLiveState(true, LocalDateTime.now().withNano(0));
        this.dbListDTOS = dbListDTOS;

        while (runningLive){
            for (int i = 0; i < dbListDTOS.size(); i++) {
                LiveThread liveThread = new LiveThread(dbListDTOS.get(i), dbCryptFilePath, fileEncryptionDir, fileEncDirInfo, rsaControl, encrypt, key, keyConfig);
                liveThreads.add(liveThread);

                liveThread.runlive();
            }
            fileZip(fileName);
            deleteEnFiles();
            try{
                Thread.sleep(180000);

            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }

    }


    public void encryptDB(List<DBListDTO> dbListDTOS, DBCryptFilePath dbCryptFilePath) {

        this.dbListDTOS = dbListDTOS;

        for (int i = 0; i < dbListDTOS.size(); i++) {
            LiveThread liveThread = new LiveThread(dbListDTOS.get(i), dbCryptFilePath, fileEncryptionDir, fileEncDirInfo, rsaControl, encrypt, key, keyConfig);
            liveThread.dbToCSV();
            liveThread.encryptCSVUsingLEA128(liveThread.dbToCSV());
        }
        Date date_now = new Date(System.currentTimeMillis());
        SimpleDateFormat fourteen_format = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String fileNameTime = fourteen_format.format(date_now);

        fileZip(fileNameTime);
        deleteEnFiles();
    }


    public void liveDBStop(String fileName) {
        runningLive = false;
        dbLiveState.setRunning(false);
        DBCryptFilePath dbCryptFilePath = new DBCryptFilePath(fileEncryptionDir.getDir() + "\\");
        for (int i = 0; i < dbListDTOS.size(); i++) {
            LiveThread liveThread = new LiveThread(dbListDTOS.get(i), dbCryptFilePath, fileEncryptionDir, fileEncDirInfo, rsaControl, encrypt, key, keyConfig);
            liveThreads.add(liveThread);

            liveThread.runlive();
        }
        fileZip(fileName);
        deleteEnFiles();

    }

    public void fileZip(String fileName) {
        List<File> files = new ArrayList<>();

        String currentPath = fileEncryptionDir.getDir();

        for (int i = 0; i < dbListDTOS.size(); i++) {
            String filename = dbListDTOS.get(i).getDbmsName() + "_" + dbListDTOS.get(i).getSchemaName() + "_" + dbListDTOS.get(i).getTableName() + "_" + dbListDTOS.get(i).getStartPoint();
            String encryptFile = currentPath + "\\Encoding_" + filename + ".csv";

            files.add(new File(encryptFile));
        }

        File zipFile = new File(currentPath + "\\DBEncrypt_" + fileName + ".zip");
        byte[] buf = new byte[4096];

        try (ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zipFile))) {

            for (File file : files) {

                try (FileInputStream in = new FileInputStream(file)) {
                    ZipEntry ze = new ZipEntry(file.getName());
                    out.putNextEntry(ze);

                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }

                    out.closeEntry();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void deleteEnFiles() {
        String currentPath = fileEncryptionDir.getDir();
        for (int i = 0; i < dbListDTOS.size(); i++) {
            String filename = dbListDTOS.get(i).getDbmsName() + "_" + dbListDTOS.get(i).getSchemaName() + "_" + dbListDTOS.get(i).getTableName() + "_" + dbListDTOS.get(i).getStartPoint();
            String encryptFile = currentPath + "\\Encoding_" + filename + ".csv";

            System.gc();
            new File(encryptFile).delete();
        }
    }
}
