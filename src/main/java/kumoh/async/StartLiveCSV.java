package kumoh.async;

import kumoh.config.AsyncConfig;
import kumoh.config.USBConnect;
import kumoh.domain.Api;
import kumoh.service.CryptoService;
import kumoh.util.csv.CSVMakeWithJSON;
import kumoh.util.csv.CSVTable;
import kumoh.util.json.request.ApiRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.CDL;
import org.json.JSONArray;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
@Lazy
public class StartLiveCSV {

    private ApiRequest apiRequest = new ApiRequest();
    private static boolean start = true;
    private static final Integer REQUEST_TIME = 3 * 60 * 1000; //1분에 한번씩 요청.
    private final StartEncryptCSV startEncryptCSV;
    private static int runningSize = 0;
    private String pkRegex;

    private final AsyncConfig asyncConfig;
    private final USBConnect usbConnect;

    public void apiRequestStart(Api api, String filePath, String pkRegex) throws IOException {
        if(!api.getIsValidation()){
            return;
        }

        this.pkRegex = pkRegex;
        try{
            File target = apiRequestStartSetting(api, filePath);
            startEncryptCSV.encryptFile(target);
            startEncryptCSV.fileDelete(target);
        }catch(HttpClientErrorException e){
            api.setIsValidation(false);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Async
    public void apiRequestAPIListStart(List<Api> apiList, String pkRegex, String zipTarget)throws InterruptedException {
        while(start){
            for (Api api : apiList) {
                try {
                    apiRequestStart(api, getPath(api), pkRegex);
                    System.out.println(api);
                } catch (IOException e) {
                    api.setIsValidation(false);
                    throw new RuntimeException(e);
                }
            }

            startEncryptCSV.getZipFile(CryptoService.getProgressPath()+"live/", zipTarget);
            if(!usbConnect.isLogin()){
                end();
            }
            Thread.sleep(REQUEST_TIME);
        }

    }

    public synchronized void start() {
        runningSize = 0;
        start = true;
    }

    public static int getRunningSize() {
        return runningSize;
    }

    public synchronized void end() {
        start = false;
        asyncConfig.setStartTime(null);
    }

    public boolean isStart(){
        return start;
    }
    private String getPath(Api api) {
        File dir = new File(CryptoService.getRequestFilePath()+"live");
        if(!dir.exists()){
            dir.mkdirs();
        }
        return CryptoService.getRequestFilePath()+"live/"+api.getTableName()+".csv";//cryptoService.getProgressPath()
    }
    private void apiRequestUpdate(Api api, String filePath, File target) throws IOException {
        JSONArray jsonArray = apiRequest.getJsonArray(api.getApi());
        CSVTable csvTable = new CSVTable(target, pkRegex);
        CSVTable toBeUpdatedData = new CSVTable(CDL.toString(jsonArray), pkRegex);

        csvTable.merge(toBeUpdatedData);
        csvTable.writeToFile(new File(filePath));
    }

    private File apiRequestStartSetting(Api api, String filePath) throws IOException {
        JSONArray jsonArray = apiRequest.getJsonArray(api.getApi());
        File zipFile = CSVMakeWithJSON.getFile(jsonArray, Path.of(filePath), pkRegex);

        return zipFile;
    }
}
