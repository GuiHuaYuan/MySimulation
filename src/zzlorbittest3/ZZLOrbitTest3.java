/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zzlorbittest3;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


import coverage.*;
import coverage.iostruct.*;
import coverage.util.*;


/**
 *
 * @author ZZL
 */
public class ZZLOrbitTest3 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        Date time1 = new Date();
        // TODO code application logic here
        int i, j;

        ////////////////////////////////////////////////////        
        
        HashMap<String, String> tleMap = ProcessResource.ReadTle("src\\resource\\tle.txt");
        String startTimeStr, endTimeStr;
        //epochTime:2016-10-19
        startTimeStr = "2014-08-01 00:00:00.000";
        endTimeStr = "2014-11-30 00:00:00.000";

        String[] satelliteXmlList;
        
        satelliteXmlList = new String[]{"gf2_2.xml"};//15-4
        satelliteXmlList = new String[]{"pleiades1a_1.xml", "pleiades1b_1.xml"};//15-5
        satelliteXmlList = new String[]{"gf1_2.xml", "zy02c_2.xml"};//15-6
        satelliteXmlList = new String[]{"gf1_3.xml", "zy02c_3.xml"};//15-7
        satelliteXmlList = new String[]{"sj9a_2.xml"};//15-9
        satelliteXmlList = new String[]{"zy3_3.xml", "sj9a_3.xml"};//15-10
        satelliteXmlList = new String[]{"zy3_4.xml", "sj9a_4.xml"};//15-11

        satelliteXmlList = new String[]{"pleiades1a_1.xml", "pleiades1b_1.xml"};//14-2
        satelliteXmlList = new String[]{"spot6_2.xml"};//14-4
        satelliteXmlList = new String[]{"zy3_3.xml", "sj9a_3.xml"};//14-6
        satelliteXmlList = new String[]{"zy02c_2.xml"};//14-7

        satelliteXmlList = new String[]{"quickbird2_1.xml", "geoeye1_1.xml", "ikonos2_1.xml"};//13-1
        satelliteXmlList = new String[]{"spot6_2.xml"};//13-4
        satelliteXmlList = new String[]{"sj9a_2.xml"};//13-8
        satelliteXmlList = new String[]{"zy02c_2.xml"};//13-9
        satelliteXmlList = new String[]{"zy02c_3.xml"};//13-10
        satelliteXmlList = new String[]{"zy02c_4.xml"};//13-11
        satelliteXmlList = new String[]{"zy3_3.xml"};//13-13
        satelliteXmlList = new String[]{"zy3_4.xml"};//13-14

        String[][] satelliteXMLListArray = new String[][]{
            new String[]{"geoeye1.xml","worldview2.xml","worldview3.xml","worldview4.xml"},
            new String[]{"pleiades1a.xml", "pleiades1b.xml"},
            new String[]{"gf2_2.xml"},
            new String[]{"pleiades1a_1.xml", "pleiades1b_1.xml"},
            new String[]{"gf1_2.xml", "zy02c_2.xml"},
            new String[]{"gf1_3.xml", "zy02c_3.xml"},
            new String[]{"sj9a_2.xml"},
            new String[]{"zy3_3.xml", "sj9a_3.xml"},
            new String[]{"zy3_4.xml", "sj9a_4.xml"},
            new String[]{"pleiades1a_1.xml", "pleiades1b_1.xml"},
            new String[]{"spot6_2.xml"},
            new String[]{"zy3_3.xml", "sj9a_3.xml"},
            new String[]{"zy02c_2.xml"},
            new String[]{"quickbird2_1.xml", "geoeye1_1.xml", "ikonos2_1.xml"},
            new String[]{"spot6_2.xml"},
            new String[]{"sj9a_2.xml"},
            new String[]{"zy02c_2.xml"},
            new String[]{"zy02c_3.xml"},
            new String[]{"zy02c_4.xml"},
            new String[]{"zy3_3.xml"},
            new String[]{"zy3_4.xml"}
        };
        String[] gridFileNameArray = new String[]{"2015_p4", "2015_p5", "2015_p6", "2015_p7", "2015_p9", "2015_p10", "2015_p11", "2014_p2", "2014_p4", "2014_p6", "2014_p7", "2013_p1", "2013_p4", "2013_p8", "2013_p9", "2013_p10", "2013_p11", "2013_p13", "2013_p14"};

        for (int gi = 0; gi < gridFileNameArray.length; gi++) {
//            gi = 4;
            satelliteXmlList = satelliteXMLListArray[gi];
            SatelliteInput[] sliList = new SatelliteInput[satelliteXmlList.length];
            for (int satelliteXmlListIndex = 0; satelliteXmlListIndex < satelliteXmlList.length; satelliteXmlListIndex++) {
                String satelliteXml = satelliteXmlList[satelliteXmlListIndex];
                SatelliteInput sli = ProcessResource.ReadSatellite("src\\resource\\satelliteServer\\" + satelliteXml,tleMap);
                sliList[satelliteXmlListIndex] = sli;
            }

            AccessInput ai;
            ai = new AccessInput();
            ai.targetName=gridFileNameArray[gi];
            ai.satelliteInput = sliList;
            ai.startTime = new String(startTimeStr);
            ai.endTime = new String(endTimeStr);
            Geometry lr = null;// = OverlapFun.shape2LinearRingArray("file\\china4.shp");
            ai.target = lr;

            Access acs = new Access();
            float[][] c = acs.calGridCoverage(ai,new AccessOutput());
            
            GridGraphic gg = new GridGraphic();
            gg.SetGrid(c);
            gg.show();
            break;
        }

    }
    
}
