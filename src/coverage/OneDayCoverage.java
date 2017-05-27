/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage;

import coverage.iostruct.AccessInput;
import coverage.iostruct.PositionVelocityOutput;
import coverage.iostruct.SatelliteInput;
import coverage.iostruct.SensorInput;
import coverage.util.AstroConst;
import coverage.util.CoorTrans;
import coverage.util.MathUtils;
import coverage.util.OverlapFun;
import coverage.util.SGP4unit;
import coverage.util.Time;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/**
 *
 * @author ZZL
 */
public class OneDayCoverage {
    
    public float[][] chinaGrid=new float[800][1400];//已经达到的覆盖情况，程序将根据该矩阵，选择侧摆角，矩阵里面的元素代表覆盖次数
    public int[][] chinaOneDayGrid = new int[800][1400];//当前天的覆盖结果，矩阵里面的元素代表覆盖次数
    public int[][] dixingGrid = new int[800][1400];
    public int[][] snowGrid = new int[800][1400];

    public boolean isDixingConsidered = false;
    public boolean isSnowConsidered = false;

    private int startCalDownLat = 16;
    private int startCalUpLat = 54;
    public double maxMountainAngle = 20;
    public double maxPlainAngle = 30;
    
    public OneDayCoverage() {
    }

    public void UpdateDays(int day) {

    }

    public void calOneDayCoverage(AccessInput ai) {

        int satelliteIndex=0;
        for (satelliteIndex = 0; ai.satelliteInput != null && satelliteIndex < ai.satelliteInput.length; satelliteIndex++) {
            SatelliteInput sli = ai.satelliteInput[satelliteIndex];
            PositionVelocityOutput[] pvo = calPosition(sli, new Time(ai.startTime), new Time(ai.endTime));
            if (sli.sensorInput[0].senType == 0) {
                //光学星计算
                for (int i = 1; i < pvo.length; i++) {
                    //降轨情况
                    if (pvo[i].Lat < startCalUpLat && startCalUpLat <= pvo[i - 1].Lat && 50 < pvo[i].Lon && pvo[i].Lon < 160) {//判断过境
                        if (!(10 <= pvo[i].Time.get(Calendar.HOUR_OF_DAY) && pvo[i].Time.get(Calendar.HOUR_OF_DAY) <= 22)) {//判断光照条件
                            int upIndex = i - 1;//记录过境瞬间对应的时间节点，遍历所有传感器时，以该时间节点为起始节点进行计算
                            for (int sensorIndex = 0; sli.sensorInput != null && sensorIndex < sli.sensorInput.length; sensorIndex++) {
                                SensorInput ssi = sli.sensorInput[sensorIndex];
                                i = upIndex;     
//                                System.out.println("降轨");
                                if (ssi.leftAngle == 0 && ssi.rightAngle == 0) {
                                    calSensorCoverage(pvo, i, ssi.fov, 0, true, false);
                                } else {
                                    double swingAngle = getBestSwingAngleForOptical(pvo, ssi, i, true);
//                                    System.out.println(swingAngle);
                                    calSensorCoverage(pvo, i, ssi.fov, swingAngle, true, false);
                                }
                                i += 12;
                            }
                            continue;
                        }
                    }
                    //升轨情况
                    if (pvo[i].Lat > startCalDownLat && startCalDownLat >= pvo[i - 1].Lat && 50 < pvo[i].Lon && pvo[i].Lon < 160) {
                        if (!(10 <= pvo[i].Time.get(Calendar.HOUR_OF_DAY) && pvo[i].Time.get(Calendar.HOUR_OF_DAY) <= 22)) {
                            int upIndex = i - 1;
                            for (int sensorIndex = 0; sli.sensorInput != null && sensorIndex < sli.sensorInput.length; sensorIndex++) {
                                SensorInput ssi = sli.sensorInput[sensorIndex];
                                i = upIndex;
//                                System.out.println("升轨");
                                if (ssi.leftAngle == 0 && ssi.rightAngle == 0) {
                                    calSensorCoverage(pvo, i, ssi.fov, 0, false, false);
                                } else {
                                    double swingAngle = getBestSwingAngleForOptical(pvo, ssi, i, false);
//                                    System.out.println(swingAngle);
                                    calSensorCoverage(pvo, i, ssi.fov, swingAngle, false, false);
                                }
                                i += 12;
                            }
                            continue;
                        }
                    }
                }
                //光学星计算至此
            } else {
                //雷达星计算
                for (int i = 1; i < pvo.length; i++) {
                    //降轨情况
                    if (pvo[i].Lat < startCalUpLat && startCalUpLat <= pvo[i - 1].Lat && 50 < pvo[i].Lon && pvo[i].Lon < 160) {

                        int upIndex = i - 1;
                        for (int sensorIndex = 0; sli.sensorInput != null && sensorIndex < sli.sensorInput.length; sensorIndex++) {
                            SensorInput ssi = sli.sensorInput[sensorIndex];
                            i = upIndex;

                            double[] SwingangleFov = getBestSwingAngleForRadar(pvo, ssi, i, true);
//                            System.out.println(SwingangleFov[0]);
//                            System.out.println(SwingangleFov[1]);
                            calSensorCoverage(pvo, i, SwingangleFov[1], SwingangleFov[0], true, false);

                            i += 12;
                        }
                        continue;
                    }
                    //升轨情况
                    if (pvo[i].Lat > startCalDownLat && startCalDownLat >= pvo[i - 1].Lat && 50 < pvo[i].Lon && pvo[i].Lon < 160) {

                        int upIndex = i - 1;
                        for (int sensorIndex = 0; sli.sensorInput != null && sensorIndex < sli.sensorInput.length; sensorIndex++) {
                            SensorInput ssi = sli.sensorInput[sensorIndex];
                            i = upIndex;
                            
                            double[] SwingangleFov = getBestSwingAngleForRadar(pvo, ssi, i, false);
//                            System.out.println(SwingangleFov[0]);
//                            System.out.println(SwingangleFov[1]);
                            calSensorCoverage(pvo, i, SwingangleFov[1], SwingangleFov[0], false, false);
                            
                            i += 12;
                        }
                        continue;
                    }
                }
                //雷达星计算至此
            }
        }
    }

