/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage.iostruct;

/**
 *
 * @author ZZL
 */
public class SatelliteInput {

    public String satName;          //# = "SPOT5";
    public String ChineseSatName;   //#
    public String id;               //#

    
    public double leftAngle;        //#  正值 +
    public double rightAngle;       //#  负值 -
    public double frontAngle;       //# +
    public double backAngle;        //# -

    public int returnPeriod;        //#
    public String direction;
    public String elementType;
    public String satElement;// = "1 33320U 08041A   15060.13241973  .00001093  00000-0  16575-3 0  9996;2 33320 098.0442 122.6437 0026179 204.2544 155.7439 14.75928044348970";
    ///////////////////////////////////////////////
//    public String epochTime;
//    public double semiMajorAxis;
//    public double eccentricity;
//    public double inclination;
//    public double raan;
//    public double argumentPerigee;
//    public double meanAnomaly;
//    public double orbitNumber;
    //////////////////////////////
//    public double slope;

    public SensorInput[] sensorInput;//#

    public String toString() {
        return String.format("%s,%d,%s", satName, returnPeriod, id == null ? "00000" : id);
    }

    public SatelliteInput clone() {
        SatelliteInput sli = new SatelliteInput();

        sli.satName = this.satName;          //# = "SPOT5";
        sli.ChineseSatName = this.ChineseSatName;
        sli.id = this.id;


        sli.leftAngle = this.leftAngle;
        sli.rightAngle = this.rightAngle;
        sli.frontAngle = this.frontAngle;
        sli.backAngle = this.backAngle;

        sli.returnPeriod = this.returnPeriod;
        sli.direction = this.direction;
        sli.elementType = this.elementType;
        sli.satElement = this.satElement;

//        sli.epochTime = this.epochTime;
//        sli.semiMajorAxis = this.semiMajorAxis;
//        sli.eccentricity = this.eccentricity;
//        sli.inclination = this.inclination;
//        sli.raan = this.raan;
//        sli.argumentPerigee = this.argumentPerigee;
//        sli.meanAnomaly = this.meanAnomaly;
//        sli.orbitNumber = this.orbitNumber;
//
//        sli.slope = this.slope;

        sli.sensorInput = null;
        return sli;
    }

}
