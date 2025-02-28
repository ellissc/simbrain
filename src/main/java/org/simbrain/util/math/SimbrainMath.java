/*
 * Part of Simbrain--a java-based neural network kit
 * Copyright (C) 2005,2007 The Authors.  See http://www.simbrain.net/credits
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.simbrain.util.math;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.util.*;

/**
 * <b>SimbrainMath</b> is the math functions used in simbrain.
 */
public class SimbrainMath {

    public static final double log10of2 = Math.log10(2);

    /**
     * Convenience function to get log base 2 of a number
     */
    public static double log2(double val) {
        return Math.log10(val) / log10of2;
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param src source point
     * @param tar target point
     * @return distance between source and target
     */
    public static double distance(final Point2D src, final Point2D tar) {
        double x1 = src.getX();
        double x2 = tar.getX();
        double y1 = src.getY();
        double y2 = tar.getY();

        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    /**
     * Scale a vector by the providing scaling factor
     *
     * @return the scaled vector
     */
    public static Point2D scale(final Point2D vector, double scalingFactor) {
        return new Point2D.Double(vector.getX() * scalingFactor, vector.getY() * scalingFactor);
    }

    /**
     * Returns a random permutation of all the integers between floor
     * (inclusive) and ceil (exclusive).
     */
    public static int[] randPermute(int floor, int ceil) {
        int[] permute = new int[ceil - floor];
        Random randi = new Random();
        for (int i = floor; i < ceil; i++) {
            int j = randi.nextInt(i - floor + 1);
            if (j != i - floor) {
                permute[i - floor] = permute[j];
            }
            permute[j] = i;
        }
        return permute;
    }

    /**
     * Returns a random permutation of all the integers between floor
     * (inclusive) and ceil (exclusive) with the exception of one number
     * (excluded) which is intentionally excluded.
     * <p>
     * This method will fail if excluded
     */
    public static int[] randPermuteWithExclusion(int floor, int ceil, int excluded) {
        int[] permute = new int[ceil - floor - 1];
        Random randi = new Random();
        int k = floor - 1;
        for (int i = floor; i < ceil; i++) {
            if (i == excluded) {
                continue;
            } else {
                k++;
            }
            int j = randi.nextInt(k - floor + 1);
            if (j != k - floor) {
                permute[k - floor] = permute[j];
            }
            permute[j] = i;

        }
        return permute;
    }

    /**
     * Finds the entry-wise product of a vector and scalar.  Analogous to
     * matlab's .* operator.   E.g. a .* (b c) = (ab ac).
     *
     * @param theVec the vector
     * @param val    the scalar
     * @return The entry wise product
     */
    public static double[] multVector(final double[] theVec, final double val) {
        double[] ret = new double[theVec.length];

        for (int i = 0; i < theVec.length; i++) {
            ret[i] = theVec[i] * val;
        }

        return ret;
    }

    /**
     * Return the greater of two integers.
     *
     * @param one first int
     * @param two second int
     * @return greater of one and two
     */
    public static int max(final int one, final int two) {
        if (one > two) {
            return one;
        }
        return two;
    }

    /**
     * Finds the longer of two arrays.
     *
     * @param one First array
     * @param two Second array
     * @return the longer array
     */
    public static double[] max(final double[] one, final double[] two) {
        if (one.length > two.length) {
            return one;
        }

        return two;
    }

    /**
     * Sums all the values in an array.
     *
     * @param arr the array to sum
     * @return the sum of the values in the array
     */
    public static double sum(double[] arr) {
        double tot = 0;
        for (int i = 0, n = arr.length; i < n; i++) {
            tot += arr[i];
        }
        return tot;
    }

    /**
     * An exponential sum of an array.
     *
     * @param arr the array to exponential sum
     * @return the exponential sum of the array
     */
    public static double exp_sum(double[] arr) {
        double tot = 0;
        for (int i = 0, n = arr.length; i < n; i++) {
            tot += Math.exp(arr[i]);
        }
        return tot;
    }

    /**
     * A normalized version of the vector, i.e. a scalar multiple of the vector
     * which sums to one. In this case each element is divided by the sum of the
     * array.
     *
     * @param vec the vector to normalize
     * @return a scalar multiple of the vector which sums to one
     */
    public static double[] normalizeVec(double[] vec) {
        double sum = sum(vec);
        double[] normVec = new double[vec.length];
        for (int i = 0, n = vec.length; i < n; i++) {
            normVec[i] = vec[i] / sum;
        }
        return normVec;
    }

    /**
     * Round the component of an array to the specified number of
     * decimal places.
     *
     * @param vec the array to round
     * @param places precision of the rounding in decimal places
     * @return the rounded array
     */
    public static double[] roundVec(double[] vec, int places) {
        double[] retVec = new double[vec.length];
        for (int i = 0, n = vec.length; i < n; i++) {
            retVec[i] = roundDouble(vec[i], places);
        }
        return retVec;
    }

    /**
     * The soft-max of the vector.
     *
     * @param vec the vector to soft-max
     * @return
     */
    public static double[] softMax(double[] vec) {
        double expSum = exp_sum(vec);
        double[] retSm = new double[vec.length];
        for (int i = 0, n = vec.length; i < n; i++) {
            retSm[i] = Math.exp(vec[i]) / expSum;
        }
        return retSm;
    }

    /**
     * Add these vectors. If one is larger than the other return a vector with
     * zeros in the difference.
     *
     * @param base Base number
     * @param add  Number to be added to base number
     * @return added vectors
     */
    public static double[] addVector(final double[] base, final double[] add) {
        double[] ret = new double[max(base.length, add.length)];

        if (add.length == base.length) {
            for (int i = 0; i < base.length; i++) {
                ret[i] = base[i] + add[i];
            }
        } else {
            // if the vectors are not the same length, add zeros in the extra
            // slots
            double[] temp = max(base, add);
            int max = max(base.length, add.length);
            int min = max - Math.abs(base.length - add.length);

            for (int i = 0; i < min; i++) {
                ret[i] = base[i] + add[i];
            }

            for (int i = min; i < max; i++) {
                ret[i] = temp[i];
            }
        }

        return ret;
    }

    /**
     * Helper function for computing mean values of an array of doubles.
     *
     * @param vals an array of values
     * @return the mean values of the array
     */
    public static double getAverage(final double[] vals) {
        double sum = 0;

        for (int i = 0; i < vals.length; i++) {
            sum += vals[i];
        }

        return sum / vals.length;
    }

    /**
     * Prints out the vector list.
     *
     * @param da Vector list
     */
    public static void printVector(final double[] da) {
        for (int i = 0; i < da.length; i++) {
            System.out.print(da[i] + " ");
        }

        System.out.println("");
    }

    /**
     * @param pt1 the first point
     * @param pt2 the second point
     * @return the midpoint between the two points
     */
    public static Point2D midpoint(Point2D pt1, Point2D pt2) {
        return SimbrainMath.add(
            new java.awt.geom.Point2D.Double((pt2.getX() - pt1.getX()) / 2, (pt2.getY() - pt1.getY()) / 2),
            pt1
        );

    }

    /**
     * Given a vector v, returns a vector perpinducular to v, that has unit length
     * @param point1 head of vector v
     * @param point2 tail of vector v
     * @return the unit normal vector.
     */
    public static Point2D getUnitNormalVector(Point2D point1, Point2D point2) {
        double dx = point2.getX() - point1.getX();
        double dy = point2.getY() - point1.getY();

        double magnitude = point1.distance(point2);

        if (magnitude == 0) {
            return new Point2D.Double(0, -1);
        }

        return new Point2D.Double(-dy / magnitude, dx / magnitude);
    }

    public static double magnitude(Point2D point) {
        return Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
    }

    public static double magnitudeSq(Point2D point) {
        return point.getX() * point.getX() + point.getY() * point.getY();
    }

    public static Comparator<Point2D> distanceComparator = (p1, p2) -> {
        if (magnitudeSq(p1) > magnitudeSq(p2)) {
            return 1;
        } else if (magnitudeSq(p2) < magnitudeSq(p2)) {
            return -1;
        } else {
            return 0;
        }
    };

    /**
     * Component-wise addition of the provided points.
     */
    public static Point2D.Double add(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX() + point2.getX(), point1.getY() + point2.getY());
    }

    /**
     * Component wise subtraction of provided points.  A geometry reminder: you can
     * think of point1 as the head, and point2 as the tail of a new vector.
     * See https://en.wikipedia.org/wiki/File:Vector_subtraction.svg
     */
    public static Point2D subtract(Point2D point1, Point2D point2) {
        return new Point2D.Double(point1.getX() - point2.getX(), point1.getY() - point2.getY());
    }

    /**
     * Returns the midpoint for a cubic Bezier curve.
     *
     * @param src   the start point of the curve
     * @param ctrl1 the first Bezier control point
     * @param ctrl2 the second Bezier control point
     * @param tar   the end or target point of the curve
     * @return the middle point for a cubic Bezier curve with the given
     * parameters.
     */
    public static Point2D cubicBezierMidpoint(Point2D src, Point2D ctrl1, Point2D ctrl2, Point2D tar) {
        return midpoint(midpoint(midpoint(src, ctrl1), midpoint(ctrl1, ctrl2)), midpoint(midpoint(tar, ctrl1), midpoint(ctrl1, ctrl2)));
    }

    /**
     * A fast determinant utility to find the determinant of the 2 by 2 matrix
     * made up of the vectors (stored as 2D points) v0 and v1, i.e. :
     * <p>
     * |v0x v1x| |v0y v1y|
     * <p>
     * Used so that more complex determinant algorithms in matrix packages are
     * avoided for such a simple operation...
     *
     * @param v0 the first vector, stored as a Point2D, making up the first
     *           column of the matrix we are taking the determinant of.
     * @param v1 the second vector, stored as a Point2D, making up the second
     *           column of the matrix we are taking the determinant of.
     * @return the determinant of the matrix composed of [v0, v1] (above).
     */
    public static double determinant2by2(Point2D v0, Point2D v1) {
        return (v0.getX() * v1.getY() - v1.getX() * v0.getY());
    }

    /**
     * Returns the intersection parameters for two line segments. Unlike the
     * Line2D function which just checks for intersection between two line
     * segments, this function provides more information by returning the
     * intersection parameters. These parameters effectively determine how close
     * to their start points the line segments intersect. If either parameter is
     * not on [0, 1], the line segments do not intersect. If all that is needed
     * is information on
     * <p>
     * if
     * <p>
     * the line segments intersect, then Lin2D.linesIntersect(...) is preferred.
     * It is up to whoever calls this function to determine if an intersection
     * actually occurs by checking the returned vector (as a Point2D) of
     * parameters.
     *
     * @param u0 the start point of the first line segment
     * @param v0 the end point of the first line segment
     * @param u1 the start point of the second line segment
     * @param v1 the end point of the second line segment
     * @return the intersection parameters for the two line segments. If null,
     * the line segments are parallel, else the parameterized equations of the
     * two lines intersect at the vector contained in the returned Point2D. If
     * the either of the returned parameters is not on [0, 1], then the line
     * <i>segments</i> do not intersect over their respective ranges. The X
     * value in the point returned represents where the first line intersects
     * the second and the Y value represents where the second line intersects
     * the first.
     */
    public static Point2D intersectParam(Point2D u0, Point2D v0, Point2D u1, Point2D v1) {

        double det = determinant2by2(v1, v0);

        if (Double.isNaN(det) || det == 0) {
            return null;
        }

        double x00 = u0.getX();
        double y00 = u0.getY();
        double x10 = u1.getX();
        double y10 = u1.getY();
        double x01 = v0.getX();
        double y01 = v0.getY();
        double x11 = v1.getX();
        double y11 = v1.getY();

        double s = (1 / det) * ((x00 - x10) * y01 - (y00 - y10) * x01);
        double t = (1 / det) * -(-(x00 - x10) * y11 + (y00 - y10) * x11);

        return new java.awt.geom.Point2D.Double(t, s);
    }

    /**
     * Calculates the inverse of the error function. Originally written by S.C.
     * Pohlig, adapted by J.N. Sanders
     *
     * @param p Parameter to find inverse of the error
     * @return inverse of the error
     */
    public static double inverf(final double p) { // 0 <= p <= 1
        /*
         * Originally written by S.C. Pohlig, adapted by J.N. Sanders
         *
         * This function returns an approximation to the inverse of the standard
         * normal probability distribution. The approximation error is less than
         * 4.5e-4. The approximation formula is from M. Abramowitz and I. A.
         * Stegun, Handbook of Mathematical Functions, eqn. 26.2.23, Dover
         * Publications, Inc.
         *
         * The C language error function returns erf(x) = (2/sqrt(pi)) *
         * Integral(0,x) of exp(-t*t)dt, which gives erf(infinity) = 1. In
         * essence, this gives the area under the curve between -x and +x,
         * normalized to 1. However, this function (inverf), solves for the
         * inverse of (1/sqrt(pi)) * Integral(-infinity, x) of exp(-t*t)dt. As a
         * result, the symmetric inverse is: x = inverf(erf(x) / 2. + .5)
         *
         * Given the integral of a unit variance gaussian, from -infinity to x,
         * normalized such that the integral to +infinity is 1, multiply this
         * result by sqrt(2) to obtain x.
         */
        double c0 = 2.515517;
        double c1 = 0.802853;
        double c2 = 0.010328;
        double d1 = 1.432788;
        double d2 = 0.189269;
        double d3 = 0.001308;
        double maxSigma = 7;

        double t1;
        double t2;
        double q;
        double x;

        if (p >= 1.) {
            return (maxSigma);
        } else if (p <= 0.) {
            return (-maxSigma);
        } else if (p == 0.5) {
            return (0.0);
        }

        if (p < 0.5) {
            q = p;
        } else {
            q = 1.0 - p;
        }

        t2 = -2.0 * Math.log(q);
        t1 = Math.sqrt(t2);

        x = t1 - ((c0 + (c1 * t1) + (c2 * t2)) / (1.0 + (d1 * t1) + (d2 * t2) + (d3 * t1 * t2)));
        x = x / Math.sqrt(2.);

        /**
         * jns
         */
        if (p < 0.5) {
            return (-x);
        } else {
            return (x);
        }
    }

    /**
     * Finds the largest value in a vector array.
     *
     * @param theVec Vector array
     * @return largest value in array
     */
    public static double getMaximum(final double[] theVec) {
        double max = Double.NEGATIVE_INFINITY;

        for (int i = 0; i < theVec.length; i++) {
            if (theVec[i] > max) {
                max = theVec[i];
            }
        }

        return max;
    }

    /**
     * Returns the maximum value of an array of numbers. Warning: comparisons
     * are done using the numbers' double values and they are
     * compared/stored/returned as doubles.
     *
     * @param arr
     * @return
     */
    public static double getMaximum(final Number[] arr) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0, n = arr.length; i < n; i++) {
            if (arr[i].doubleValue() > max) {
                max = arr[i].doubleValue();
            }
        }
        return max;
    }

    /**
     * Returns the minimum value of an array of numbers. Warning: comparisons
     * are done using the numbers' double values and they are
     * compared/stored/returned as doubles.
     *
     * @param arr
     * @return
     */
    /**
     * Returns the minimum value of an array of numbers.
     */
    public static double getMinimum(final double[] arr) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0, n = arr.length; i < n; i++) {
            if (arr[i] < min) {
                min = arr[i];
            }
        }
        return min;
    }

    /**
     * Normalize between minimum and maximium value of vectors.
     * @param vec
     * @return
     */
    public static double[] minMaxNormalize(double[] vec) {
        double min = getMinimum(vec);
        double max = getMaximum(vec);
        double[] normedVec = new double[vec.length];
        for (int i = 0, n = vec.length; i < n; i++) {
            normedVec[i] = (vec[i] - min) / (max - min);
        }
        return normedVec;
    }

    /**
     * Add noise to a vector.
     *
     * @param vector     vector to which noise should be added
     * @param noiselevel Noise level
     */
    public static void addNoise(final double[] vector, final double noiselevel) {
        double randUniform;
        double sigma = noiselevel * SimbrainMath.getMaximum(vector);
        double sqrt2 = Math.sqrt(2);

        for (int i = 0; i < vector.length; i++) {
            randUniform = Math.random();
            vector[i] += (sigma * sqrt2 * SimbrainMath.inverf(randUniform));
        }
    }

    /**
     * Add noise to a vector and return the result.
     *
     * @param vector     vector to which noise should be added
     * @param noiselevel Noise level
     * @return resuling vector
     */
    public static double[] getNoisyVector(final double[] vector, final double noiselevel) {
        double randUniform;
        double sigma = noiselevel * SimbrainMath.getMaximum(vector);
        double sqrt2 = Math.sqrt(2);
        double[] returnVector = new double[vector.length];

        for (int i = 0; i < vector.length; i++) {
            randUniform = Math.random();
            returnVector[i] = vector[i] + (sigma * sqrt2 * SimbrainMath.inverf(randUniform));
        }
        return returnVector;
    }

    /**
     * Create a random vector with i components.
     *
     * @param length i number of components in this vector.
     * @return the random vector.
     */
    public static double[] randomVector(int length) {
        double[] returnVector = new double[length];
        for (int i = 0; i < returnVector.length; i++) {
            returnVector[i] = Math.random();
        }
        return returnVector;
    }

    /**
     * Returns a vector of random values between min and max.
     *
     * @param length number of components in the vector
     * @param min    minimum value for random values
     * @param max    maximum value for random values
     * @return the random vector
     */
    public static double[] randomVector(final int length, final double min, final double max) {
        double[] returnVector = new double[length];
        for (int i = 0; i < returnVector.length; i++) {
            returnVector[i] = min + Math.random() * Math.abs(max - min);
        }
        return returnVector;
    }

    /**
     * Round a double value to a specified number of places.
     * <p>
     * (From user dforbu on the Sun Java Programming forum).
     *
     * @param d      double value to round
     * @param places placed to round do
     * @return rounded value
     */
    public static final double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, places)) / Math.pow(10, places);
    }

    /**
     * Returns the Euclidean norm of the supplied vector.
     *
     * @param vector vector to check
     * @return the norm
     */
    public static double getVectorNorm(double[] vector) {
        double ret = 0;
        for (int i = 0; i < vector.length; i++) {
            ret += Math.pow(vector[i], 2);
        }
        return Math.sqrt(ret);
    }

    /**
     * Returns the Euclidean distance between src and target vectors. TODO:
     * Generalize to arbitrary dimensions and deal with redundant
     * distance(Point,Point) method.
     *
     * @param src source vector.
     * @param tar target vector
     * @return the distance between them.
     */
    public static double distance(final double[] src, final double[] tar) {
        if (src.length != tar.length) {
            throw new IllegalArgumentException("Source and target vector lengths do not match");
        }
        double distance = 0;
        for (int ii = 0; ii < src.length; ii++) {
            distance += (src[ii] - tar[ii]) * (src[ii] - tar[ii]);
        }
        return Math.sqrt(distance);
    }

    /**
     * Returns the hamming distances for a pair of double arrays, in the sense of a number indicating how many
     * components they differ on.
     *
     * TODO: Deal with precision
     */
    public static int hamming(final double[] src, final double[] tar) {
        if (src.length != tar.length) {
            throw new IllegalArgumentException("Source and target vector lengths do not match");
        }
        int diff = 0;
        for (int i = 0; i < src.length; i++) {
            if (src[i] != tar[i]) diff += 1;
        }
        return diff;
    }


    /**
     * Returns the mean squared error between a source and target vector.
     *
     * @param src source vector
     * @param tar target vector
     * @return the mse
     * @throws IllegalArgumentException if src and tar have different lengths
     */
    public static double getMeanSquaredError(final double[] src,
                                             final double[] tar) throws IllegalArgumentException {

        if (src.length != tar.length) {
            String srcVec = Arrays.toString(src);
            String tarVec = Arrays.toString(tar);
            throw new IllegalArgumentException("Source vector " + srcVec
                + " has " + src.length + " components.  Target vector "
                + tarVec + " has " + tar.length + " components.");
        }

        double retVal = 0;
        double n = src.length;
        if (n == 0) {
            return 0;
        }
        for (int i = 0; i < n; i++) {
            retVal += Math.pow(tar[i] - src[i], 2);
        }
        return retVal / n;
    }

    /**
     * Returns a row compressed representation of a weight matrix represented as
     * a 2D array of doubles. This particular row compression begins with the
     * first element of the returned array containing the total number of
     * non-zero elements in <b>wtMatrix</b>. The next <b>NZ + N</b> elements of
     * the returned array (indices 1 to  <b>NZ + N</b>) where <b>NZ</b> is the
     * number of non-zero elements and <b>N</b> is the number of rows of
     * <b>wtMatrix</b>, contain all the column index values for the non-zero
     * entries with row changes separated by a marker (-1), hence NZ + N values.
     * The remaining N entries in the returned array contain (in the same order
     * as the index values) all the non-zero weight values in <b>wtMatrix</b>.
     * <p>
     * So the matrix:
     * <p>
     * 0    2    1    0    0    7 0    0    0    0    0    0 0    5    6    0
     * 1    1 0    2    0    0    3    0 2    0    0    1    2    0
     * <p>
     * Would have its indices stored as: 1  2  5  -1  -1  1  2  4  5  -1  1 4
     * -1  0  3  4
     * <p>
     * And its values stored as: 2  1  7  5  6  1  1  2  3  2  1  2
     * <p>
     * Thus the returned array would be:
     * <p>
     * [12  1  2  5  -1  -1  1  2  4  5  -1  1  4  -1  0  3  4  2  1  7  5 ... 6
     * 1  1  2  3  2  1  2 ]
     * <p>
     * While this ultimately doesn't save any memory, consider that the example
     * array has a sparsity of 0.4. Sparse matrix compression should usually be
     * used for very sparse matrices to see appreciable savings in memory,
     * definitely less than 0.2 and usually less than 0.05.
     *
     * @param wtMatrix
     * @return
     */
    public static long[] getMatrixRowCompression(double[][] wtMatrix) {
        List<Long> wts = new ArrayList<Long>();
        List<Integer> colIndArrL;
        int numNZ = 0;
        colIndArrL = new ArrayList<Integer>();
        for (int k = 0, n = wtMatrix.length; k < n; k++) {
            for (int l = 0, m = wtMatrix[0].length; l < m; l++) {
                if (m != wtMatrix[k].length) {
                    throw new IllegalArgumentException("Matrix does not have" + " equal columns.");
                }
                if (wtMatrix[k][l] != 0) {
                    numNZ++;
                    colIndArrL.add(l);
                    wts.add(Double.doubleToLongBits(wtMatrix[k][l]));
                }
            }
            // No need for a row-end code for the last row.
            if (k < n - 1) {
                colIndArrL.add(-1);
            }
        }
        long[] rowCompression = new long[colIndArrL.size() + numNZ + 1];
        rowCompression[0] = numNZ;
        for (int w = 1, n = colIndArrL.size(); w <= n; w++) {
            rowCompression[w] = colIndArrL.get(w - 1).intValue();
        }
        for (int w = colIndArrL.size() + 1, n = rowCompression.length; w < n; w++)
        {
            rowCompression[w] = wts.get(w - colIndArrL.size() - 1);
        }
        return rowCompression;
    }

    /**
     * Returns a bounded value of a number.
     * 
     * @param <T> T has to be a number
     *
     * @param number the number to check
     * @param lowerBound lower bound
     * @param upperBound upper bound
     * @return the clipped value
     */
    public static <T extends Number & Comparable<T>> T clip(T number, T lowerBound, T upperBound) {
        if (upperBound.compareTo(lowerBound) < 0) {
            T temp = upperBound;
            upperBound = lowerBound;
            lowerBound = temp;
        }

        if (number.compareTo(lowerBound) < 0) {
            return lowerBound;
        } else if (number.compareTo(upperBound) > 0) {
            return upperBound;
        } else {
            return number;
        }

    }

    /**
     * Test randPermuteWithExclusion.
     *
     * @param yarhg
     */
    public static void main(String[] yarhg) {
        int[][] blarg = new int[10000][9999];
        long time = System.nanoTime();
        for (int i = 0; i < 10000; i++) {
            blarg[i] = randPermuteWithExclusion(0, 10000, i);
        }
        System.out.println(roundDouble((System.nanoTime() - time) / Math.pow(10, 9), 4) + " secs.");
        // System.out.println(Arrays.toString(randPermute(1, 10)));
        // System.out.println(Arrays.toString(randPermuteWithExclusion(1, 10,
        // 6)));
    }

    /**
     * Approximate the angle of the tangent at a given parameter t on a Bezier curve
     *
     * @return theta in radians
     */
    public static double approximateTangentAngleOfBezierCurve(QuadCurve2D curve2D, double t) {
        Point2D p1 = findPointOnBezierCurve(curve2D, t + 0.01);
        Point2D p2 = findPointOnBezierCurve(curve2D, t - 0.01);
        Point2D vec = subtract(p2, p1);
        return Math.atan2(vec.getY(), vec.getX());
    }

    /**
     * Find the point on a Bezier curve at parameter t
     */
    public static Point2D findPointOnBezierCurve(QuadCurve2D curve2D, double t) {
        Point2D p1 = findEndPointOfLineSegmentAfterScaling(curve2D.getP1(), curve2D.getCtrlPt(), t);
        Point2D p2 = findEndPointOfLineSegmentAfterScaling(curve2D.getCtrlPt(), curve2D.getP2(), t);
        return findEndPointOfLineSegmentAfterScaling(p1, p2, t);
    }

    /**
     * Creates a vector from p1 to p2, and scales it.  Returns the endpoint of the new vector.
     */
    private static Point2D findEndPointOfLineSegmentAfterScaling(Point2D p1, Point2D p2, double scalingFactor) {
        return add(scale(subtract(p2, p1), scalingFactor), p1);
    }

    /**
     * Takes a value in one interval and returns the proportional value in another interval.
     * E.g. .5 in 0,1 will be mapped to 2 in 0,4. Think of it as a function from a source to a target interval.
     *
     * @param val source value
     * @param low low end of source interval
     * @param high high end of source interval
     * @param targetLow low end of target interval
     * @param targetHigh high end of target interval
     * @return rescaled value in the target interval
     */
    public static double rescale(double val, double low, double high, double targetLow, double targetHigh) {
        double clipped = clip(val, low, high);
        return ((high - clipped) * targetLow + (clipped - low) * targetHigh) / (high - low);
    }

}