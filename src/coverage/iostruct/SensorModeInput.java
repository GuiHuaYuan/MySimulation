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

    public String SatelliteSensorModeName;//����-������-ģʽ��ʶ�������ڸ����û�Ҫ����˴�������ģʽ����ֵ��ReadSatellite���������ɣ���Ҫ�ڳ������й����и��ĸñ�����ֵ

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
