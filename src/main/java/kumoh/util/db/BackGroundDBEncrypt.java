package kumoh.util.db;

import kumoh.dto.db.DBListDTO;
import kumoh.util.db.ThreeCycle.LiveDBThreadList;

import java.util.List;

public class BackGroundDBEncrypt implements Runnable{
    LiveDBThreadList threadList;
    DBEncryptionDir fileEncryptionDir;
    List<DBListDTO> dbListDTOS;
    String fileNameTime;

    public BackGroundDBEncrypt(LiveDBThreadList threadList, DBEncryptionDir fileEncryptionDir, List<DBListDTO> dbListDTOS, String fileNameTime) {
        this.threadList = threadList;
        this.fileEncryptionDir = fileEncryptionDir;
        this.dbListDTOS = dbListDTOS;
        this.fileNameTime = fileNameTime;
    }

    @Override
    public void run() {
        threadList.liveDBRun(dbListDTOS,fileNameTime);
    }

    public void stopDBEncrypt(){
        threadList.liveDBStop(fileNameTime);
    }
}
