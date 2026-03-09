package com.example.tflmcpserver.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOption;
import com.example.tflmcpserver.model.tfl.journey.TflDisruption;
import com.example.tflmcpserver.model.tfl.journey.TflIdentifier;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResult;
import com.example.tflmcpserver.model.tfl.journey.TflInstruction;
import com.example.tflmcpserver.model.tfl.journey.TflJourney;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyFare;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyLeg;
import com.example.tflmcpserver.model.tfl.journey.TflPath;
import com.example.tflmcpserver.model.tfl.journey.TflPlannedWork;
import com.example.tflmcpserver.model.tfl.journey.TflRouteOption;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultJourneyResponseMapperTest {

	private final DefaultJourneyResponseMapper mapper = new DefaultJourneyResponseMapper();

	@Test
	void mapsDetailedJourneyAndLegFields() {
		TflInstruction instruction = new TflInstruction();
		instruction.setSummary("Central line to Tottenham Court Road");
		instruction.setDetailed("Central line towards Ealing Broadway");

		TflIdentifier mode = new TflIdentifier();
		mode.setId("tube");
		mode.setName("tube");

		TflIdentifier line = new TflIdentifier();
		line.setName("Central");

		TflRouteOption routeOption = new TflRouteOption();
		routeOption.setName("IgnoreMeWhenLineIdentifierExists");
		routeOption.setDirection("Inbound");
		routeOption.setLineIdentifier(line);

		TflDisruption disruption = new TflDisruption();
		disruption.setSummary("Central Line: Minor delays.");

		TflPlannedWork plannedWork = new TflPlannedWork();
		plannedWork.setDescription("Planned escalator maintenance.");

		TflIdentifier stop1 = new TflIdentifier();
		stop1.setName("Bank Underground Station");
		TflIdentifier stop2 = new TflIdentifier();
		stop2.setId("940GZZLUTCR");
		TflPath path = new TflPath();
		path.setStopPoints(List.of(stop1, stop2));

		TflJourneyLeg leg = new TflJourneyLeg();
		leg.setDuration(11);
		leg.setDepartureTime("2026-03-05T23:39:00");
		leg.setArrivalTime("2026-03-05T23:50:00");
		leg.setDistance(1200.5);
		leg.setInstruction(instruction);
		leg.setMode(mode);
		leg.setRouteOptions(List.of(routeOption));
		leg.setInterChangeDuration("6");
		leg.setInterChangePosition("AFTER");
		leg.setDisrupted(true);
		leg.setDisruptions(List.of(disruption));
		leg.setPlannedWorks(List.of(plannedWork));
		leg.setPath(path);

		TflJourneyFare fare = new TflJourneyFare();
		fare.setTotalCost(310);

		TflJourney journey = new TflJourney(18, "2026-03-05T23:39:00", "2026-03-05T23:57:00", List.of(leg));
		journey.setDescription("Test description");
		journey.setAlternativeRoute(false);
		journey.setFare(fare);

		TflItineraryResult itineraryResult = new TflItineraryResult(List.of(journey));
		var details = mapper.toTopJourneyDetails(itineraryResult, 5);

		assertEquals(1, details.size());
		assertEquals(18, details.get(0).durationMinutes());
		assertEquals("Test description", details.get(0).description());
		assertEquals(310, details.get(0).totalFarePence());
		assertEquals(1, details.get(0).legs().size());
		assertEquals("Central line to Tottenham Court Road", details.get(0).legs().get(0).instructionSummary());
		assertEquals("Central line towards Ealing Broadway", details.get(0).legs().get(0).instructionDetailed());
		assertEquals("tube", details.get(0).legs().get(0).modeId());
		assertEquals("tube", details.get(0).legs().get(0).modeName());
		assertEquals("Central", details.get(0).legs().get(0).routeName());
		assertEquals("Inbound", details.get(0).legs().get(0).direction());
		assertEquals("6", details.get(0).legs().get(0).interChangeDuration());
		assertEquals("AFTER", details.get(0).legs().get(0).interChangePosition());
		assertEquals(true, details.get(0).legs().get(0).disrupted());
		assertEquals(List.of("Central Line: Minor delays."), details.get(0).legs().get(0).disruptionSummaries());
		assertEquals(List.of("Planned escalator maintenance."), details.get(0).legs().get(0).plannedWorkDescriptions());
		assertEquals(List.of("Bank Underground Station", "940GZZLUTCR"), details.get(0).legs().get(0).stopPoints());
	}

	@Test
	void fallsBackForRouteNameAndDisruptionDescription() {
		TflDisruption disruption = new TflDisruption();
		disruption.setDescription("Use alternative transport.");

		TflRouteOption routeOption = new TflRouteOption();
		routeOption.setName("Northern");
		routeOption.setDirection("Inbound");

		TflJourneyLeg leg = new TflJourneyLeg();
		leg.setDuration(2);
		leg.setRouteOptions(List.of(routeOption));
		leg.setDisruptions(List.of(disruption));

		TflJourney journey = new TflJourney(2, "2026-03-05T23:39:00", "2026-03-05T23:41:00", List.of(leg));
		var details = mapper.toTopJourneyDetails(new TflItineraryResult(List.of(journey)), 5);

		assertEquals("Northern", details.get(0).legs().get(0).routeName());
		assertEquals(List.of("Use alternative transport."), details.get(0).legs().get(0).disruptionSummaries());
	}

	@Test
	void sortsAndLimitsDisambiguationSuggestions() {
		List<TflDisambiguationOption> options = List.of(new TflDisambiguationOption(50, "A", "/a"),
				new TflDisambiguationOption(null, "B", "/b"), new TflDisambiguationOption(90, "C", "/c"),
				new TflDisambiguationOption(80, " ", "/blank"), new TflDisambiguationOption(85, "D", "/d"),
				new TflDisambiguationOption(70, "E", "/e"), new TflDisambiguationOption(60, "F", "/f"));

		var suggestions = mapper.toDisambiguationSuggestions(options);

		assertEquals(5, suggestions.size());
		assertEquals("C", suggestions.get(0).parameterValue());
		assertEquals("D", suggestions.get(1).parameterValue());
		assertEquals("E", suggestions.get(2).parameterValue());
		assertEquals("F", suggestions.get(3).parameterValue());
		assertEquals("A", suggestions.get(4).parameterValue());
	}
}
