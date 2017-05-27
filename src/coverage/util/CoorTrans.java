/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package coverage.util;

/**
 *
 * @author PC TME杞琂2000
 */


//本类用于计算卫星轨道时，进行各种坐标系的转换
public class CoorTrans {

    private static double tempJD;
    private static double[] tempNutAngles;

    //TEME坐标系（即TLE根数使用的坐标系）转J2000惯性系转换矩阵
    //输入：Tle根数历元时刻的儒略日时间
    //输出：坐标转换矩阵:3X3
    public static double[][] TEME_J2000I(double jD) {
        double Rad = Math.PI / 180;
        double T = (jD - 2451545.0) / 36525;//自2000年1月1.5日（J2000）历元开始的儒略世纪数
        double dpsi = 0;//黄经章动
        double deps = 0;//交角章动
        double eps = 0;//黄赤交角
        eps = Rad * (23.43929111 - (46.8150 + (0.00059 - 0.001813 * T) * T) * T / 3600);
        double[] an = NutAngles(jD);
        dpsi = an[0];
        deps = an[1];
        double[][] Rz = MathUtils.R_z(dpsi * Math.cos(eps));
        double[][] N = NutMatrix(jD);
        double[][] P = PrecMatrix(jD);
        double[][] W = MathUtils.inv(MathUtils.mult(MathUtils.mult(MathUtils.inv(Rz), N), P));
        return W;
    }

    //J2000至WGS84地固系转换，暂不考虑极移影响
    public static double[][] J2000I_J2000C(double JD,double[] r,double[] v) {
        double gast = GAST(JD);
        double[][] Nut = NutMatrix(JD);
        double[][] Prec = PrecMatrix(JD);
        double[][] T1 = MathUtils.mult(Nut, Prec);
        double[] r1 = MathUtils.mult(T1, r);
        double[] v1 = MathUtils.mult(T1, v);
        double[] res = Gps2Norad(gast, r1[0], r1[1], r1[2], v1[0], v1[1], v1[2]);
        return new double[][]{{res[0], res[1], res[2]}, {res[3], res[4], res[5]}};
    }

    //地固系坐标转大地坐标
    public static double[] J2000C_LonLat(double[] r) {
        double R_equ = 6378.137e3;//赤道半径
        double f = 1.0 / 298.257223563;//地球扁率
        double e2 = f * (2 - f);
        double X = r[0];
        double Y = r[1];
        double Z = r[2];
        double rho2 = X * X + Y * Y;
        double dZ = 0;
        double ZdZ = 0;
        double sinPhi = 0;
        double N = 0;
        double dZ2 = e2 * Z;

        do {
            dZ = dZ2;
            ZdZ = Z + dZ;
            sinPhi = ZdZ / Math.sqrt(rho2 + ZdZ * ZdZ);
            N = R_equ / Math.sqrt(1 - e2 * sinPhi * sinPhi);
            dZ2 = N * e2 * sinPhi;
        } while (Math.abs(dZ2 - dZ) > 0.000001);//限值选择

        double[] g = new double[3];
        g[0] =MathUtils.rad2deg* Math.atan2(Y, X);//大地经度
        g[1] = MathUtils.rad2deg* Math.atan2(ZdZ, Math.sqrt(rho2));//大地纬度
        g[2] = Math.sqrt(rho2 + ZdZ * ZdZ) - N;//大地高程	
        return g;
    }

    public static double[] Gps2Norad(double eangle, double Gpsx, double Gpsy, double Gpsz, double Gpsvx, double Gpsvy, double Gpsvz) {

        double x = 0, y = 0, vx = 0, vy = 0;
        double ARG = eangle;//地球旋转角
        x = Gpsx * Math.cos(ARG) + Gpsy * Math.sin(ARG);
        y = -Gpsx * Math.sin(ARG) + Gpsy * Math.cos(ARG);
        Gpsx = x;
        Gpsy = y;

        vx = Gpsvx * Math.cos(ARG) + Gpsvy * Math.sin(ARG);
        vy = -Gpsvx * Math.sin(ARG) + Gpsvy * Math.cos(ARG);
        //
        double radius = 0;
        radius = Math.sqrt(Gpsx * Gpsx + Gpsy * Gpsy);

        double earth_v = 0;
        earth_v = radius * 0.00007292115;//earth_rot，地球旋转速率

        double angle = 0;
        // angle=atan2(fabs(Gpsx),fabs(Gpsy));
        angle = Math.atan(Math.abs(Gpsx) / Math.abs(Gpsy));
        double evx = 0, evy = 0;  //earth_velocity

        evx = earth_v * Math.cos(angle);
        evy = earth_v * Math.sin(angle);
        double _zero = 0.000000000001;
        if ((x > _zero) && (y > _zero)) {
            Gpsvx = vx + evx;
            Gpsvy = vy - evy;
        } else if ((x < _zero) && (y > _zero)) {
            Gpsvx = vx + evx;
            Gpsvy = vy + evy;
        } else if ((x < _zero) && (y < _zero)) {
            Gpsvx = vx - evx;
            Gpsvy = vy + evy;
        } else {
            Gpsvx = vx - evx;
            Gpsvy = vy - evy;
        }
        double[] pv = {Gpsx, Gpsy, Gpsz, Gpsvx, Gpsvy, Gpsvz};
        return pv;

    }

