package claudiosoft.minotauro;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.stream.Collectors;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONException;
import org.json.JSONObject;

public class OllamaAPIExample {

    private static final String ENDPOINT = "http://localhost:11434";
    private static final String MODEL_ID = "codellama:7b";
    private static final String API_KEY = "AAAAC3NzaC1lZDI1NTE5AAAAIEu0UaBGQaQ7yeTbaCW6h5kTC1qt6kmKQr375H2T4JiZ";

    public static void main(String[] args) {
        // Set up the HTTP client and endpoint URL
        CloseableHttpClient httpClient = HttpClients.createDefault();
        URI endpointURI;
        try {
            endpointURI = new URIBuilder()
                    .setScheme("http")
                    .setHost(ENDPOINT)
                    .setPath("/v1/models/" + MODEL_ID + "/generate")
                    .setParameter("stream", "false")
                    .build();
        } catch (URISyntaxException e) {
            System.out.println("Error: " + e.getMessage());
            return;
        }

        // Set up the request body and headers
        HttpPost httpPost = new HttpPost(endpointURI);
        httpPost.setHeader("Content-Type", ContentType.APPLICATION_JSON.toString());
        httpPost.setHeader("Authorization", "Bearer " + API_KEY);

        // Create a JSON object with the input data
        JSONObject jsonInput = new JSONObject();
        jsonInput.put("input", "Hello, world!");

        // Set up the request body as JSON
        HttpEntity httpEntity = new ByteArrayEntity(jsonInput.toString().getBytes());
        httpPost.setEntity(httpEntity);

        // Invoke the model and get the response
        CloseableHttpResponse response;
        try {
            response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            String responseString = new BufferedReader(new InputStreamReader(responseEntity.getContent())).lines().collect(Collectors.joining());
            JSONObject jsonResponse = new JSONObject(responseString);
            System.out.println("Response: " + jsonResponse.toString(4));
        } catch (IOException | JSONException e) {
            System.out.println("Error: " + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}
