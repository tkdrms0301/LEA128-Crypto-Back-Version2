package kumoh.util.file;

import kumoh.domain.Log;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class FileIO {
    private FileWriter fw = null;
    private FileReader fr = null;
    private BufferedWriter bw = null;
    private BufferedReader br = null;

    public FileIO(){

    }

    // 폴더 경로 상의 모든 파일의 경로 재귀적 탐색
    private void recursivefileList(String path, List<String> fileList){
        File dir = new File(path);
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                recursivefileList(file.getPath(), fileList);
            } else {
                fileList.add(file.getPath());
            }
        }
    }

    public List<String> fileList(String path){
        List<String> fileList = new ArrayList<>();
        if(!path.substring(path.length() - 1).equals("/")){
            path += "/";
        }
        recursivefileList(path, fileList);
        return fileList;
    }

    // 파일 라인마다 읽기 후 문자열 리스트 반환
    public List<String> readFile(String path) throws IOException {
        List<String> lines = new ArrayList<>();
        try{
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line = "";
            while((line = br.readLine()) != null){
                lines.add(line);
            }
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
        return lines;
    }

    // 파일 문자열 리스트로 라인마다 파일쓰기
    public void writeFile(String path, List<String> lines) throws IOException {
        try{
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            for(String line : lines){
                bw.write(line);
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(bw != null){
                bw.close();
            }
            if(fw != null){
                fw.close();
            }
        }
    }

    public List<Log> readLogFile(String path) throws IOException {
        List<Log> lines = new ArrayList<>();
        try{
            fr = new FileReader(path);
            br = new BufferedReader(fr);
            String line = "";
            String[] log = new String[4];
            while((line = br.readLine()) != null){
                log = line.split(" ");
                lines.add(new Log(log[0], log[1], log[2], log[3])); // 대칭키, 제목, 기록날짜. 작성자
            }
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
        return lines;
    }

    public void writeLogFile(String path, List<Log> lines) throws IOException {
        try{
            fw = new FileWriter(path);
            bw = new BufferedWriter(fw);
            for(Log log : lines){
                bw.write(log.getSymmetricKey() + " " + log.getTitle() + " " + log.getRecordDate() + " " + log.getAuthorInfo()); // 대칭키, 제목, 기록날짜. 작성자
                bw.newLine();
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if(bw != null){
                bw.close();
            }
            if(fw != null){
                fw.close();
            }
        }
    }
}
