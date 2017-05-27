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
public class SensorModeInput implements Serializable {

    public String Mode;
    public double resolution;

    public double maxIncidence;
    public double minIncidence;

    public String SatelliteSensorModeName;//卫星-传感器-模式标识符，用于根据用户要求过滤传感器、模式，其值在ReadSatellite函数中生成，不要在程序运行过程中更改该变量的值

    public SensorModeInput clone() {
        SensorModeInput smi = new SensorModeInput();
        smi.Mode = this.Mode;
        smi.resolution = this.resolution;
        smi.maxIncidence = this.maxIncidence;
        smi.minIncidence = this.minIncidence;
        smi.SatelliteSensorModeName = this.SatelliteSensorModeName;
        return smi;
    }

}
