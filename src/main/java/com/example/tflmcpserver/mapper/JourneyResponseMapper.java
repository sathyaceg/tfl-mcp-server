package com.example.tflmcpserver.mapper;

import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResultWire;
import java.util.List;

public interface JourneyResponseMapper {

	List<JourneyDisambiguationSuggestion> toDisambiguationSuggestions(List<TflDisambiguationOptionWire> options);

	List<JourneyOptionDetail> toTopJourneyDetails(TflItineraryResultWire itineraryResult, int limit);
}
