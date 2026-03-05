package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflPath {

	private List<TflIdentifier> stopPoints;

	public List<TflIdentifier> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(List<TflIdentifier> stopPoints) {
		this.stopPoints = stopPoints;
	}
}
