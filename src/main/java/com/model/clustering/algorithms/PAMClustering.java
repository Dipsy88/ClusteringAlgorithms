package com.model.clustering.algorithms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PAMClustering {
	private int numCluster;
	private List<String> centroidList = new ArrayList<>(); // list of centers
//	private List<String> oldCentroidList = new ArrayList<>(); // list of old centers
	private Map<String, Map<String, Double>> dataSetToOtherValuesMap = new HashMap<>();

	// map of clusters
	private Map<Integer, Cluster> clusterMap = new HashMap<>();

	public List<Cluster> run() {
		boolean isChanged = true;
		// build phase
		init(this.dataSetToOtherValuesMap);
		while (isChanged) {
			// build phase
			this.clusterMap = new HashMap<>();
			assignCluster(this.dataSetToOtherValuesMap);
			// change phase
			// change all the cluster
			isChanged = changeCenter();
		}
		return getClusters(this.clusterMap);
	}

	// return this clusters
	public List<Cluster> getClusters(Map<Integer, Cluster> clusterMap) {
		List<Cluster> clusterList = new ArrayList<Cluster>();
		for (Cluster cluster : clusterMap.values())
			clusterList.add(cluster);

		return clusterList;
	}

	public boolean changeCenter() {
		boolean isChanged = false;
		List<String> newCentroidList = new ArrayList<String>();
		for (Map.Entry<Integer, Cluster> entry : clusterMap.entrySet()) {
			Cluster cluster = entry.getValue();
			newCentroidList.add(cluster.getDataCenterList().get(assignToCluster(cluster)));
		}
		isChanged = !listEqualsIgnoreOrder(newCentroidList, centroidList);
		centroidList = newCentroidList;

		return isChanged;
	}

	public static <T> boolean listEqualsIgnoreOrder(List<T> list1, List<T> list2) {
		return new HashSet<>(list1).equals(new HashSet<>(list2));
	}

	public int assignToCluster(Cluster cluster) {
		int bestCenter = 0;
		double similarity = 0;
		double bestSimilarity = Double.MAX_VALUE;

		for (int i = 0; i < cluster.getDataCenterList().size(); i++) {
			similarity = 0;
			for (int j = 0; j < cluster.getDataCenterList().size(); j++) {
				if (i == j)
					continue;
				similarity += calculateDistance(cluster.getDataCenterList().get(i), cluster.getDataCenterList().get(j));
			}
			if (similarity < bestSimilarity) {
				bestSimilarity = similarity;
				bestCenter = i;
			}
		}
		return bestCenter;
	}

	public double calculateDistance(String center, String to) {
		double distance = 0;
		Map<String, Double> dcFromCenter = dataSetToOtherValuesMap.get(center);
		if (dcFromCenter.containsKey(to)) {
			distance = dcFromCenter.get(to);
		} else { // if dist from cent to cluster does not exist but only other way
			dcFromCenter = dataSetToOtherValuesMap.get(to);
			distance = dcFromCenter.get(center);
		}
		return distance;
	}

	// distribute datacenters to different clusters
	public void assignCluster(Map<String, Map<String, Double>> dataSetToOtherValuesMap) {
		double max = Double.MAX_VALUE;
		double min = max;
		double distance = 0.0;
		int addToCluster = -1;

		for (Map.Entry<String, Map<String, Double>> entry : dataSetToOtherValuesMap.entrySet()) {
			min = max; // reset
			for (int i = 0; i < centroidList.size(); i++) {
				// if this cluster was actually selected as a cluster center
				if (centroidList.get(i).equalsIgnoreCase(entry.getKey())) {
					min = 0.0;
					addToCluster = i;
					break;
				} else {
					// compute distance from cluster center

					distance = calculateDistance(centroidList.get(i), entry.getKey());
					if (distance < min) {
						min = distance;
						addToCluster = i;
					}
//					System.out.println(i);
				}
			}
			Cluster cluster = new Cluster();
			List<String> dataCenterList = new ArrayList<>();
			if (this.clusterMap.containsKey(addToCluster)) {
				cluster = this.clusterMap.get(addToCluster);
				dataCenterList = cluster.getDataCenterList();
			}
			dataCenterList.add(entry.getKey());
			cluster.setDataCenterList(dataCenterList);
			this.clusterMap.put(addToCluster, cluster);
		}

	}

	// set random datacenter as centers
	public void init(Map<String, Map<String, Double>> dataSetToOtherValuesMap) {
		List<String> keysAsArray = new ArrayList<String>(dataSetToOtherValuesMap.keySet());
		Random r = new Random();

		String key;

		for (int i = 0; i < numCluster; i++) {
			do {
				key = keysAsArray.get(r.nextInt(keysAsArray.size()));
			} while (this.centroidList.contains(key));
			this.centroidList.add(key);
		}

	}

	public class Cluster {
		private List<String> dataCenterList;

		public List<String> getDataCenterList() {
			return dataCenterList;
		}

		public void setDataCenterList(List<String> dataCenterList) {
			this.dataCenterList = dataCenterList;
		}

		@Override
		public String toString() {
			String text = "";
			text += this.dataCenterList + " ";
			return text;
		}

	}

	public int getNumCluster() {
		return numCluster;
	}

	public void setNumCluster(int numCluster) {
		this.numCluster = numCluster;
	}

	public List<String> getCentroidList() {
		return centroidList;
	}

	public void setCentroidList(List<String> centroidList) {
		this.centroidList = centroidList;
	}

	public Map<String, Map<String, Double>> getDataSetToOtherValuesMap() {
		return dataSetToOtherValuesMap;
	}

	public void setDataSetToOtherValuesMap(Map<String, Map<String, Double>> dataSetToOtherValuesMap) {
		this.dataSetToOtherValuesMap = dataSetToOtherValuesMap;
	}
}
