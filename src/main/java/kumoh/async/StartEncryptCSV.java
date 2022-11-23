package kumoh.async;

import kumoh.service.CryptoService;
import kumoh.util.zip.FileScanner;
import kumoh.util.zip.ZipConvertor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
@Slf4j
public class StartEncryptCSV {
    private final CryptoService cryptoService;
    private boolean encryptDone = false;

    private static String downloadPath;

    public void encryptFile(File csv){
        log.info(Thread.currentThread() + " : " +csv.getName() + " encryptStart");
        cryptoService.encryptCSVUsingLEA128(csv);
    }

    @Async
    public void fileDelete(File file){
        for(int i = 0 ; i < 100; i++){
            if(file.delete()){
                System.out.println(file.getName() + " : 삭제 성공");
                break;
            }else{
                System.out.println(file.getName() + " : 삭제 실패");

                try{
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public File getZipFile(String path, String target) {
        ZipConvertor convertor = new ZipConvertor(new FileScanner(path)); // 파일이 모여 있는 경로
        File zipFile = convertor.convert(LocalDateTime.now().toString().split("T")[0]+".zip", target);//asyncConfig.getStartTime().split(" ")[0]); // 파일 이름만 넣으면 모여 있는 경로에 저장, 혹은 src/test/a/a.zip 과 같이 넣을 수 있음    }
        Arrays.stream(new FileScanner(CryptoService.getProgressPath()+"live/")
                        .getAllFiles())
                        .forEach(dir -> fileDelete(new File(dir)));
        return zipFile;
    }
}
