package com.model.clustering.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;

public class GenerateData {

	private String[] dataPoints = { "a", "b", "c", "d", "e", "f", "g", "h", "i", "j" }; // to cluster
//	private String[] dataPoints = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" }; // to cluster
	private double median = 0;

	public Map<String, Map<String, Double>> run() {
		Map<String, Map<String, Double>> dataSetMap = generateDataDP();
		return dataSetMap;
	}

	// generate distance between the data points
	public Map<String, Map<String, Double>> generateDataDP() {
		Map<String, Map<String, Double>> dataSetMap = new HashMap<>(); // create links between diff
		List<Double> valList = new ArrayList<Double>(); // to compute median
		for (int i = 0; i < dataPoints.length; i++) {
			for (int j = 0; j < dataPoints.length; j++) {
				double val = 0;
				Map<String, Double> varMap = new HashMap<>();
				if (dataSetMap.containsKey(dataPoints[i]))
					varMap = dataSetMap.get(dataPoints[i]);
				if (i == j) // same datapoint
					val = 0;
				else {
					int randomNum = ThreadLocalRandom.current().nextInt(1, 10 + 1);
					val = randomNum / (double) 10; // distance between 0.1 and 1
					valList.add(val);
				}
				varMap.put(dataPoints[j], val);
				dataSetMap.put(dataPoints[i], varMap);
			}
		}
		this.median = computeMedian(valList);
		return dataSetMap;
	}

	public void printGeneratedData(Map<String, Map<String, Double>> dataSetMap) {
		for (Entry<String, Map<String, Double>> entry : dataSetMap.entrySet()) {
			for (Entry<String, Double> val : entry.getValue().entrySet()) {
				if (val.getKey().equalsIgnoreCase(entry.getKey()))
					continue;
				System.out.println(entry.getKey() + " " + val.getKey() + " " + val.getValue());
			}
		}
	}

	public double computeMedian(List<Double> valList) {
		double ret = 0;
		Collections.sort(valList);
		if (valList.size() % 2 == 0)
			ret = (valList.get(valList.size() / 2) + valList.get(valList.size() / 2 - 1)) / 2;
		else
			ret = valList.get(valList.size() / 2);
		return ret;
	}

	public double getMedian() {
		return median;
	}

	public void setMedian(double median) {
		this.median = median;
	}

	public Map<String, Map<String, Double>> readFile(String file) {
		Map<String, Map<String, Double>> dataSetMap = new HashMap<>(); // create links between diff
		List<Double> valList = new ArrayList<Double>(); // to compute median

		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String str;

			while ((str = in.readLine()) != null) {
				String line = str;
				String[] details = line.split("  ");
				double val = 0;
				Map<String, Double> varMap = new HashMap<>();
				if (dataSetMap.containsKey(details[0]))
					varMap = dataSetMap.get(details[0]);

				val = Double.parseDouble(details[2].replace("-", ""));

				valList.add(val);

				varMap.put(details[1], val);
				dataSetMap.put(details[0], varMap);
			}
		} catch (IOException e) {
		}
		this.median = computeMedian(valList);
		return dataSetMap;
	}
}
