package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguationOptionWire {

	private Integer matchQuality;
	private String parameterValue;
	private String uri;

	public TflDisambiguationOptionWire() {
	}

	public TflDisambiguationOptionWire(Integer matchQuality, String parameterValue, String uri) {
		this.matchQuality = matchQuality;
		this.parameterValue = parameterValue;
		this.uri = uri;
	}

	public Integer getMatchQuality() {
		return matchQuality;
	}

	public void setMatchQuality(Integer matchQuality) {
		this.matchQuality = matchQuality;
	}

	public String getParameterValue() {
		return parameterValue;
	}

	public void setParameterValue(String parameterValue) {
		this.parameterValue = parameterValue;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}
}
