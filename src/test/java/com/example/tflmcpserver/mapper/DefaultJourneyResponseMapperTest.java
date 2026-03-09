package com.example.tflmcpserver.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.tfl.journey.TflDisambiguationOptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflDisruptionWire;
import com.example.tflmcpserver.model.tfl.journey.TflIdentifierWire;
import com.example.tflmcpserver.model.tfl.journey.TflItineraryResultWire;
import com.example.tflmcpserver.model.tfl.journey.TflInstructionWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyFareWire;
import com.example.tflmcpserver.model.tfl.journey.TflJourneyLegWire;
import com.example.tflmcpserver.model.tfl.journey.TflPathWire;
import com.example.tflmcpserver.model.tfl.journey.TflPlannedWorkWire;
import com.example.tflmcpserver.model.tfl.journey.TflRouteOptionWire;
import java.util.List;
import org.junit.jupiter.api.Test;

class DefaultJourneyResponseMapperTest {

	private final DefaultJourneyResponseMapper mapper = new DefaultJourneyResponseMapper();

	@Test
	void mapsDetailedJourneyAndLegFields() {
		TflInstructionWire instruction = new TflInstructionWire();
		instruction.setSummary("Central line to Tottenham Court Road");
		instruction.setDetailed("Central line towards Ealing Broadway");

		TflIdentifierWire mode = new TflIdentifierWire();
		mode.setId("tube");
		mode.setName("tube");

		TflIdentifierWire line = new TflIdentifierWire();
		line.setName("Central");

		TflRouteOptionWire routeOption = new TflRouteOptionWire();
		routeOption.setName("IgnoreMeWhenLineIdentifierExists");
		routeOption.setDirection("Inbound");
		routeOption.setLineIdentifier(line);

		TflDisruptionWire disruption = new TflDisruptionWire();
		disruption.setSummary("Central Line: Minor delays.");

		TflPlannedWorkWire plannedWork = new TflPlannedWorkWire();
		plannedWork.setDescription("Planned escalator maintenance.");

		TflIdentifierWire stop1 = new TflIdentifierWire();
		stop1.setName("Bank Underground Station");
		TflIdentifierWire stop2 = new TflIdentifierWire();
		stop2.setId("940GZZLUTCR");
		TflPathWire path = new TflPathWire();
		path.setStopPoints(List.of(stop1, stop2));

		TflJourneyLegWire leg = new TflJourneyLegWire();
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

		TflJourneyFareWire fare = new TflJourneyFareWire();
		fare.setTotalCost(310);

		TflJourneyWire journey = new TflJourneyWire(18, "2026-03-05T23:39:00", "2026-03-05T23:57:00", List.of(leg));
		journey.setDescription("Test description");
		journey.setAlternativeRoute(false);
		journey.setFare(fare);

		TflItineraryResultWire itineraryResult = new TflItineraryResultWire(List.of(journey));
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
		TflDisruptionWire disruption = new TflDisruptionWire();
		disruption.setDescription("Use alternative transport.");

		TflRouteOptionWire routeOption = new TflRouteOptionWire();
		routeOption.setName("Northern");
		routeOption.setDirection("Inbound");

		TflJourneyLegWire leg = new TflJourneyLegWire();
		leg.setDuration(2);
		leg.setRouteOptions(List.of(routeOption));
		leg.setDisruptions(List.of(disruption));

		TflJourneyWire journey = new TflJourneyWire(2, "2026-03-05T23:39:00", "2026-03-05T23:41:00", List.of(leg));
		var details = mapper.toTopJourneyDetails(new TflItineraryResultWire(List.of(journey)), 5);

		assertEquals("Northern", details.get(0).legs().get(0).routeName());
		assertEquals(List.of("Use alternative transport."), details.get(0).legs().get(0).disruptionSummaries());
	}

	@Test
	void sortsAndLimitsDisambiguationSuggestions() {
		List<TflDisambiguationOptionWire> options = List.of(new TflDisambiguationOptionWire(50, "A", "/a"),
				new TflDisambiguationOptionWire(null, "B", "/b"), new TflDisambiguationOptionWire(90, "C", "/c"),
				new TflDisambiguationOptionWire(80, " ", "/blank"), new TflDisambiguationOptionWire(85, "D", "/d"),
				new TflDisambiguationOptionWire(70, "E", "/e"), new TflDisambiguationOptionWire(60, "F", "/f"));

		var suggestions = mapper.toDisambiguationSuggestions(options);

		assertEquals(5, suggestions.size());
		assertEquals("C", suggestions.get(0).parameterValue());
		assertEquals("D", suggestions.get(1).parameterValue());
		assertEquals("E", suggestions.get(2).parameterValue());
		assertEquals("F", suggestions.get(3).parameterValue());
		assertEquals("A", suggestions.get(4).parameterValue());
	}
}
