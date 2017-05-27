// Shawn E. Gano
/**
 * =====================================================================
 * Copyright (C) 2009 Shawn E. Gano
 *
 * This file is part of JSatTrak.
 *
 * JSatTrak is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * JSatTrak is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JSatTrak. If not, see <http://www.gnu.org/licenses/>.
 * =====================================================================
 */
package coverage.util;

/**
 * Various Math functions; many of them for vector and matrix operations for 3
 * dimensions.
 */
public class MathUtils {
    
    public static double rad2deg=180/Math.PI;
    public static double deg2rad=Math.PI/180;

    /**
     * 矩阵求逆 (3x3)
     *
     * @param a 3x3矩阵
     * @return 矩阵 axb
     */
    public static double[][] inv(double[][] a) {
        int l = a.length;
        double[][] imat = new double[l][l];
        double[][] jmat = a;
        for (int i = 0; i < l; i++) {
            imat[i][i] = 1;
        }
        for (int i = 0; i < l; i++) {
            for (int j = 0; j < l; j++) {
                if (i != j) {
                    double t = jmat[j][i] / jmat[i][i];
                    for (int k = 0; k < l; k++) {
                        jmat[j][k] -= jmat[i][k] * t;
                        imat[j][k] -= imat[i][k] * t;
                    }
                }
            }
        }
        for (int i = 0; i < l; i++) {
            if (jmat[i][i] != 1) {
                double t = jmat[i][i];
                for (int j = 0; j < l; j++) {
                    jmat[i][j] = jmat[i][j] / t;
                    imat[i][j] = imat[i][j] / t;
                }
            }
        }
        return imat;
    }

