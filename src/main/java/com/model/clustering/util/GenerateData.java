package com.model.clustering.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateData {

	private String[] dataPoints = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" }; // to cluster
	// create links between diff
	private Map<String, Map<String, Double>> dataSetMap = new HashMap<>();

	public void run() {
		generateDataDP();
	}

	// generate distance between the data points
	public void generateDataDP() {
		for (int i = 0; i < dataPoints.length; i++) {
			for (int j = 0; j < dataPoints.length; j++) {
				if (i == j) // same datapoint
					continue;
				Map<String, Double> varMap = new HashMap<>();
				if (dataSetMap.containsKey(dataPoints[i]))
					varMap = dataSetMap.get(dataPoints[i]);
				int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
				double val = randomNum / (double) 10; // distance between 0.1 and 1
				varMap.put(dataPoints[j], val);
				dataSetMap.put(dataPoints[i], varMap);
			}
		}
	}

	public Map<String, Map<String, Double>> getDataSetMap() {
		return dataSetMap;
	}

	public void setDataSetMap(Map<String, Map<String, Double>> dataSetMap) {
		this.dataSetMap = dataSetMap;
	}

}
