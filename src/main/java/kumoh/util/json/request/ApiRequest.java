package kumoh.util.json.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Component
public class ApiRequest {

    private static final int TIMEOUT = 5000;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplate;

    private HttpEntity<?> httpEntity;

    public ApiRequest() { restTemplate = new RestTemplate();}


    public JSONArray getJsonArray(String url) {
        JSONArray result;
        setHttpEntity();

        ResponseEntity<?> resultMap = getResultMap(url, HttpMethod.GET, httpEntity, String.class);

        String jsonData = getJSONStringFromResponseEntity(resultMap);
        result = stringToJSONArray(jsonData);

        return result;
    }

    public HttpStatus getHttpValidate(String url){
        setHttpEntity();

        ResponseEntity<?> resultMap = getResultMap(url, HttpMethod.GET, httpEntity, String.class);
        return resultMap.getStatusCode();
    }

    public JSONObject getJsonObject(String url){
        JSONObject jsonObject;
        setHttpEntity();

        ResponseEntity<?> resultMap = getResultMap(url, HttpMethod.GET, httpEntity, String.class);
        String jsonData = getJSONStringFromResponseEntity(resultMap);
        jsonObject = stringToJSONObject(jsonData);
        return jsonObject;
    }

    private JSONObject stringToJSONObject(String jsonString){
        return new JSONObject(jsonString);
    }

    private String getJSONStringFromResponseEntity(ResponseEntity<?> packet){
        return packet.getBody().toString() != null ? packet.getBody().toString() : "{}";
    }

    private ResponseEntity<?> getResultMap(String url, HttpMethod httpMethod,HttpEntity entity, Class classOrient){
        return restTemplate.exchange(url, httpMethod, entity, classOrient);
    }

    private JSONArray stringToJSONArray(String jsonString){
        return new JSONArray(jsonString);
    }

    private HttpComponentsClientHttpRequestFactory setState() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(TIMEOUT);
        factory.setReadTimeout(TIMEOUT);
        return factory;
    }

    private void setHttpEntity(){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpEntity = new HttpEntity<>(httpHeaders);

    }
}