    private double[] getBestSwingAngleForRadar(PositionVelocityOutput[] pvo, SensorInput ssi, int i, boolean isJiangGui) {
        ArrayList<CalBestSwingAngleClass> cbsList = new ArrayList<CalBestSwingAngleClass>();
        double[] SwingangleFov=new double[2];
        for (int modeIndex = 0; ssi.sensorMode != null && modeIndex < ssi.sensorMode.length; modeIndex++) {
            double tsin = AstroConst.R_Earth * Math.sin(ssi.sensorMode[modeIndex].minIncidence * MathUtils.deg2rad) / Math.sqrt(pvo[i].x_J2000C * pvo[i].x_J2000C + pvo[i].y_J2000C * pvo[i].y_J2000C + pvo[i].z_J2000C * pvo[i].z_J2000C);
            if (tsin >= 1 || tsin < 0) {
                System.out.println("radar swing wrong!");
                continue;
            }
            double angle1 = Math.asin(tsin) * MathUtils.rad2deg;
            tsin = AstroConst.R_Earth * Math.sin(ssi.sensorMode[modeIndex].maxIncidence * MathUtils.deg2rad) / Math.sqrt(pvo[i].x_J2000C * pvo[i].x_J2000C + pvo[i].y_J2000C * pvo[i].y_J2000C + pvo[i].z_J2000C * pvo[i].z_J2000C);
            if (tsin >= 1 || tsin < 0) {
                System.out.println("radar swing wrong!");
                continue;
            }
            double angle2 = Math.asin(tsin) * MathUtils.rad2deg;
            CalBestSwingAngleClass cbs;
            cbs = calSensorCoverage(pvo, i, Math.abs(angle2 - angle1), (angle2 + angle1) / 2, isJiangGui, true);
            if (cbs != null) {
                cbsList.add(cbs);
            }
            cbs = calSensorCoverage(pvo, i, Math.abs(angle2 - angle1), -(angle2 + angle1) / 2, isJiangGui, true);
            SwingangleFov[0]=(angle2 + angle1) / 2;
            SwingangleFov[1]=Math.abs(angle2 - angle1);
            if (cbs != null) {
                cbsList.add(cbs);
            }
        }
        if (!cbsList.isEmpty()) {
            Collections.sort(cbsList);
            SwingangleFov[0] = cbsList.get(0).angle;
            SwingangleFov[1]=cbsList.get(0).fov;
        } else {
//            System.out.println("All swingAngle are inapproprate!");
        }

        return SwingangleFov;
    }

