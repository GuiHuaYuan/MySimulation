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
public class CloudDataBase {

    public float[][] CloudGrid = new float[800][1400];
    private CloudDBRecord[] CloudDBTable;//该表用于存储整个mycloud的数据，一次性将mycloud数据表里的内容读出，放入内存，加快运行速度
    public CloudDataBase() {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            sql = "select * from mycloud";
            ResultSet rst = stmt.executeQuery(sql);
            ArrayList<CloudDBRecord> crList = new ArrayList<CloudDBRecord>();
            while (rst.next()) {
                CloudDBRecord cr = new CloudDBRecord();
                cr.id = rst.getInt(1);
                cr.lon = rst.getFloat(2);
                cr.lat = rst.getFloat(3);
                cr.cloudArray = new float[36];
                for (int i = 0; i < 36; i++) {
                    cr.cloudArray[i] = rst.getFloat(i + 4);
                }
                crList.add(cr);
            }
            stmt.close();
            c.close();
            CloudDBTable = (CloudDBRecord[]) crList.toArray(new CloudDBRecord[crList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            CloudDBTable = null;
        }

    }

    //month代表月份  xun代表旬（0,1,2依次代表上旬 中旬 下旬）
    public void UpdateCloudGrid(int month, int xun) {
        if (CloudDBTable == null) {
            return;
        }
        int index = 3 * (month - 1) + xun;
        double[] LonLat = new double[2];
        int[] GridXY = new int[2];
        for (CloudDBRecord cr : CloudDBTable) {
            LonLat[0] = cr.lon + 0.02501f;
            LonLat[1] = cr.lat + 0.02501f;
            OverlapFun.LonLat2GridXY(LonLat, GridXY);
            if (0 + 3 <= GridXY[1] && GridXY[1] <= 800 - 3 && 0 + 3 <= GridXY[0] && GridXY[0] <= 1400 - 3) {
                float f = cr.cloudArray[index];
                for (int m = -2; m <= 2; m++) {
                    for (int n = -2; n <= 2; n++) {
                        CloudGrid[GridXY[1] + m][GridXY[0] + n] = f;
                    }
                }
            }
        }

    }

    public void UpdateCloudGrid(int month1, int xun1, int month2, int xun2) {
        if (CloudDBTable == null) {
            return;
        }
        int index1 = 3 * (month1 - 1) + xun1;
        int index2 = 3 * (month2 - 1) + xun2;
        if (index1 > index2) {
            return;
        }
        double[] LonLat = new double[2];
        int[] GridXY = new int[2];
        for (CloudDBRecord cr : CloudDBTable) {
            LonLat[0] = cr.lon + 0.02501f;
            LonLat[1] = cr.lat + 0.02501f;
            OverlapFun.LonLat2GridXY(LonLat, GridXY);
            if (0 + 3 <= GridXY[1] && GridXY[1] <= 800 - 3 && 0 + 3 <= GridXY[0] && GridXY[0] <= 1400 - 3) {
                float f = 0;
                for (int i = index1; i <= index2; i++) {
                    f += cr.cloudArray[i];
                }
                f /= index2 - index1 + 1;
                for (int m = -2; m <= 2; m++) {
                    for (int n = -2; n <= 2; n++) {
                        CloudGrid[GridXY[1] + m][GridXY[0] + n] = f;
                    }
                }
            }
        }

    }

}

class CloudDBRecord {
    public int id;
    public float lon;
    public float lat;
    public float[] cloudArray;
}
