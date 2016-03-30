package org.h2.expression;
import java.util.ArrayList;
import java.util.List;


public class Cluster {

	private double centroid;
	private List<Double> datapoints;
	private int clusterID;
	
	public Cluster(int clusterID){
		this.clusterID=clusterID;
		datapoints= new ArrayList<Double>();
	}
	
	public void setCentroid(double centroids){
		this.centroid=centroids;
	}
	public void setDataPoints(double datapoint){
		datapoints.add(datapoint);
	}
	public double getCentroid(){
		return this.centroid;
	}
	public List<Double> getDataPoints(){
		return this.datapoints;
	}
	public int getClusterID(){
		return this.clusterID;
	}
	public void clearCluster(){
		datapoints.clear();
	}
	public float returnMean(){
		float sum=0;
		for(int index=0; index<datapoints.size();index++){
			sum+=datapoints.get(index);
		}
		//System.out.println("New Centroids: "+ sum/datapoints.size());
		return sum/datapoints.size();
	}
}
