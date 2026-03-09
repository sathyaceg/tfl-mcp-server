package com.example.tflmcpserver.mapper;

import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOption;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResult;
import java.util.List;

public interface JourneyResponseMapper {

	List<JourneyDisambiguationSuggestion> toDisambiguationSuggestions(List<TflDisambiguationOption> options);

	List<JourneyOptionDetail> toTopJourneyDetails(TflItineraryResult itineraryResult, int limit);
}
