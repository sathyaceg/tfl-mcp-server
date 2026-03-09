package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflFareWire {

	private Integer cost;
	private String chargeProfileName;

	public Integer getCost() {
		return cost;
	}

	public void setCost(Integer cost) {
		this.cost = cost;
	}

	public String getChargeProfileName() {
		return chargeProfileName;
	}

	public void setChargeProfileName(String chargeProfileName) {
		this.chargeProfileName = chargeProfileName;
	}
}
