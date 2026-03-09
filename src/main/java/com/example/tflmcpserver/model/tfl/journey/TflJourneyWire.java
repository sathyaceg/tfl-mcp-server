package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourneyWire {

	private int duration;
	private String startDateTime;
	private String arrivalDateTime;
	private String description;
	private boolean alternativeRoute;
	private TflJourneyFareWire fare;
	private List<TflJourneyLegWire> legs;

	public TflJourneyWire() {
	}

	public TflJourneyWire(int duration, String startDateTime, String arrivalDateTime, List<TflJourneyLegWire> legs) {
		this.duration = duration;
		this.startDateTime = startDateTime;
		this.arrivalDateTime = arrivalDateTime;
		this.legs = legs;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public String getStartDateTime() {
		return startDateTime;
	}

	public void setStartDateTime(String startDateTime) {
		this.startDateTime = startDateTime;
	}

	public String getArrivalDateTime() {
		return arrivalDateTime;
	}

	public void setArrivalDateTime(String arrivalDateTime) {
		this.arrivalDateTime = arrivalDateTime;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isAlternativeRoute() {
		return alternativeRoute;
	}

	public void setAlternativeRoute(boolean alternativeRoute) {
		this.alternativeRoute = alternativeRoute;
	}

	public TflJourneyFareWire getFare() {
		return fare;
	}

	public void setFare(TflJourneyFareWire fare) {
		this.fare = fare;
	}

	public List<TflJourneyLegWire> getLegs() {
		return legs;
	}

	public void setLegs(List<TflJourneyLegWire> legs) {
		this.legs = legs;
	}
}
