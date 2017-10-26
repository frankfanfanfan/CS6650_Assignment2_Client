/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skiclient;

/**
 *
 * @author Frankfan
 */
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Frankfan
 */
public class SkiClient {
    private static final int GET_NUM = 40000;
    private static final int GET_THREADS = 100;
    private static final int POST_THREADS = 0;
    
    public static void main(String[] args) {

        CSVReader csvReader = new CSVReader();
        List<RFIDLiftData> RFIDList = csvReader.getLiftList();
//        RFIDList = RFIDList.subList(0, 200000);
    NewJerseyClient testClient = new NewJerseyClient("34.215.68.92", "8080");
//    String result = testClient.getIt(2, 1);
//    System.out.println(result);
 
        multiThread("34.215.68.92", "8080", RFIDList);
//          multiThread(100, "127.0.0.1", "8080", RFIDList);
        
    }

    public static void multiThread(String ip, String port, List<RFIDLiftData> RFIDList) {
        List<List<RFIDLiftData>> listForThread = divideList(RFIDList, POST_THREADS);
        List<Long> postLantencies = new ArrayList<>();
        List<Long> getLantencies = new ArrayList<>();
//        List<Long> postLantencies = new ArrayList<>();
        
        long start = System.currentTimeMillis();
	System.out.println("Client starting..... Time: " + start);
        System.out.println("All threads running......");
        
        List<Task> taskList = accessData(ip, port, listForThread);
        
        long end = System.currentTimeMillis();
        System.out.println("All threads complete..... Time: " + end);
        
        double wallTime = (double)(end - start) / (double)1000;
        DecimalFormat df = new DecimalFormat("#.0");
        System.out.println("Test Wall Time: " + df.format(wallTime) + " seconds");
        
        int countGet = 0;
        int countPost = 0;
        for (Task t : taskList) {
            countGet += t.getCountGet();
            countPost += t.getCountPost();
            postLantencies.addAll(t.getPostTimeList());
            getLantencies.addAll(t.getGetTimeList());
        }
        
        int sent_post = POST_THREADS == 0 ? 0 : RFIDList.size();
        int sent_get = GET_THREADS == 0 ? 0 : GET_NUM;
                
        System.out.println("Total number of post request sent: " + sent_post);
        System.out.println("Total number of get request sent: " + sent_get);
        System.out.println("Total number of successful post responses: " + countPost);
        System.out.println("Total number of successful get responses: " + countGet);
        
        if (POST_THREADS != 0) {
            System.out.println("Post request measurements: ");
            latencyCounter(postLantencies);
        }
        if (GET_THREADS != 0) {
            System.out.println("Get request measurements: ");
            latencyCounter(getLantencies);
        }
        
        
    }
    
    public static List<Task> accessData(
            String ip, 
            String port,  
            List<List<RFIDLiftData>> listForThread) {
        
        ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(GET_THREADS + POST_THREADS);
        List<Task> taskList = new ArrayList<>();
        
        for (int i = 0; i < GET_THREADS; i++) {
            Task task = new Task(ip, port, new ArrayList<RFIDLiftData>(), true);
            executor.submit(task);
            taskList.add(task);
        }
        for (int i = 0; i < POST_THREADS; i++) {
            Task task = new Task(ip, port, listForThread.get(i), false);
            executor.submit(task);
            taskList.add(task);
        }
        
        executor.shutdown();
        
        try {
            executor.awaitTermination(1, TimeUnit.DAYS);
        } catch (InterruptedException ex) {
            Logger.getLogger(SkiClient.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return taskList;
         
    }
    
    public static List<List<RFIDLiftData>> divideList (List<RFIDLiftData> RFIDList, int number) {
        List<List<RFIDLiftData>> result = new ArrayList<List<RFIDLiftData>>();
        if (number == 0) { return result; }
        int size = RFIDList.size();
        
        for (int i = 0; i < number; i++) {
            List<RFIDLiftData> cur = new ArrayList<>();
            result.add(cur);
        }
        for (int i = 0; i < size; i++) {
            int num = i % number;
            result.get(num).add(RFIDList.get(i));
        }
        return result;
    }
    
    public static void latencyCounter(List<Long> latencyList) {
        Collections.sort(latencyList);
        int count = latencyList.size();
        DecimalFormat df = new DecimalFormat("#.0");
        
        long medianLatency = latencyList.get(count / 2);
        long ninetyNineLatency = latencyList.get(count / 100 * 99);
        long ninetyFiveLatency = latencyList.get(count / 100 * 95);
        
        long meanLatency = 0;
        for (long latency : latencyList) {
            meanLatency += latency;
        }
        
        double meanResult = (double)meanLatency / (double)count;

        System.out.println("99th percentile latency is: " + ninetyNineLatency + " ms");
        System.out.println("95th percentile latency is: " + ninetyFiveLatency + " ms");
        System.out.println("Median latency is: " + medianLatency + " ms");
        System.out.println("Mean Latency is: " + df.format(meanResult) + " ms");
    }
    
}

