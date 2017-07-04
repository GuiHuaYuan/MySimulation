/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coverage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import coverage.iostruct.RegionEntry;

/**
 *
 * @author ZZL
 */


//���������Ϊ ������Ч���Ǵ��������������ȸ��ݵ��������������漰�������������������ж�Ӧ�ð����ĸ��������������������������������������ĸ�����������������������������룬���ƣ���������������������������������������Ч���Ǵ�����
//1.�ж�Ӧ�ð����ĸ�������������ʡ���أ�����ķ������£��ȿ��������������漰�˶��ٸ��أ�����ٸ����ཻ�������С��maxXianAmount��������Ϊ�����λ��������������жϵ������������漰�˶��ٸ��У����С��maxShiAmount��������Ϊ�����λ���������������ʡΪ�����λ�������
//�����ж�ʱ�����Ȱѵ����������������������������������뵽һ��list���棬����hashsetȥ�����е��ظ��������루��listת��Ϊhashset����ȥ���ظ�����ж�hashset�����Ԫ�ظ����Ƿ�С��maxXianAmount�����С�ڣ������ȷ������Ϊ������λ���������regionUnit��ֵΪRegionUnit.XIAN,�Ҵ�ʱ��hashset������������������Ҫ�������Ŀ�����򣬶�hashset����������������100����ȡ���������ǰ4λ���õ��е��������룬����list��hashset��ת���������������������Եõ�������һ��������λΪ�����λregionUnit���Լ������λ�����������б�codeList��
//2.ͨ����1�� �õ������λregionUnit�����������б�codeList�� RegionEntry��һ����Ŀ�࣬���ڴ�������������������������������ĵ��������������������������򹲰����ĵ������������������������������Ч���Ǵ����ܺ͡�Ȼ�����EffectiveGrid��XianCodeGrid�����regionEntryArray
//
public class Point2Xian {
    public int maxXianAmount = 20;//�����������ֵ �������λ������С�ڸ�ֵ��������Ϊ�����λ
    public int maxShiAmount = 20;//�����������ֵ �������λ������С�ڸ�ֵ��������Ϊ�����λ
    public float[][] EffectiveGrid = new float[800][1400];//�ӹ滮�㷨�еõ�����Ч���Ǵ�������-10�����ڵ����������򣬷��������Ч���Ǵ���
    private int[][] XianCodeGrid = new int[800][1400];//�������ÿһ��Ԫ�ش���ţ�����������Ӧ���ص���������
    
    public Point2Xian() {
    }
    public RegionEntry[] GetRegionCoverageResult() {

        ReadDataBase();//��point2xian���ݿ�

        //���������������漰���صĴ������codeList��
        ArrayList<Integer> codeList = new ArrayList<Integer>();
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                if (EffectiveGrid[i][j] > -9) {
                    codeList.add(XianCodeGrid[i][j]);
                }
            }
        }
        HashSet<Integer> codeHashSet;
        RegionUnit regionUnit;
        codeHashSet = new HashSet<Integer>(codeList);//listתhashset��ȥ���ظ���
        //�ж����漰���ص������Ƿ����maxXianAmount
        if (codeHashSet.size() > maxXianAmount) {
            codeList.clear();
            for (int n : codeHashSet) {
                codeList.add(n / 100);//ȡ�����������ǰ4λ��ת��Ϊ�е���������
            }
            codeHashSet = new HashSet<Integer>(codeList);
            if (codeHashSet.size() > maxShiAmount) {
                codeList.clear();
                for (int n : codeHashSet) {
                    codeList.add(n / 100);//ȡ�����������ǰ4λ��ת��Ϊʡ����������
                }
                codeHashSet = new HashSet<Integer>(codeList);
                codeList= new ArrayList<Integer>(codeHashSet);
                regionUnit = RegionUnit.SHENG;
            } else {
                codeList = new ArrayList<Integer>(codeHashSet);
                regionUnit = RegionUnit.SHI;
            }
        } else {
            codeList = new ArrayList<Integer>(codeHashSet);
            regionUnit = RegionUnit.XIAN;
        }
        
        //���ˣ�regionUnit��������λ��codeList������������б�

        Collections.sort(codeList);
        int codeListSize = codeList.size();
        RegionEntry[] regionEntryArray = new RegionEntry[codeListSize];
        for (int i = 0; i < codeListSize; i++) {
            regionEntryArray[i]=new RegionEntry();
            regionEntryArray[i].regionCode =codeList.get(i);
        }
        for (int i = 0; i < 800; i++) {
            for (int j = 0; j < 1400; j++) {
                int xianCode=XianCodeGrid[i][j];
                switch (regionUnit) {
                    case SHENG:
                        xianCode/=10000;
                        break;
                    case SHI:
                        xianCode /= 100;
                        break;
                    case XIAN:
                        xianCode /= 1;
                        break;
                }
                for (int k = 0; k < codeListSize; k++) {
                    if (regionEntryArray[k].regionCode == xianCode) {
                        //�����i�е�j�е��������������� ���� ����Ŀ���������� ˵���������λ�ڸ���Ŀ�����������Ӧ�������У��� �������������������1��Ŀ������ͳ��ÿһ�����������������
                        regionEntryArray[k].totalPoint++;
                        if (EffectiveGrid[i][j] > -9) {
                            //�����i�е�j�е�EffectiveGrid������-10����λ�ڵ������������ڲ�����
                            regionEntryArray[k].groundTaskRegionTotalPoint++;
                            //regionEntryArray[k].coverageTimes += EffectiveGrid[i][j];
                            regionEntryArray[k].coverageTimes += EffectiveGrid[i][j] >= 1.0f ? 1.0f : 0;
                        }
                        break;
                    }
                }
            }
        }
        
        //�����ݿ��ж�ȡÿһ����Ŀ�����������Ӧ�������������
        for (int k = 0; k < codeListSize; k++) {
            regionEntryArray[k].regionName=ReadFromSSXDataBase(regionEntryArray[k].regionCode,regionUnit);
        }
        return regionEntryArray;
    }

    //��point2xian���ݿ⣬�����ÿһ����¼Ϊ����������Ӧ���صĴ���
    //�ú���ʵ�ָ������ݿ�ļ�¼�����ص������������XianCodeGrid
    private void ReadDataBase() {
        Connection c = null;
        Statement stmt;
        String sql;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            stmt = c.createStatement();
            sql = "select * from mypoint2xian";
            ResultSet rst = stmt.executeQuery(sql);
            while (rst.next()) {
                int id = rst.getInt(1);
                int i = rst.getInt(2);
                int j = rst.getInt(3);
                int code = rst.getInt(4);
                XianCodeGrid[i][j] = code;
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //�����ݿ��ж�ȡ���������Ӧ��������
    private String ReadFromSSXDataBase(int code, RegionUnit ru) {
        String regionString=null;
        Connection c = null;
        Statement stmt;
        String sql;
        ResultSet rst;
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
            stmt = c.createStatement();
            switch (ru) {
                case SHENG:
                    sql = "select * from myshp_sheng where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(2);
                        break;
                    }
                    break;
                case SHI:
                    sql = "select * from myshp_shi where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(3)+rst.getString(2);
                        break;
                    }
                    break;
                case XIAN:
                    sql = "select * from myshp_xian where code=" + code;
                    rst = stmt.executeQuery(sql);
                    while (rst.next()) {
                        regionString = rst.getString(5)+rst.getString(4)+rst.getString(3);
                        break;
                    }
                    break;
            }
            stmt.close();
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return regionString;
    }

    private enum RegionUnit {
        SHENG, SHI, XIAN;
    }
}
