package com.model.clustering.algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// from https://github.com/rodrigooleao/teste/blob/master/papers/MLA_AffinityPropagation.pdf
// https://github.com/jincheng9/AffinityPropagation/blob/master/affinity_propagation.cpp

public class AffinityPropagation2 {
	private int numberOfDataPoints;
	private double[][] matrix;
	private double[][] s; // similarity matrix
	private double[][] r; // responsiblity matrix
	private double[][] a; // availability matrix
	private int iter = 2000; // maximum number of iterations
	private double lambda = 0.5; // damping factor

	private double median = 0.;

	public List<ClusterIds> run() {
		// compute preferences before;
		updatePreferences();

		int inactive = 0;
		for (int m = 0; m < this.iter; m++) {
			// update responsibility matrix
			for (int i = 0; i < this.numberOfDataPoints; i++) {
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					double max = Double.NEGATIVE_INFINITY;
					for (int kk = 0; kk < k; kk++) {
						if (this.s[i][kk] + a[i][kk] > max)
							max = this.s[i][kk] + this.a[i][kk];
					}
					for (int kk = k + 1; kk < this.numberOfDataPoints; kk++) {
						if (this.s[i][kk] + this.a[i][kk] > max)
							max = this.s[i][kk] + this.a[i][kk];
					}
					this.r[i][k] = (1 - this.lambda) * (this.s[i][k] - max) + lambda * r[i][k];
				}
			}
			// update availability
			for (int i = 0; i < this.numberOfDataPoints; i++) {
				for (int k = 0; k < this.numberOfDataPoints; k++) {
					double sum = 0.;
					if (i == k) {
						for (int ii = 0; ii < i; ii++)
							sum += (0. > this.r[ii][k]) ? 0. : this.r[ii][k];
						for (int ii = i + 1; ii < this.numberOfDataPoints; ii++)
							sum += (0. > this.r[ii][k]) ? 0. : this.r[ii][k];
						this.a[i][k] = (1 - lambda) * sum + lambda * this.a[i][k];
					} else {
						int maxik = (i > k) ? i : k;
						int minik = (i < k) ? i : k;
						for (int ii = 0; ii < minik; ii++)
							sum += (0. > this.r[ii][k]) ? 0. : this.r[ii][k];
						for (int ii = minik + 1; ii < maxik; ii++)
							sum += (0. > this.r[ii][k]) ? 0. : this.r[ii][k];
						for (int ii = maxik + 1; ii < this.numberOfDataPoints; ii++)
							sum += (0. > this.r[ii][k]) ? 0. : this.r[ii][k];
						this.a[i][k] = (1 - lambda) * ((0. < (this.r[k][k] + sum)) ? 0. : this.r[k][k] + sum)
								+ lambda * this.a[i][k];

					}
				}
			}
		}
		// find the exemplar
		double[][] e = new double[numberOfDataPoints][numberOfDataPoints];
		List<Integer> center = new ArrayList<Integer>();
//		e[this.numberOfDataPoints][this.numberOfDataPoints];
		for (int i = 0; i < this.numberOfDataPoints; i++) {
			e[i][i] = r[i][i] + a[i][i];
			if (e[i][i] > 0)
				center.add(i);

		}
		// data point assignment, idx[i] is the exemplar for data point i
		int[] idx = new int[this.numberOfDataPoints];
		for (int i = 0; i < this.numberOfDataPoints; i++) {
			int idxI = 0;
			double maxSim = Double.NEGATIVE_INFINITY;
			for (int j = 0; j < center.size(); j++) {
				int c = center.get(j);
				if (this.s[i][c] > maxSim) {
					maxSim = this.s[i][c];
					idxI = c;
				}
			}
			idx[i] = idxI;
		}

		// store similar ids to cluster
		Map<Integer, List<Integer>> idClusteredMap = new HashMap<>();
		int position = 0;
		for (Integer item : idx) {
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

	public AffinityPropagation2(double[][] matrix) {
		this.matrix = matrix;
		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];

		this.s = matrix;
	}

	public AffinityPropagation2(double[][] matrix, double median) {
		this.matrix = matrix;
		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];
		this.s = matrix;
		this.median = median;
	}

	public AffinityPropagation2(double[][] matrix, int iter, double lambda) {
		this.matrix = matrix;
		this.iter = iter;
		this.lambda = lambda;

		this.numberOfDataPoints = matrix.length;

		this.r = new double[numberOfDataPoints][numberOfDataPoints];
		this.a = new double[numberOfDataPoints][numberOfDataPoints];
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