    //恒星时矩阵，即自转矩阵
    public static double[][] GHAMatrix(double JD) {
        double T = (JD - 2451545.0) / 36525;//自2000年1月1.5日（J2000）历元开始的儒略世纪数
        double Rad = Math.PI / 180;//一度对应的弧度
        //double radpm=Math.PI/(180*60);//一分对应的弧度
        //double radps=Math.PI/(180*3600);//一秒对应的弧度
        double eps = Rad * (23.43929111 - (46.8150 + (0.00059 - 0.001813 * T) * T) * T / 3600);
        double[] an = NutAngles(JD);
        double dpsi = an[0];
        double deps = an[1];
        //格林尼治时角
        double GAST = (280.4606 + 360.9856473 * (JD - 2451545.0) + 0.000387933 * T * T - T * T * T / 38710000) * Rad + dpsi * Math.cos(eps + deps);
        return MathUtils.R_z(GAST);
    }


    /*------------------------------------------------------------------------------------------*/
    //章动矩阵
    public static double[][] NutMatrix(double JD) {
        double Rad = Math.PI / 180;
        double T = (JD - 2451545.0) / 36525;//自2000年1月1.5日（J2000）历元开始的儒略世纪数
        double dpsi = 0;//黄经章动
        double deps = 0;//交角章动
        double eps = 0;//黄赤交角
        eps = Rad * (23.43929111 - (46.8150 + (0.00059 - 0.001813 * T) * T) * T / 3600);
        double[] an = NutAngles(JD);
        dpsi = an[0];
        deps = an[1];
        double[][] Rx1 = MathUtils.R_x(-eps - deps);
        double[][] Rz = MathUtils.R_z(-dpsi);
        double[][] Rx2 = MathUtils.R_x(eps);
        return MathUtils.mult(MathUtils.mult(Rx1, Rz), Rx2);
    }

    //岁差矩阵
    public static double[][] PrecMatrix(double JD) {
        double T = (JD - 2451545.0) / 36525;//自2000年1月1.5日（J2000）历元开始的儒略世纪数
        double radps = Math.PI / (180 * 3600);//一秒对应的弧度
        double zeta = ((0.017998 * T + 0.30188) * T + 2306.2181) * T * radps;
        double theta = ((0.041833 * T + 0.42665) * T + 2004.3109) * T * radps;
        double z = ((0.018203 * T + 1.09468) * T + 2306.2181) * T * radps;
        double[][] Rz1 = MathUtils.R_z(-z);
        double[][] Ry = MathUtils.R_y(theta);
        double[][] Rz2 = MathUtils.R_z(-zeta);
        return MathUtils.mult(MathUtils.mult(Rz1, Ry), Rz2);
    }

