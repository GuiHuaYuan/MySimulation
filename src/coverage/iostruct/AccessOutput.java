/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package coverage.iostruct;

import java.util.TreeMap;

/**
 *
 * @author ZZL
 */
public class AccessOutput {
    
    public boolean isSuccess;
    public int totalGrid;
    public String[] timeNodeArray;
    public float[] progressArray;       //progressArray��timeNodeArrayһһ��Ӧ��progressArray[i]��ʾtimeNodeArray[i]��һ��ʱ��ڵ����ڵ�Ѯ����ʱ���ﵽ�ĸ��ǽ��ȣ�ȡֵ��ΧΪ:0.00~1.00
    public int[][] coverageTimesArray;  //progressArray��coverageTimesArrayһһ��Ӧ��progressArray[i]��ʾtimeNodeArray[i]��һ��ʱ��ڵ����ڵ�Ѯ����ʱ������ĸ������
                                        //��һά�ĳ��ȵ���timeNodeArray�ĳ��ȣ��ڶ�ά�ĳ���Ϊ20���ڶ�ά��ÿһ��Ԫ�ش����Ǵ�����
                                        //�磺coverageTimesArray[3][5]��ʾtimeNodeArray[3]��Ӧ��ʱ��ڵ㣬���Ǵ���Ϊ5�������ĸ���
    public int[][] cloudArray;          //progressArray��cloudArrayһһ��Ӧ��progressArray[i]��ʾtimeNodeArray[i]��һ��ʱ��ڵ����ڵ�Ѯ ���������
    
    public int[] difficultyDegree;//���ݻ�ȡ�����׳̶ȣ����飬����Ϊ4������Ϊ�����ڻ�ȡ������ٷֱȣ����׻�ȡ������ٷֱȣ����ѻ�ȡ������ٷֱȣ����ڻ�ȡ������ٷֱ�
    
    public byte[] pngArray;//���ս��ͼƬתΪ����洢���������л�
    
    public  RegionEntry[] regionCoverageInfoArray;


}



