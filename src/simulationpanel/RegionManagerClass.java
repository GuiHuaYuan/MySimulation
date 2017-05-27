/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package simulationpanel;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import gov.nasa.worldwind.geom.LatLon;
import gov.nasa.worldwind.util.*;
import gov.nasa.worldwind.layers.RenderableLayer;
import gov.nasa.worldwind.render.BasicShapeAttributes;
import gov.nasa.worldwind.render.Material;
import gov.nasa.worldwind.render.ShapeAttributes;
import gov.nasa.worldwind.render.SurfacePolygon;
import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.*;

/**
 *
 * @author ZZL
 */
public class RegionManagerClass {

    public HashMap<String, MultiPolygon> regionIdentification2MultiPolygonMap = new HashMap<String, MultiPolygon>();
    private RenderableLayer layer;
    public DefaultListModel selectedRegionModel;//被选择区域
    private HashMap<String, SurfacePolygon[]> regionIdentification2SurfacePolygonMap = new HashMap<String, SurfacePolygon[]>();

    public TreeMap<Integer, String> shengMap;
    public TreeMap<Integer, String> shiMap;
    public TreeMap<Integer, String> xianMap;

    public RegionManagerClass(RenderableLayer layer) {
        this.layer = layer;
        selectedRegionModel = new DefaultListModel();

        shengMap = new TreeMap<Integer, String>();
        shiMap = new TreeMap<Integer, String>();
        xianMap = new TreeMap<Integer, String>();
        PostGisDatabaseClass.getCode2NameMap(shengMap, shiMap, xianMap);
    }


    public int addRegionByCounty(String regionIdentification,String shengNameString,String shiNameString,String xianNameString) {
        MultiPolygon mp = null;
        if (xianNameString != null && !xianNameString.equals("全部")) {
            //添加县

            int shengCode = 0;
            for (Map.Entry<Integer, String> entry : shengMap.entrySet()) {
                if (entry.getValue().equals(shengNameString)) {
                    shengCode = entry.getKey();
                    break;
                }
            }
            int shiCode = 0;
            for (Map.Entry<Integer, String> entry : shiMap.entrySet()) {
                if (entry.getKey() / 100 == shengCode && entry.getValue().equals(shiNameString)) {
                    shiCode = entry.getKey();
                    break;
                }
            }
            int xianCode=0;
            for (Map.Entry<Integer, String> entry : xianMap.entrySet()) {
                if (entry.getKey() / 10000 == shengCode && entry.getKey() / 100 == shiCode && entry.getValue().equals(xianNameString)) {
                    xianCode = entry.getKey();
                    break;
                }
            }
            ArrayList<gov.nasa.worldwind.geom.LatLon[]> arrayList = PostGisDatabaseClass.getLonLatList(xianCode);
            if (arrayList == null || arrayList.size() == 0) {
                return 1;
            }
            mp = Draw2MultiPolygon(arrayList.get(0));
            for (int i = 1; i < arrayList.size(); i++) {
                mp = (MultiPolygon) mp.union(Draw2MultiPolygon(arrayList.get(i)));
            }

        } else if (xianNameString != null && xianNameString.equals("全部")) {
            //添加地级市
            
            int shengCode = 0;
            for (Map.Entry<Integer, String> entry : shengMap.entrySet()) {
                if (entry.getValue().equals(shengNameString)) {
                    shengCode = entry.getKey();
                    break;
                }
            }
            int shiCode = 0;
            for (Map.Entry<Integer, String> entry : shiMap.entrySet()) {
                if (entry.getKey() / 100 == shengCode && entry.getValue().equals(shiNameString)) {
                    shiCode = entry.getKey();
                    break;
                }
            }
            ArrayList<gov.nasa.worldwind.geom.LatLon[]> arrayList = PostGisDatabaseClass.getLonLatList(shiCode);
            if (arrayList == null || arrayList.size() == 0) {
                return 1;
            }
            mp = Draw2MultiPolygon(arrayList.get(0));
            for (int i = 1; i < arrayList.size(); i++) {
                mp = (MultiPolygon) mp.union(Draw2MultiPolygon(arrayList.get(i)));
            }

        } else if (shiNameString != null && shiNameString.equals("全部")) {
            //添加省

            int shengCode = 0;
            for (Map.Entry<Integer, String> entry : shengMap.entrySet()) {
                if (entry.getValue().equals(shengNameString)) {
                    shengCode = entry.getKey();
                    break;
                }
            }
            ArrayList<gov.nasa.worldwind.geom.LatLon[]> arrayList = PostGisDatabaseClass.getLonLatList(shengCode);
            if (arrayList == null || arrayList.size() == 0) {
                return 1;
            }
            mp = Draw2MultiPolygon(arrayList.get(0));
            for (int i = 1; i < arrayList.size(); i++) {
                mp = (MultiPolygon) mp.union(Draw2MultiPolygon(arrayList.get(i)));
            }
             
        }else
        {
            //添加全国
            
            ArrayList<gov.nasa.worldwind.geom.LatLon[]> arrayList = PostGisDatabaseClass.getLonLatList(0);
            mp = Draw2MultiPolygon(arrayList.get(0));
            for (int i = 1; i < arrayList.size(); i++) {
                mp = (MultiPolygon) mp.union(Draw2MultiPolygon(arrayList.get(i)));
            }
        }
        if (mp == null) {
            return 1;
        }
        regionIdentification2MultiPolygonMap.put(regionIdentification, mp);
        SurfacePolygon[] spArray = MultiPolygon2SurfacePolygon(mp);
        regionIdentification2SurfacePolygonMap.put(regionIdentification, spArray);
        for (SurfacePolygon sp : spArray) {
            layer.addRenderable(sp);
        }
        this.selectedRegionModel.addElement(regionIdentification);
        return 0;
        
    }