    /**
     * 矩阵乘 ( 3x3 x 3x3 )
     *
     * @param a 3x3矩阵
     * @param b 3x3矩阵
     * @return 矩阵 axb
     */
    public static double[][] mult(double[][] a, double[][] b) {
        double[][] c = new double[3][3];
        for (int i = 0; i < 3; i++) // row
        {
            for (int j = 0; j < 3; j++) { // col
                c[i][j] = 0.0;
                for (int k = 0; k < 3; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    /**
     * 矩阵乘 ( nxn x nx1 )
     *
     * @param a nxn矩阵
     * @param b nx1向量
     * @return 矩阵a x b
     */
    public static double[] mult(double[][] a, double[] b) {
        double[] c = new double[b.length];
        for (int i = 0; i < b.length; i++) {
            c[i] = 0.0;
            for (int k = 0; k < b.length; k++) {
                c[i] += a[i][k] * b[k];
            }
        }
        return c;
    }

    // dot product for 3D vectors
    /**
     * 向量点乘(3x1)
     *
     * @param a 3x1 vector
     * @param b 3x1 vector
     * @return a dot b
     */
    public static double dot(double[] a, double[] b) {
        double c = 0;
        for (int i = 0; i < 3; i++) {
            c += a[i] * b[i];
        }
        return c;
    }

    /**
     * 矩阵转置(3x3)
     *
     * @param a 3x3 matrix
     * @return a^T
     */
    public static double[][] transpose(double[][] a) {
        double[][] c = new double[3][3];
        for (int i = 0; i < 3; i++) {
            for (int k = 0; k < 3; k++) {
                c[i][k] = a[k][i];
            }
        }
        return c;
    }

    /**
     * 向量减
     *
     * @param a vector of length 3
     * @param b vector of length 3
     * @return a-b
     */
    public static double[] sub(double[] a, double[] b) {
        double[] c = new double[3];
        for (int i = 0; i < 3; i++) {
            c[i] = a[i] - b[i];
        }
        return c;
    }

    /**
     * 向量加
     *
     * @param a vector of length 3
     * @param b vector of length 3
     * @return a+b
     */
    public static double[] add(double[] a, double[] b) {
        double[] c = new double[3];
        for (int i = 0; i < 3; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    /**
     * 向量模
     *
     * @param a vector of length 3
     * @return norm(a)
     */
    public static double norm(double[] a) {
        double c = 0.0;
        for (int i = 0; i < a.length; i++) {
            c += a[i] * a[i];
        }
        return Math.sqrt(c);
    }

    /**
     * 向量数乘
     *
     * @param a a vector of length 3
     * @param b scalar
     * @return a * b
     */
    public static double[] scale(double[] a, double b) {
        double[] c = new double[3];
        for (int i = 0; i < 3; i++) {
            c[i] = a[i] * b;
        }
        return c;
    }

    /**
     * 向量差乘
     *
     * @param left vector of length 3
     * @param right vector of length 3
     * @return a cross b
     */
    public static double[] cross(final double[] left, final double[] right) {
        if ((left.length != 3) || (right.length != 3)) {
            System.out.println("ERROR: Invalid dimension in Cross(Vector,Vector)");
        }
        double[] Result = new double[3];
        Result[0] = left[1] * right[2] - left[2] * right[1];
        Result[1] = left[2] * right[0] - left[0] * right[2];
        Result[2] = left[0] * right[1] - left[1] * right[0];
        return Result;
    }

    /**
     * 浮点型取小数部分 (y=x-[x])
     *
     * @param x number
     * @return the fractional part of that number (e.g., for 5.3 would return
     * 0.3)
     */
    public static double Frac(double x) {
        return x - Math.floor(x);
    }

    ;

	/**
	 * x mod y
	 * @param x   value
	 * @param y   value
	 * @return x mod y
	 */
	public static double modulo(double x, double y) {
        return y * Frac(x / y);
    }

	// Elementary rotation matrix about x axis
    /**
     * 向量单位化
     *
     * @param vec any vector n-dimensional
     * @return unit vector (n-dimensional) with norm = 1
     */
    public static double[] unitVector(double[] vec) {
        int n = vec.length;
        double[] unitVect = new double[n];
        double normVec = MathUtils.norm(vec);
        unitVect = MathUtils.scale(vec, 1.0 / normVec);
        return unitVect;
    }

    /**
     * 旋转矩阵(x轴)
     *
     * @param Angle Angle in radians
     * @return Elementary rotation matrix about x axis
     */
    public static double[][] R_x(double Angle) {
        final double C = Math.cos(Angle);
        final double S = Math.sin(Angle);
        double[][] U = new double[3][3];
        U[0][0] = 1.0;
        U[0][1] = 0.0;
        U[0][2] = 0.0;
        U[1][0] = 0.0;
        U[1][1] = +C;
        U[1][2] = +S;
        U[2][0] = 0.0;
        U[2][1] = -S;
        U[2][2] = +C;
        return U;
    }

    /**
     * 旋转矩阵(y轴)
     *
     * @param Angle Angle in radians
     * @return Elementary rotation matrix about y axis
     */
    public static double[][] R_y(double Angle) {
        final double C = Math.cos(Angle);
        final double S = Math.sin(Angle);
        double[][] U = new double[3][3];
        U[0][0] = +C;
        U[0][1] = 0.0;
        U[0][2] = -S;
        U[1][0] = 0.0;
        U[1][1] = 1.0;
        U[1][2] = 0.0;
        U[2][0] = +S;
        U[2][1] = 0.0;
        U[2][2] = +C;
        return U;
    }

    /**
     * 旋转矩阵(z轴)
     *
     * @param Angle Angle in radians
     * @return Elementary rotation matrix about z axis
     */
    public static double[][] R_z(double Angle) {
        final double C = Math.cos(Angle);
        final double S = Math.sin(Angle);
        double[][] U = new double[3][3];
        U[0][0] = +C;
        U[0][1] = +S;
        U[0][2] = 0.0;
        U[1][0] = -S;
        U[1][1] = +C;
        U[1][2] = 0.0;
        U[2][0] = 0.0;
        U[2][1] = 0.0;
        U[2][2] = 1.0;
        return U;
    }

//	/********************** 以下方法仅针对遥感卫星传感器使用 *******************************/
//	
//	/**
//    * 计算直线与[地球椭球]的近端交点
//    * @author MeiHuaibo 2012-04-07
//    * @param lineVector 直线方向矢量 vx,vy,vz
//    * @param lineFrom 直线起点 x0,y0,z0
//    * @param earthMajorRadius 地球长半轴半径（m）
//    * @param earthMinorRadius 地球短半轴半径（m）
//    * @return
//    *   返回最近端交点
//    */
//   public static double[] lineIntersectEllipsoid( 
//   		double[] lineFrom, double[] lineVector, 
//   		double earthMajorRadius, double earthMinorRadius){
//   	
//		// 直线方程： (X-x0)/(x1-x0) = (Y-y0)/(y1-y0) = (Z-z0)/(z1-z0)
//		// 椭球方程： X2/Ra2 + Y2/Ra2 + Z2/Rb2 = 1
//	    double[] vUnit = MathUtils.unitVector(lineVector);
////	    double[] vUnit = lineVector;
////	    double scale = Earth.AVERAGE_RADIUS;
//	    double scale = AstroConst.R_Earth_mean;
//	   
//		double Ra = earthMajorRadius;
//		double Rb = earthMinorRadius;
//		double x0 = lineFrom[0];
//		double y0 = lineFrom[1];
//		double z0 = lineFrom[2];
//		double x1 = x0 + scale * vUnit[0];
//		double y1 = y0 + scale * vUnit[1];
//		double z1 = z0 + scale * vUnit[2];
//
//		double x02 = x0 * x0;
//		double y02 = y0 * y0;
//		double z02 = z0 * z0;
//		double x12 = x1 * x1;
//		double y12 = y1 * y1;
//		double z12 = z1 * z1;
//		double Ra2 = Ra * Ra;
//		double Rb2 = Rb * Rb;
//		double Ra4 = Ra * Ra * Ra * Ra;
//
//		double[] xTemp = new double[2];
//		double[] yTemp = new double[2];
//		double[] zTemp = new double[2];
//
//		/********************* 计算公式来自Matlab程序 ****************************
//		 * %以下程序需在Matlab环境下运行
//		 * S = solve(...
//		 * '(y1-y0)*(x-x0)=(x1-x0)*(y-y0)',...
//		 * '(z1-z0)*(x-x0)=(x1-x0)*(z-z0)',...
//		 * '(x/Ra)^2+(y/Ra)^2+(z/Rb)^2=1',...
//		 * 'x','y','z');
//		 * xTemp = S.x
//		 * yTemp = S.y
//		 * zTemp = S.z
//		 *************************************************************************/
//		
//		xTemp[0] = -(x0
//				* z1
//				- x1
//				* z0
//				- (x0 * (Rb
//						* z0
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb
//						* z1
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						+ Rb2 * x02 * z1 + Rb2 * x12 * z0 + Rb2 * y02 * z1
//						+ Rb2 * y12 * z0 - Rb2 * x0 * x1 * z0 - Rb2 * x0 * x1
//						* z1 - Rb2 * y0 * y1 * z0 - Rb2 * y0 * y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12) + (x1 * (Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) + Rb2 * x02 * z1
//				+ Rb2 * x12 * z0 + Rb2 * y02 * z1 + Rb2 * y12 * z0 - Rb2 * x0
//				* x1 * z0 - Rb2 * x0 * x1 * z1 - Rb2 * y0 * y1 * z0 - Rb2 * y0
//				* y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12))
//				/ (z0 - z1);
//		xTemp[1] = -(x0
//				* z1
//				- x1
//				* z0
//				+ (x0 * (Rb
//						* z0
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb
//						* z1
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb2 * x02 * z1 - Rb2 * x12 * z0 - Rb2 * y02 * z1
//						- Rb2 * y12 * z0 + Rb2 * x0 * x1 * z0 + Rb2 * x0 * x1
//						* z1 + Rb2 * y0 * y1 * z0 + Rb2 * y0 * y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12) - (x1 * (Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) - Rb2 * x02 * z1
//				- Rb2 * x12 * z0 - Rb2 * y02 * z1 - Rb2 * y12 * z0 + Rb2 * x0
//				* x1 * z0 + Rb2 * x0 * x1 * z1 + Rb2 * y0 * y1 * z0 + Rb2 * y0
//				* y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12))
//				/ (z0 - z1);
//
//		yTemp[0] = -(y0
//				* z1
//				- y1
//				* z0
//				- (y0 * (Rb
//						* z0
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb
//						* z1
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						+ Rb2 * x02 * z1 + Rb2 * x12 * z0 + Rb2 * y02 * z1
//						+ Rb2 * y12 * z0 - Rb2 * x0 * x1 * z0 - Rb2 * x0 * x1
//						* z1 - Rb2 * y0 * y1 * z0 - Rb2 * y0 * y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12) + (y1 * (Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) + Rb2 * x02 * z1
//				+ Rb2 * x12 * z0 + Rb2 * y02 * z1 + Rb2 * y12 * z0 - Rb2 * x0
//				* x1 * z0 - Rb2 * x0 * x1 * z1 - Rb2 * y0 * y1 * z0 - Rb2 * y0
//				* y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12))
//				/ (z0 - z1);
//
//		yTemp[1] = -(y0
//				* z1
//				- y1
//				* z0
//				+ (y0 * (Rb
//						* z0
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb
//						* z1
//						* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12
//								+ Ra2 * Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1
//								+ Ra2 * Rb2 * x12 + Ra2 * Rb2 * y02 - 2 * Ra2
//								* Rb2 * y0 * y1 + Ra2 * Rb2 * y12 - Ra2 * x02
//								* z12 + 2 * Ra2 * x0 * x1 * z0 * z1 - Ra2 * x12
//								* z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//								* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12
//								+ 2 * Rb2 * x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//						- Rb2 * x02 * z1 - Rb2 * x12 * z0 - Rb2 * y02 * z1
//						- Rb2 * y12 * z0 + Rb2 * x0 * x1 * z0 + Rb2 * x0 * x1
//						* z1 + Rb2 * y0 * y1 * z0 + Rb2 * y0 * y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12) - (y1 * (Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) - Rb2 * x02 * z1
//				- Rb2 * x12 * z0 - Rb2 * y02 * z1 - Rb2 * y12 * z0 + Rb2 * x0
//				* x1 * z0 + Rb2 * x0 * x1 * z1 + Rb2 * y0 * y1 * z0 + Rb2 * y0
//				* y1 * z1))
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12))
//				/ (z0 - z1);
//
//		zTemp[0] = (Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) + Rb2 * x02 * z1
//				+ Rb2 * x12 * z0 + Rb2 * y02 * z1 + Rb2 * y12 * z0 - Rb2 * x0
//				* x1 * z0 - Rb2 * x0 * x1 * z1 - Rb2 * y0 * y1 * z0 - Rb2 * y0
//				* y1 * z1)
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12);
//
//		zTemp[1] = -(Rb
//				* z0
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02)
//				- Rb
//				* z1
//				* Math.sqrt(Ra4 * z02 - 2 * Ra4 * z0 * z1 + Ra4 * z12 + Ra2
//						* Rb2 * x02 - 2 * Ra2 * Rb2 * x0 * x1 + Ra2 * Rb2 * x12
//						+ Ra2 * Rb2 * y02 - 2 * Ra2 * Rb2 * y0 * y1 + Ra2 * Rb2
//						* y12 - Ra2 * x02 * z12 + 2 * Ra2 * x0 * x1 * z0 * z1
//						- Ra2 * x12 * z02 - Ra2 * y02 * z12 + 2 * Ra2 * y0 * y1
//						* z0 * z1 - Ra2 * y12 * z02 - Rb2 * x02 * y12 + 2 * Rb2
//						* x0 * x1 * y0 * y1 - Rb2 * x12 * y02) - Rb2 * x02 * z1
//				- Rb2 * x12 * z0 - Rb2 * y02 * z1 - Rb2 * y12 * z0 + Rb2 * x0
//				* x1 * z0 + Rb2 * x0 * x1 * z1 + Rb2 * y0 * y1 * z0 + Rb2 * y0
//				* y1 * z1)
//				/ (Ra2 * z02 - 2 * Ra2 * z0 * z1 + Ra2 * z12 + Rb2 * x02 - 2
//						* Rb2 * x0 * x1 + Rb2 * x12 + Rb2 * y02 - 2 * Rb2 * y0
//						* y1 + Rb2 * y12);
//
//		double[] distance2Intersect = new double[2];
//		
//		for(int i=0; i<2; i++){
//			double[] intersect = new double[]{ xTemp[i], yTemp[i], zTemp[i]};
////			double radiusTemp = MathUtils.norm( intersect );
//			distance2Intersect[i] = MathUtils.norm( MathUtils.sub( lineFrom, intersect) );
////			System.out.println("radius:  " + radiusTemp );
////			System.out.println("distance:" + distance2Intersect[i] );
//		}
//		int index = distance2Intersect[0]<=distance2Intersect[1] ? 0 : 1;
//		double[] result = new double[]{xTemp[index], yTemp[index], zTemp[index]};
//   	return result ;
//   } 
}
