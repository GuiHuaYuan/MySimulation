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
public class RegionEntry {

    public int id;
    public int regionCode;//区划代码
    public String regionName;
    public int totalPoint = 0;//每一个区划的网格点数
    public int groundTaskRegionTotalPoint = 0;//每一个区划的地面任务区域的网格点数
    public float coverageTimes = 0;//每一个行政区划的有效覆盖次数的总和
}
