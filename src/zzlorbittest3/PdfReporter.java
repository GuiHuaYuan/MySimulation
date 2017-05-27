/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package zzlorbittest3;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

import coverage.iostruct.*;
import coverage.util.Time;
import java.util.Calendar;

/**
 *
 * @author ZZL
 */
public class PdfReporter {

    private BaseFont songBaseFont;
    private Font titleFont;
    private Font subTitleFont;
    private Font contentFont;

    public PdfReporter() {
        try {
            songBaseFont = BaseFont.createFont("C:\\Windows\\Fonts\\SIMSUN.TTC,1", BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
            titleFont = new Font(songBaseFont, 18, Font.BOLD, BaseColor.BLACK);
            subTitleFont = new Font(songBaseFont, 13);
            contentFont = new Font(songBaseFont, 10);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void report(AccessInput ai,AccessOutput ao,String path,BufferedImage screenShot) {
        Document doc = new Document();
        try {
            PdfWriter.getInstance(doc, new FileOutputStream(path));
            doc.open();
            Image img; 
            String str1,str2;
            addTitle(doc,"评估报告");   
            
            /////////////////////////            第1部分             ///////////////////////// 
            addSubTitle(doc, "一、输入信息概况");
            str1 = "所选卫星:";
            for (SatelliteInput sli : ai.satelliteInput) {
                str1 += sli.satName + " ";
            }
            addContent(doc, str1);

            Time t1 = new Time(ai.startTime);
            Time t2 = new Time(ai.endTime);
            str1 = String.format("%d年%d月%d日", t1.getBJ(Calendar.YEAR), t1.getBJ(Calendar.MONTH) + 1, t1.getBJ(Calendar.DAY_OF_MONTH));
            str2 = String.format("%d年%d月%d日", t2.getBJ(Calendar.YEAR), t2.getBJ(Calendar.MONTH) + 1, t2.getBJ(Calendar.DAY_OF_MONTH));
            addContent(doc, "任务开始时间:" + str1 + "  任务结束时间:" + str2);


            /////////////////////////            第2部分             ///////////////////////// 
            
            int len = ao.timeNodeArray.length;
//            len=4;
//            ao.progressArray[0]=0.6f;
//            ao.progressArray[1]=0.3f;
//            ao.progressArray[2]=0.9f;

            PdfPic pdfpic = new PdfPic();

            pdfpic.xArray = new float[len];
            pdfpic.xCoorStrArray = new String[len];
            
            Time t0 = new Time(ao.timeNodeArray[0]);

            //如果时间节点的个数过长，则以月为x坐标的时间间隔 否则，以旬为时间间隔
            if (len >= 10) {
                for (int i = 0; i < len; i++) {
                    Time t = new Time(ao.timeNodeArray[i]);
                    pdfpic.xArray[i] = (t.getGregorianCalendar().getTime().getTime() - t0.getGregorianCalendar().getTime().getTime()) / 1000.0f / 3600;
                    if (t.getBJ(Calendar.DAY_OF_MONTH) == 1) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "月";
                    } else {
                        pdfpic.xCoorStrArray[i] = null;
                    }
                }
            } else {
                for (int i = 0; i < len; i++) {
                    Time t = new Time(ao.timeNodeArray[i]);
                    pdfpic.xArray[i] = (t.getGregorianCalendar().getTime().getTime() - t0.getGregorianCalendar().getTime().getTime()) / 1000.0f / 3600;
                    if (t.getBJ(Calendar.DAY_OF_MONTH) == 1) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "月上旬";
                    } else if (t.getBJ(Calendar.DAY_OF_MONTH) == 11) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "月中旬";
                    } else if (t.getBJ(Calendar.DAY_OF_MONTH) == 21) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "月下旬";
                    } else {
                        pdfpic.xCoorStrArray[i] = null;
                    }
                }

            }
  
            addSubTitle(doc,"二、拍摄时间周期内地面任务区域的预测云量（按旬）");

            pdfpic.yArray = new float[len];
            for (int i = 0; i < len - 1; i++) {
                for (int j = 0; j < 20; j++) {
                    pdfpic.yArray[i] += ao.cloudArray[i][j] * ((1.0f / 20) * j + 0.025f);
                }
                pdfpic.yArray[i] /= ao.totalGrid;
            }
            img = pdfpic.getImage(PdfPic.PDFImageType.CLOUD);
            img.setAlignment(Element.ALIGN_CENTER);
            doc.add(img);

            /////////////////////////            第3部分             ///////////////////////// 

            addSubTitle(doc,"三、地面任务区域拍摄进度分析");
            
            pdfpic.yArray = ao.progressArray;   

            img = pdfpic.getImage(PdfPic.PDFImageType.PROGRESS);
            img.setAlignment(Element.ALIGN_CENTER);
            doc.add(img);

            str1 = String.format("%.1f", 100 * ao.progressArray[len - 2]);
            Time t = new Time(ao.timeNodeArray[len - 1]);
            str2 = String.format("%d年%d月%d日", t.getBJ(Calendar.YEAR), 1+t.getBJ(Calendar.MONTH), t.getBJ(Calendar.DAY_OF_MONTH));
            addContent(doc, "地面任务区域预期覆盖情况：" + str1 + "%,完全覆盖预期需要至:" + str2);

            /////////////////////////            第5部分             ///////////////////////// 
            addSubTitle(doc, "四、潜在薄弱区");
            if (screenShot != null) {
                img = Image.getInstance(screenShot, Color.BLACK);
                img.scaleToFit(400, 500);
                img.setAlignment(1);
                doc.add(img);

            }
            str1 = "图中蓝色部分的区域是易于获取的区域，黄色部分的区域是较易获取的区域，橙色部分的区域是较难获取的区域，红色部分的区域是难于获取的区域，";
            addContent(doc, str1);

            /////////////////////////            第6部分             ///////////////////////// 

            PdfPTable table;
            Paragraph para;
            PdfPCell cell;
            Font tableFont = new Font(songBaseFont, 8);

            addSubTitle(doc, "五、各行政区预期覆盖情况");
            
            table = new PdfPTable(3);
            table.setWidthPercentage(50);
            para = new Paragraph("行政区划", tableFont);
            para.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(para);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            
            para = new Paragraph("任务区面积比例", tableFont);
            para.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(para);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            
            para = new Paragraph("预期覆盖率", tableFont);
            para.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(para);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);