    public int addRegionByShape(String regionIdentification, String path) {
        MultiPolygon mp = RegionManagerClass.Shape2MultiPolygon(path);
        if (mp == null) {
            return 1;
        }
        regionIdentification2MultiPolygonMap.put(regionIdentification, mp);
        SurfacePolygon[] spArray = MultiPolygon2SurfacePolygon(mp);
        regionIdentification2SurfacePolygonMap.put(regionIdentification, spArray);
        for (SurfacePolygon sp : spArray) {
            layer.addRenderable(sp);
        }
        this.selectedRegionModel.addElement(regionIdentification);
        return 0;
    }

    public int addRegionByDraw(String regionIdentification, ArrayList<LatLon> latlonlist) {

        MultiPolygon mp = RegionManagerClass.Draw2MultiPolygon(latlonlist);
        regionIdentification2MultiPolygonMap.put(regionIdentification, mp);
        SurfacePolygon[] spArray = MultiPolygon2SurfacePolygon(mp);
        regionIdentification2SurfacePolygonMap.put(regionIdentification, spArray);
        for (SurfacePolygon sp : spArray) {
            layer.addRenderable(sp);
        }
        this.selectedRegionModel.addElement(regionIdentification);

        return 0;
    }

    public Set<String> getAllIdentification() {
        Set<String> res = regionIdentification2MultiPolygonMap.keySet();
        return res;
    }

    public void removeRegion(String regionIdentification) {
        regionIdentification2MultiPolygonMap.remove(regionIdentification);
        SurfacePolygon[] spArray = regionIdentification2SurfacePolygonMap.get(regionIdentification);
        regionIdentification2SurfacePolygonMap.remove(regionIdentification);

        for (SurfacePolygon sp : spArray) {
            layer.removeRenderable(sp);
        }

        this.selectedRegionModel.removeElement(regionIdentification);
    }

    public static MultiPolygon Shape2MultiPolygon(String path)
    {
//        String path="";
        File file = new File(path);
        Shapefile shape = new Shapefile(file);
        if (shape.getShapeType().equals("gov.nasa.worldwind.formats.shapefile.Shapefile.ShapePolygon")) {
            ArrayList<com.vividsolutions.jts.geom.Polygon> polygonList = new ArrayList<com.vividsolutions.jts.geom.Polygon>();
            while (shape.hasNext()) {
                ShapefileRecord record = shape.nextRecord();
                for (int j = 0; j < record.getNumberOfParts(); j++) {
                    VecBuffer vb = record.getPointBuffer(j);
                    int ptNum = record.getNumberOfPoints(j);
                    Coordinate[] coor = new Coordinate[ptNum];
                    for (int k = 0; k < ptNum; k++) {
                        coor[k] = new Coordinate(vb.getLocation(k).getLongitude().radians, vb.getLocation(k).getLatitude().radians);
                    }
                    polygonList.add(new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(coor), null));
                }
            }
            return new GeometryFactory().createMultiPolygon((com.vividsolutions.jts.geom.Polygon[]) polygonList.toArray(new com.vividsolutions.jts.geom.Polygon[polygonList.size()]));
        } else {
            return null;
        }
    }

    public static MultiPolygon Draw2MultiPolygon(LatLon[] latlonArray) {
        com.vividsolutions.jts.geom.Polygon[] polygons = new com.vividsolutions.jts.geom.Polygon[1];
        int pointsNum = latlonArray.length;
        Coordinate[] coordinates = new Coordinate[pointsNum];
        for (int j = 0; j < pointsNum; j++) {
            coordinates[j] = new Coordinate(latlonArray[j].longitude.radians, latlonArray[j].latitude.radians);
        }
        polygons[0] = new GeometryFactory().createPolygon(new GeometryFactory().createLinearRing(coordinates), null);
        return new GeometryFactory().createMultiPolygon(polygons);
    }
    
    public static MultiPolygon Draw2MultiPolygon(ArrayList<LatLon> latlonlist) {
        return Draw2MultiPolygon((LatLon[]) latlonlist.toArray(new LatLon[latlonlist.size()]));
    }
    
    
    private static final ShapeAttributes mtAttr;
    static
    {
        mtAttr = new BasicShapeAttributes();
        mtAttr.setInteriorMaterial(new Material(new Color(255, 255, 255), new Color(153, 153, 102), new Color(45, 45, 30), new Color(0, 0, 0), 80));
        mtAttr.setInteriorOpacity(0.502f);
        mtAttr.setOutlineMaterial(new Material(new Color(255, 255, 255), new Color(255, 255, 0), new Color(76, 76, 0), new Color(0, 0, 0), 80));
        mtAttr.setOutlineWidth(2);
        mtAttr.setEnableLighting(false);
        mtAttr.setEnableAntialiasing(true);
        mtAttr.setDrawOutline(true);
        mtAttr.setDrawInterior(true);
        mtAttr.setUnresolved(false);
    }

    public static SurfacePolygon[] MultiPolygon2SurfacePolygon(MultiPolygon mp) {
        int geoNum = mp.getNumGeometries();
        
        SurfacePolygon[] spArray=new SurfacePolygon[geoNum];

        for (int i = 0; i < geoNum; i++) {
            Geometry g = mp.getGeometryN(i);
            Coordinate[] coor = g.getCoordinates();
            ArrayList<LatLon> l = new ArrayList<>();
            for (Coordinate coor1 : coor) {
                l.add(LatLon.fromRadians(coor1.y, coor1.x));
            }
            SurfacePolygon plg = new SurfacePolygon(l);
            plg.setAttributes(mtAttr);
            spArray[i]=plg;

        }
        return spArray;
    }
}


//class 