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
    public float[] progressArray;       //progressArray与timeNodeArray一一对应，progressArray[i]表示timeNodeArray[i]这一个时间节点所在的旬结束时，达到的覆盖进度，取值范围为:0.00~1.00
    public int[][] coverageTimesArray;  //progressArray与coverageTimesArray一一对应，progressArray[i]表示timeNodeArray[i]这一个时间节点所在的旬结束时，区域的覆盖情况
                                        //第一维的长度等于timeNodeArray的长度，第二维的长度为20，第二维的每一个元素代表覆盖次数。
                                        //如：coverageTimesArray[3][5]表示timeNodeArray[3]对应的时间节点，覆盖次数为5的网格点的个数
    public int[][] cloudArray;          //progressArray与cloudArray一一对应，progressArray[i]表示timeNodeArray[i]这一个时间节点所在的旬 的云量情况
    
    public int[] difficultyDegree;//数据获取的难易程度，数组，长度为4，依次为：易于获取的区域百分比，较易获取的区域百分比，较难获取的区域百分比，难于获取的区域百分比
    
    public byte[] pngArray;//最终结果图片转为数组存储，便于序列化
    
    public  RegionEntry[] regionCoverageInfoArray;


}



