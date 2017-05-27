/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coverage;


import com.alibaba.fastjson.JSON;
import com.vividsolutions.jts.geom.*;
import coverage.iostruct.*;
import coverage.util.*;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
//import java.math.*;

/**
 *
 * @author ZZL
 */
public class Access {
    
    public Access() {
        System.out.println("This is master");
    }

    //经度：70-140  50~160     纬度：15-55

    int[] cloud2times = new int[]{
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //0-10
        1, 1, 1, 1, 1, 1, 1, 1, 1, 1, //11-20
        2, 2, 2, 2, 2, 2, 2, 2, 2, 2, //21-30
        3, 3, 3, 3, 3, 3, 3, 3, 3, 3, //31-40
        3, 3, 3, 3, 3, 4, 4, 4, 4, 4, //41-50
        4, 4, 5, 5, 5, 6, 6, 7, 7, 7, //51-60
        8, 8, 8, 8, 8, 9, 9, 9, 9, 9, //61-70
        99, 99, 99, 99, 99, 99, 99, 99, 99, 99, //71-80
        99, 99, 99, 99, 99, 99, 99, 99, 99, 99, //81-90
        99, 99, 99, 99, 99, 99, 99, 99, 99, 99 //91-100 
    };

    //对外接口函数
    public float[][] calGridCoverage(AccessInput ai, AccessOutput ao) {

        for (SatelliteInput sli : ai.satelliteInput) {
            //如果整星可侧摆，传感器不可侧摆，转化为整星不可侧摆，传感器可侧摆
            if (sli.leftAngle != 0 || sli.rightAngle != 0) {
                for (SensorInput ssi : sli.sensorInput) {
                    ssi.leftAngle = sli.leftAngle;
                    ssi.rightAngle = sli.rightAngle;
                }
                sli.leftAngle = 0;
                sli.rightAngle = 0;
            }
            //如果传感器的侧摆角大于30，则设定为30，防止过大的侧摆角造成扫描线超出地球的范围
            for (SensorInput ssi : sli.sensorInput) {
                if (Math.abs(ssi.leftAngle) > 30 || Math.abs(ssi.rightAngle) > 30) {
                    ssi.leftAngle = -30;
                    ssi.rightAngle = 30;
                }
            }
        }
        int maxGridPerDay = 0;//所有卫星一天可以覆盖的最大点数
        for (SatelliteInput sli : ai.satelliteInput) {
            //计算传感器的最大成像能力
            for (SensorInput ssi : sli.sensorInput) {
                if ((int) (ssi.maxAreaPerDay) != 0) {
                    maxGridPerDay += (int) (ssi.maxAreaPerDay);
                } else {
                    maxGridPerDay = 10000000;
                    System.out.println("忽略最大成像能力！");
                    break;
                }
                if (maxGridPerDay == 10000000) {
                    break;
                }
            }
        }
//        maxGridPerDay = 10000000;
        maxGridPerDay /= 25;//面积转点数。每个点数代表25平方公里

        //存储用户输入的起止时间
        Time taskStartTime = new Time(ai.startTime);
        Time taskEndTime = new Time(ai.endTime);
        taskEndTime.add(Calendar.DAY_OF_MONTH, 1);//客户端传入的结束时间是当天的0：00，但实际调度的结束时间是次日的0：00

        OneDayCoverage odc = new OneDayCoverage();//卫星覆盖次数计算工具类
        CloudDataBase cloudDataBase = new CloudDataBase();//读取云量数据库工具类
        DixingDataBase dixingDatabase = new DixingDataBase();//读取地形数据库工具类
        odc.dixingGrid = dixingDatabase.DixingGrid;
        SnowDataBase snowDataBase = new SnowDataBase();//读取雪量数据库工具类
        odc.snowGrid = snowDataBase.SnowGrid;
        odc.isSnowConsidered = ai.isSnowConseidered;//是否考虑雪量影响
        odc.isDixingConsidered = ai.isDixingConseidered;//是否考虑地形影响
        if (odc.isDixingConsidered == true) {
            odc.maxMountainAngle = ai.mountainSwingAngle;
            odc.maxPlainAngle = ai.plainSwingAngle;
        }

        int[] MonthXunStart = taskStartTime.getMonthXun();//获取开始时间所在的月份和旬
        int[] MonthXunEnd = taskEndTime.getMonthXun();//获取结束时间所在的月份和旬
        
        String path;
        int totalNode = 0;
        totalNode = Geometry2Grid(ai, odc);//对输入的Geometry进行采样，返回地面任务区域的总点数
        ao.totalGrid = totalNode;
        System.out.println("采样结束");
        
        
        
//        try {
//            DataOutputStream dis = new DataOutputStream(new BufferedOutputStream(new FileOutputStream("D:\\a.txt")));
//            for (int i = 0; i < 800; i++) {
//                for (int j = 0; j < 1400; j++) {
//                    dis.writeInt((int)odc.chinaGrid[i][j]);
//                }
//            }
//            dis.close();
//            for (int i = 0; i < 800; i++) {
//                for (int j = 0; j < 1400; j++) {
//                    if (odc.chinaGrid[i][j] == 0) {
//                        totalNode += 1;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }

        //////////////////////////
        
//        int totalNode = 0;
//        path = "grid\\" + "cg" + ".txt";
//        path = "grid\\" + "China_NF" + ".txt";
//
//        try {
//            DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(path)));
//            for (int i = 0; i < 800; i++) {
//                for (int j = 0; j < 1400; j++) {
//                    int n = dis.readInt();
//                    odc.chinaGrid[i][j] = n;
//                    odc.chinaOneDayGrid[i][j] = n;
//                }
//            }
//            dis.close();
//            for (int i = 0; i < 800; i++) {
//                for (int j = 0; j < 1400; j++) {
//                    if (odc.chinaGrid[i][j] >= -1) {
//                        totalNode += 1;
//                    }
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        ao.totalGrid= totalNode;
        
        /////////////////////////////
        

//        taskFinalTime.add(Calendar.DAY_OF_MONTH, 180);
//        Time[] timeNodeArray = this.getTimeNode(ai.startTime, ai.endTime, taskFinalTime.toBJTime());
        Time[] timeNodeArray = this.getTimeNode(taskStartTime, taskEndTime);
        ao.timeNodeArray = new String[timeNodeArray.length];//时间节点
        ao.progressArray = new float[timeNodeArray.length];//拍摄进度
        ao.coverageTimesArray = new int[timeNodeArray.length][];//覆盖次数
        ao.cloudArray = new int[timeNodeArray.length][];//云量情况

        float[][] effectiveTimesGrid = new float[800][1400];//该矩阵用于存放网格点的有效覆盖次数，即：实际覆盖次数*(1/各个网格点云量所对应的覆盖次数)
        float[][] formerChinaGrid=new float[800][1400];//本函数按照旬进行步进，为得到当前旬的覆盖进度，需知道上一个旬的覆盖情况。该矩阵用于存放上个旬的覆盖情况

        int timeNodeIndex;//时间节点序列索引，按照旬为计算单位进行计算
        for (timeNodeIndex = 0; timeNodeIndex < timeNodeArray.length - 1; timeNodeIndex++) {
            
            Time xunStartTime = timeNodeArray[timeNodeIndex].clone();//当前旬的开始时间
            ao.timeNodeArray[timeNodeIndex] = xunStartTime.toBJTime();
            ao.coverageTimesArray[timeNodeIndex] = new int[20];
            ao.cloudArray[timeNodeIndex] = new int[20];
            int[] ms = xunStartTime.getMonthXun();
            snowDataBase.UpdateSnowGrid(ms[0]);//获取旬所在的月份，更新雪量情况

            //在当期旬内，以天为时间单位，计算该旬的卫星覆盖情况
            Time DayStartTime = xunStartTime.clone();//每一天的起始时间，从当前旬的第一天开始算起
            while (DayStartTime.before(timeNodeArray[timeNodeIndex + 1])) {
                Time DayEndTime = DayStartTime.clone();//每一天的结束时间
//                System.out.println(DayEndTime.toBJTime());
                DayEndTime.add(Calendar.DAY_OF_MONTH, 1);
                ai.startTime = DayStartTime.toBJTime();
                ai.endTime = DayEndTime.toBJTime();//注意，此行代码用于向odc传递时间信息，但同时造成ai.endTime和ai.startTime一直在变
                odc.calOneDayCoverage(ai);//调用calOneDayCoverage，贪心算法，根据各个网格点已经覆盖的次数，选择侧摆角，得到改天的覆盖情况
                
                //统计一天共覆盖了多少个点
                int totalGridOneDay = 0;//每天覆盖的总点数
                for (int i = 0; i < 800; i++) {
                    for (int j = 0; j < 1400; j++) {
                        if (odc.chinaGrid[i][j] > -9 && odc.chinaOneDayGrid[i][j] >= 1) {
                            totalGridOneDay++;
                        }
                    }
                }

                //如果每天覆盖的点数大于最大覆盖点数，则计算有效覆盖次数时，将乘以“最大能力修正系数”
                totalGridOneDay = totalGridOneDay <= 0 ? 1 : totalGridOneDay;
                float maxGridPerDayRate = (float) maxGridPerDay / totalGridOneDay;
                maxGridPerDayRate = maxGridPerDayRate >= 1 ? 1 : maxGridPerDayRate;//最大能力修正系数
//                System.out.println("ToTalPointOneDay:" + totalGridOneDay + "  maxGridPerDayRate:" + maxGridPerDayRate);

                for (int i = 0; i < 800; i++) {
                    for (int j = 0; j < 1400; j++) {
                        //chinaGrid中，-10代表不位于地面任务区域内部
                        //chinaOneDayGrid表示当天的覆盖情况
                        if (odc.chinaGrid[i][j] >-9 && odc.chinaOneDayGrid[i][j] >= 1) {//如果[i][j]位于地面任务区域内部，且覆盖次数超过1次
                            odc.chinaGrid[i][j] += 1 * maxGridPerDayRate;//一天内覆盖多次按照一次计算,并乘以“最大能力修正系数”
                            odc.chinaOneDayGrid[i][j] = 0;//覆盖次数增量矩阵重新置零，用于下一次计算
                        }
                    }
                }
                DayStartTime.add(Calendar.DAY_OF_MONTH, 1);//计算时间增加一天
            }

            //根据云量情况和实际覆盖情况计算有效覆盖次数
            cloudDataBase.UpdateCloudGrid(ms[0], ms[1]);//更新云量情况
            for (int i = 0; i < 800; i++) {
                for (int j = 0; j < 1400; j++) {
                    if (odc.chinaGrid[i][j] != -10) {//首先判断是否位于地面任务区域内部
                        float coverageTimes = odc.chinaGrid[i][j];//点[i][j]的当前旬实际覆盖次数
                        coverageTimes = coverageTimes >= 19 ? 19 : coverageTimes;//超过19次，则忽略

                        ao.coverageTimesArray[timeNodeIndex][(int) Math.floor(coverageTimes)] += 1;//覆盖次数计入AccessOutput中，

                        float xunTimes = odc.chinaGrid[i][j] - formerChinaGrid[i][j];//计算当前旬的实际覆盖次数=当前旬的总覆盖次数-上一旬的总覆盖次数
                        int cloudIndex = (int) (cloudDataBase.CloudGrid[i][j] * 100);//计算该点云量的百分数
                        cloudIndex = cloudIndex > 99 ? 99 : cloudIndex;//等于100时，按照99算
                        ao.cloudArray[timeNodeIndex][cloudIndex / 5]++;//云量情况计入AccessOutput中

                        effectiveTimesGrid[i][j] += (float) xunTimes / cloud2times[cloudIndex];//计算有效覆盖次数=实际覆盖次数 * (1/该点的云量情况对应的覆盖次数)

                        formerChinaGrid[i][j] = odc.chinaGrid[i][j];
                    }
                }
            }
            
            //计算覆盖进度，当前旬的覆盖进度=地面任务区域的所有点有效覆盖次数的总和（大于1次按照1次算）/总点数
            float progress = 0;
            for (int i = 0; i < 800; i++) {
                for (int j = 0; j < 1400; j++) {
                    if (effectiveTimesGrid[i][j] >= 1.0f) {
                        progress += 1.0f;
                    } else {
                        progress += effectiveTimesGrid[i][j];
                    }
                }
            }
            ao.progressArray[timeNodeIndex] = progress / totalNode;
            
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //                                         到达任务结束时间                                                   //
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            if (timeNodeArray[timeNodeIndex + 1].equal(taskEndTime)) {
                ao.pngArray = Grid2png(odc.chinaGrid, effectiveTimesGrid);//将有效覆盖次数矩阵转化为PNG，作为结果返回
                ao.difficultyDegree = new int[4];//获取数据的难易程度数组，[0][1][2][3]依次代表易于、较易、较难、难于，对应的有效覆盖次数依次为 0-0.5 0.5-1 1-1.5 1.5以上
                for (int i = 0; i < 800; i++) {
                    for (int j = 0; j < 1400; j++) {
                        if (odc.chinaGrid[i][j] >-9) {
                            if (effectiveTimesGrid[i][j] < 0.5f) {
                                ao.difficultyDegree[3]++;
                            } else if (effectiveTimesGrid[i][j] < 1.0f) {
                                ao.difficultyDegree[2]++;
                            } else if (effectiveTimesGrid[i][j] < 1.5f) {
                                ao.difficultyDegree[1]++;
                            } else {
                                ao.difficultyDegree[0]++;
                            }
                        }
                    }
                }
                Point2Xian p2x = new Point2Xian();
                for (int i = 0; i < 800; i++) {
                    for (int j = 0; j < 1400; j++) {
                        if (odc.chinaGrid[i][j] > -9) {
                            p2x.EffectiveGrid[i][j] = effectiveTimesGrid[i][j];
                        } else {
                            p2x.EffectiveGrid[i][j] = -10;
                        }
                    }
                }
                ao.regionCoverageInfoArray = p2x.GetRegionCoverageResult();
                break;
            }
        }
        ao.timeNodeArray[timeNodeIndex + 1] = timeNodeArray[timeNodeIndex + 1].toBJTime();//将最后一个时间节点加入AccessOutput中
        ao.isSuccess = true;
//        System.out.println("计算结束");

//        System.out.printf("%.2f",(float)(ao.difficultyDegree[0]+ao.difficultyDegree[1])/totalNode);
//        System.out.printf("%.3f", (ao.difficultyDegree[0] + ao.difficultyDegree[1]) * 25.0f / 10000);
//        System.out.println("");
        
        
//        stat(odc.chinaGrid);

 
        float ttf = 0;
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (odc.chinaGrid[i][j] > -9) {
                    float f1 = effectiveTimesGrid[i][j] >= 1 ? 1 : effectiveTimesGrid[i][j];
//                    f1 = (f1 < 0.5 ? 0 : f1);
                    ttf += f1;
                }
            }
        }
        System.out.println(ttf * 25.0f / 10000);

