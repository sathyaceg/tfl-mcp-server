package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourney {

	private int duration;
	private String startDateTime;
	private String arrivalDateTime;
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

	public List<TflJourneyLeg> getLegs() {
		return legs;
	}

	public void setLegs(List<TflJourneyLeg> legs) {
		this.legs = legs;
	}
}
