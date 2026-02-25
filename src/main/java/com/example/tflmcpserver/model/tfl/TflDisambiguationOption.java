package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguationOption {

	private String parameterValue;
	private String uri;

	public TflDisambiguationOption() {
	}

	public TflDisambiguationOption(String parameterValue, String uri) {
		this.parameterValue = parameterValue;
		this.uri = uri;
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
