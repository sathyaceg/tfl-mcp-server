package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflPathWire {

	private List<TflIdentifierWire> stopPoints;

	public List<TflIdentifierWire> getStopPoints() {
		return stopPoints;
	}

	public void setStopPoints(List<TflIdentifierWire> stopPoints) {
		this.stopPoints = stopPoints;
	}
}
