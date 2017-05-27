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
public class DixingDataBase {

    public int[][] DixingGrid = new int[800][1400];

    private DixingDBRecord[] DixingDBTable;

    public DixingDataBase() {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            sql = "select * from mydixing";
            ResultSet rst = stmt.executeQuery(sql);
            ArrayList<DixingDBRecord> drList = new ArrayList<DixingDBRecord>();
            while (rst.next()) {
                DixingDBRecord dr = new DixingDBRecord();
                dr.id = rst.getInt(1);
                dr.lon = rst.getFloat(2);
                dr.lat = rst.getFloat(3);
                dr.isHighLand = rst.getBoolean(5);
                
                drList.add(dr);
            }
            stmt.close();
            c.close();
            DixingDBTable = (DixingDBRecord[]) drList.toArray(new DixingDBRecord[drList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            DixingDBTable = null;
        }
        
        double[] LonLat = new double[2];
        int[] GridXY = new int[2];
        for (DixingDBRecord sr : DixingDBTable) {
            LonLat[0] = sr.lon + 0.02501f;
            LonLat[1] = sr.lat + 0.02501f;
            OverlapFun.LonLat2GridXY(LonLat, GridXY);
            if (0 + 3 <= GridXY[1] && GridXY[1] <= 800 - 3 && 0 + 3 <= GridXY[0] && GridXY[0] <= 1400 - 3) {
                int b = sr.isHighLand == true ? 1 : 0;
                for (int m = -2; m <= 2; m++) {
                    for (int n = -2; n <= 2; n++) {
                        DixingGrid[GridXY[1] + m][GridXY[0] + n] = b;
                    }
                }
            }
        }
    }
}

class DixingDBRecord {
    public int id;
    public float lon;
    public float lat;
    public boolean isHighLand;

}