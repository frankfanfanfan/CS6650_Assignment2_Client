/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skiclient;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

/**
 *
 * @author Frankfan
 */
public class Task implements Runnable {
    
    private String ip;
    private String port;
    private List<RFIDLiftData> RFIDList;
    private int countGet = 0;
    private int countPost = 0;
    private List<Long> getTimeList = new ArrayList<>();
    private List<Long> postTimeList = new ArrayList<>();
    private NewJerseyClient client;
    private boolean isGet = false;
    private final int LOOP = 400;
    
    public Task() {}
    
    public Task(String ip, String port,  List<RFIDLiftData> RFIDList, boolean isGet) {
        this.ip = ip;
        this.port = port;
        this.RFIDList = RFIDList;
        this.client = new NewJerseyClient(ip, port);
        this.isGet = isGet;

    }
            
    @Override
    public void run() {
        if (isGet) {
            for (int i = 0; i < LOOP; i++) {
                long start = System.currentTimeMillis();
                try {
                    String getResult = client.getIt(2 ,1);
                    System.out.println("length is: " + getResult.length());
                    countGet++;
                } catch (Exception e) {
                    e.printStackTrace();
                }             

                long end = System.currentTimeMillis();
                getTimeList.add(end - start);
                System.out.println((end - start) + " " + countGet);
            }
        } else {
                for (RFIDLiftData rfid : RFIDList) {
                long start = System.currentTimeMillis();
                try {
                    client.postData(rfid);
                    countPost++;
                } catch (Exception e) {
                    e.printStackTrace();
                } 

                long end = System.currentTimeMillis();
                postTimeList.add(end - start);
                System.out.println((end - start) + " " + countPost);
            }
        }

    } 
    public int getCountGet() {
        return countGet;
    }
    
    public int getCountPost() {
        return countPost;
    }
    
    public List<Long> getGetTimeList() {
        return getTimeList;
    }
    
    public List<Long> getPostTimeList() {
        return postTimeList;
    }
}
