package kumoh.util.log;

import kumoh.domain.Log;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Component
@NoArgsConstructor
public class LogUtils {
    private List<Log> log;

    private String LOG_DIR = LogFileDir.getDir();
    private String LOG_FILE = "log.txt";

    public LogUtils(String dir) {
        LOG_DIR = dir + "/";
    }

    public List<Log> logRead() throws IOException {
        return logRead(LOG_DIR, LOG_FILE);
    }

    public void logWrite(List<Log> newLog) throws IOException{
        logWrite(LOG_DIR, LOG_FILE, newLog);
    }

    private List<Log> logRead(String dir, String logFile) throws IOException {
        Path path = Paths.get(dir + logFile);

        try(FileReader fr = new FileReader(path.toString());
            BufferedReader br = new BufferedReader(fr);) {

            List<Log> logList = new ArrayList<>();
            String readLine = null;
            String[] splitLog;
            LocalDateTime dateTime;
            while((readLine = br.readLine()) != null){
                splitLog = readLine.split(" ");
                logList.add(new Log(splitLog[0], splitLog[1], splitLog[2], splitLog[3]));
            }
            setLog(logList);
            return logList;
        } catch (FileNotFoundException e) {
            File file = new File(dir + logFile);
            if (!file.exists()){
                LocalDateTime now = LocalDateTime.now();
                String year = String.valueOf(now.getYear());
                String month = year + "/" + now.getMonth().getValue();
                String day = month + "/" + now.getDayOfMonth();
                makeFolder(dir.split(year)[0], year); // dir split : Decrypt/src/main/resources/log/
                makeFolder(dir.split(year)[0], month);
                makeFolder(dir.split(year)[0], day);
            }
            file.createNewFile();
            return new ArrayList<>();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //로그 내용 덮어쓰기
    private void logWrite(String dir, String logFile, List<Log> newLog) throws IOException {
        Path path = Paths.get(dir + logFile);
        try(FileWriter fw = new FileWriter(path.toString(), false);
            BufferedWriter bw = new BufferedWriter(fw);){
            for(Log log : newLog){
                bw.write(log.getSymmetricKey() + " " + log.getTitle() + " " + log.getRecordDate() + " " + log.getAuthorInfo());
                bw.newLine();
            }
            bw.flush();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 폴더 생성
    private void makeFolder(String defaultDir, String date){
        File file = new File(defaultDir + date);
        if (!file.exists()){
            try{
                file.mkdir();
            }
            catch(Exception mkdirErr){
                mkdirErr.getStackTrace();
            }
        }
    }


}