    //计算黄经章动和交角章动
    private static double[] NutAngles(double JD) {
        if (Math.abs(JD - tempJD) < 10) {
            //return null;
            return tempNutAngles;
        } else {
            // TODO Auto-generated method stub
            double T = (JD - 2451545.0) / 36525;
            double T2 = T * T;
            double T3 = T2 * T;
            double rev = 360.0 * 3600.0;  // arcsec/revolution
            double radps = Math.PI / (180 * 3600);//一秒对应的弧度
            final int N_coeff = 106;

            double l, lp, F, D, Om;
            double arg;

            l = MathUtils.modulo(485866.733 + (1325.0 * rev + 715922.633) * T
                    + 31.310 * T2 + 0.064 * T3, rev);
            lp = MathUtils.modulo(1287099.804 + (99.0 * rev + 1292581.224) * T
                    - 0.577 * T2 - 0.012 * T3, rev);
            F = MathUtils.modulo(335778.877 + (1342.0 * rev + 295263.137) * T
                    - 13.257 * T2 + 0.011 * T3, rev);
            D = MathUtils.modulo(1072261.307 + (1236.0 * rev + 1105601.328) * T
                    - 6.891 * T2 + 0.019 * T3, rev);
            Om = MathUtils.modulo(450160.280 - (5.0 * rev + 482890.539) * T
                    + 7.455 * T2 + 0.008 * T3, rev);

            double[] an = new double[2];
            for (int i = 0; i < N_coeff; i++) {
                arg = (C[i][0] * l + C[i][1] * lp + C[i][2] * F + C[i][3] * D + C[i][4] * Om) * radps;
                an[0] += (C[i][5] + C[i][6] * T) * Math.sin(arg);
                an[1] += (C[i][7] + C[i][8] * T) * Math.cos(arg);
            };

            an[0] = 1.0E-5 * an[0] * radps;
            an[1] = 1.0E-5 * an[1] * radps;
            tempJD = JD;
            tempNutAngles = new double[]{an[0], an[1]};
            return an;
        }
    }

