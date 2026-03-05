package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourneyLeg {

	private int duration;
	private String departureTime;
	private String arrivalTime;
	private Double distance;
	private TflInstruction instruction;
	private TflIdentifier mode;
	private List<TflRouteOption> routeOptions;
	private String interChangeDuration;
	private String interChangePosition;
	@JsonProperty("isDisrupted")
	private boolean isDisrupted;
	private List<TflDisruption> disruptions;
	private List<TflPlannedWork> plannedWorks;
	private TflPath path;

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getDepartureTime() {
		return departureTime;
	}

	public void setDepartureTime(String departureTime) {
		this.departureTime = departureTime;
	}

	public String getArrivalTime() {
		return arrivalTime;
	}

	public void setArrivalTime(String arrivalTime) {
		this.arrivalTime = arrivalTime;
	}

	public Double getDistance() {
		return distance;
	}

	public void setDistance(Double distance) {
		this.distance = distance;
	}

	public TflInstruction getInstruction() {
		return instruction;
	}

	public void setInstruction(TflInstruction instruction) {
		this.instruction = instruction;
	}

	public TflIdentifier getMode() {
		return mode;
	}

	public void setMode(TflIdentifier mode) {
		this.mode = mode;
	}

	public List<TflRouteOption> getRouteOptions() {
		return routeOptions;
	}

	public void setRouteOptions(List<TflRouteOption> routeOptions) {
		this.routeOptions = routeOptions;
	}

	public String getInterChangeDuration() {
		return interChangeDuration;
	}

	public void setInterChangeDuration(String interChangeDuration) {
		this.interChangeDuration = interChangeDuration;
	}

	public String getInterChangePosition() {
		return interChangePosition;
	}

	public void setInterChangePosition(String interChangePosition) {
		this.interChangePosition = interChangePosition;
	}

	public boolean isDisrupted() {
		return isDisrupted;
	}

	public void setDisrupted(boolean disrupted) {
		isDisrupted = disrupted;
	}

	public List<TflDisruption> getDisruptions() {
		return disruptions;
	}

	public void setDisruptions(List<TflDisruption> disruptions) {
		this.disruptions = disruptions;
	}

	public List<TflPlannedWork> getPlannedWorks() {
		return plannedWorks;
	}

	public void setPlannedWorks(List<TflPlannedWork> plannedWorks) {
		this.plannedWorks = plannedWorks;
	}

	public TflPath getPath() {
		return path;
	}

	public void setPath(TflPath path) {
		this.path = path;
	}
}