            for (int i = 0; i < ao.regionCoverageInfoArray.length; i++) {
                if (ao.regionCoverageInfoArray[i].regionCode == 0) {
                    continue;
                }
                if (ao.regionCoverageInfoArray[i].groundTaskRegionTotalPoint <= 5) {
                    continue;
                }
                str1 = ao.regionCoverageInfoArray[i].regionName;
                para = new Paragraph(str1, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

                str1 = String.format("%.1f%%", 100.0f * ao.regionCoverageInfoArray[i].groundTaskRegionTotalPoint / ao.regionCoverageInfoArray[i].totalPoint);
                para = new Paragraph(str1, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);

                str1 = String.format("%.1f%%", 100.0f * ao.regionCoverageInfoArray[i].coverageTimes / ao.regionCoverageInfoArray[i].groundTaskRegionTotalPoint);
                para = new Paragraph(str1, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
            }
            doc.add(table);
            
            /////////////////////////            第7部分             ///////////////////////// 
            addSubTitle(doc, "六、总结");
            String summaryString;
            str1 = String.format("%.1f", 100 * ao.progressArray[len - 2]);
            t = new Time(ao.timeNodeArray[len - 1]);
            str2 = String.format("%d年%d月%d日", t.getBJ(Calendar.YEAR), 1 + t.getBJ(Calendar.MONTH), t.getBJ(Calendar.DAY_OF_MONTH));
            summaryString = "    地面任务区域在拍摄时间周期内，数据获取难以程度总体为" + (100.0f * ao.difficultyDegree[0] / ao.totalGrid + 100.0f * ao.difficultyDegree[1] / ao.totalGrid >= 50f ? "易于获取" : "难于获取");
            addContent(doc, summaryString);
            summaryString = "    预期达到的覆盖率为：" + str1 + "  预期在" + str2 + "能够实现全覆盖";
            addContent(doc, summaryString);
            summaryString = String.format("    其中易于获取的区域占: %.1f%%,较易获取的区域占：%.1f%%", 100.0f * ao.difficultyDegree[0] / ao.totalGrid, 100.0f * ao.difficultyDegree[1] / ao.totalGrid);
            addContent(doc, summaryString);
            summaryString = String.format("    其中较难获取的区域占: %.1f%%,难于获取的区域占：%.1f%%", 100.0f * ao.difficultyDegree[2] / ao.totalGrid, 100.0f * ao.difficultyDegree[3] / ao.totalGrid);
            addContent(doc, summaryString);

//            summaryString = "二、对地面任务区域可完成2次全覆盖。";
//            addContent(doc, summaryString);
            
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////         添加附表 1           ////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            doc.newPage();
            this.addSubTitle(doc, "附表1 地面任务区域云量情况");

            table = new PdfPTable(11);
            tableFont = new Font(songBaseFont, 8);
            table.addCell("");
            for (int i = 0; i < 100; i += 10) {
                String str = String.format("%02d%%-%02d%%", i, i + 10);
                para = new Paragraph(str, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
               cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
            }

            for (int i = 0; i < len - 1; i++) {

                t = new Time(ao.timeNodeArray[i]);
                int day = t.getBJ(Calendar.DAY_OF_MONTH);
                int mon = 1 + t.getBJ((Calendar.MONTH));
                String str = Integer.toString(mon) + "月";
                if (i == 0) {
                    if (day < 11) {
                        str += "上旬";
                    } else if (day < 21) {
                        str += "中旬";
                    } else {
                        str += "下旬";
                    }
                } else {
                    if (day == 1) {
                        str += "上旬";
                    } else if (day == 11) {
                        str += "中旬";
                    } else if (day == 21) {
                        str += "下旬";
                    } else {
                        continue;
                    }
                }

                para = new Paragraph(str, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
                for (int j = 0; j < 10; j++) {

                    str = String.format("%.1f%%", 100.0f * (ao.cloudArray[i][2 * j] + ao.cloudArray[i][2 * j + 1]) / ao.totalGrid);
                    para = new Paragraph(str, tableFont);
                    para.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(para);
                    cell.setHorizontalAlignment(1);
                    table.addCell(cell);
                }

            }

            doc.add(table);
            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////         添加附表 2           ////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////           
            
            doc.newPage();
            this.addSubTitle(doc, "附表2 地面任务区域覆盖情况");

            float[] widthArray=new float[]{2,1,1,1,1,1,1,1,1,1,1,1,1,2};
            table = new PdfPTable(widthArray);
            tableFont = new Font(songBaseFont, 8);
            table.addCell("");
            for (int i = 0; i < 13; i++) {
                String str = String.format("%02d次", i);
                if (i == 12) {
                    str = String.format("%02d次及以上", i);
                }
                para = new Paragraph(str, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
            }

            for (int i = 0; i < len - 1; i++) {

                t = new Time(ao.timeNodeArray[i]);
                int day = t.getBJ(Calendar.DAY_OF_MONTH);
                int mon = 1 + t.getBJ((Calendar.MONTH));
                String str = Integer.toString(mon) + "月";
                if (i == 0) {
                    if (day < 11) {
                        str += "上旬";
                    } else if (day < 21) {
                        str += "中旬";
                    } else {
                        str += "下旬";
                    }
                } else {
                    if (day == 1) {
                        str += "上旬";
                    } else if (day == 11) {
                        str += "中旬";
                    } else if (day == 21) {
                        str += "下旬";
                    } else {
                        continue;
                    }
                }

                para = new Paragraph(str, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
                for (int j = 0; j < 13; j++) {
                    str = String.format("%.1f%%", 100.0f * ao.coverageTimesArray[i][j] / ao.totalGrid);
                    if (j == 12) {
                        str = String.format("%.1f%%", 100.0f * (ao.coverageTimesArray[i][12] + ao.coverageTimesArray[i][13] + ao.coverageTimesArray[i][14] + ao.coverageTimesArray[i][15] + ao.coverageTimesArray[i][16] + ao.coverageTimesArray[i][17] + ao.coverageTimesArray[i][18] + ao.coverageTimesArray[i][19]) / ao.totalGrid);
                    }
                    para = new Paragraph(str, tableFont);
                    para.setAlignment(Element.ALIGN_CENTER);
                    cell = new PdfPCell(para);
                    cell.setHorizontalAlignment(1);
                    table.addCell(cell);
                }

            }

            doc.add(table);
            doc.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private void addTitle(Document doc, String title) throws Exception {
        Paragraph para;
        para = new Paragraph(title, titleFont);
        para.setAlignment(Element.ALIGN_CENTER);
        para.setSpacingAfter(7);
        doc.add(para);
    }

    private void addSubTitle(Document doc, String subTitle) throws Exception {
        Paragraph para;
        para = new Paragraph(subTitle, subTitleFont);
        para.setSpacingAfter(7);
        doc.add(para);
    }

    private void addContent(Document doc, String content) throws Exception {
        Paragraph para;
        para = new Paragraph(content, contentFont);
//            para.add("所选卫星：");
        para.setFirstLineIndent(20);
        para.setSpacingAfter(7);
        doc.add(para);
    }

}

class PdfPic {
    
    
    public String[] xCoorStrArray;
    public float[] yArray;
    public float[] xArray;


    private BufferedImage bimg;
    private java.awt.Graphics2D g2d;
    private static int imgWidth = 400;
    private static int imgHeight = 150;
    private static int leftMargin = 30;
    private static int rightMargin = 20;
    private static int upMargin = 10;
    private static int downMargin = 20;

    public PdfPic() {

    }

    public enum PDFImageType {
        PROGRESS, CLOUD
    }

    public Image getImage(PDFImageType type) {

        bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        g2d = (java.awt.Graphics2D) bimg.getGraphics();
        drawBackgroundImage();
        if (PDFImageType.PROGRESS.equals(type)) {
            drawProgressData();
        } else {
            drawCloudData();
        }
        drawXCoor();
        Image img = null;
        try {
            img = Image.getInstance(bimg, Color.BLACK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return img;
    }

    private void drawProgressData() {
        int len = xArray.length;
        float[] xf = new float[len];
        for (int i = 0; i < len; i++) {
            xf[i] = xArray[i] - xArray[0];
        }

        float gap = 90.0f / xf[len - 1];
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));

        Point cpt1, cpt2;
        Point pt1 = new Point(0, 0);
        Point pt2 = new Point(0, 0);
        for (int i = 1; i < len; i++) {
            pt1.x = 5 + (int) (xf[i - 1] * gap);
            if (i - 2 >= 0) {
                pt1.y = (int) (100 * yArray[i - 2]);
            } else {
                pt1.y = 0;
            }
            pt1.y = pt1.y > 100 ? 100 : pt1.y;
            cpt1 = convertPoint(pt1);
            pt2.x = 5 + (int) (xf[i - 0] * gap);
            pt2.y = (int) (100 * yArray[i - 1]);
            pt2.y = pt2.y > 100 ? 100 : pt2.y;
            cpt2 = convertPoint(pt2);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        }
    }

    private void drawCloudData() {
        int len = xArray.length;
        float[] xf = new float[len];
        for (int i = 0; i < len; i++) {
            xf[i] = xArray[i] - xArray[0];
        }

        float gap = 90.0f / xf[len - 1];
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(1.0f));

        Point cpt1, cpt2;
        Point pt1 = new Point(0, 0);
        Point pt2 = new Point(0, 0);
        for (int i = 0; i < len - 1; i++) {
            pt1.x = 5 + (int) (xf[i] * gap);
            pt1.y = (int) (100 * yArray[i]);
            pt1.y = pt1.y > 100 ? 100 : pt1.y;
            cpt1 = convertPoint(pt1);
            pt2.x = 5 + (int) (xf[i + 1] * gap);
            if (i != len - 2) {
                pt2.y = (int) (100 * yArray[i + 1]);
                pt2.y = pt2.y > 100 ? 100 : pt2.y;
            }
            cpt2 = convertPoint(pt2);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        }
    }

    private void drawXCoor() {
        int len = xArray.length;
        float[] xf = new float[len];
        for (int i = 0; i < len; i++) {
            xf[i] = xArray[i] - xArray[0];
        }

        float gap = 90.0f / xf[len - 1];

        g2d.setColor(Color.BLACK);
        g2d.setFont(java.awt.Font.decode("宋体-plain-12"));

        Point cpt1;
        Point pt1 = new Point(0, 0);
        for (int i = 0; i < len ; i++) {
            pt1.x = 5 + (int) (xf[i] * gap);
            pt1.y = -10;
            cpt1 = convertPoint(pt1);
            if (this.xCoorStrArray[i] != null) {
                g2d.drawString(this.xCoorStrArray[i], cpt1.x, cpt1.y);
            }
        }
    }

    private BufferedImage drawBackgroundImage() {

        g2d.setColor(new Color(255, 255, 255));
        g2d.fillRect(0, 0, imgWidth, imgHeight);
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(2.0f));

        Point pt1, pt2, cpt1, cpt2;
        //绘制坐标轴
        pt1 = new Point(0, 0);
        cpt1 = convertPoint(pt1);
        pt2 = new Point(100, 0);
        cpt2 = convertPoint(pt2);
        g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        pt2 = new Point(0, 100);
        cpt2 = convertPoint(pt2);
        g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);

        //绘制0%-100%
        g2d.setFont(new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 10));
        for (int i = 0; i <= 100; i += 10) {
            pt1 = new Point(0, i);
            cpt1 = convertPoint(pt1);
            g2d.drawString(i + "%", 5, 4 + cpt1.y);
        }

        return bimg;
    }

    //将逻辑坐标转化为绘图坐标。逻辑坐标：x取值范围(0,100),y取值范围(0,100),
    private static Point convertPoint(Point logicCoor) {
        Point pt = new Point();

        float k = ((float) (imgWidth - rightMargin - leftMargin)) / 100.0f;
        pt.x = (int) (leftMargin + k * logicCoor.x);

        k = (upMargin - imgHeight + downMargin) / 100.0f;
        pt.y = (int) (imgHeight - downMargin + k * logicCoor.y);

        return pt;
    }
}
