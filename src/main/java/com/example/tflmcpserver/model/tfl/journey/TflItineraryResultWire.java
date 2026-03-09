package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflItineraryResultWire {

	@JsonProperty("$type")
	private String type;
	private List<TflJourneyWire> journeys;
	private TflDisambiguationWire fromLocationDisambiguation;
	private TflDisambiguationWire toLocationDisambiguation;

	public TflItineraryResultWire() {
	}

	public TflItineraryResultWire(List<TflJourneyWire> journeys) {
		this.journeys = journeys;
	}

	public List<TflJourneyWire> getJourneys() {
		return journeys;
	}

	public void setJourneys(List<TflJourneyWire> journeys) {
		this.journeys = journeys;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TflDisambiguationWire getFromLocationDisambiguation() {
		return fromLocationDisambiguation;
	}

	public void setFromLocationDisambiguation(TflDisambiguationWire fromLocationDisambiguation) {
		this.fromLocationDisambiguation = fromLocationDisambiguation;
	}

	public TflDisambiguationWire getToLocationDisambiguation() {
		return toLocationDisambiguation;
	}

	public void setToLocationDisambiguation(TflDisambiguationWire toLocationDisambiguation) {
		this.toLocationDisambiguation = toLocationDisambiguation;
	}
}
