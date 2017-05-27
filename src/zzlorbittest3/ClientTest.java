/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zzlorbittest3;

import coverage.iostruct.*;
import coverage.Access;

/**
 *
 * @author ZZL
 */
public class ClientTest {

    public static void GetRes(AccessInput ai,AccessOutput ao) {
        
        
        Access acs=new Access();
        acs.calGridCoverage(ai, ao);
        
        
        
        
        /*
        Access ei = new Access();
        for (SatelliteInput sli : ai.satelliteInput) {
            if (sli.leftAngle != 0 || sli.rightAngle != 0) {
                for (SensorInput ssi : sli.sensorInput) {
                    ssi.leftAngle = sli.leftAngle;
                    ssi.rightAngle = sli.rightAngle;
                }
                sli.leftAngle = 0;
                sli.rightAngle = 0;
            }
        }

        for (SatelliteInput sli : ai.satelliteInput) {
            System.out.println(sli.satName + ":" + (sli.satElement == null ? "No Tle" : "Tle exist"));
            for (SensorInput ssi : sli.sensorInput) {
                System.out.println("    " + ssi.senName + " fov:" + ssi.fov + " " + ssi.leftAngle + " " + ssi.rightAngle + " ");
            }

        }
        System.out.println(ai.startTime);
        System.out.println(ai.endTime);
*/
    }

}