//        System.out.println(JSON.toJSONString(ao));
//        getResult(odc, cloudDataBase, MonthXunStart[0], MonthXunStart[1], MonthXunEnd[0], MonthXunEnd[1], totalNode);
//        return odc.chinaGrid;
        
        
        
        
        return effectiveTimesGrid;
    }
    
    
    //本函数用于计算用户设置时间段之间的所有时间节点，每隔1旬获取一个时间节点，用于计算该旬的任务情况
    //例如：用户设置任务起止时间是2015-10-05 ~ 2015-11-17
    //本函数返回Time数组：2015-10-05 2015-10-11 2015-10-21 2015-11-01 2015-11-11 2015-11-17
    private Time[] getTimeNode(Time startTimeNode, Time endTimeNode) {
        ArrayList<Time> timeList=new ArrayList<Time>();//时间节点数组
        timeList.add(startTimeNode);
        Time newTimeNode = startTimeNode.clone();
        int days = newTimeNode.getBJ(Calendar.DAY_OF_MONTH);
        if (days <= 10) {
            newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 11);
        } else if (days <= 20) {
            newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 21);
        } else {
            newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 1);
            int mon = newTimeNode.getBJ(Calendar.MONTH);
            int year = newTimeNode.getBJ(Calendar.YEAR);
            if (mon >= 11) {
                mon = 0;
                year += 1;
                newTimeNode.setBJ(Calendar.MONTH, mon);
                newTimeNode.setBJ(Calendar.YEAR, year);
            } else {
                mon += 1;
                newTimeNode.setBJ(Calendar.MONTH, mon);
            }
        }

        if (newTimeNode.before(endTimeNode)) {
            timeList.add(newTimeNode);
            while (true) {
                newTimeNode = newTimeNode.clone();
                days = newTimeNode.getBJ(Calendar.DAY_OF_MONTH);
                if (days <= 10) {
                    newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 11);
                } else if (days <= 20) {
                    newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 21);
                } else {
                    newTimeNode.setBJ(Calendar.DAY_OF_MONTH, 1);
                    int mon = newTimeNode.getBJ(Calendar.MONTH);
                    int year = newTimeNode.getBJ(Calendar.YEAR);
                    if (mon >= 11) {
                        mon = 0;
                        year += 1;
                        newTimeNode.setBJ(Calendar.MONTH, mon);
                        newTimeNode.setBJ(Calendar.YEAR, year);
                    } else {
                        mon += 1;
                        newTimeNode.setBJ(Calendar.MONTH, mon);
                    }
                }
                if (newTimeNode.before(endTimeNode)) {
                    timeList.add(newTimeNode);
                } else {      
                    break;
                }
            }
        }
        timeList.add(endTimeNode);
        return (Time[])timeList.toArray(new Time[timeList.size()]);
    }
    
    private Time[] getTimeNode(Time time1, Time time2, Time time3) {
        Time[] nodeArray1 = getTimeNode(time1, time2);
        Time[] nodeArray2 = getTimeNode(time2, time3);
        int len1 = nodeArray1.length;
        int len2 = nodeArray2.length;
        Time[] nodeArray = new Time[len1 + len2 - 1];
        for (int i = 0; i < len1; i++) {
            nodeArray[i] = nodeArray1[i];
        }
        for (int i = 1; i < len2; i++) {
            nodeArray[len1 + i - 1] = nodeArray2[i];
        }
        return nodeArray;
    }
    
    private void getResult(OneDayCoverage odc, CloudDataBase cloudDataBase, int m1, int x1, int m2, int x2, int totalNode) {
        //////////////////////      统一统计云量数据     //////////////////////////////////
        float[] f = stat(odc.chinaGrid);
        cloudDataBase.UpdateCloudGrid(m1, x1, m2, x2);
        float[] cloudFreq = new float[100];
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (odc.chinaGrid[i][j] != -10) {
                    int cloud = (int) (100 * cloudDataBase.CloudGrid[i][j]);
                    cloud = cloud >= 100 ? 99 : cloud;
                    cloudFreq[cloud] += 1;
                }
            }
        }
        for (int i = 0; i < 100; i++) {
            cloudFreq[i] /= totalNode;
        }
        float t = 0;
        System.out.print("云量： ");
        for (int i = 0; i < 20; i++) {
            float tt = cloudFreq[5 * i + 0] + cloudFreq[5 * i + 1] + cloudFreq[5 * i + 2] + cloudFreq[5 * i + 3] + cloudFreq[5 * i + 4];
            System.out.print(String.format("%.1f ", tt * 100));
            t += tt;
        }
        System.out.println("");

        for (int i = 0; i < 100; i++) {
            cloudFreq[i] *= f[cloud2times[i] > 19 ? 19 : cloud2times[i]];
        }
        t = 0;
        System.out.print("相乘 ");
        for (int i = 0; i < 20; i++) {
            float tt = cloudFreq[5 * i + 0] + cloudFreq[5 * i + 1] + cloudFreq[5 * i + 2] + cloudFreq[5 * i + 3] + cloudFreq[5 * i + 4];
            System.out.print(String.format("%.1f ", tt * 100));
            t += tt;
        }
        System.out.println("");

        System.out.println(t);
    }

    public float[] stat(float[][] chinaGrid) {
        int[] coverageTimes = new int[20];
        List<Float> l = new ArrayList<Float>(1000 * 1400);
//        float f = 0;
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (chinaGrid[i][j] > -9) {
                    l.add(chinaGrid[i][j]);
//                    f += chinaGrid[i][j];
                }
            }
        }
