/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import coverage.util.OverlapFun;


/**
 *
 * @author ZZL
 */
public class SnowDataBase {
    
    public int[][] SnowGrid = new int[800][1400];
    private SnowDBRecord[] SnowDBTable;
    public SnowDataBase() {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            sql = "select * from mysnow";
            ResultSet rst = stmt.executeQuery(sql);
            ArrayList<SnowDBRecord> srList = new ArrayList<SnowDBRecord>();
            while (rst.next()) {
                SnowDBRecord sr = new SnowDBRecord();
                sr.id = rst.getInt(1);
                sr.lon = rst.getFloat(2);
                sr.lat = rst.getFloat(3);
                sr.snowArray = new boolean[12];
                for (int i = 0; i < 12; i++) {
                    sr.snowArray[i] = rst.getBoolean(i + 5);
                }
                srList.add(sr);
            }
            stmt.close();
            c.close();
            SnowDBTable = (SnowDBRecord[]) srList.toArray(new SnowDBRecord[srList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            SnowDBTable = null;
        }

    }

    //month代表月份 
    public void UpdateSnowGrid(int month) {
        if (SnowDBTable == null) {
            return;
        }
        int index = month - 1;
        double[] LonLat = new double[2];
        int[] GridXY = new int[2];
        for (SnowDBRecord sr : SnowDBTable) {
            LonLat[0] = sr.lon + 0.02501f;
            LonLat[1] = sr.lat + 0.02501f;
            OverlapFun.LonLat2GridXY(LonLat, GridXY);
            if (0 + 3 <= GridXY[1] && GridXY[1] <= 800 - 3 && 0 + 3 <= GridXY[0] && GridXY[0] <= 1400 - 3) {
                int b = (sr.snowArray[index]) == true ? 1 : 0;
                if (sr.snowArray[0] == true && sr.snowArray[1] == true && sr.snowArray[2] == true && sr.snowArray[3] == true
                        && sr.snowArray[4] == true && sr.snowArray[5] == true && sr.snowArray[6] == true && sr.snowArray[7] == true
                        && sr.snowArray[8] == true && sr.snowArray[9] == true && sr.snowArray[10] == true && sr.snowArray[11] == true) {
                    b = 0;//终年积雪区认为无雪
//                    System.out.println("znjx");
                }

                if (b == 1) {
//                    System.out.println(b);
                }
                for (int m = -2; m <= 2; m++) {
                    for (int n = -2; n <= 2; n++) {
                        SnowGrid[GridXY[1] + m][GridXY[0] + n] = b;
                    }
                }
            }
        }

    }

}

class SnowDBRecord {
    public int id;
    public float lon;
    public float lat;
    public boolean[] snowArray;

}