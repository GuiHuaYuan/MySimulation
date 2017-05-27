/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coverage.iostruct;

import java.io.*;
import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

/**
 *
 * @author ZZL
 */
public class ProcessResource {

    public static SatelliteInput ReadSatellite(String xmlFileName,HashMap<String, String> id2tleMap) {
        try {
            //xmlFileName = "resource\\coreh2o.xml";
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlFileName));
            Element element, childElement;
            NodeList nodeList, childNodeList;
            Node node;
            String str;
            SatelliteInput sli = new SatelliteInput();
            //中文名字
            element = (Element) doc.getElementsByTagName("satellite").item(0);
            sli.ChineseSatName = element.getAttribute("cn");
            sli.satName = element.getAttribute("name");
            //回归周期 
            node = doc.getElementsByTagName("repeatCycle").item(0).getFirstChild();
            if (node == null) {
                sli.returnPeriod = 0;
            } else {
                str = node.getNodeValue();
                sli.returnPeriod = (int) Double.parseDouble(str);
            }
            //id
            node = doc.getElementsByTagName("id").item(0).getFirstChild();
            if (node == null) {
                sli.id = null;
                sli.satElement = null;
            } else {
                sli.id = node.getNodeValue().trim();
                if (id2tleMap.containsKey(sli.id)) {
                    sli.satElement = id2tleMap.get(sli.id);
                } else {
                    sli.satElement = null;
                }
            }
            
            //整星左右侧摆
            element = (Element) doc.getElementsByTagName("sideAbility").item(0);
            str = element.getAttribute("sideMin");
            sli.rightAngle = Double.parseDouble(str);
            str = element.getAttribute("sideMax");
            sli.leftAngle = Double.parseDouble(str);

            //整星前后侧摆
            element = (Element) doc.getElementsByTagName("orbitAbility").item(0);
            str = element.getAttribute("orbitMin");
            sli.backAngle = Double.parseDouble(str);
            str = element.getAttribute("orbitMax");
            sli.frontAngle = Double.parseDouble(str);

