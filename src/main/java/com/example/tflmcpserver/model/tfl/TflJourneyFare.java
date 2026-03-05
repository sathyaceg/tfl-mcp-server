package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflJourneyFare {

	private Integer totalCost;
	private List<TflFare> fares;

	public Integer getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Integer totalCost) {
		this.totalCost = totalCost;
	}

	public List<TflFare> getFares() {
		return fares;
	}

	public void setFares(List<TflFare> fares) {
		this.fares = fares;
	}
}
