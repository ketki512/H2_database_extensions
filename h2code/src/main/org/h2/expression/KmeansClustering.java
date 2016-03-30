package org.h2.expression;

import java.awt.geom.Ellipse2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.h2.value.Value;
import org.h2.value.ValueInt;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.ApplicationFrame;

public class KmeansClustering extends ApplicationFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static ArrayList<Value> allDataPoints = new ArrayList<>();
	private int num_of_Clusters;
	HashMap<Integer, Cluster> clusters;
	double[] centroids;
	static int PrevSSE, diff;
	static Value bestCluster;
	static HashMap<Integer, Integer> SSEvsNumClust = new HashMap<Integer, Integer>();

	public KmeansClustering(ArrayList<Value> dataPoints, int num_of_Clusters) {
		super("SSEvsK");
		this.num_of_Clusters = num_of_Clusters;
		allDataPoints = dataPoints;
		centroids = new double[num_of_Clusters];
		clusters = new HashMap<Integer, Cluster>();
	}

	/*
	 * public static void createRandomPoints() { int count = 0; Random random =
	 * new Random(); while (count < 500) { double number = random.nextInt(100);
	 * allDataPoints.add(number); count++; } }
	 */
	public KmeansClustering(ChartPanel chartPanel2) {
		super("SSEvsK");
		setContentPane(chartPanel2);
	}

	public void createRandomCentroids() {
		Random random = new Random();
		Cluster cluster;
		for (int index = 1; index <= num_of_Clusters; index++) {
			cluster = new Cluster(index);
			int number = /* index*2; */random.nextInt(allDataPoints.size());
			centroids[index - 1] = allDataPoints.get(number).getDouble();
			cluster.setCentroid(centroids[index - 1]);
			clusters.put(index, cluster);
			System.out.println("Cluster num: " + (index) + " : "
					+ cluster.getCentroid());
		}
	}

	public void assignPoints() {
		int distance = Integer.MAX_VALUE, ID = 0;
		System.out.println("Number of data points: " + allDataPoints.size());
		for (int index = 0; index < allDataPoints.size(); index++) {
			distance = Integer.MAX_VALUE;
			for (int index2 = 0; index2 < centroids.length; index2++) {
				double diff = Math.abs(centroids[index2]
						- allDataPoints.get(index).getDouble());
				if (diff < distance) {
					ID = index2 + 1;
					distance = (int) diff;
				}
			}
			clusters.get(ID)
					.setDataPoints(allDataPoints.get(index).getDouble());
		}
	}

	public void iterate() {
		int i = 1;
		boolean done = false;
		while (!done) {
			System.out.println("Round " + i);
			if (i == 1) {
				print();
				System.out.println();
				System.out.println("--------------------------------");
				System.out.println();
			}
			double[] prevCentroids = new double[centroids.length];
			for (int index = 0; index < centroids.length; index++) {
				prevCentroids[index] = centroids[index];
			}
			// get the prev centroids
			System.out.println("Calculate new centroids");
			CalcNewCentroid();
			// calculate the new centroids
			clearAll();
			// clear the prev data points
			assignPoints();
			// reassign the data points
			i++;
			print();
			int dist = calcDistance(prevCentroids);
			// System.out.println(dist);
			// compare the prev centroids and current ones
			if (dist < 1) {
				done = true;
			}
			num_of_Clusters--;
			System.out.println();
			System.out.println("--------------------------------");
			System.out.println();
		}
	}

	public void print() {
		for (int index = 1; index <= clusters.size(); index++) {
			List<Double> points = clusters.get(index).getDataPoints();
			System.out.println("Cluster: " + index);
			for (int index2 = 0; index2 < points.size(); index2++) {
				System.out.print(points.get(index2) + " , ");
			}
			System.out.println();
		}
	}

	public int calcDistance(double[] prevCentroids) {
		int distance = 0;
		for (int index = 0; index < clusters.size(); index++) {
			// System.out.println(centroids[index]+" ; "+prevCentroids[index]);
			distance += Math.abs((centroids[index] - prevCentroids[index]));
			// System.out.println(distance);
		}
		return distance;
	}

	public void clearAll() {
		for (int index = 1; index <= clusters.size(); index++) {
			clusters.get(index).clearCluster();
		}
	}

	public void CalcNewCentroid() {
		// take the mean of the data points in that cluster
		for (int index = 1; index <= clusters.size(); index++) {
			clusters.get(index).setCentroid(clusters.get(index).returnMean());
			centroids[index - 1] = clusters.get(index).returnMean();
			System.out.println("New centroid num: " + index + " : "
					+ centroids[index - 1]);
		}
	}

	public static void calcSSE(HashMap<Integer, Cluster> clusters, int roundNum) {
		int SSE = 0;
		int dist = 0;
		for (int i : clusters.keySet()) {
			System.out.println(clusters.get(i).getCentroid());
			for (Double f : clusters.get(i).getDataPoints()) {
				dist += Math.pow((clusters.get(i).getCentroid() - f), 2);
			}
			SSE += dist;
			dist = 0;

			if (roundNum == 1) {
				PrevSSE = SSE;
				continue;
			}
			if (roundNum == 2) {
				diff = Math.abs(PrevSSE - SSE);
				bestCluster = new ValueInt(roundNum);
				System.out.println("DISTANCE:   " + diff);
				continue;
			}
			if (diff > Math.abs(PrevSSE - SSE)) {
				diff = Math.abs(PrevSSE - SSE);
				bestCluster = new ValueInt(roundNum);
			}

		}
		System.out.println("+++++++++++++++++++++++++++++" + bestCluster);
		SSEvsNumClust.put(clusters.size(), SSE);
	}

	private static XYSeriesCollection createDataset() {
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries SSEvsk = new XYSeries("SSEvsk", true, true);
		for (Integer d : SSEvsNumClust.keySet()) {
			SSEvsk.add(d, SSEvsNumClust.get(d));
		}

		dataset.addSeries(SSEvsk);
		return dataset;
	}

	public static void graph() {
		int width = 640; // Width of the image
		int height = 480; // Height of the image

		// Plotting of graphs
		JFreeChart XYChart2 = ChartFactory.createXYLineChart("SSEvsK Plot",
				"k (no. of Cluster)", "SSE", (XYDataset) createDataset(),
				PlotOrientation.VERTICAL, true, true, false);

		XYPlot xyPlot = (XYPlot) XYChart2.getPlot();
		double xradius = .5;
		double yradius = 1000;

		// circle to show point of interest
		xyPlot.addAnnotation(new XYShapeAnnotation(new Ellipse2D.Double(
				5 - xradius, 6000 - yradius, xradius + xradius, yradius
						+ yradius)));

		ChartPanel chartPanel2 = new ChartPanel(XYChart2);
		chartPanel2.setPreferredSize(new java.awt.Dimension(400, 400));

		File XYChartJPEG = new File("SSEvsK.jpeg");
		try {
			ChartUtilities
					.saveChartAsJPEG(XYChartJPEG, XYChart2, width, height);
		} catch (IOException e) {
			e.printStackTrace();
		}
		new KmeansClustering(chartPanel2);
	}

	/*
	 * public static void main(String[] args) { //createRandomPoints(); for (int
	 * numClusters = 1; numClusters < 4; numClusters++) { KmeansClustering obj =
	 * new KmeansClustering(numClusters); obj.createRandomCentroids();
	 * obj.assignPoints(); obj.iterate(); calcSSE(obj.clusters, numClusters); }
	 * 
	 * System.out.println(SSEvsNumClust.values());
	 * System.out.println(bestCluster); }
	 */
}