            //传感器
            nodeList = doc.getElementsByTagName("sensor");
            SensorInput[] sensorInputList = new SensorInput[nodeList.getLength()];
            SensorInput ssi;
            for (int i = 0; i < nodeList.getLength(); i++) {
                ssi = new SensorInput();
                element = (Element) nodeList.item(i);
                //传感器名字
                ssi.senName = element.getAttribute("name");
                //传感器类型 0-光学 1-雷达
                str = element.getAttribute("type");
                ssi.senType = Integer.parseInt(str);

                float imagingAbilityValue = 0;
                float imagingAbilityUnit = 0;
                //读取传感器最大成像能力
                if (((Element) element.getElementsByTagName("imagingAbility").item(0)).getElementsByTagName("value").item(0).getFirstChild() != null) {
                    str = ((Element) element.getElementsByTagName("imagingAbility").item(0)).getElementsByTagName("value").item(0).getFirstChild().getNodeValue();
                    imagingAbilityValue = Float.parseFloat(str);
                }
                if (((Element) element.getElementsByTagName("imagingAbility").item(0)).getElementsByTagName("unit").item(0).getFirstChild() != null) {
                    str = ((Element) element.getElementsByTagName("imagingAbility").item(0)).getElementsByTagName("unit").item(0).getFirstChild().getNodeValue();
                    imagingAbilityUnit = Float.parseFloat(str);
                }
                ssi.maxAreaPerDay = imagingAbilityValue * imagingAbilityUnit;

                if (ssi.senType == 0) {
                    //光学视场角
                    str = element.getElementsByTagName("fov").item(0).getFirstChild().getNodeValue();
                    ssi.fov = Double.parseDouble(str);
                    //光学幅宽
                    str = element.getElementsByTagName("swath").item(0).getFirstChild().getNodeValue();
                    ssi.swath = Double.parseDouble(str);

                    //光学传感器左右侧摆
                    childElement = (Element) element.getElementsByTagName("sideAbility").item(0);
                    str = childElement.getAttribute("sideMin");
                    ssi.rightAngle = Double.parseDouble(str);
                    str = childElement.getAttribute("sideMax");
                    ssi.leftAngle = Double.parseDouble(str);

                    //光学传感器前后侧摆
                    childElement = (Element) element.getElementsByTagName("orbitAbility").item(0);
                    str = childElement.getAttribute("orbitMin");
                    ssi.backAngle = Double.parseDouble(str);
                    str = childElement.getAttribute("orbitMax");
                    ssi.frontAngle = Double.parseDouble(str);

                    childNodeList = element.getElementsByTagName("mode");
                    SensorModeInput[] sensorModeInputList = new SensorModeInput[childNodeList.getLength()];
                    SensorModeInput smi;
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        smi = new SensorModeInput();
                        childElement = (Element) childNodeList.item(j);
                        //光学模式
                        smi.Mode = childElement.getAttribute("name").trim();
                        str = childElement.getElementsByTagName("resolution").item(0).getFirstChild().getNodeValue();
                        //光学分辨率
                        smi.resolution = Double.parseDouble(str);
                        smi.SatelliteSensorModeName = sli.satName + '_' + ssi.senName + '_' + smi.Mode + '_' + Double.toString(smi.resolution);
                        sensorModeInputList[j] = smi;
                    }
                    ssi.sensorMode = sensorModeInputList;
                } else {

                    /////////////////        雷达传感器            //////////////////////
                    childNodeList = element.getElementsByTagName("mode");
                    SensorModeInput[] sensorModeInputList = new SensorModeInput[childNodeList.getLength()];
                    SensorModeInput smi;
                    for (int j = 0; j < childNodeList.getLength(); j++) {
                        smi = new SensorModeInput();
                        childElement = (Element) childNodeList.item(j);
                        //雷达模式
                        smi.Mode = childElement.getAttribute("name").trim();
                        str = childElement.getElementsByTagName("resolution").item(0).getFirstChild().getNodeValue();
                        //雷达分辨率
                        smi.resolution = Double.parseDouble(str);

                        ArrayList<Double> maxList = new ArrayList<Double>();
                        ArrayList<Double> minList = new ArrayList<Double>();
                        int k;
                        Element bandElement;
                        NodeList bandNodeList;
                        bandNodeList = childElement.getElementsByTagName("incidenceMin");
                        for (k = 0; k < bandNodeList.getLength(); k++) {
                            bandElement = (Element) bandNodeList.item(k);
                            str = bandElement.getFirstChild().getNodeValue();
                            minList.add(Double.parseDouble(str));
                        }
                        Collections.sort(minList);
                        smi.minIncidence = minList.get(0);
                        bandNodeList = childElement.getElementsByTagName("incidenceMax");
                        for (k = 0; k < bandNodeList.getLength(); k++) {
                            bandElement = (Element) bandNodeList.item(k);
                            str = bandElement.getFirstChild().getNodeValue();
                            maxList.add(Double.parseDouble(str));
                        }
                        Collections.sort(maxList);
                        smi.maxIncidence = maxList.get(minList.size() - 1);
                        smi.SatelliteSensorModeName = sli.satName + '_' + ssi.senName + '_' + smi.Mode + '_' + Double.toString(smi.resolution);
                        sensorModeInputList[j] = smi;
                    }
                    ssi.sensorMode = sensorModeInputList;
                }
                sensorInputList[i] = ssi;
            }
            sli.sensorInput = sensorInputList;
            return sli;
        } catch (Exception ex) {
            System.out.println("read " + xmlFileName + " Wrong-->" + ex.toString());
            ex.printStackTrace();
       
            return null;
        }
    }

    private static double class_1_resolution=1.5;
    private static double class_2_PAN_resolution=2.6;
