package kumoh.util.crc;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.CRC32;

@Slf4j

public class CRC {
    private final CRC32 crc32;
    private static final int TYPE_BYTE = 1;
    private static final int CRC_VALUE_SIZE = 4;
    private static final int BUFFER_SIZE = 16;

    public CRC() {crc32 = new CRC32();}

    /**
     *
     * @param file
     * 파일의 바이트에 대한 판단 결과를 파일의 끝 4바이트에 추가함.
     */
    public void addCRCValueWithFile(File file) {

        byte[] CRCData = getValueWithFile(file);

        FileOutputStream fileOutputStream = null;
        try {

            fileOutputStream = new FileOutputStream(file.getPath(),true);
            fileOutputStream.write(CRCData);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally{
            try {
                if(fileOutputStream != null){
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                throw new RuntimeException("치명적인 오류 발생");
            }

        }
    }

    /**
     *
     * @param file
     * @return 파일의 바이트에 대한 판단 결과를 리턴
     */
    private byte[] getValueWithFile(File file){
        byte[] buffer = new byte[BUFFER_SIZE];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int length = 0;
            while((length = fileInputStream.read(buffer)) != -1){
                crc32.update(buffer, 0, length );
            }

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if(fileInputStream != null)
                    fileInputStream.close();

            } catch (IOException e) {
                throw new RuntimeException("치명적인 오류 발생");
            }
        }
        int value = (int)crc32.getValue();

        return ByteBuffer.allocate(Integer.BYTES).putInt(value).array();
    }

    private int toValue(byte[] data){
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(data);
        buffer.flip();
        return buffer.getInt();
    }


    public void detection(File file) {
        //int value =  removeCRCValueWithFile(file); //;

        byte[] fileData = new byte[BUFFER_SIZE];

        try(FileInputStream fileInputStream = new FileInputStream(file)) {

            int length = 0;
            while((length = fileInputStream.read(fileData)) != -1){

                crc32.update(fileData, 0, length);
            }

            int detectionValue = (int)crc32.getValue();
//            if(value != detectionValue){
//                throw new DetectionNotMatchException("파일이 맞지 않습니다.");
//            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }


    private int removeCRCValueWithFile(File file){
        byte[] readData = new byte[CRC_VALUE_SIZE];
        byte[] beforeData = new byte[CRC_VALUE_SIZE];
        int value;
        File tempFile = new File(file.getPath()+".$cache");
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {


            fileInputStream = new FileInputStream(file);
            fileOutputStream = new FileOutputStream(tempFile);
            boolean isFirstRound = true;
            fileOutputStream.write(fileInputStream.read()); //마지막 4개 갯수 맞추기 위함.
            while( (value= fileInputStream.read(readData)) != -1){
                // 첫 라운드에는 beforeData에 아무런 값이 없으므로 write X
                // 이후 라운드부터는 이전 라운드 값인 beforeData를 넣어줌
                // 이렇게하면 마지막 라운드에 8바이트 전 데이터까지 들어가고 while 문 종료
                if(!isFirstRound){
                    fileOutputStream.write(beforeData, 0 , value);

                }
                else
                    isFirstRound = false;

                beforeData = readData.clone(); //clone 제거시 readData와 beforeData가 레퍼런스가 묶이는 현상 발생. 앞에 녀석 지워짐.

            }


            Path desPath = Paths.get(file.getPath());
            Path srcPath = Paths.get(tempFile.getPath());
            fileInputStream.close();
            fileOutputStream.close();
            Files.move(srcPath, desPath, StandardCopyOption.REPLACE_EXISTING);

            value = toValue(beforeData);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            try {
                fileInputStream.close();
                fileOutputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("치명적인 오류 발생");
            }

        }
        return value;
    }

    public void printByte(byte[] arr){
        for(int i = 0 ; i < arr.length; i++){
            System.out.print(arr[i]);
        }
        System.out.println();
    }

}
