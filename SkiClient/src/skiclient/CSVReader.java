/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package skiclient;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Frankfan
 */
public class CSVReader {
     private List<RFIDLiftData> liftList = new ArrayList<>();
     
     public CSVReader() {
        String csvFile = "/E:/NEU/Distributed System/A2/BSDSAssignment2Day1.csv";
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            line = br.readLine();
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] skiInfo = line.split(cvsSplitBy);
                liftList.add(new RFIDLiftData(
                        Integer.parseInt(skiInfo[0]),
                        Integer.parseInt(skiInfo[1]), 
                        Integer.parseInt(skiInfo[2]), 
                        Integer.parseInt(skiInfo[3]), 
                        Integer.parseInt(skiInfo[4])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
     }
     
     public List<RFIDLiftData> getLiftList() {
         return this.liftList;
     }
}