//    private static double class_2_MS_resolution=2.5;
    private static double class_3_PAN_resolution=2.6;
    private static double class_3_MS_resolution=7;
    private static double class_4_PAN_resolution=7;
 
    public static void SensorFileterByClass(AccessInput ai, int classIndex) {
        java.util.ArrayList<String> selectedModeNameList = new java.util.ArrayList<String>();
        for (SatelliteInput sli : ai.satelliteInput) {
            for (SensorInput ssi : sli.sensorInput) {
                for (SensorModeInput smi : ssi.sensorMode) {
                    switch (classIndex) {
                        case 1:
                            if (("PAN".equals(smi.Mode.trim()) || "MS".equals(smi.Mode.trim())) && smi.resolution <= class_1_resolution) {
                                selectedModeNameList.add(smi.SatelliteSensorModeName);
                            }
                            break;
                        case 2:
                            if ("PAN".equals(smi.Mode.trim()) && smi.resolution <= class_2_PAN_resolution /*|| "MS".equals(smi.Mode.trim()) && smi.resolution < class_2_MS_resolution*/) {
                                selectedModeNameList.add(smi.SatelliteSensorModeName);
                            }
                            break;   
                        case 3:
                            if ("PAN".equals(smi.Mode.trim()) && smi.resolution <= class_3_PAN_resolution || "MS".equals(smi.Mode.trim()) && smi.resolution <= class_3_MS_resolution) {
                                selectedModeNameList.add(smi.SatelliteSensorModeName);
                            }
                            break;
                        case 4:
                            if ("PAN".equals(smi.Mode.trim()) && smi.resolution <= class_4_PAN_resolution) {
                                selectedModeNameList.add(smi.SatelliteSensorModeName);
                            }
                            break;          
                        default:
                            System.out.println("ClassIndex Wrong!");
                            break;
                    }
                }
            }
        }
        SensorFileterByName(ai, selectedModeNameList);
        /*
        if (classIndex == 2) {
            ArrayList<SatelliteInput> sliList = new ArrayList<SatelliteInput>();
            for (SatelliteInput sli : ai.satelliteInput) {
                java.util.ArrayList<String> PAN_MS_NameList = new java.util.ArrayList<String>();
                for (SensorInput ssi : sli.sensorInput) {
                    for (SensorModeInput smi : ssi.sensorMode) {
                        PAN_MS_NameList.add(smi.Mode.trim());
                    }
                }
                boolean PAN_exist = false, MS_exist = false;
                for (String str : PAN_MS_NameList) {
                    if (str.equals("PAN")) {
                        PAN_exist = true;
                        break;
                    }
                }
                for (String str : PAN_MS_NameList) {
                    if (str.equals("MS")) {
                        MS_exist = true;
                        break;
                    }
                }
                if (PAN_exist == true && MS_exist == true) {
                    sliList.add(sli);
                }
            }
            ai.satelliteInput=(SatelliteInput[])sliList.toArray(new SatelliteInput[sliList.size()]);
        }
                */

    }

    public static void SensorFileterByResolution(AccessInput ai, double res) {
        java.util.ArrayList<String> selectedModeNameList = new java.util.ArrayList<String>();
        for (SatelliteInput sli : ai.satelliteInput) {
            for (SensorInput ssi : sli.sensorInput) {
                for (SensorModeInput smi : ssi.sensorMode) {
                    if (smi.resolution <= res) {
                        selectedModeNameList.add(smi.SatelliteSensorModeName);
                    }
                }
            }
        }
        SensorFileterByName(ai, selectedModeNameList);
    }

    public static void SensorFileterByName(AccessInput ai, java.util.ArrayList<String> selectedModeNameList) {
        ArrayList<SensorModeInput> smiList = new ArrayList<SensorModeInput>();
        ArrayList<SensorInput> ssiList = new ArrayList<SensorInput>();
        ArrayList<SatelliteInput> sliList = new ArrayList<SatelliteInput>();
        for (SatelliteInput sli : ai.satelliteInput) {
            ssiList.clear();
            for (SensorInput ssi : sli.sensorInput) {
                smiList.clear();
                for (SensorModeInput smi : ssi.sensorMode) {
                    for (String selectedModeName : selectedModeNameList) {
                        if (smi.SatelliteSensorModeName.equals(selectedModeName)) {
                            smiList.add(smi);
                            break;
                        }
                    }
                }
                if (!smiList.isEmpty()) {
                    ssi.sensorMode = (SensorModeInput[]) smiList.toArray(new SensorModeInput[smiList.size()]);
                    ssiList.add(ssi);
                }
            }
            if (!ssiList.isEmpty()) {
                sli.sensorInput = (SensorInput[]) ssiList.toArray(new SensorInput[ssiList.size()]);
                sliList.add(sli);
            }
        }
        if (!sliList.isEmpty()) {
            ai.satelliteInput = (SatelliteInput[]) sliList.toArray(new SatelliteInput[sliList.size()]);
        } else {
            ai.satelliteInput = new SatelliteInput[0];
        }


    }

    public static HashMap<String, String> ReadTle(String tlePath) {
//        String tlePath = "resource\\tle.txt";
        List<String> strList = new ArrayList<String>();
        try {
            FileReader reader = new FileReader(tlePath);
            BufferedReader br = new BufferedReader(reader);
            String str;
            while ((str = br.readLine()) != null) {
                if (str.charAt(0) == '1' || str.charAt(0) == '2') {
                    strList.add(str);
                }
            }
            br.close();
            reader.close();
        } catch (Exception ex) {
            System.out.println("read TEL.txt Wrong!--> " + ex);
            return null;
        }
        HashMap<String, String> idTleMap = new HashMap<String, String>();
        String id, tle, tle1, tle2;
        for (int i = 0; i < strList.size(); i += 2) {
            tle1 = strList.get(i);
            tle2 = strList.get(i + 1);
            id = tle1.substring(2, 7);
            tle = tle1 + ";" + tle2;
            idTleMap.put(id, tle);
        }
        return idTleMap;
    }
}
