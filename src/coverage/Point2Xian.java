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
import java.util.Collections;
import java.util.HashSet;
import coverage.iostruct.RegionEntry;

/**
 *
 * @author ZZL
 */


//本类的作用为 输入有效覆盖次数网格点矩阵，首先根据地面任务区域所涉及的行政区划的数量，判断应该按照哪个级别的行政区划输出结果。输出各个行政区划的覆盖情况（包括行政区划的行政代码，名称，面积，地面任务区域的面积，地面任务区域的有效覆盖次数）
//1.判断应该按照哪个行政区划级别（省市县）输出的方法如下：先看地面任务区域共涉及了多少个县（与多少个县相交），如果小于maxXianAmount，则按照县为输出单位进行输出；否则，判断地面任务区域共涉及了多少个市，如果小于maxShiAmount，则按照市为输出单位进行输出；否则按照省为输出单位进行输出
//进行判断时，首先把地面任务区域的所有网格点的县行政代码存入到一个list里面，利用hashset去除其中的重复行政代码（将list转化为hashset即可去除重复项），判断hashset里面的元素个数是否小于maxXianAmount，如果小于，则可以确定以县为行政单位进行输出，regionUnit赋值为RegionUnit.XIAN,且此时的hashset里面的行政代码就是需要输出的条目；否则，对hashset里面的行政代码除以100，即取行政代码的前4位，得到市的行政代码，利用list与hashset的转换，按照上述方法，可以得到按照哪一级行政单位为输出单位regionUnit，以及输出单位的行政代码列表codeList。
//2.通过第1步 得到输出单位regionUnit和行政代码列表codeList。 RegionEntry是一个条目类，用于存放行政区划名，该行政区划共包含的点数，该行政区划地面任务区域共包含的点数，该行政区划地面任务区域的有效覆盖次数总和。然后根据EffectiveGrid和XianCodeGrid，填充regionEntryArray
//
public class Point2Xian {
    public int maxXianAmount = 20;//县数量的最大值 当输出单位的数量小于该值，则以县为输出单位
    public int maxShiAmount = 20;//市数量的最大值 当输出单位的数量小于该值，则以市为输出单位
    public float[][] EffectiveGrid = new float[800][1400];//从规划算法中得到的有效覆盖次数矩阵，-10代表不在地面任务区域，否则代表有效覆盖次数
    private int[][] XianCodeGrid = new int[800][1400];//数组里的每一个元素存放着：各个网格点对应的县的行政代码
    
    public Point2Xian() {
    }
    public RegionEntry[] GetRegionCoverageResult() {

        ReadDataBase();//读point2xian数据库

        //将地面任务区域涉及的县的代码存入codeList中
        ArrayList<Integer> codeList = new ArrayList<Integer>();
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (EffectiveGrid[i][j] > -9) {
                    codeList.add(XianCodeGrid[i][j]);
                }
            }
        }
        HashSet<Integer> codeHashSet;
        RegionUnit regionUnit;
        codeHashSet = new HashSet<Integer>(codeList);//list转hashset，去除重复项
        //判断所涉及的县的总数是否大于maxXianAmount
        if (codeHashSet.size() > maxXianAmount) {
            codeList.clear();
            for (int n : codeHashSet) {
                codeList.add(n / 100);//取县行政代码的前4位，转化为市的行政代码
            }
            codeHashSet = new HashSet<Integer>(codeList);
            if (codeHashSet.size() > maxShiAmount) {
                codeList.clear();
                for (int n : codeHashSet) {
                    codeList.add(n / 100);//取市行政代码的前4位，转化为省的行政代码
                }
                codeHashSet = new HashSet<Integer>(codeList);
                codeList= new ArrayList<Integer>(codeHashSet);
                regionUnit = RegionUnit.SHENG;
            } else {
                codeList = new ArrayList<Integer>(codeHashSet);
                regionUnit = RegionUnit.SHI;
            }
        } else {
            codeList = new ArrayList<Integer>(codeHashSet);
            regionUnit = RegionUnit.XIAN;
        }
        
        //至此，regionUnit存放输出单位，codeList存放行政代码列表

        Collections.sort(codeList);
        int codeListSize = codeList.size();
        RegionEntry[] regionEntryArray = new RegionEntry[codeListSize];
        for (int i = 0; i < codeListSize; i++) {
            regionEntryArray[i]=new RegionEntry();
            regionEntryArray[i].regionCode =codeList.get(i);
        }
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                int xianCode=XianCodeGrid[i][j];
                switch (regionUnit) {
                    case SHENG:
                        xianCode/=10000;
                        break;
                    case SHI:
                        xianCode /= 100;
                        break;
                    case XIAN:
                        xianCode /= 1;
                        break;
                }
                for (int k = 0; k < codeListSize; k++) {
                    if (regionEntryArray[k].regionCode == xianCode) {
                        //如果第i行第j列的网格点的行政代码 等于 该条目的行政代码 说明该网格点位于该条目的行政代码对应的区划中，则将 该区划的网格点总数增1，目的用于统计每一个区划的总网格点数
                        regionEntryArray[k].totalPoint++;
                        if (EffectiveGrid[i][j] > -9) {
                            //如果第i行第j列的EffectiveGrid不等于-10，即位于地面任务区域内部，则：
                            regionEntryArray[k].groundTaskRegionTotalPoint++;
                            //regionEntryArray[k].coverageTimes += EffectiveGrid[i][j];
                            regionEntryArray[k].coverageTimes += EffectiveGrid[i][j] >= 1.0f ? 1.0f : 0;
                        }
                        break;
                    }
                }
            }
        }
        
        //从数据库中读取每一个条目的行政代码对应的区域的中文名
        for (int k = 0; k < codeListSize; k++) {
            regionEntryArray[k].regionName=ReadFromSSXDataBase(regionEntryArray[k].regionCode,regionUnit);
        }
        return regionEntryArray;
    }

    //读point2xian数据库，里面的每一条记录为各个网格点对应的县的代码
    //该函数实现根据数据库的记录，用县的行政代码填充XianCodeGrid
    private void ReadDataBase() {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            stmt = c.createStatement();
            sql = "select * from mypoint2xian";
            ResultSet rst = stmt.executeQuery(sql);
            while (rst.next()) {
                int id = rst.getInt(1);
                int i = rst.getInt(2);
                int j = rst.getInt(3);
                int code = rst.getInt(4);
                XianCodeGrid[i][j] = code;
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //从数据库中读取行政代码对应的中文名
    private String ReadFromSSXDataBase(int code, RegionUnit ru) {
        String regionString=null;
        Connection c = null;
        Statement stmt;
        String sql;
        ResultSet rst;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            stmt = c.createStatement();
            switch (ru) {
                case SHENG:
                    sql = "select * from myshp_sheng where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(2);
                        break;
                    }
                    break;
                case SHI:
                    sql = "select * from myshp_shi where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(3)+rst.getString(2);
                        break;
                    }
                    break;
                case XIAN:
                    sql = "select * from myshp_xian where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(5)+rst.getString(4)+rst.getString(3);
                        break;
                    }
                    break;
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regionString;
    }

    private enum RegionUnit {
        SHENG, SHI, XIAN;
    }
}
