package kumoh.util.db.DBListManage;


import kumoh.dto.db.DBListDTO;
import kumoh.service.RSAService;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DBList {
    private final RSAService rsaControl;
    public DBList(RSAService rsaControl) {
        this.rsaControl = rsaControl;
    }

    List<DBListDTO> testDTOS;
    public void toLiveDBList(List<DBListDTO> dbListDTOS) {
        try {
            File dbList = new File("DBList");

            if(!dbList.exists()){
                dbList.mkdir();
            }

            //true면 이어쓰기, false면 덮어쓰기, default는 false
            FileWriter fw = new FileWriter("DBList/DBList.txt");
            BufferedWriter bw = new BufferedWriter(fw);

            for (int i = 0; i < dbListDTOS.size(); i++){
                DBListDTO dto = rsaControl.encryptDBList(dbListDTOS.get(i));

                bw.append(dto.getDbmsName());
                bw.append('|');
                bw.append(dto.getUserID());
                bw.append('|');
                bw.append(dto.getUserPW());
                bw.append('|');
                bw.append(dto.getSchemaName());
                bw.append('|');
                bw.append(dto.getTableName());
                bw.append('|');
                bw.append(dto.getStartPoint());
                bw.append('|');
                bw.append(dto.getDbAddress());
                bw.append('\n');
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            System.out.println("error : " + e);
        }
    }

    public List<DBListDTO> readFileToDBList(){
        testDTOS = new ArrayList<>();
        try {
            File f = new File("DBList/DBList.txt");
            BufferedReader br = new BufferedReader(new FileReader(f));
            String line = "";

            while ((line = br.readLine()) !=null){
                DBListDTO testDTO = new DBListDTO();
                String[] lineArr = line.split("\\|");

                testDTO.setDbmsName(lineArr[0]);
                testDTO.setUserID(lineArr[1]);
                testDTO.setUserPW(lineArr[2]);
                testDTO.setSchemaName(lineArr[3]);
                testDTO.setTableName(lineArr[4]);
                testDTO.setStartPoint(lineArr[5]);
                testDTO.setDbAddress(lineArr[6]);

                testDTOS.add(rsaControl.decryptDBList(testDTO));
            }
        }catch (Exception e){
            System.out.println(e);
        }
        return testDTOS;
    }
}
