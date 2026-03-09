package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourneyLegWire {

	private int duration;
	private String departureTime;
	private String arrivalTime;
	private Double distance;
	private TflInstructionWire instruction;
	private TflIdentifierWire mode;
	private List<TflRouteOptionWire> routeOptions;
	private String interChangeDuration;
	private String interChangePosition;
	@JsonProperty("isDisrupted")
	private boolean isDisrupted;
	private List<TflDisruptionWire> disruptions;
	private List<TflPlannedWorkWire> plannedWorks;
	private TflPathWire path;

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

	public TflInstructionWire getInstruction() {
		return instruction;
	}

	public void setInstruction(TflInstructionWire instruction) {
		this.instruction = instruction;
	}

	public TflIdentifierWire getMode() {
		return mode;
	}

	public void setMode(TflIdentifierWire mode) {
		this.mode = mode;
	}

	public List<TflRouteOptionWire> getRouteOptions() {
		return routeOptions;
	}

	public void setRouteOptions(List<TflRouteOptionWire> routeOptions) {
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

	public List<TflDisruptionWire> getDisruptions() {
		return disruptions;
	}

	public void setDisruptions(List<TflDisruptionWire> disruptions) {
		this.disruptions = disruptions;
	}

	public List<TflPlannedWorkWire> getPlannedWorks() {
		return plannedWorks;
	}

	public void setPlannedWorks(List<TflPlannedWorkWire> plannedWorks) {
		this.plannedWorks = plannedWorks;
	}

	public TflPathWire getPath() {
		return path;
	}

	public void setPath(TflPathWire path) {
		this.path = path;
	}
}