    private double getBestSwingAngleForOptical(PositionVelocityOutput[] pvo, SensorInput ssi, int i, boolean isJiangGui) {
        ArrayList<CalBestSwingAngleClass> cbsList = new ArrayList<CalBestSwingAngleClass>();
        double swingAngle;
        for (swingAngle = ssi.rightAngle; swingAngle <= ssi.leftAngle; swingAngle += 1) {
            CalBestSwingAngleClass cbs = calSensorCoverage(pvo, i, ssi.fov, swingAngle, isJiangGui, true);
            if (cbs != null) {
                cbsList.add(cbs);
            }
        }
        if (!cbsList.isEmpty()) {
            Collections.sort(cbsList);
            swingAngle = cbsList.get(0).angle;
//            if(swingAngle==32.0f)
//            {
//                System.out.println("");
//            }
        } else {
            swingAngle = 0;
//            System.out.println("All swingAngle are inapproprate!");
        }
//        System.out.println(swingAngle);
        return swingAngle;
        
    }

    private CalBestSwingAngleClass calSensorCoverage(PositionVelocityOutput[] pvo, int i, double fov, double swingAngle, boolean isJiangGui,boolean isCalSwingAngle) {
        
        //i代表星下点轨迹经过中国所在区域南北边界时刻对应的pvo索引，也就是说,pvo[i]代表该时刻对应的位置速度

        ArrayList<Float> CoverageTimesList=new ArrayList<Float>();
        for (int minute = 0; minute < 12; minute++) {
            if (i + 1 >= pvo.length) {
                break;
            }
            double[] r1 = new double[]{pvo[i].x_J2000C, pvo[i].y_J2000C, pvo[i].z_J2000C};
            double[] v1 = new double[]{pvo[i].vx_J2000C, pvo[i].vy_J2000C, pvo[i].vz_J2000C};
            double[] r2 = new double[]{pvo[i + 1].x_J2000C, pvo[i + 1].y_J2000C, pvo[i + 1].z_J2000C};
            double[] v2 = new double[]{pvo[i + 1].vx_J2000C, pvo[i + 1].vy_J2000C, pvo[i + 1].vz_J2000C};

            double[] LonLatLeft1 = CoorTrans.getScanLatLon(r1, v1, swingAngle + fov / 2, 0);     //左侧 前一分钟 扫描边界与地球交点经纬度
            double[] LonLatRight1 = CoorTrans.getScanLatLon(r1, v1, swingAngle - fov / 2, 0);    //右侧 前一分钟 扫描边界与地球交点经纬度
            double[] LonLatLeft2 = CoorTrans.getScanLatLon(r2, v2, swingAngle + fov / 2, 0);   //左侧 后一分钟 扫描边界与地球交点经纬度
            double[] LonLatRight2 = CoorTrans.getScanLatLon(r2, v2, swingAngle - fov / 2, 0);  //右侧 后一分钟 扫描边界与地球交点经纬度

            double currentLat = pvo[i].Lat;
            while (true) {//该循环的实现每一分钟卫星拍摄区域按照纬度方向进行遍历，
                if (isJiangGui) {
                    if (currentLat >= pvo[i + 1].Lat) {
                        currentLat -= 0.05;//纬度南移0.05
                    } else {
                        break;
                    }
                } else {
                    if (currentLat <= pvo[i + 1].Lat) {
                        currentLat += 0.05;
                    } else {
                        break;
                    }
                }
                if (currentLat >= startCalUpLat || currentLat <= startCalDownLat) {
                    continue;
                }

                double LeftLon = OverlapFun.getCrossPoint(LonLatLeft1[0], LonLatLeft1[1], LonLatLeft2[0], LonLatLeft2[1], currentLat);
                double RightLon = OverlapFun.getCrossPoint(LonLatRight1[0], LonLatRight1[1], LonLatRight2[0], LonLatRight2[1], currentLat);

                double[] LonLat;
                LonLat = new double[]{LeftLon, currentLat};
                int[] GridXY1 = new int[2];//X代表chinaGrid第2个索引，表列数，由经度产生；Y代表chinaGrid第1个索引，表行数，由纬度产生
                OverlapFun.LonLat2GridXY(LonLat, GridXY1);

                LonLat = new double[]{RightLon, currentLat};
                int[] GridXY2 = new int[2];
                OverlapFun.LonLat2GridXY(LonLat, GridXY2);
                int smallRowIndex, bigRowIndex;
                if (GridXY1[0] < GridXY2[0]) {
                    smallRowIndex = GridXY1[0];
                    bigRowIndex = GridXY2[0];
                } else {
                    smallRowIndex = GridXY2[0];
                    bigRowIndex = GridXY1[0];
                }

                smallRowIndex = smallRowIndex < 0 ? 0 : smallRowIndex;
                bigRowIndex = bigRowIndex > 1400 - 1 ? 1400 - 1 : bigRowIndex;

                for (int rowIndex = smallRowIndex; rowIndex <= bigRowIndex; rowIndex++) {
                    if (isCalSwingAngle == false) {
                        if (chinaOneDayGrid[GridXY1[1]][rowIndex] >-9) {
                            if (isSnowConsidered == false
                                    || (isSnowConsidered == true && snowGrid[GridXY1[1]][rowIndex] == 0)) {
                                if (isDixingConsidered == false 
                                        || (isDixingConsidered == true && dixingGrid[GridXY1[1]][rowIndex] == 1 && Math.abs(swingAngle) <= maxMountainAngle)
                                        || (isDixingConsidered == true && dixingGrid[GridXY1[1]][rowIndex] == 0 && Math.abs(swingAngle) <= maxPlainAngle)
                                        ) {
                                    chinaOneDayGrid[GridXY1[1]][rowIndex]++;
                                }
                            }
                        }
                    } else {
                        if (chinaOneDayGrid[GridXY1[1]][rowIndex]>-9) {
                            if (isSnowConsidered == false
                                    || (isSnowConsidered == true && snowGrid[GridXY1[1]][rowIndex] == 0)) {
                                if (isDixingConsidered == false && Math.abs(swingAngle) <= maxPlainAngle
                                        || (isDixingConsidered == true && dixingGrid[GridXY1[1]][rowIndex] == 1 && Math.abs(swingAngle) <= maxMountainAngle)
                                        || (isDixingConsidered == true && dixingGrid[GridXY1[1]][rowIndex] == 0 && Math.abs(swingAngle) <= maxPlainAngle)
                                        ) {
                                    CoverageTimesList.add(chinaGrid[GridXY1[1]][rowIndex]);
                                }
                            }
                        }
                    }
                }

            }
            i++;//轨道运算步进1分钟
            if (i >= pvo.length) {
                break;
            }
        }
        if (isCalSwingAngle) {
            if (!CoverageTimesList.isEmpty()) {
//                Collections.sort(CoverageTimesList);
                CalBestSwingAngleClass cbs = new CalBestSwingAngleClass();
                cbs.angle = swingAngle;
                cbs.fov=fov;
                float sum = 0;
                for (float n : CoverageTimesList) {
                    sum += n;
                }
                cbs.averageCoverageTimes = sum / CoverageTimesList.size();
//                cbs.minCoverageTimes = CoverageTimesList.get(0);
//                cbs.minCoverageTimesFreq = Collections.frequency(CoverageTimesList, cbs.minCoverageTimes);

                return cbs;
            } else {
//                System.out.println("This swingAngle is out of task region!");
                return null;
            }
        } else {
            return null;
        }

    }

    

    
    private PositionVelocityOutput[] calPosition(SatelliteInput sli,Time start,Time end) {
//        Time start = new Time(sli.startTime);
//        Time end = new Time(sli.endTime);
        Time current = start.clone();//当前计算时间
        SGP4unit.readTLE(sli.satName, sli.satElement.split(";")[0], sli.satElement.split(";")[1]);
        double[][] pvtm = CoorTrans.TEME_J2000I(SGP4unit.sgp4SatData.jdsatepoch);//获取瞬时惯性系到J2000惯性系的矩阵
        ArrayList<PositionVelocityOutput> outList = new ArrayList<PositionVelocityOutput>();
        double[] r;
        double[] v;

        while (current.before(end)) {
            double JDMod;//单位：天
            if (sli.returnPeriod > 0) {
                //利用回归周期计算
                int k;
                k = (int) Math.floor((current.getJulianDate() - SGP4unit.sgp4SatData.jdsatepoch) / sli.returnPeriod + 0.5);
                JDMod = current.getJulianDate() - k * sli.returnPeriod;//利用回归周期，将任意时刻转换为离历元时刻附近
            } else {
                JDMod = current.getJulianDate();//没有回归周期，则直接计算
            }

            r = new double[3];//瞬时惯性系位置
            v = new double[3];//瞬时惯性系速度
            double tsince = (JDMod - SGP4unit.sgp4SatData.jdsatepoch) * 24 * 60;
            SGP4unit.sgp4(tsince, r, v);
            double[] r_J2000I = MathUtils.mult(pvtm, r);    //J2000惯性系位置
            double[] v_J2000I = MathUtils.mult(pvtm, v);    //J2000惯性系速度
            double[][] rv_J2000C = CoorTrans.J2000I_J2000C(JDMod, r_J2000I, v_J2000I);//J2000惯性系转换为J2000地固系
            double[] r_J2000C = rv_J2000C[0];               //J2000地固系位置
            double[] v_J2000C = rv_J2000C[1];               //J2000地固系速度
            double[] LonLat = CoorTrans.J2000C_LonLat(r_J2000C);//星下点经纬度    // CoorTrans.getScanLatLon(r_J2000C, v_J2000C, 0, 0);

            PositionVelocityOutput ao = new PositionVelocityOutput();
            ao.x_J2000C = r_J2000C[0];//J2000地固系位置x
            ao.y_J2000C = r_J2000C[1];//J2000地固系位置y
            ao.z_J2000C = r_J2000C[2];//J2000地固系位置z
            ao.vx_J2000C = v_J2000C[0];//J2000地固系速度x
            ao.vy_J2000C = v_J2000C[1];//J2000地固系速度y
            ao.vz_J2000C = v_J2000C[2];//J2000地固系速度z
            ao.Time = current.clone();
            ao.Lon = LonLat[0];
            ao.Lat = LonLat[1];
            ao.Alt = LonLat[2];

            outList.add(ao);
            current.addSeconds(60);
        }
        PositionVelocityOutput[] ao = (PositionVelocityOutput[]) outList.toArray(new PositionVelocityOutput[outList.size()]);
        return ao;
    }

}