//        System.out.println(f);
        for (float t : l) {
            t = t >= 19 ? 19 : t;
            coverageTimes[(int)Math.floor(t)] += 1;
        }
        float[] cumulationTimes = new float[20];
        int len = l.size();
        System.out.print("覆盖次数： ");
        for (int i = 0; i < 20; i++) {
            cumulationTimes[i] = (float) coverageTimes[i] / len;
            System.out.print(" " + coverageTimes[i]);
        }
//        System.out.println("累加结果 ");
//        for (int i = 18; i >= 0; i--) {
//            cumulationTimes[i] += cumulationTimes[i + 1];
//        }
//        for (int i = 0; i < 20; i++) {
//            String str=String.format("%.3f ", cumulationTimes[i]);
//            System.out.print(str);
//        }
//        System.out.println();
        return cumulationTimes;
    }
    
    //快速采样函数
    private static int Geometry2Grid(AccessInput ai, OneDayCoverage odc) {
        MultiPolygon mp = (MultiPolygon) ai.target;
        GeometryFactory gf = new GeometryFactory();
        double[] LonLat = new double[2];
        int[] GridXY = new int[2];
        for (int i = 0; i < 800; i += 20) {
            for (int j = 0; j < 1400; j += 20) {
                // 1个网格点代表0.05经纬度，以20个网格点（1经纬度）为一个步进，判断所在的正方形与地面任务区域的相交情况
                Coordinate[] coor = new Coordinate[5];
                GridXY[0] = j;
                GridXY[1] = i;
                OverlapFun.GridXY2LonLat(GridXY, LonLat);
                coor[0] = new Coordinate(LonLat[0] * Math.PI / 180.0f, LonLat[1] * Math.PI / 180.0f);
                coor[4] = new Coordinate(LonLat[0] * Math.PI / 180.0f, LonLat[1] * Math.PI / 180.0f);
                GridXY[0] = j + 20;
                GridXY[1] = i;
                OverlapFun.GridXY2LonLat(GridXY, LonLat);
                coor[1] = new Coordinate(LonLat[0] * Math.PI / 180.0f, LonLat[1] * Math.PI / 180.0f);
                GridXY[0] = j + 20;
                GridXY[1] = i + 20;
                OverlapFun.GridXY2LonLat(GridXY, LonLat);
                coor[2] = new Coordinate(LonLat[0] * Math.PI / 180.0f, LonLat[1] * Math.PI / 180.0f);
                GridXY[0] = j;
                GridXY[1] = i + 20;
                OverlapFun.GridXY2LonLat(GridXY, LonLat);
                coor[3] = new Coordinate(LonLat[0] * Math.PI / 180.0f, LonLat[1] * Math.PI / 180.0f);
                Geometry poly = gf.createPolygon(coor);
                if (poly.within(mp)) {
                    //如果正方形位于地面任务区域内部，则正方形内的点均位于地面任务区域内部
                    for (int di = 0; di < 20; di++) {
                        for (int dj = 0; dj < 20; dj++) {
                            odc.chinaGrid[i + di][j + dj] = 0;
                            odc.chinaOneDayGrid[i + di][j + dj] = 0;
                        }
                    }
                } else if (poly.intersects(mp)) {
                    //如果正方形与地面任务区域相交，则需依次判断正方形内部的点与地面任务区域的关系
                    for (int di = 0; di < 20; di++) {
                        for (int dj = 0; dj < 20; dj++) {
                            GridXY[0] = j + dj;
                            GridXY[1] = i + di;
                            OverlapFun.GridXY2LonLat(GridXY, LonLat);
                            Coordinate coor1 = new Coordinate(LonLat[0] * Math.PI / 180.0, LonLat[1] * Math.PI / 180.0);
                            Point p = gf.createPoint(coor1);
                            if (mp.contains(p)) {
                                odc.chinaGrid[i + di][j + dj] = 0;
                                odc.chinaOneDayGrid[i + di][j + dj] = 0;
                            } else {
                                odc.chinaGrid[i + di][j + dj] = -10;
                                odc.chinaOneDayGrid[i + di][j + dj] = -10;
                            }
                        }
                    }
                } else {
                    //如果正方形位于地面任务区域外部，则正方形内的点均位于地面任务区域外部
                    for (int di = 0; di < 20; di++) {
                        for (int dj = 0; dj < 20; dj++) {
                            odc.chinaGrid[i + di][j + dj] = -10;//-10代表位于地面任务区域之外
                            odc.chinaOneDayGrid[i + di][j + dj] = -10;//-10代表位于地面任务区域之外                           
                        }
                    }
                }
            }
        }
        //统计地面任务区域网格点总数
        int totalGrid = 0;
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (odc.chinaGrid[i][j] > -9) {
                    totalGrid++;
                }
            }
        }
        return totalGrid;
    }

    public byte[] Grid2png(float[][] chinaGrid, float[][] effectiveTimesGrid) {

        int width = 1400;
        int height = 800;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = image.createGraphics();

        Color color1 = new Color(0, 255, 255, 255);
        Color color2 = new Color(255, 255, 0, 255);
        Color color3 = new Color(255, 128, 0, 255);
        Color color4 = new Color(255, 0, 0, 255);

        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (chinaGrid[i][j] != -10) {
                    if (effectiveTimesGrid[i][j] < 0.5f) {
                        g2d.setColor(color4);
                    } else if (effectiveTimesGrid[i][j] < 1.0f) {
                        g2d.setColor(color3);
                    } else if (effectiveTimesGrid[i][j] < 1.5f) {
                        g2d.setColor(color2);
                    } else {
                        g2d.setColor(color1);
                    }
                    g2d.fillRect(j, i, 1, 1);
                }
            }
        }
        byte[] bt = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bout);
            bt = bout.toByteArray();
            ByteArrayInputStream bin = new ByteArrayInputStream(bt);
            image = ImageIO.read(bin);
