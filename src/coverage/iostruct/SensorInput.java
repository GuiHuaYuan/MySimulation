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
    public int senType;             //#0-��ѧ 1-�״�
    public double fov;              //# �Ƕȣ�
    public double swath;            //#
    public float maxAreaPerDay;     //��������ÿ�������������ƽ������
//    public double sideStep;
    public double leftAngle;         //# ��ֵ +
    public double rightAngle;        //# ��ֵ -
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