    final static double[][] C
            = {//
                // l  l' F  D Om    dpsi    *T     deps     *T       #
                //
                {0, 0, 0, 0, 1, -1719960, -1742, 920250, 89}, //   1
                {0, 0, 0, 0, 2, 20620, 2, -8950, 5}, //   2
                {-2, 0, 2, 0, 1, 460, 0, -240, 0}, //   3
                {2, 0, -2, 0, 0, 110, 0, 0, 0}, //   4
                {-2, 0, 2, 0, 2, -30, 0, 10, 0}, //   5
                {1, -1, 0, -1, 0, -30, 0, 0, 0}, //   6
                {0, -2, 2, -2, 1, -20, 0, 10, 0}, //   7
                {2, 0, -2, 0, 1, 10, 0, 0, 0}, //   8
                {0, 0, 2, -2, 2, -131870, -16, 57360, -31}, //   9
                {0, 1, 0, 0, 0, 14260, -34, 540, -1}, //  10
                {0, 1, 2, -2, 2, -5170, 12, 2240, -6}, //  11
                {0, -1, 2, -2, 2, 2170, -5, -950, 3}, //  12
                {0, 0, 2, -2, 1, 1290, 1, -700, 0}, //  13
                {2, 0, 0, -2, 0, 480, 0, 10, 0}, //  14
                {0, 0, 2, -2, 0, -220, 0, 0, 0}, //  15
                {0, 2, 0, 0, 0, 170, -1, 0, 0}, //  16
                {0, 1, 0, 0, 1, -150, 0, 90, 0}, //  17
                {0, 2, 2, -2, 2, -160, 1, 70, 0}, //  18
                {0, -1, 0, 0, 1, -120, 0, 60, 0}, //  19
                {-2, 0, 0, 2, 1, -60, 0, 30, 0}, //  20
                {0, -1, 2, -2, 1, -50, 0, 30, 0}, //  21
                {2, 0, 0, -2, 1, 40, 0, -20, 0}, //  22
                {0, 1, 2, -2, 1, 40, 0, -20, 0}, //  23
                {1, 0, 0, -1, 0, -40, 0, 0, 0}, //  24
                {2, 1, 0, -2, 0, 10, 0, 0, 0}, //  25
                {0, 0, -2, 2, 1, 10, 0, 0, 0}, //  26
                {0, 1, -2, 2, 0, -10, 0, 0, 0}, //  27
                {0, 1, 0, 0, 2, 10, 0, 0, 0}, //  28
                {-1, 0, 0, 1, 1, 10, 0, 0, 0}, //  29
                {0, 1, 2, -2, 0, -10, 0, 0, 0}, //  30
                {0, 0, 2, 0, 2, -22740, -2, 9770, -5}, //  31
                {1, 0, 0, 0, 0, 7120, 1, -70, 0}, //  32
                {0, 0, 2, 0, 1, -3860, -4, 2000, 0}, //  33
                {1, 0, 2, 0, 2, -3010, 0, 1290, -1}, //  34
                {1, 0, 0, -2, 0, -1580, 0, -10, 0}, //  35
                {-1, 0, 2, 0, 2, 1230, 0, -530, 0}, //  36
                {0, 0, 0, 2, 0, 630, 0, -20, 0}, //  37
                {1, 0, 0, 0, 1, 630, 1, -330, 0}, //  38
                {-1, 0, 0, 0, 1, -580, -1, 320, 0}, //  39
                {-1, 0, 2, 2, 2, -590, 0, 260, 0}, //  40
                {1, 0, 2, 0, 1, -510, 0, 270, 0}, //  41
                {0, 0, 2, 2, 2, -380, 0, 160, 0}, //  42
                {2, 0, 0, 0, 0, 290, 0, -10, 0}, //  43
                {1, 0, 2, -2, 2, 290, 0, -120, 0}, //  44
                {2, 0, 2, 0, 2, -310, 0, 130, 0}, //  45
                {0, 0, 2, 0, 0, 260, 0, -10, 0}, //  46
                {-1, 0, 2, 0, 1, 210, 0, -100, 0}, //  47
                {-1, 0, 0, 2, 1, 160, 0, -80, 0}, //  48
                {1, 0, 0, -2, 1, -130, 0, 70, 0}, //  49
                {-1, 0, 2, 2, 1, -100, 0, 50, 0}, //  50
                {1, 1, 0, -2, 0, -70, 0, 0, 0}, //  51
                {0, 1, 2, 0, 2, 70, 0, -30, 0}, //  52
                {0, -1, 2, 0, 2, -70, 0, 30, 0}, //  53
                {1, 0, 2, 2, 2, -80, 0, 30, 0}, //  54
                {1, 0, 0, 2, 0, 60, 0, 0, 0}, //  55
                {2, 0, 2, -2, 2, 60, 0, -30, 0}, //  56
                {0, 0, 0, 2, 1, -60, 0, 30, 0}, //  57
                {0, 0, 2, 2, 1, -70, 0, 30, 0}, //  58
                {1, 0, 2, -2, 1, 60, 0, -30, 0}, //  59
                {0, 0, 0, -2, 1, -50, 0, 30, 0}, //  60
                {1, -1, 0, 0, 0, 50, 0, 0, 0}, //  61
                {2, 0, 2, 0, 1, -50, 0, 30, 0}, //  62
                {0, 1, 0, -2, 0, -40, 0, 0, 0}, //  63
                {1, 0, -2, 0, 0, 40, 0, 0, 0}, //  64
                {0, 0, 0, 1, 0, -40, 0, 0, 0}, //  65
                {1, 1, 0, 0, 0, -30, 0, 0, 0}, //  66
                {1, 0, 2, 0, 0, 30, 0, 0, 0}, //  67
                {1, -1, 2, 0, 2, -30, 0, 10, 0}, //  68
                {-1, -1, 2, 2, 2, -30, 0, 10, 0}, //  69
                {-2, 0, 0, 0, 1, -20, 0, 10, 0}, //  70
                {3, 0, 2, 0, 2, -30, 0, 10, 0}, //  71
                {0, -1, 2, 2, 2, -30, 0, 10, 0}, //  72
                {1, 1, 2, 0, 2, 20, 0, -10, 0}, //  73
                {-1, 0, 2, -2, 1, -20, 0, 10, 0}, //  74
                {2, 0, 0, 0, 1, 20, 0, -10, 0}, //  75
                {1, 0, 0, 0, 2, -20, 0, 10, 0}, //  76
                {3, 0, 0, 0, 0, 20, 0, 0, 0}, //  77
                {0, 0, 2, 1, 2, 20, 0, -10, 0}, //  78
                {-1, 0, 0, 0, 2, 10, 0, -10, 0}, //  79
                {1, 0, 0, -4, 0, -10, 0, 0, 0}, //  80
                {-2, 0, 2, 2, 2, 10, 0, -10, 0}, //  81
                {-1, 0, 2, 4, 2, -20, 0, 10, 0}, //  82
                {2, 0, 0, -4, 0, -10, 0, 0, 0}, //  83
                {1, 1, 2, -2, 2, 10, 0, -10, 0}, //  84
                {1, 0, 2, 2, 1, -10, 0, 10, 0}, //  85
                {-2, 0, 2, 4, 2, -10, 0, 10, 0}, //  86
                {-1, 0, 4, 0, 2, 10, 0, 0, 0}, //  87
                {1, -1, 0, -2, 0, 10, 0, 0, 0}, //  88
                {2, 0, 2, -2, 1, 10, 0, -10, 0}, //  89
                {2, 0, 2, 2, 2, -10, 0, 0, 0}, //  90
                {1, 0, 0, 2, 1, -10, 0, 0, 0}, //  91
                {0, 0, 4, -2, 2, 10, 0, 0, 0}, //  92
                {3, 0, 2, -2, 2, 10, 0, 0, 0}, //  93
                {1, 0, 2, -2, 0, -10, 0, 0, 0}, //  94
                {0, 1, 2, 0, 1, 10, 0, 0, 0}, //  95
                {-1, -1, 0, 2, 1, 10, 0, 0, 0}, //  96
                {0, 0, -2, 0, 1, -10, 0, 0, 0}, //  97
                {0, 0, 2, -1, 2, -10, 0, 0, 0}, //  98
                {0, 1, 0, 2, 0, -10, 0, 0, 0}, //  99
                {1, 0, -2, -2, 0, -10, 0, 0, 0}, // 100
                {0, -1, 2, 0, 1, -10, 0, 0, 0}, // 101
                {1, 1, 0, -2, 1, -10, 0, 0, 0}, // 102
                {1, 0, -2, 2, 0, -10, 0, 0, 0}, // 103
                {2, 0, 0, 2, 0, 10, 0, 0, 0}, // 104
                {0, 0, 2, 4, 2, -10, 0, 0, 0}, // 105
                {0, 1, 0, 1, 0, 10, 0, 0, 0} // 106
            };//end C参数阵