//            ImageIO.write(image, "png", new File("E:\\桌面\\a.png"));
//            System.out.println("");
        } catch (Exception ex) {
            System.out.println("生成PNG图片失败");
        }
        g2d.dispose();
        return bt;
        
/*
        int width = 700;
        int height = 400;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = image.createGraphics();

        Color color1 = new Color(0, 255, 0, 128);
        Color color2 = new Color(255, 255, 0, 128);
        Color color3 = new Color(0, 0, 255, 128);
        Color color4 = new Color(255, 0, 0, 128);

        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (i % 2 == 0 && j % 2 == 0 && chinaGrid[i][j] != -10) {
                    if (effectiveTimesGrid[i][j] < 0.5f) {
                        g2d.setColor(color4);
                    } else if (effectiveTimesGrid[i][j] < 1.0f) {
                        g2d.setColor(color3);
                    } else if (effectiveTimesGrid[i][j] < 1.5f) {
                        g2d.setColor(color2);
                    } else {
                        g2d.setColor(color1);
                    }
                    g2d.fillRect(j / 2, i / 2, 2, 2);
                }
            }
        }
        byte[] bt = null;
        try {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            ImageIO.write(image, "png", bout);
            bt = bout.toByteArray();
            ByteArrayInputStream bin = new ByteArrayInputStream(bt);
            image = ImageIO.read(bin);
//            ImageIO.write(image, "png", new File("E:\\桌面\\b.png"));
//            System.out.println("");
        } catch (Exception ex) {
            System.out.println("生成PNG图片失败");
        }
        g2d.dispose();
        return bt;
        */
    }
}

//class XYindex implements Comparable<XYindex> {
//    int x;
//    int y;
//    static int dataGrid[][] = null;
//    public XYindex(int x, int y) {
//        this.x = x;
//        this.y = y;
//    }
//    @Override
//    public int compareTo(XYindex xy) {
//        if (dataGrid[this.x][this.y] > dataGrid[xy.x][xy.y]) {
//            return 1;
//        } else if (dataGrid[this.x][this.y] == dataGrid[xy.x][xy.y]) {
//            return 0;
//        } else {
//            return -1;
//        }
//    }
//}











