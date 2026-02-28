package com.example.tflmcpserver.client;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.tflmcpserver.model.TflStationDataProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class TflStationDataClientTest {

	@TempDir
	Path tempDir;

	@Test
	void findsStationsAndToiletsFromCsvData() throws IOException {
		Files.writeString(tempDir.resolve("Stations.csv"), """
				UniqueId,Name
				HUBABW,Abbey Wood
				910GBNHAM,Burnham
				""");
		Files.writeString(tempDir.resolve("Toilets.csv"),
				"""
						StationUniqueId,Id,IsAccessible,HasBabyChanging,IsInsideGateLine,Location,IsFeeCharged,Type,IsManagedByTfL
						910GBNHAM,1,TRUE,FALSE,TRUE,Located on platform 2,FALSE,Male,TRUE
						910GBNHAM,2,TRUE,TRUE,TRUE,"Located on platforms 2,3,4",FALSE,Unisex,TRUE
						""");
		TflStationDataClient client = new TflStationDataClient(new TflStationDataProperties(tempDir.toString()));

		List<TflStationDataClient.StationRecord> stations = client.findStationsByName("Burnham station", 5);
		List<TflStationDataClient.ToiletRecord> toilets = client.findToiletsByStationUniqueId("910GBNHAM", 10);

		assertEquals(1, stations.size());
		assertEquals("910GBNHAM", stations.get(0).uniqueId());
		assertEquals(2, toilets.size());
		assertEquals("Located on platform 2", toilets.get(0).location());
	}
}
