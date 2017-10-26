/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skiclient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Jersey REST client generated for REST resource:LoadData [load]<br>
 * USAGE:
 * <pre>
 *        NewJerseyClient client = new NewJerseyClient();
 *        Object response = client.XXX(...);
 *        // do whatever with response
 *        client.close();
 * </pre>
 *
 * @author Frankfan
 */
public class NewJerseyClient {

    private WebTarget webTargetGet, webTargetPost;
    private Client client;
    private static String BASE_URI = "";

    public NewJerseyClient(String ip, String port) {
        client = javax.ws.rs.client.ClientBuilder.newClient();
        BASE_URI = "http://" + ip + ":" + port + "/SkierServer/webapi";
        webTargetGet = client.target(BASE_URI).path("/myvert");
        webTargetPost = client.target(BASE_URI).path("/load");
    }
    
    public void postData(RFIDLiftData rfid) {
        webTargetPost.request().post(Entity.json(rfid));
    }
    
    public String getIt(int skierId, int dayNum) {
        if (String.valueOf(skierId) != null) {
            webTargetGet = webTargetGet.queryParam("skierId", skierId);
        }
        if (String.valueOf(dayNum) != null) {
            webTargetGet = webTargetGet.queryParam("dayNum", dayNum);
        }
        return webTargetGet.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
    }

    public WebTarget postWebTarget() {
        return webTargetPost;
    }
    
    public WebTarget getWebTarget() {
        return webTargetGet;
    }

    public void close() {
        client.close();
    }
    
}
