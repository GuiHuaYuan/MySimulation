/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coverage.iostruct;


import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;

/**
 *
 * @author ZZL
 */
public class AccessInput {

    public Geometry target;
    public String targetName;
    public SatelliteInput[] satelliteInput;
    
    public String startTime;        // = "2015-03-01 10:00:00.040";
    public String endTime;          // = "2015-03-01 10:15:00.000";
    
    public boolean isWeatherConseidered;
    public boolean isSnowConseidered;
    public boolean isDixingConseidered;
    public float plainSwingAngle;
    public float mountainSwingAngle;

    public AccessInput clone() {
        AccessInput ai = new AccessInput();
        if (this.target != null) {
            ai.target = (Geometry) this.target.clone();
        }
        ai.targetName = this.targetName;

        ai.startTime = this.startTime;
        ai.endTime = this.endTime;
        
        ai.isWeatherConseidered=this.isWeatherConseidered;
        ai.isSnowConseidered=this.isSnowConseidered;
        ai.isDixingConseidered=this.isDixingConseidered;
        ai.plainSwingAngle=this.plainSwingAngle;
        ai.mountainSwingAngle=this.mountainSwingAngle;
        

        SatelliteInput sli;
        SensorInput ssi;
        SensorModeInput smi;
        int satelliteIndex, sensorIndex, modeIndex;

        ai.satelliteInput = new SatelliteInput[satelliteInput.length];
        for (satelliteIndex = 0; satelliteIndex < this.satelliteInput.length; satelliteIndex++) {
            sli = this.satelliteInput[satelliteIndex];
            ai.satelliteInput[satelliteIndex] = sli.clone();

            ai.satelliteInput[satelliteIndex].sensorInput = new SensorInput[sli.sensorInput.length];
            for (sensorIndex = 0; sensorIndex < sli.sensorInput.length; sensorIndex++) {
                ssi = sli.sensorInput[sensorIndex];
                ai.satelliteInput[satelliteIndex].sensorInput[sensorIndex] = ssi.clone();

                ai.satelliteInput[satelliteIndex].sensorInput[sensorIndex].sensorMode = new SensorModeInput[ssi.sensorMode.length];
                for (modeIndex = 0; modeIndex < ssi.sensorMode.length; modeIndex++) {
                    smi = ssi.sensorMode[modeIndex];
                    ai.satelliteInput[satelliteIndex].sensorInput[sensorIndex].sensorMode[modeIndex] = smi.clone();
                }
            }
        }
        return ai;
    }

}
