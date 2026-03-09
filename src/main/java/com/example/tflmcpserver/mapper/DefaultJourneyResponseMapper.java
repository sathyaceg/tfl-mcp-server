package com.example.tflmcpserver.mapper;

import com.example.tflmcpserver.model.api.journey.JourneyDisambiguationSuggestion;
import com.example.tflmcpserver.model.api.journey.JourneyLegDetail;
import com.example.tflmcpserver.model.api.journey.JourneyOptionDetail;
import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOption;
import com.example.tflmcpserver.model.tfl.journey.TflDisruption;
import com.example.tflmcpserver.model.tfl.journey.TflIdentifier;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResult;
import com.example.tflmcpserver.model.tfl.journey.TflJourney;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyLeg;
import com.example.tflmcpserver.model.tfl.journey.TflPlannedWork;
import com.example.tflmcpserver.model.tfl.journey.TflRouteOption;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DefaultJourneyResponseMapper implements JourneyResponseMapper {

	@Override
	public List<JourneyDisambiguationSuggestion> toDisambiguationSuggestions(List<TflDisambiguationOption> options) {
		if (options == null) {
			return List.of();
		}
		return options.stream()
				.filter(option -> option.getParameterValue() != null && !option.getParameterValue().isBlank())
				.sorted(Comparator.comparing(TflDisambiguationOption::getMatchQuality,
						Comparator.nullsLast(Comparator.reverseOrder())))
				.map(option -> new JourneyDisambiguationSuggestion(option.getParameterValue(),
						option.getMatchQuality()))
				.limit(5).toList();
	}

	@Override
	public List<JourneyOptionDetail> toTopJourneyDetails(TflItineraryResult itineraryResult, int limit) {
		if (itineraryResult == null || itineraryResult.getJourneys() == null) {
			return List.of();
		}
		return itineraryResult.getJourneys().stream()
				.sorted(Comparator.comparingInt(TflJourney::getDuration)
						.thenComparing(TflJourney::getArrivalDateTime, Comparator.nullsLast(String::compareTo))
						.thenComparing(TflJourney::getStartDateTime, Comparator.nullsLast(String::compareTo)))
				.limit(limit).map(this::toDetail).toList();
	}

	private JourneyOptionDetail toDetail(TflJourney journey) {
		List<JourneyLegDetail> legDetails = journey.getLegs() == null
				? List.of()
				: journey.getLegs().stream().map(this::toLegDetail).toList();
		int legCount = legDetails.size();
		Integer totalFarePence = journey.getFare() == null ? null : journey.getFare().getTotalCost();
		return new JourneyOptionDetail(journey.getDuration(), journey.getStartDateTime(), journey.getArrivalDateTime(),
				legCount, journey.getDescription(), journey.isAlternativeRoute(), totalFarePence, legDetails);
	}

	private JourneyLegDetail toLegDetail(TflJourneyLeg leg) {
		String instructionSummary = leg.getInstruction() == null ? null : leg.getInstruction().getSummary();
		String instructionDetailed = leg.getInstruction() == null ? null : leg.getInstruction().getDetailed();
		String modeId = leg.getMode() == null ? null : leg.getMode().getId();
		String modeName = leg.getMode() == null ? null : leg.getMode().getName();

		String routeName = null;
		String direction = null;
		if (leg.getRouteOptions() != null && !leg.getRouteOptions().isEmpty()) {
			TflRouteOption primaryRouteOption = leg.getRouteOptions().get(0);
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
				: leg.getPlannedWorks().stream().map(TflPlannedWork::getDescription)
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

	private String disruptionSummary(TflDisruption disruption) {
		if (disruption == null) {
			return null;
		}
		if (disruption.getSummary() != null && !disruption.getSummary().isBlank()) {
			return disruption.getSummary();
		}
		return disruption.getDescription();
	}
}
