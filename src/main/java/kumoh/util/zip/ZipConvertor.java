package kumoh.util.zip;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipConvertor extends Decorator{
    public ZipConvertor(FileScanner fileScanner){
        this.fileScanner = fileScanner;
    }

    public File convert(String zipFileName, String targetDir) {
        String[] fileList = fileScanner.getAllFiles();
        if (!zipFileName.contains("/"))
            zipFileName = targetDir + zipFileName;

        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFileName))) {
            for (String srcPath : fileList) {
                if (srcPath.contains(".zip"))
                    continue;
                Path src = Paths.get(srcPath);
                try (FileInputStream fis = new FileInputStream(src.toFile())) {

                    ZipEntry zipEntry = new ZipEntry(src.getFileName().toString());
                    zos.putNextEntry(zipEntry);

                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, len);
                    }
                    zos.closeEntry();
                }
                catch (IOException e){
                    throw new RuntimeException("압축할 파일을 읽어오던 중 문제가 발생하였습니다.");
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("경로 또는 압축명이 잘못되어 압축 파일을 생성할 수 없습니다.");
        }
        fileScanner.setPath(zipFileName);
        return fileScanner.getFile();
    }

    public void setFileScanner(FileScanner fileScanner){
        this.fileScanner = fileScanner;
    }
}
