package kumoh.util.csv;

import java.io.*;
import java.util.*;

public class CSVTable {

    private final TreeMap<Integer,CSVRow> csv;

    private final List<String> columns;

    private String pkRegex;

    public CSVTable(String pkRegex) {
        csv = new TreeMap<>(Comparator.comparingInt(o -> o));
        columns = new ArrayList<>();
        setPkRegex(pkRegex);
    }


    public CSVTable(String CDLData, String pkRegex){
        csv = new TreeMap<>(Comparator.comparingInt(o -> o));
        this.pkRegex = pkRegex;

        String[] cdlSplit = CSVRow.splitData(CDLData, "\\n");

        String[] columnArray = CSVRow.splitData(cdlSplit[0], ",");

        columns = new ArrayList<>();
        columns.addAll(Arrays.asList(columnArray));
        if(!columns.contains("isDeleted"))
            columns.add("isDeleted");

        for(int i = 1; i < cdlSplit.length; i++) {
            if(cdlSplit[i].equals("")){
                continue;
            }
            CSVRow csvRow = new CSVRow(cdlSplit[i], columns);

            csv.put(Integer.parseInt(csvRow.getId(pkRegex)), csvRow);
        }
    }

    public CSVTable(File csvFile, String pkRegex) throws IOException {
        if(!csvFile.getName().contains(".csv")){
            throw new FileNotFoundException("파일이 맞지 않습니다.");
        }
        try(BufferedReader br = new BufferedReader(new FileReader(csvFile))){

            csv = new TreeMap<>(Comparator.comparingInt(o -> o));
            this.pkRegex = pkRegex;

            String input;
            if((input = br.readLine()) != null){

                String[] columnArray = CSVRow.splitData(input, ",");
                columns = new ArrayList<>();
                columns.addAll(Arrays.asList(columnArray));
                if(!columns.contains("isDeleted"))
                    columns.add("isDeleted");

            }else{
                throw new InputMismatchException("파일 내부가 맞지 않습니다.");
            }

            while((input = br.readLine()) != null && !"".equals(input)){
                CSVRow csvRow = new CSVRow(input, columns);
                csv.put(Integer.parseInt(csvRow.getId(pkRegex)), csvRow);
            }
        }

    }

    public void setPkRegex(String pkRegex){
        this.pkRegex = pkRegex;
    }

    public List<String> getColumns() {
        return columns;
    }


    public boolean add(String CDLData){
            CSVRow csvColumn = new CSVRow(CDLData, columns);
            if(csv.containsKey(Integer.parseInt(csvColumn.getId(pkRegex)))){
                csv.put(Integer.parseInt(csvColumn.getId(pkRegex)),csvColumn);
                return true;
            }else{
                return false;
            }
    }


    public boolean update(String id, String CDLData){
        Integer idInt = Integer.parseInt(id);
        if(csv.get(idInt) != null){
            CSVRow csvRow = new CSVRow(CDLData, columns);
            csv.replace(idInt, csvRow);
            return true;
        }
        return false;
    }

    public boolean update(CSVRow csvRow) {
        Integer id = Integer.parseInt(csvRow.getId(pkRegex));
        return csv.replace(id, csvRow) != null;
    }

    public void merge(CSVTable withUpdateData){
        for(Map.Entry<Integer, CSVRow> entry : withUpdateData.csv.entrySet()){
            Integer id = entry.getKey();
            if(entry.getValue().getId("isDeleted").equals("true")){
                csv.remove(id);
                continue;
            }
            if(!update(entry.getValue())){
                csv.put(id, entry.getValue());
            }
        }
    }

    public boolean delete(String key){
        int id = Integer.parseInt(key);

        return csv.remove(id) != null;
    }

    public void writeToFile(File target) {
        try(    FileWriter fw = new FileWriter(target);
                BufferedWriter bw = new BufferedWriter(fw)) {

            bw.append(CSVRow.columnToString(columns));
            bw.newLine();
            for(Map.Entry<Integer, CSVRow> entry : csv.entrySet()){
                bw.append(entry.getValue().toString());
                bw.newLine();
            }
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