class CalBestSwingAngleClass implements Comparable<CalBestSwingAngleClass> {

    double angle;//光学传感器侧摆角
    double fov;
    
    int modeIndex;//雷达传感器模式索引
    boolean direction;//左视右视 true-  false- 
    
    float averageCoverageTimes;//覆盖次数平均值
    
//    float minCoverageTimes;//覆盖次数最小值
//    int minCoverageTimesFreq;//覆盖次数最小值 的出现次数
    
        @Override
    public int compareTo(CalBestSwingAngleClass at) {
        if (this.averageCoverageTimes > at.averageCoverageTimes) {
            return 1;
        } else if (this.averageCoverageTimes < at.averageCoverageTimes) {
            return -1;
        } else {
            if (Math.abs(this.angle) > Math.abs(at.angle)) {
                return 1;
            } else if (Math.abs(this.angle) < Math.abs(at.angle)) {
                return -1;
            } else {
                return 0;
            }
        }
    }

//    @Override
//    public int compareTo(CalBestSwingAngleClass at) {
//        if (this.minCoverageTimes < at.minCoverageTimes) {
//            return -1;
//        } else if (this.minCoverageTimes > at.minCoverageTimes) {
//            return 1;
//        } else {
//            if (this.minCoverageTimesFreq < at.minCoverageTimesFreq) {
//                return 1;
//            } else if (this.minCoverageTimesFreq > at.minCoverageTimesFreq) {
//                return -1;
//            } else {
//                return 0;
//            }
//
//        }
//    }
}


