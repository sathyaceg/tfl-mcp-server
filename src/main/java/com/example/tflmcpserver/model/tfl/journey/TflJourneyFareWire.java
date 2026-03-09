package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourneyFareWire {

	private Integer totalCost;
	private List<TflFareWire> fares;

	public Integer getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Integer totalCost) {
		this.totalCost = totalCost;
	}

	public List<TflFareWire> getFares() {
		return fares;
	}

	public void setFares(List<TflFareWire> fares) {
		this.fares = fares;
	}
}
