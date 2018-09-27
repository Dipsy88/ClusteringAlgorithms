package com.model.clustering.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// from elki
//https://github.com/elki-project/elki/
public class AffinityPropagation {
	private int numberOfDataPoints;
	private double[][] matrix;
	private double[][] s; // similarity matrix
	private double[][] r; // responsibility matrix
	private double[][] a; // availability matrix
	private int iter = 2000; // maximum number of iterations
	private double lambda = 0.5; // damping factor
	private int convergence = 10; // terminations after "10" number of iterations with no changes
	// for preferences for all data points, compute median if is 0
	private double median = 0.;

	private int[] assignment;

	public int[] getAssignment() {
		return assignment;
	}

	public void setAssignment(int[] assignment) {
		this.assignment = assignment;
	}

	public List<ClusterIds> run() {
		// compute preferences before;
		updatePreferences();

		int inactive = 0;
		for (int i = 0; i < this.iter && inactive < convergence; i++) {
			// update responsibility matrix
			for (int j = 0; j < this.numberOfDataPoints; j++) {
				double[] aj = this.a[j], rj = this.r[j], sj = this.s[j];
				// find the two largest values
				double max1 = Double.NEGATIVE_INFINITY, max2 = Double.NEGATIVE_INFINITY;
				int maxk = -1;
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					double val = aj[k] + sj[k];
					if (val > max1) {
						max2 = max1;
						max1 = val;
						maxk = k;
					} else if (val > max2)
						max2 = val;
				}
				// update r with the maximum known value
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					double val = sj[k] - ((k != maxk) ? max1 : max2);
					rj[k] = rj[k] * this.lambda + val * (1. - lambda);
				}
			}
			// update the availability matrix
			for (int j = 0; j < this.numberOfDataPoints; j++) {
				// compute sum of max(0, r_kj) for all k
				// do not apply the max for r_jj
				double colposum = 0;
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					if (k == j || this.r[k][j] > 0.) {
						colposum += this.r[k][j];
					}
				}
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					double val = colposum;
					// adjust column sum by one extra term
					if (k == j || this.r[k][j] > 0.)
						val -= this.r[k][j];
					if (k != j && val > 0.)
						val = 0.;
					this.a[k][j] = this.a[k][j] * this.lambda + val * (1 - this.lambda);
				}
			}
			int changed = 0;
			for (int k = 0; k < this.numberOfDataPoints; k++) {
				double[] ak = this.a[k], rk = this.r[k];
				double max = Double.NEGATIVE_INFINITY;
				int maxj = -1;
				for (int j = 0; j < this.numberOfDataPoints; j++) {
					double v = ak[j] + rk[j];
					if (v > max || (k == j && v >= max)) {
						max = v;
						maxj = j;
					}
				}
				if (this.assignment[k] != maxj) {
					changed += 1;
					this.assignment[k] = maxj;
				}
			}
			inactive = (changed > 0) ? 0 : (inactive + 1);
		}

		// store similar ids to cluster
		Map<Integer, List<Integer>> idClusteredMap = new HashMap<>();
		int position = 0;
		for (Integer item : this.assignment) {
			List<Integer> valList = new ArrayList<Integer>();
			if (idClusteredMap.containsKey(item))
				valList = idClusteredMap.get(item);
			valList.add(position);
			idClusteredMap.put(item, valList);
			position++;
		}

		List<ClusterIds> clusterIdList = new ArrayList<ClusterIds>();
		for (Map.Entry<Integer, List<Integer>> entry : idClusteredMap.entrySet()) {
			ClusterIds clusterId = new ClusterIds(entry.getValue());
			clusterIdList.add(clusterId);
		}
		return clusterIdList;

	}

	public AffinityPropagation(double[][] matrix) {
		this.matrix = matrix;
		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];
		this.assignment = new int[numberOfDataPoints];
		this.s = matrix;
	}

	public AffinityPropagation(double[][] matrix, double median) {
		this.matrix = matrix;
		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];
		this.assignment = new int[numberOfDataPoints];
		this.s = matrix;
		this.median = median;
	}

	public AffinityPropagation(double[][] matrix, int iter, double lambda, int covergence) {
		this.matrix = matrix;
		this.iter = iter;
		this.lambda = lambda;
		this.convergence = covergence;

		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];
		assignment = new int[numberOfDataPoints];
		this.s = matrix;
	}

	// compute preferences for all data points
	public void updatePreferences() {
//		for (int i=0;)
		if (this.median == 0.)
			computeMedian(this.s);
		for (int i = 0; i < s.length; i++)
			this.s[i][i] = this.median;

	}

	// compute median if it was not provided
	public void computeMedian(double[][] s) {
		double ret = 0;
		List<Double> valList = new ArrayList<Double>(); // to compute median
		for (int i = 0; i < s.length; i++) {
			for (int j = 0; j < s.length; j++) {
				if (i == j)
					continue;
				valList.add(s[i][j]);
			}
		}
		Collections.sort(valList);
		if (valList.size() % 2 == 0)
			ret = (valList.get(valList.size() / 2) + valList.get(valList.size() / 2 - 1)) / 2;
		else
			ret = valList.get(valList.size() / 2);
		this.median = ret;
	}

	public class ClusterIds {
		private List<Integer> dataCenterIdList;

		public ClusterIds(List<Integer> dataCenterIdList) {
			this.dataCenterIdList = dataCenterIdList;
		}

		public List<Integer> getDataCenterIdList() {
			return dataCenterIdList;
		}

		public void setDataCenterIdList(List<Integer> dataCenterIdList) {
			this.dataCenterIdList = dataCenterIdList;
		}

	}

}
