package com.example.tflmcpserver.model.tfl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflItineraryResult {

	@JsonProperty("$type")
	private String type;
	private List<TflJourney> journeys;
	private TflDisambiguation fromLocationDisambiguation;
	private TflDisambiguation toLocationDisambiguation;

	public TflItineraryResult() {
	}

	public TflItineraryResult(List<TflJourney> journeys) {
		this.journeys = journeys;
	}

	public List<TflJourney> getJourneys() {
		return journeys;
	}

	public void setJourneys(List<TflJourney> journeys) {
		this.journeys = journeys;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public TflDisambiguation getFromLocationDisambiguation() {
		return fromLocationDisambiguation;
	}

	public void setFromLocationDisambiguation(TflDisambiguation fromLocationDisambiguation) {
		this.fromLocationDisambiguation = fromLocationDisambiguation;
	}

	public TflDisambiguation getToLocationDisambiguation() {
		return toLocationDisambiguation;
	}

	public void setToLocationDisambiguation(TflDisambiguation toLocationDisambiguation) {
		this.toLocationDisambiguation = toLocationDisambiguation;
	}
}
