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
            String str1,str2,SummaryString;
            Time t;
            addTitle(doc,"��������");   
            
            /////////////////////////            ��1����             ///////////////////////// 
            addSubTitle(doc, "һ��������Ϣ�ſ�");
            str1 = "��ѡ����:";
            if (ai.satelliteInput != null) {
                for (SatelliteInput sli : ai.satelliteInput) {
                    str1 += sli.satName + " ";
                }
            }
            addContent(doc, str1);

            Time t1 = new Time(ai.startTime);
            Time t2 = new Time(ai.endTime);
            str1 = String.format("%d��%d��%d��", t1.getBJ(Calendar.YEAR), t1.getBJ(Calendar.MONTH) + 1, t1.getBJ(Calendar.DAY_OF_MONTH));
            str2 = String.format("%d��%d��%d��", t2.getBJ(Calendar.YEAR), t2.getBJ(Calendar.MONTH) + 1, t2.getBJ(Calendar.DAY_OF_MONTH));
            addContent(doc, "����ʼʱ��:" + str1 + "  �������ʱ��:" + str2);


            /////////////////////////            ��ȡ����С��100%��ʱ��ڵ��б�             ///////////////////////// 
            
            
            int len = ao.timeNodeArray.length;
            for (int i = 0; i < ao.timeNodeArray.length; i++) {
                if (ao.progressArray[i] >= 0.99f) {
                    len = i > ao.endTimeNode ? i : ao.endTimeNode;
                    len += 1;
                    break;
                }
            }


            PdfPic pdfpic = new PdfPic();

            pdfpic.xArray = new float[len];
            pdfpic.xCoorStrArray = new String[len];
            
            Time t0 = new Time(ao.timeNodeArray[0]);

            //���ʱ��ڵ�ĸ���������������Ϊx�����ʱ���� ������ѮΪʱ����
            if (len >= 10) {
                for (int i = 0; i < len; i++) {
                    t = new Time(ao.timeNodeArray[i]);
                    pdfpic.xArray[i] = (t.getGregorianCalendar().getTime().getTime() - t0.getGregorianCalendar().getTime().getTime()) / 1000.0f / 3600;
                    if (t.getBJ(Calendar.DAY_OF_MONTH) == 1) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "��";
                    } else {
                        pdfpic.xCoorStrArray[i] = null;
                    }
                }
            } else {
                for (int i = 0; i < len; i++) {
                    t = new Time(ao.timeNodeArray[i]);
//                    System.out.println(t.toBJTime());
//                    System.out.println(t.getBJ(Calendar.DAY_OF_MONTH));
                    pdfpic.xArray[i] = (t.getGregorianCalendar().getTime().getTime() - t0.getGregorianCalendar().getTime().getTime()) / 1000.0f / 3600;
                    if (t.getBJ(Calendar.DAY_OF_MONTH) == 1) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "����Ѯ";
                    } else if (t.getBJ(Calendar.DAY_OF_MONTH) == 11) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "����Ѯ";
                    } else if (t.getBJ(Calendar.DAY_OF_MONTH) == 21) {
                        pdfpic.xCoorStrArray[i] = Integer.toString(t.getBJ(Calendar.MONTH) + 1) + "����Ѯ";
                    } else {
                        pdfpic.xCoorStrArray[i] = null;
                    }
                }

            }
  
            addSubTitle(doc,"��������ʱ�������ڵ������������ƽ����������Ѯ��");

            /////////////////////////            ��2���� ����      ///////////////////////// 
            pdfpic.yArray = new float[len];
            for (int i = 0; i < len - 1; i++) {
                for (int j = 0; j < 20; j++) {
                    pdfpic.yArray[i] += ao.cloudArray[i][j] * ((1.0f / 20) * j + 0.025f);
                }
                pdfpic.yArray[i] /= ao.totalGrid;
            }
            pdfpic.yArray[len - 1] = pdfpic.yArray[len - 2];
            pdfpic.endTimeNode = -1;
            img = pdfpic.getImage();
            img.setAlignment(Element.ALIGN_CENTER);
            doc.add(img);

            /////////////////////////            ��3����  ���ǽ���           ///////////////////////// 

            addSubTitle(doc,"����������������������ȷ���");
            
            pdfpic.yArray = ao.progressArray;   
            pdfpic.yArray=new float[len];
            pdfpic.yArray[0]=0;
            pdfpic.endTimeNode = ao.endTimeNode;
            for (int i = 1; i < len; i++) {
                pdfpic.yArray[i] = ao.progressArray[i - 1];
            }

            img = pdfpic.getImage();
            img.setAlignment(Element.ALIGN_CENTER);
            doc.add(img);

            //�ж�1���ʱ���Ƿ��������ȫ����,ao.progressArray[ao.progressArray.length - 1]��ֵ��0
            if (ao.progressArray[ao.progressArray.length - 2] >= 0.99f) {
                //�������ȫ���ǣ�����մﵽ��ȫ������Ҫ��ʱ��

                int completeNodeIndex = ao.progressArray.length - 2;
                for (int i = 0; i < ao.timeNodeArray.length - 1; i++) {
                    if (ao.progressArray[i] >= 0.99f) {
                        completeNodeIndex = i;
                        break;
                    }
                }
                str1 = String.format("%.1f", 100 * ao.progressArray[ao.endTimeNode-1]);
                t = new Time(ao.timeNodeArray[completeNodeIndex + 1]);
                str2 = String.format("%d��%d��%d��", t.getBJ(Calendar.YEAR), 1 + t.getBJ(Calendar.MONTH), t.getBJ(Calendar.DAY_OF_MONTH));
                addContent(doc, "������������Ԥ�ڸ��������" + str1 + "%,��ȫ����Ԥ����Ҫ��:" + str2);
                SummaryString="    Ԥ�ڴﵽ�ĸ�����Ϊ��" + str1 + "%  Ԥ����" + str2 + "�ܹ�ʵ��ȫ����";

            } else {
                //δ�����ȫ���ǣ����1���ĸ�����
                str1 = String.format("%.1f", 100 * ao.progressArray[ao.endTimeNode-1]);
                t = new Time(ao.timeNodeArray[len - 1]);
                str2 = String.format("%.1f%%", 100.0f * ao.progressArray[ao.progressArray.length - 2]);
                addContent(doc, "������������Ԥ�ڸ��������" + str1 + "%,һ���Ԥ�ڸ�����:" + str2);
                SummaryString="    Ԥ�ڴﵽ�ĸ�����Ϊ��" + str1 +  "%,һ���Ԥ�ڸ�����:" + str2;
            }

            /////////////////////////            ��5����             ///////////////////////// 
            addSubTitle(doc, "�ġ�Ǳ�ڱ�����");
            if (screenShot != null) {
                img = Image.getInstance(screenShot, Color.BLACK);
                img.scaleToFit(400, 500);
                img.setAlignment(1);
                doc.add(img);

            }
            str1 = "ͼ����ɫ���ֵ����������ڻ�ȡ�����򣬻�ɫ���ֵ������ǽ��׻�ȡ�����򣬳�ɫ���ֵ������ǽ��ѻ�ȡ�����򣬺�ɫ���ֵ����������ڻ�ȡ������";
            addContent(doc, str1);

