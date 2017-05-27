/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package zzlorbittest3;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LinearRing;
import gov.nasa.worldwind.formats.shapefile.Shapefile;
import gov.nasa.worldwind.formats.shapefile.ShapefileRecord;
import java.awt.*;
import java.io.File;
import javax.swing.*;
import java.util.*;
import javax.swing.JFrame;

/**
 *
 * @author ZZL
 */
public class GridGraphic {

    public GridGraphicPanel ggp;

    public GridGraphic() {
        ggp = new GridGraphicPanel();
    }

    public void show() {
        JFrame frame = new JFrame("MyFrame");
        frame.setSize(1400, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //frame.setLayout(new FlowLayout(FlowLayout.LEFT));
        frame.add(ggp);

        //p.repaint();
        frame.setVisible(true);
    }

    public void SetGrid(float[][] grid) {
        int i, j;
        for (i = 0; i < 800; i++) {
            for (j = 0; j < 1400; j++) {
                ggp.chinaGrid[i][j]=(int)grid[i][j];
            }
        }
    }

}

class GridGraphicPanel extends JPanel {

    public float[][] chinaGrid = new float[800][1400];
    public GridGraphicPanel()
    {
    initChina();
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.white);
        g.fillRect(0, 0, 1400, 800);
        
        
        drawGrid(g);
        drawChina(g);

    }

    private void drawGrid(Graphics g) {
        Color[] colorTable = new Color[30];
        int i, j;
        for(i=0;i<25;i++)
        {
            colorTable[i]=new Color(255-i*40>=0?255-i*40:0,255-i*20>=0?255-i*20:0,255);
        }

        
        
        Color emptyColor=new Color(255,200,200);

        float sum = 0;
        for (i = 0; i < 800; i++) {
            for (j = 0; j < 1400; j++) {
                ////////////////////////////////////

//                if(chinaGrid[i][j]>=1)
//                {
//                    g.setColor(new Color((int)(chinaGrid[i][j]*10>255?255:chinaGrid[i][j]*10),0,0));
//                        g.drawRect(j , i , 3, 3);
//                }
                if (chinaGrid[i][j] >= 1) {
                    g.setColor(new Color(100, 0, 0));
                    g.drawRect(j, i, 3, 3);
                }
//                else
//                {
//                    g.setColor(new Color(0, 0, 0));
//                    g.drawRect(j, i, 3, 3);
//                }

            }

        }
    }


    
    
    private void drawChina(Graphics g) {
        Coordinate coors[];
        int i, j;
        double LonLat[] = new double[2];
        int gra[] = new int[2];
        Polygon polygon;
        g.setColor(Color.green);
        for (i = 0; i < chinaMap.length; i++) {
            coors = chinaMap[i].getCoordinates();
            polygon = new Polygon();
            for (j = 0; j < coors.length; j++) {
                LonLat[0] = coors[j].x;
                LonLat[1] = coors[j].y;
                LonLat2Graphics(LonLat, gra);
                polygon.addPoint(gra[0], gra[1]);
            }
            g.drawPolygon(polygon);

        }
        g.setColor(Color.black);
    }
    public LinearRing[] chinaMap;

    private void initChina() {
        String chinaPath = "file\\china4.shp";
        chinaMap = shape2LinearRingList(chinaPath);
    }

    public LinearRing[] shape2LinearRingList(String path) {
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

    private void LonLat2Graphics(double[] LonLat, int[] gra) {
        gra[0] = (int) ((LonLat[0] - 70) * 20) ;//¾­¶È
        gra[1] = (int) ((55 - LonLat[1]) * 20);//Î³¶È
    }
}
