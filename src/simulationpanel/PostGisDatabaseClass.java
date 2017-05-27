/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulationpanel;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 *
 * @author ZZL
 */
public class PostGisDatabaseClass {

    public PostGisDatabaseClass() {

    }

    public static void getCode2NameMap(TreeMap<Integer, String> shengMap, TreeMap<Integer, String> shiMap, TreeMap<Integer, String> xianMap) {

        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            ResultSet rst;
            sql = "select * from myshp_sheng";
            rst = stmt.executeQuery(sql);
            while (rst.next()) {
                shengMap.put(rst.getInt("code"), rst.getString("name"));
            }
            sql = "select * from myshp_shi";
            rst = stmt.executeQuery(sql);
            while (rst.next()) {
                shiMap.put(rst.getInt("code"), rst.getString("name"));
            }
            sql = "select * from myshp_xian";
            rst = stmt.executeQuery(sql);
            while (rst.next()) {
                xianMap.put(rst.getInt("code"), rst.getString("name"));
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<gov.nasa.worldwind.geom.LatLon[]> getLonLatList(int regionCode) {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
//            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            String codeString = Integer.toString(regionCode);
            switch (codeString.length()) {
                case 1:
                    sql = "select * from myshp_guo";
                    break;
                case 2:
                    sql = "select * from myshp_sheng where code=" + codeString;
                    break;
                case 4:
                    sql = "select * from myshp_shi where code=" + codeString;
                    break;
                case 6:
                    sql = "select * from myshp_xian where code=" + codeString;
                    break;
                default:
                    System.out.println("cannot find regionCode!");
                    return null;
            }
            ResultSet rst = stmt.executeQuery(sql);
            ArrayList<gov.nasa.worldwind.geom.LatLon[]> arrayList = new ArrayList<gov.nasa.worldwind.geom.LatLon[]>();
            while (rst.next()) {
                org.postgis.PGgeometry geom = (org.postgis.PGgeometry) rst.getObject("geom");
                org.postgis.Geometry g = geom.getGeometry();
                if (g.getType() == org.postgis.Geometry.POLYGON) {
//                    System.out.println("type=POLYGON");
                    arrayList.add(PGPolygon2LonLatList((org.postgis.Polygon) g));
                } else if (g.getType() == org.postgis.Geometry.MULTIPOLYGON) {
//                    System.out.println("type=MULTIPOLYGON");
                    org.postgis.MultiPolygon mpol = (org.postgis.MultiPolygon) g;
                    int polygonCount = mpol.numPolygons();
                    for (int polygonCountIndex = 0; polygonCountIndex < polygonCount; polygonCountIndex++) {
                        org.postgis.Polygon pol = mpol.getPolygon(polygonCountIndex);
                        arrayList.add(PGPolygon2LonLatList(pol));
                    }
                } else {
                    System.out.println("Wrong PostGis Format!");
                }
                break;
            }
            stmt.close();
            c.close();
            return arrayList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static gov.nasa.worldwind.geom.LatLon[] PGPolygon2LonLatList(org.postgis.Polygon polygon) {

        ArrayList<gov.nasa.worldwind.geom.LatLon> latlonList = new ArrayList<gov.nasa.worldwind.geom.LatLon>();
        org.postgis.LinearRing linearRing = polygon.getRing(0);
        org.postgis.Point[] linearRingPointArray = linearRing.getPoints();
        for (org.postgis.Point point : linearRingPointArray) {
            latlonList.add(gov.nasa.worldwind.geom.LatLon.fromDegrees(point.y, point.x));
        }
        return (gov.nasa.worldwind.geom.LatLon[]) latlonList.toArray(new gov.nasa.worldwind.geom.LatLon[latlonList.size()]);
    }

}
