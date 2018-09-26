package com.model.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.model.clustering.algorithms.PAMClustering;
import com.model.clustering.util.GenerateData;

public class Main {

	private final static int NUM_CLUSTER = 3; // for clustering algorithm that need "k"

	public static void main(String[] args) {
		// initialize data
		GenerateData generateData = new GenerateData();
		generateData.run();

		Map<String, Map<String, Double>> dataSetMap = generateData.getDataSetMap();
		List<PAMClustering.Cluster> clusterPAM = clusterPAM(dataSetMap);
		System.out.println(clusterPAM);
		// for affinity propagation clustering
		// get a list of datapoints
		List<String> dataPoints = getListDP(dataSetMap);
		// put data in a two dimensional matrix
		double[][] matrix = convertMatrix(dataSetMap, dataPoints);
	}

	// use PAM clustering algorithm
	public static List<PAMClustering.Cluster> clusterPAM(Map<String, Map<String, Double>> dataSetMap) {
		PAMClustering clustering = new PAMClustering();
		clustering.setNumCluster(NUM_CLUSTER);
		clustering.setDataSetToOtherValuesMap(dataSetMap);

		List<PAMClustering.Cluster> pamCluster = clustering.run();
		return pamCluster;
	}

	// put data in a two dimensional matrix
	public static double[][] convertMatrix(Map<String, Map<String, Double>> dataSetMap, List<String> dataPoints) {
		double ret[][] = new double[dataSetMap.size()][dataSetMap.size()];
		for (int i = 0; i < dataPoints.size(); i++) {
			for (int j = 0; j < dataPoints.size(); j++) {
				if (i == j) {
					ret[i][j] = 0;
				}
				// connection to other datapoint exists
				if (dataSetMap.get(dataPoints.get(i)).containsKey(dataPoints.get(j)))
					ret[i][j] = dataSetMap.get(dataPoints.get(i)).get(dataPoints.get(j));
				// connection does not exist
				else
					ret[i][j] = -1;
			}
		}
		return ret;
	}

	// get a list of datapoints
	public static List<String> getListDP(Map<String, Map<String, Double>> dataSetMap) {
		List<String> dpList = new ArrayList<String>();
		for (String key : dataSetMap.keySet())
			dpList.add(key);
		return dpList;
	}

}
