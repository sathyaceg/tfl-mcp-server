package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguation {

	private List<TflDisambiguationOption> disambiguationOptions;

	public TflDisambiguation() {
	}

	public TflDisambiguation(List<TflDisambiguationOption> disambiguationOptions) {
		this.disambiguationOptions = disambiguationOptions;
	}

	public List<TflDisambiguationOption> getDisambiguationOptions() {
		return disambiguationOptions;
	}

	public void setDisambiguationOptions(List<TflDisambiguationOption> disambiguationOptions) {
		this.disambiguationOptions = disambiguationOptions;
	}
}