//            doc.close();
//            int e=1/0;
            /////////////////////////            ��6����             ///////////////////////// 

            PdfPTable table;
            Paragraph para;
            PdfPCell cell;
            Font tableFont = new Font(songBaseFont, 8);

            addSubTitle(doc, "�塢��������Ԥ�ڸ������");
            
            table = new PdfPTable(3);
            table.setWidthPercentage(50);
            para = new Paragraph("��������", tableFont);
            para.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(para);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            
            para = new Paragraph("�������������", tableFont);
            para.setAlignment(Element.ALIGN_CENTER);
            cell = new PdfPCell(para);
            cell.setHorizontalAlignment(1);
            table.addCell(cell);
            
            para = new Paragraph("Ԥ�ڸ�����", tableFont);
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

                float rate=100.0f * ao.regionCoverageInfoArray[i].coverageTimes / ao.regionCoverageInfoArray[i].groundTaskRegionTotalPoint;
                rate=rate<=100?rate:100;
                str1 = String.format("%.1f%%", rate);
                para = new Paragraph(str1, tableFont);
                para.setAlignment(Element.ALIGN_CENTER);
                cell = new PdfPCell(para);
                cell.setHorizontalAlignment(1);
                table.addCell(cell);
            }
            doc.add(table);
            
            /////////////////////////            ��7����             ///////////////////////// 
            addSubTitle(doc, "�����ܽ�");
            String summaryString;
            str1 = String.format("%.1f%%", 100 * ao.progressArray[ao.endTimeNode-1]);
            t = new Time(ao.timeNodeArray[len - 1]);
            str2 = String.format("%d��%d��%d��", t.getBJ(Calendar.YEAR), 1 + t.getBJ(Calendar.MONTH), t.getBJ(Calendar.DAY_OF_MONTH));
            summaryString = "    ������������������ʱ�������ڣ����ݻ�ȡ���Գ̶�����Ϊ" + (100.0f * ao.difficultyDegree[0] / ao.totalGrid + 100.0f * ao.difficultyDegree[1] / ao.totalGrid >= 50f ? "���ڻ�ȡ" : "���ڻ�ȡ");
            addContent(doc, summaryString);
            summaryString = SummaryString;
            addContent(doc, summaryString);
            summaryString = String.format("    �������ڻ�ȡ������ռ: %.1f%%,���׻�ȡ������ռ��%.1f%%", 100.0f * ao.difficultyDegree[0] / ao.totalGrid, 100.0f * ao.difficultyDegree[1] / ao.totalGrid);
            addContent(doc, summaryString);
            summaryString = String.format("    ���н��ѻ�ȡ������ռ: %.1f%%,���ڻ�ȡ������ռ��%.1f%%", 100.0f * ao.difficultyDegree[2] / ao.totalGrid, 100.0f * ao.difficultyDegree[3] / ao.totalGrid);
            addContent(doc, summaryString);


            
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            /////////////////////////////////         ��Ӹ��� 1           ////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////
            
            doc.newPage();
            this.addSubTitle(doc, "����1 �������������������");

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
                String str = Integer.toString(mon) + "��";
                if (i == 0) {
                    if (day < 11) {
                        str += "��Ѯ";
                    } else if (day < 21) {
                        str += "��Ѯ";
                    } else {
                        str += "��Ѯ";
                    }
                } else {
                    if (day == 1) {
                        str += "��Ѯ";
                    } else if (day == 11) {
                        str += "��Ѯ";
                    } else if (day == 21) {
                        str += "��Ѯ";
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
            /////////////////////////////////         ��Ӹ��� 2           ////////////////////////////////////////////
            //////////////////////////////////////////////////////////////////////////////////////////////////////////           
            
            doc.newPage();
            this.addSubTitle(doc, "����2 �����������򸲸����");

            float[] widthArray=new float[]{2,1,1,1,1,1,1,1,1,1,1,1,1,2};
            table = new PdfPTable(widthArray);
            tableFont = new Font(songBaseFont, 8);
            table.addCell("");
            for (int i = 0; i < 13; i++) {
                String str = String.format("%02d��", i);
                if (i == 12) {
                    str = String.format("%02d�μ�����", i);
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
                String str = "��ֹ" + Integer.toString(mon) + "��";
                if (i == 0) {
                    if (day < 11) {
                        str += "��Ѯ";
                    } else if (day < 21) {
                        str += "��Ѯ";
                    } else {
                        str += "��Ѯ";
                    }
                } else {
                    if (day == 1) {
                        str += "��Ѯ";
                    } else if (day == 11) {
                        str += "��Ѯ";
                    } else if (day == 21) {
                        str += "��Ѯ";
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
//            para.add("��ѡ���ǣ�");
        para.setFirstLineIndent(20);
        para.setSpacingAfter(7);
        doc.add(para);
    }

}

//����������ɻ�ͼ����ͼ���ܣ��������ߵĸ�����ĺ������꣬�Լ�������������Ӧ������ 

class PdfPic {
    
    public float[] xArray;//����ͼ������ĺ�����
    public String[] xCoorStrArray;//����ͼ������������Ӧ��x������
    public float[] yArray;//����ͼ�������������
    
    public int endTimeNode;//��ʾ�ڼ����ڵ��ǽ���ʱ��ڵ�,-1��ʾ�����ƽ���ʱ����
    


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

//    public enum PDFImageType {
//        PROGRESS, CLOUD
//    }

    public Image getImage() {

        bimg = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
        g2d = (java.awt.Graphics2D) bimg.getGraphics();
        drawBackgroundImage();
//        if (PDFImageType.PROGRESS.equals(type)) {
            drawData();
//        } else {
//            drawCloudData();
//        }
        drawXCoor();
        Image img = null;
        try {
            img = Image.getInstance(bimg, Color.BLACK);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return img;
    }

    private void drawData() {
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
            //������
            pt1.x = 5 + (int) (xf[i] * gap);
            pt1.y = (int) (100 * yArray[i]);
            pt1.y = pt1.y > 100 ? 100 : pt1.y;
            cpt1 = convertPoint(pt1);
            
            pt2.x = 5 + (int) (xf[i + 1] * gap);
            pt2.y = (int) (100 * yArray[i + 1]);
            pt2.y = pt2.y > 100 ? 100 : pt2.y;
            cpt2 = convertPoint(pt2);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
//            g2d.drawRect(cpt1.x, cpt1.y,1,1);

            //��������
            pt1.y = 0;
            cpt1 = convertPoint(pt1);
            pt1.y = xCoorStrArray[i] == null ? 2: 5;
            cpt2 = convertPoint(pt1);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        }
        //�����һ��ʱ��ڵ�Ķ�����
        pt1.x = 5 + (int) (xf[len-1] * gap);
        pt1.y = 0;
        cpt1 = convertPoint(pt1);
        pt1.y = xCoorStrArray[len - 1] == null ? 2 : 5;
        cpt2 = convertPoint(pt1);
        g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        
        //��ǽ�ֹʱ��
        if (endTimeNode >= 0) {
            g2d.setColor(Color.RED);
            pt1.x = 5 + (int) (xf[endTimeNode] * gap);
            pt1.y = 0;
            cpt1 = convertPoint(pt1);
            pt2.x = 5 + (int) (xf[endTimeNode] * gap);
            pt2.y = (int) (100 * yArray[endTimeNode]);
            cpt2 = convertPoint(pt2);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);

            pt1.x = 0;
            pt1.y = (int) (100 * yArray[endTimeNode]);
            cpt1 = convertPoint(pt1);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
            g2d.setColor(Color.BLACK);
        }
    }

//    private void drawCloudData() {
//        int len = xArray.length;
//        float[] xf = new float[len];
//        for (int i = 0; i < len; i++) {
//            xf[i] = xArray[i] - xArray[0];
//        }
//
//        float gap = 90.0f / xf[len - 1];
//        g2d.setColor(Color.BLACK);
//        g2d.setStroke(new BasicStroke(1.0f));
//
//        Point cpt1, cpt2;
//        Point pt1 = new Point(0, 0);
//        Point pt2 = new Point(0, 0);
//        for (int i = 0; i < len - 1; i++) {
//            pt1.x = 5 + (int) (xf[i] * gap);
//            pt1.y = (int) (100 * yArray[i]);
//            pt1.y = pt1.y > 100 ? 100 : pt1.y;
//            cpt1 = convertPoint(pt1);
//            pt2.x = 5 + (int) (xf[i + 1] * gap);
//            if (i != len - 2) {
//                pt2.y = (int) (100 * yArray[i + 1]);
//                pt2.y = pt2.y > 100 ? 100 : pt2.y;
//            }
//            cpt2 = convertPoint(pt2);
//            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
//        }
//    }

    private void drawXCoor() {
        int len = xArray.length;
        float[] xf = new float[len];
        for (int i = 0; i < len; i++) {
            xf[i] = xArray[i] - xArray[0];
        }

        float gap = 90.0f / xf[len - 1];

        g2d.setColor(Color.BLACK);
        g2d.setFont(java.awt.Font.decode("����-plain-12"));

        Point cpt1;
        Point pt1 = new Point(0, 0);
        for (int i = 0; i < len-1 ; i++) {//���һ��ʱ��ڵ㲻��ʾ����
            pt1.x = 5 + (int) (xf[i] * gap)+1;
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
        //����������
        pt1 = new Point(0, 0);
        cpt1 = convertPoint(pt1);
        pt2 = new Point(100, 0);
        cpt2 = convertPoint(pt2);
        g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        pt2 = new Point(0, 100);
        cpt2 = convertPoint(pt2);
        g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);

        //����0%-100%
        g2d.setStroke(new BasicStroke(1.0f));
        g2d.setFont(new java.awt.Font("Times New Roman", java.awt.Font.PLAIN, 10));
        for (int i = 0; i <= 100; i += 10) {
            pt1 = new Point(0, i);
            cpt1 = convertPoint(pt1);
            g2d.drawString(i + "%", 5, 4 + cpt1.y);
            
            pt2 = new Point(1, i);
            cpt2 = convertPoint(pt2);
            g2d.drawLine(cpt1.x, cpt1.y, cpt2.x, cpt2.y);
        }

        return bimg;
    }

    //���߼�����ת��Ϊ��ͼ���ꡣ�߼����꣺xȡֵ��Χ(0,100),yȡֵ��Χ(0,100),
    private static Point convertPoint(Point logicCoor) {
        Point pt = new Point();

        float k = ((float) (imgWidth - rightMargin - leftMargin)) / 100.0f;
        pt.x = (int) (leftMargin + k * logicCoor.x);

        k = (upMargin - imgHeight + downMargin) / 100.0f;
        pt.y = (int) (imgHeight - downMargin + k * logicCoor.y);

        return pt;
    }
}
