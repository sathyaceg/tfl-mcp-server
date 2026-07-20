package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguationOptionWire {

	private Integer matchQuality;
	private String parameterValue;
	private String uri;
	private TflDisambiguationPlaceWire place;

	public TflDisambiguationOptionWire() {
	}

	public TflDisambiguationOptionWire(Integer matchQuality, String parameterValue, String uri) {
		this.matchQuality = matchQuality;
		this.parameterValue = parameterValue;
		this.uri = uri;
	}

	public TflDisambiguationOptionWire(Integer matchQuality, String parameterValue, String uri,
			TflDisambiguationPlaceWire place) {
		this(matchQuality, parameterValue, uri);
		this.place = place;
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

	public TflDisambiguationPlaceWire getPlace() {
		return place;
	}

	public void setPlace(TflDisambiguationPlaceWire place) {
		this.place = place;
	}
}
