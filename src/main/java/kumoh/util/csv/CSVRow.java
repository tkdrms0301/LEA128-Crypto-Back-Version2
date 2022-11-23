package kumoh.util.csv;


import lombok.*;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CSVRow {

    private LinkedHashMap<String, String> lows;


    public CSVRow(String CDLString, List<String> columns){
        toLowColumns(CDLString, columns);
    }

    public static String[] splitData(String rowColumns, String regex){
        return rowColumns.split(regex+"(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    }

    private void toLowColumns(String CDLData, List<String> columns){
        LinkedHashMap<String, String> lows = new LinkedHashMap<>();
        String[] values = splitData(CDLData, ",");

        for(int i = 0 ; i < values.length; i++){
            lows.put(columns.get(i),values[i]);
        }
        lows.put("isDeleted","false");
        this.lows = lows;
    }

    public String getId(String pkRegex){
        return lows.get(pkRegex);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Map.Entry<String,String>> dataIterator = lows.entrySet().iterator();
        Map.Entry<String, String> before = null;
        while(dataIterator.hasNext()){
            if(before == null){
                before = dataIterator.next();
            }
            sb.append(before.getValue());
            if(dataIterator.hasNext()){
                before = dataIterator.next();
                if(before.getKey().equals("isDeleted")){
                    before.setValue("");
                    continue;
                }
                sb.append(",");
            }
        }
        return sb.toString();
    }
/*
        StringBuilder sb = new StringBuilder();
        Iterator<String> dataIterator = data.iterator();
        String before  = null;
        while(dataIterator.hasNext()){
            if (before == null){
                before = dataIterator.next();
            }
            //String column = dataIterator.next();
//            if(before.equals("isDeleted")){
//                continue;
//            }
            sb.append(before);
            if(dataIterator.hasNext()){
                before = dataIterator.next();
                if (before.equals("isDeleted")){
                    before = "";
                    continue;
                }

                sb.append(",");
            }
        }
 */
    public static String columnToString(List<String> data){
        StringBuilder sb = new StringBuilder();
        Iterator<String> dataIterator = data.iterator();
        String before  = null;
        while(dataIterator.hasNext()){
            if (before == null){
                before = dataIterator.next();
            }
            //String column = dataIterator.next();
//            if(before.equals("isDeleted")){
//                continue;
//            }
            sb.append(before);
            if(dataIterator.hasNext()){
                before = dataIterator.next();

                if (before.equals("isDeleted")){
                    before = "";
                    continue;
                }
                sb.append(",");
            }
        }
        return sb.toString();
    }

}
