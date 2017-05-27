/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulationpanel;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;
//import simulationpanel.iostruct.*;

import coverage.iostruct.*;

/**
 *
 * @author ZZL
 */
public class SatelliteManagerClass {
    
    public SatelliteInput[] allSatelliteArray=new SatelliteInput[0];//���������б�   ��Ϊ����ԭʼxml���ݣ�������ֻ�ɶ�ȡ����Ҫ����������Ԫ�ص�����
    public SatelliteInput[] currentSelectedSatelliteArray=new SatelliteInput[0];//��ѡ��������б�
    public DefaultTableModel selectedSensorTableModel;//��ʾ��ѡ������ʹ�õ�MODEL
    
    public SatelliteManagerClass() {
        readAllSatellite();
    }
    
    private void readAllSatellite()
    {
        String satFilePath = "src\\resource\\satellite";
        String tlePath = "src\\resource\\tle.txt";
        HashMap<String, String> id2tleMap=ProcessResource.ReadTle(tlePath);
        File satFile = new File(satFilePath);
        ArrayList<SatelliteInput> allSliList = new ArrayList<SatelliteInput>();
        for (File satXml : satFile.listFiles()) {
            String satXmlName = satXml.getPath();
            if (satXmlName.endsWith(".xml")) {
                //System.out.println(satXmlName);
                allSliList.add(ProcessResource.ReadSatellite(satXmlName,id2tleMap));
            }
        }
        allSatelliteArray = (SatelliteInput[]) allSliList.toArray(new SatelliteInput[allSliList.size()]);
    }
    
    
    public void refreshModel() {
        for (int i = selectedSensorTableModel.getRowCount() - 1; i >= 0; i--) {
            selectedSensorTableModel.removeRow(i);
        }
        if (currentSelectedSatelliteArray != null) {
            for (SatelliteInput sli : currentSelectedSatelliteArray) {
                for (SensorInput ssi : sli.sensorInput) {
                    for (SensorModeInput smi : ssi.sensorMode) {
                        selectedSensorTableModel.addRow(new String[]{sli.satName, ssi.senName, smi.Mode, Double.toString(smi.resolution)});
                    }
                }
            }
        }
    }

    
}
