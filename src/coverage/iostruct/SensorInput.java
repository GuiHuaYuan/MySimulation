/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage.iostruct;

import java.io.Serializable;


/**
 *
 * @author ZZL
 */
public class SensorInput implements Serializable{
    
    public String senName;          //#
    public int senType;             //#0-光学 1-雷达
    public double fov;              //# 角度！
    public double swath;            //#
    public float maxAreaPerDay;     //传感器的每天最大成像面积：平方公里
//    public double sideStep;
    public double leftAngle;         //# 正值 +
    public double rightAngle;        //# 负值 -
    public double frontAngle;        //# +
    public double backAngle;         //# -
    public SensorModeInput[] sensorMode;
    
    public SensorInput clone()
    {
        SensorInput ssi=new SensorInput();
        ssi.senName=this.senName;
        ssi.senType=this.senType;
        ssi.fov=this.fov;
        ssi.swath=this.swath;
        ssi.maxAreaPerDay=this.maxAreaPerDay;
//        ssi.sideStep=this.sideStep;
        ssi.leftAngle=this.leftAngle;
        ssi.rightAngle=this.rightAngle;
        ssi.frontAngle=this.frontAngle;
        ssi.backAngle=this.backAngle;
        ssi.sensorMode=null;
        return ssi;
    }
    
}