    public static double GAST(double JD) {
        double T = (JD - 2451545.0) / 36525;//自2000年1月1.5日（J2000）历元开始的儒略世纪数
        double Rad = Math.PI / 180;//一度对应的弧度
        //double radpm=Math.PI/(180*60);//一分对应的弧度
        //double radps=Math.PI/(180*3600);//一秒对应的弧度
        double eps = Rad * (23.43929111 - (46.8150 + (0.00059 - 0.001813 * T) * T) * T / 3600);
        double[] an = NutAngles(JD);
        double dpsi = an[0];
        double deps = an[1];
        //格林尼治时角
        double GAST = (280.4606 + 360.9856473 * (JD - 2451545.0) + 0.000387933 * T * T - T * T * T / 38710000) * Rad + dpsi * Math.cos(eps + deps);
        return GAST - Math.floor(GAST / (2 * Math.PI)) * 2 * Math.PI;
    }

    public static double[] getScanLatLon(double[] r_J2000C, double[] v_J2000C, double lrAngle, double fbAngle) {
        lrAngle *= MathUtils.deg2rad;
        fbAngle *= MathUtils.deg2rad;
        double[] M = MathUtils.cross(r_J2000C, v_J2000C);
        double[] L = MathUtils.add(MathUtils.scale(MathUtils.unitVector(M), Math.sin(lrAngle)), MathUtils.scale(MathUtils.unitVector(r_J2000C), -Math.cos(lrAngle)));
        double RL = AstroConst.R_Earth_major;
        double RS = AstroConst.R_Earth_minor;
        double a, b, c, la, lb, lc, x0, y0, z0, delta, k1, k2;
        la = L[0];
        lb = L[1];
        lc = L[2];
        x0 = r_J2000C[0];
        y0 = r_J2000C[1];
        z0 = r_J2000C[2];
        a = RS * RS * (la * la + lb * lb) + RL * RL * lc * lc;
        b = 2 * RS * RS * (la * x0 + lb * y0) + 2 * RL * RL * lc * z0;
        c = RS * RS * (x0 * x0 + y0 * y0) + RL * RL * z0 * z0 - RL * RL * RS * RS;
        delta = b * b - 4 * a * c;
        if (delta <= 0) {
            System.out.println("intersection out of earth");
            return null;
        }
        k1 = (-b + Math.sqrt(delta)) / 2 / a;
        k2 = (-b - Math.sqrt(delta)) / 2 / a;
        double[] r = MathUtils.norm(MathUtils.scale(L, k1)) < MathUtils.norm(MathUtils.scale(L, k2)) ? MathUtils.add(MathUtils.scale(L, k1), r_J2000C) : MathUtils.add(MathUtils.scale(L, k2), r_J2000C);
        return J2000C_LonLat(r);
    }

}
