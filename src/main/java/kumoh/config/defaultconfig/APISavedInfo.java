package kumoh.config.defaultconfig;

import kumoh.domain.Api;
import kumoh.util.txt.MappingHashMap;
import lombok.extern.java.Log;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Log
@Component

public class APISavedInfo {

    private static List<Api> apiList;

    private static int size = 0;

    private final MappingHashMap mappingHashMap;

    public APISavedInfo(MappingHashMap mappingHashMap) {
        apiList = new ArrayList<>();
        this.mappingHashMap = mappingHashMap;
    }

    static void loadApiList(LinkedHashMap<String, String> configInfo) {
        if(configInfo.containsKey("api_nums"))
            size = Integer.parseInt(configInfo.get("api_nums"));


        for(int i = 0 ; i < size; i++){
            System.out.println(configInfo.get("api"+i));
            if (!apiList.contains(Api.parseApi(configInfo.get("api"+i)).setValidation())){
                apiList.add(Api.parseApi(configInfo.get("api"+i)).setValidation());
            }

        }
    }

    public void updateApiList(List<Api> apiList) throws IOException {
        updateApiList(apiList, ConfigLoader.getConfigFile(), ConfigLoader.getLinkedHashMap());
    }


    private void updateApiList(List<Api> api, File configFile, LinkedHashMap<String, String> configInfo) throws IOException {
        if(api == null){
            return;
        }

        for(int i = 0 ; i < size; i++){
            configInfo.remove("api"+i);
            mappingHashMap.deleteHashMap("api"+i, configFile);
        }

        apiList.clear();

        size = 0;

        for(int i = 0 ; i < api.size(); i++){
            api.get(i).setId(i);
            apiList.add(api.get(i).setValidation());
            mappingHashMap.saveHashMap("api"+i, api.get(i).toString(), configFile);
            size++;
        }

        configInfo.replace("api_nums", Integer.toString(size));
        mappingHashMap.updateHashMap("api_nums", Integer.toString(size), configFile);
    }

    public static List<Api> getApiList() {
        return apiList;
    }


}
