package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourney {

	private int duration;
	private String startDateTime;
	private String arrivalDateTime;
	private String description;
	private boolean alternativeRoute;
	private TflJourneyFare fare;
	private List<TflJourneyLeg> legs;

	public TflJourney() {
	}

	public TflJourney(int duration, String startDateTime, String arrivalDateTime, List<TflJourneyLeg> legs) {
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

	public TflJourneyFare getFare() {
		return fare;
	}

	public void setFare(TflJourneyFare fare) {
		this.fare = fare;
	}

	public List<TflJourneyLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<TflJourneyLeg> legs) {
		this.legs = legs;
	}
}
