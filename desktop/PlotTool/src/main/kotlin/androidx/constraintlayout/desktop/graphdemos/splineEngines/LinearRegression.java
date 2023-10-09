package androidx.constraintlayout.desktop.graphdemos.splineEngines;

import androidx.constraintlayout.desktop.graphdemos.utils.Utils;

import java.text.DecimalFormat;

/**
 * Fit curve to polynomial using Linear Regression
 *
 */
public class LinearRegression {
	private static double sEPS = 1E-14;

	public interface Func {
		double f(double x);
	}
	/**
	 *
	 * @param dataX x values
	 * @param dataY y values
	 * @param funcs a collection of function f[0] must return 0
	 * @return polynomial coefficients [0] = the constant;
	 */
	public static double[] functionFit(double[] dataX, double[] dataY, Func[]funcs) {
		int w = funcs.length;
		int h = funcs.length;
		double[][] m = new double[h][w + 1];
		for (int y = 0; y < h; y++) {
			for (int i = 0; i < dataX.length; i++) {
				m[y][w] += dataY[i] *  funcs[y].f(dataX[i]);
			}
		}
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int i = 0; i < dataX.length; i++) {
					m[y][x] += funcs[x].f(dataX[i]) * funcs[y].f(dataX[i]);
				}
			}
		}
		//printMatrix(m);
		boolean ret = gaussianElimination(m);
		//System.out.println(ret);
		//printMatrix(m);
		if (ret) {
			double[] r = new double[w];
			for (int i = 0; i < r.length; i++) {
				r[i] = m[i][w];
			}
			return r;
		}
		return null;
	}
	/**
	 * 
	 * @param dataX x values
	 * @param dataY y values
	 * @param degree the degree of the polynomial 3 = a[0]+a[1]*x+a[2]*x*x
	 * @return polynomial coefficients [0] = the constant;
	 */
	public static double[] fit(double[] dataX, double[] dataY, int degree) {
		int w = degree+1;
		int h = degree+1;
		double[][] m = new double[h][w + 1];
		for (int y = 0; y < h; y++) {
			for (int i = 0; i < dataX.length; i++) {
				m[y][w] += dataY[i] * Math.pow(dataX[i], y);

			}
		}
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int i = 0; i < dataX.length; i++) {
					m[y][x] += Math.pow(dataX[i], x + y);
				}

			}

		}
		//printMatrix(m);
		boolean ret = gaussianElimination(m);
		//System.out.println(ret);
		//printMatrix(m);
		if (ret) {
			double[] r = new double[w];
			for (int i = 0; i < r.length; i++) {
				r[i] = m[i][w];
			}
			return r;
		} else {
			Utils.logStack(" Singular Matrix !",3);
		}
		return null;
	}

	public static double[] fit(double[][] data, int degree) {
		int w = degree+1;
		int h = degree+1;
		double[][] m = new double[h][w + 1];
		for (int y = 0; y < h; y++) {
			for (int i = 0; i < data.length; i++) {
				m[y][w] += data[i][1] * Math.pow(data[i][0], y);
				;
			}
		}
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				for (int i = 0; i < data.length; i++) {
					m[y][x] += Math.pow(data[i][0], x + y);
				}

			}

		}
		// printMatrix(m);
		boolean ret = gaussianElimination(m);
		// System.out.println(ret);
		// printMatrix(m);
		if (ret) {
			double[] r = new double[w];
			for (int i = 0; i < r.length; i++) {
				r[i] = m[i][w];
			}
			return r;
		}
		return null;
	}
	
	static void printMatrix(double[][] m) {
		System.out.println();
		for (int y = 0; y < m.length; y++) {
			for (int x = 0; x < m[y].length; x++) {
				System.out.print(m[y][x] + " ");
			}
			System.out.println();
		}
	}

	/*
	 * Transforms the given matrix into a row echelon matrix
	 */
	private static boolean gaussianElimination(double[][] m) {
		int h = m.length;
		int w = m[0].length;
		for (int y = 0; y < h; y++) {
			int maxrow = y;
			for (int y2 = y + 1; y2 < h; y2++) { // Find max pivot
				if (Math.abs(m[y2][y]) > Math.abs(m[maxrow][y])) {
					maxrow = y2;
				}
			}
			// swap
			for (int i = 0; i < m[0].length; i++) {
				double t = m[y][i];
				m[y][i] = m[maxrow][i];
				m[maxrow][i] = t;
			}
			if (Math.abs(m[y][y]) <= sEPS) { // Singular Matrix
				return false;
			}
			for (int y2 = y + 1; y2 < h; y2++) { // Eliminate column y
				double c = m[y2][y] / m[y][y];
				for (int x = y; x < w; x++) {
					m[y2][x] -= m[y][x] * c;
				}
			}
		}
		for (int y = h - 1; y > -1; y--) { // Back substitution
			double c = m[y][y];
			for (int y2 = 0; y2 < y; y2++) {
				for (int x = w - 1; x > y - 1; x--) {
					m[y2][x] -= m[y][x] * m[y2][y] / c;
				}
			}
			m[y][y] /= c;
			for (int x = h; x < w; x++) { // Normalize row y
				m[y][x] /= c;
			}
		}
		return true;
	}

	public static double getY(double[] poly, double x) {
		double sum = 0;
		int n = poly.length ;
		double pow = 1;
		for (int i = 0; i < n; i++) {
			sum += poly[i] * pow;
			pow *= x;
		}
		return sum;
	}
	static DecimalFormat df = new DecimalFormat("##.######");
	public static String printEq(double[]v) {
		String s = df.format(v[0])+"+";
		s+=df.format(v[1]) +"*x";
		for (int i = 2; i < v.length ; i++) {
			if (v[i]>0.000001)
			s+="+"+df.format(v[i])+"*x^"+i;
		}
		return s;
	}
	
	    public static String eqToString(double[]v) {
	        String s = df.format(v[0]);

	        s+=((v[1]>0)?"+":"")+df.format(v[1]) +"*x";
	        for (int i = 2; i < v.length ; i++) {
	            if (Math.abs(v[i])>0.01)
	                s+=((v[i]>0)?"+":"")+df.format(v[i])+"*x^"+i;
	        }
	        return s;
	    }
	    
	    
	public static void main(String[] args) {
		double[] x = new double[21];
		double[] y = new double[21];
		for (int i = 0; i < x.length; i++) {
			x[i] = i;
			y[i] = i*23+47+i*i*i;
		}
 		double[] v = LinearRegression.fit(x, y, 3);
		System.out.println(printEq(v));

	}

}
