/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage.util;

import com.vividsolutions.jts.geom.*;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import java.io.File;
import java.util.*;
/**
 *
 * @author ZZL
 */
public class OverlapFun {

    /////////////////////////       网格点            ////////////////////////////////////
    
    public static void LonLat2GridXY(double[] LonLat, int[] GridXY) {
        GridXY[0] = (int) ((LonLat[0] - 70) * 20);
        GridXY[1] = (int) ((55 - LonLat[1]) * 20);
        //单位：角度
    }

    public static void GridXY2LonLat(int[] GridXY, double[] LonLat) {

        LonLat[0] = GridXY[0] / 20.0 + 70;//经度
        LonLat[1] = 55 - GridXY[1] / 20.0;
        //单位：角度
    }

    
    //本函数的作用是计算： 纬度lat所在的直线 与 点(x1,y1)和点(x2,y2)所确定直线 的交点的经度
    public static double getCrossPoint(double x1, double y1, double x2, double y2, double lat) {
        double k;
        k = (y2 - y1) / (x2 - x1);
        return x1 + (lat - y1) / k;
    }

    public static LinearRing[] shape2LinearRingArray(String path) {
        File file = new File(path);
        Shapefile sf = new Shapefile(file);
        if (!sf.getShapeType().equals("gov.nasa.worldwind.formats.shapefile.Shapefile.ShapePolygon")) {
            System.out.println("FALSE");
        }
        java.util.List<LinearRing> LinearRingList1 = new java.util.ArrayList<LinearRing>();
        while (sf.hasNext()) {
            ShapefileRecord sr = sf.nextRecord();

            int recordPartsTotal = sr.getNumberOfParts();
            int i, j;
            for (i = 0; i < recordPartsTotal; i++) {
                Coordinate[] coors = new Coordinate[sr.getNumberOfPoints(i)];
                for (j = 0; j < sr.getNumberOfPoints(i); j++) {
                    coors[j] = new Coordinate(sr.getPointBuffer(i).getLocation(j).getLongitude().degrees, sr.getPointBuffer(i).getLocation(j).getLatitude().degrees);
                }
                LinearRing pol = new GeometryFactory().createLinearRing(coors);
                LinearRingList1.add(pol);
            }
        }
        return (LinearRing[]) LinearRingList1.toArray(new LinearRing[LinearRingList1.size()]);
    }
}
