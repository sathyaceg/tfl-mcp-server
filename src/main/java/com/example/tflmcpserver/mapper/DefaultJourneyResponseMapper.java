package com.example.tflmcpserver.mapper;

import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyLegDetail;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflDisruptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflIdentifierWire;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResultWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyLegWire;
import com.example.tflmcpserver.model.tfl.journey.TflPlannedWorkWire;
import com.example.tflmcpserver.model.tfl.journey.TflRouteOptionWire;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefaultJourneyResponseMapper implements JourneyResponseMapper {

	@Override
	public List<JourneyDisambiguationSuggestion> toDisambiguationSuggestions(
			List<TflDisambiguationOptionWire> options) {
		if (options == null) {
			return List.of();
		}
		return options.stream()
				.filter(option -> option.getParameterValue() != null && !option.getParameterValue().isBlank())
				.sorted(Comparator.comparing(TflDisambiguationOptionWire::getMatchQuality,
						Comparator.nullsLast(Comparator.reverseOrder())))
				.map(option -> new JourneyDisambiguationSuggestion(option.getParameterValue(),
						option.getMatchQuality()))
				.limit(5).toList();
	}

	@Override
	public List<JourneyOptionDetail> toTopJourneyDetails(TflItineraryResultWire itineraryResult, int limit) {
		if (itineraryResult == null || itineraryResult.getJourneys() == null) {
			return List.of();
		}
		return itineraryResult.getJourneys().stream()
				.sorted(Comparator.comparingInt(TflJourneyWire::getDuration)
						.thenComparing(TflJourneyWire::getArrivalDateTime, Comparator.nullsLast(String::compareTo))
						.thenComparing(TflJourneyWire::getStartDateTime, Comparator.nullsLast(String::compareTo)))
				.limit(limit).map(this::toDetail).toList();
	}

	private JourneyOptionDetail toDetail(TflJourneyWire journey) {
		List<JourneyLegDetail> legDetails = journey.getLegs() == null
				? List.of()
				: journey.getLegs().stream().map(this::toLegDetail).toList();
		int legCount = legDetails.size();
		Integer totalFarePence = journey.getFare() == null ? null : journey.getFare().getTotalCost();
		return new JourneyOptionDetail(journey.getDuration(), journey.getStartDateTime(), journey.getArrivalDateTime(),
				legCount, journey.getDescription(), journey.isAlternativeRoute(), totalFarePence, legDetails);
	}

	private JourneyLegDetail toLegDetail(TflJourneyLegWire leg) {
		String instructionSummary = leg.getInstruction() == null ? null : leg.getInstruction().getSummary();
		String instructionDetailed = leg.getInstruction() == null ? null : leg.getInstruction().getDetailed();
		String modeId = leg.getMode() == null ? null : leg.getMode().getId();
		String modeName = leg.getMode() == null ? null : leg.getMode().getName();

		String routeName = null;
		String direction = null;
		if (leg.getRouteOptions() != null && !leg.getRouteOptions().isEmpty()) {
			TflRouteOptionWire primaryRouteOption = leg.getRouteOptions().get(0);
			routeName = primaryRouteOption.getLineIdentifier() != null
					&& primaryRouteOption.getLineIdentifier().getName() != null
							? primaryRouteOption.getLineIdentifier().getName()
							: primaryRouteOption.getName();
			direction = primaryRouteOption.getDirection();
		}

		List<String> disruptionSummaries = leg.getDisruptions() == null
				? List.of()
				: leg.getDisruptions().stream().map(this::disruptionSummary)
						.filter(text -> text != null && !text.isBlank()).toList();

		List<String> plannedWorkDescriptions = leg.getPlannedWorks() == null
				? List.of()
				: leg.getPlannedWorks().stream().map(TflPlannedWorkWire::getDescription)
						.filter(text -> text != null && !text.isBlank()).toList();

		List<String> stopPoints = (leg.getPath() == null || leg.getPath().getStopPoints() == null)
				? List.of()
				: leg.getPath().getStopPoints().stream()
						.map(stopPoint -> stopPoint.getName() != null && !stopPoint.getName().isBlank()
								? stopPoint.getName()
								: stopPoint.getId())
						.filter(text -> text != null && !text.isBlank()).toList();

		return new JourneyLegDetail(leg.getDuration(), leg.getDepartureTime(), leg.getArrivalTime(), leg.getDistance(),
				instructionSummary, instructionDetailed, modeId, modeName, routeName, direction,
				leg.getInterChangeDuration(), leg.getInterChangePosition(), leg.isDisrupted(), disruptionSummaries,
				plannedWorkDescriptions, stopPoints);
	}

	private String disruptionSummary(TflDisruptionWire disruption) {
		if (disruption == null) {
			return null;
		}
		if (disruption.getSummary() != null && !disruption.getSummary().isBlank()) {
			return disruption.getSummary();
		}
		return disruption.getDescription();
	}
}
