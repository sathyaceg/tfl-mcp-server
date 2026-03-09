package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguationWire {

	private List<TflDisambiguationOptionWire> disambiguationOptions;

	public TflDisambiguationWire() {
	}

	public TflDisambiguationWire(List<TflDisambiguationOptionWire> disambiguationOptions) {
		this.disambiguationOptions = disambiguationOptions;
	}

	public List<TflDisambiguationOptionWire> getDisambiguationOptions() {
		return disambiguationOptions;
	}

	public void setDisambiguationOptions(List<TflDisambiguationOptionWire> disambiguationOptions) {
		this.disambiguationOptions = disambiguationOptions;
	}
}
