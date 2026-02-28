package com.example.tflmcpserver.client;

import com.example.tflmcpserver.model.TflStationDataProperties;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class TflStationDataClient {

	private static final String STATIONS_FILE = "Stations.csv";
	private static final String TOILETS_FILE = "Toilets.csv";
	private static final int MAX_MATCHES = 10;

	private final List<StationRecord> allStations;
	private final Map<String, List<StationRecord>> stationsByNormalizedName;
	private final Map<String, List<ToiletRecord>> toiletsByStationUniqueId;

	public TflStationDataClient(TflStationDataProperties properties) {
		Path detailedDir = Path.of(properties.detailedDir()).normalize();
		this.allStations = loadStations(detailedDir.resolve(STATIONS_FILE));
		this.stationsByNormalizedName = indexStationsByName(allStations);
		this.toiletsByStationUniqueId = loadToilets(detailedDir.resolve(TOILETS_FILE));
	}

	public List<StationRecord> findStationsByName(String stationName, int limit) {
		String normalized = normalizeName(stationName);
		List<StationRecord> exactMatches = stationsByNormalizedName.getOrDefault(normalized, List.of());
		if (!exactMatches.isEmpty()) {
			return exactMatches.stream().limit(Math.min(limit, MAX_MATCHES)).toList();
		}

		return allStations.stream()
				.map(station -> new StationScore(station, score(station.normalizedName(), normalized)))
				.filter(stationScore -> stationScore.score() > 0)
				.sorted(Comparator.comparingInt(StationScore::score).reversed()
						.thenComparing(stationScore -> stationScore.station().name())
						.thenComparing(stationScore -> stationScore.station().uniqueId()))
				.limit(Math.min(limit, MAX_MATCHES)).map(StationScore::station).toList();
	}

	public List<ToiletRecord> findToiletsByStationUniqueId(String stationUniqueId, int limit) {
		if (!StringUtils.hasText(stationUniqueId)) {
			return List.of();
		}
		List<ToiletRecord> results = toiletsByStationUniqueId.getOrDefault(stationUniqueId.trim(), List.of());
		return results.stream().limit(limit).toList();
	}

	private List<StationRecord> loadStations(Path stationsCsvPath) {
		List<Map<String, String>> rows = readCsv(stationsCsvPath);
		List<StationRecord> stations = new ArrayList<>();
		for (Map<String, String> row : rows) {
			String uniqueId = row.get("UniqueId");
			String name = row.get("Name");
			if (!StringUtils.hasText(uniqueId) || !StringUtils.hasText(name)) {
				continue;
			}
			stations.add(new StationRecord(uniqueId.trim(), name.trim(), normalizeName(name)));
		}
		return stations;
	}

	private Map<String, List<StationRecord>> indexStationsByName(List<StationRecord> stations) {
		Map<String, List<StationRecord>> index = new HashMap<>();
		for (StationRecord station : stations) {
			index.computeIfAbsent(station.normalizedName(), ignored -> new ArrayList<>()).add(station);
		}
		for (List<StationRecord> values : index.values()) {
			values.sort(Comparator.comparing(StationRecord::name).thenComparing(StationRecord::uniqueId));
		}
		return index;
	}

	private Map<String, List<ToiletRecord>> loadToilets(Path toiletsCsvPath) {
		Map<String, List<ToiletRecord>> toiletsByStation = new LinkedHashMap<>();
		List<Map<String, String>> rows = readCsv(toiletsCsvPath);
		for (Map<String, String> row : rows) {
			String stationUniqueId = trimToNull(row.get("StationUniqueId"));
			if (stationUniqueId == null) {
				continue;
			}
			ToiletRecord record = new ToiletRecord(trimToNull(row.get("Location")),
					parseBoolean(row.get("IsAccessible")), parseBoolean(row.get("HasBabyChanging")),
					parseBoolean(row.get("IsInsideGateLine")), parseBoolean(row.get("IsFeeCharged")),
					Optional.ofNullable(trimToNull(row.get("Type"))).orElse("Unknown"));
			toiletsByStation.computeIfAbsent(stationUniqueId, ignored -> new ArrayList<>()).add(record);
		}
		return toiletsByStation;
	}

	private List<Map<String, String>> readCsv(Path csvPath) {
		try {
			List<String> lines = Files.readAllLines(csvPath);
			if (lines.isEmpty()) {
				return List.of();
			}
			List<String> headers = parseCsvLine(lines.get(0));
			List<Map<String, String>> rows = new ArrayList<>();
			for (int i = 1; i < lines.size(); i++) {
				String line = lines.get(i);
				if (!StringUtils.hasText(line)) {
					continue;
				}
				List<String> values = parseCsvLine(line);
				Map<String, String> row = new HashMap<>();
				for (int j = 0; j < headers.size(); j++) {
					String key = headers.get(j);
					String value = j < values.size() ? values.get(j) : "";
					row.put(key, value);
				}
				rows.add(row);
			}
			return rows;
		} catch (IOException ex) {
			throw new IllegalStateException("Failed to read station data file: " + csvPath, ex);
		}
	}

	private List<String> parseCsvLine(String line) {
		List<String> values = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		boolean inQuotes = false;
		for (int i = 0; i < line.length(); i++) {
			char c = line.charAt(i);
			if (c == '"') {
				if (inQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
					current.append('"');
					i++;
				} else {
					inQuotes = !inQuotes;
				}
				continue;
			}
			if (c == ',' && !inQuotes) {
				values.add(current.toString().trim());
				current.setLength(0);
				continue;
			}
			current.append(c);
		}
		values.add(current.toString().trim());
		return values;
	}

	private int score(String stationName, String query) {
		if (!StringUtils.hasText(stationName) || !StringUtils.hasText(query)) {
			return 0;
		}
		if (Objects.equals(stationName, query)) {
			return 100;
		}
		if (stationName.startsWith(query)) {
			return 70;
		}
		if (stationName.contains(query)) {
			return 50;
		}
		List<String> queryTokens = Arrays.stream(query.split("\\s+")).filter(StringUtils::hasText).toList();
		long matchingTokens = queryTokens.stream().filter(stationName::contains).count();
		if (matchingTokens == 0) {
			return 0;
		}
		return 20 + (int) matchingTokens * 10;
	}

	private String normalizeName(String value) {
		if (!StringUtils.hasText(value)) {
			return "";
		}
		String normalized = value.toLowerCase(Locale.ROOT).trim().replaceAll("\\s+", " ");
		if (normalized.endsWith(" station")) {
			return normalized.substring(0, normalized.length() - " station".length()).trim();
		}
		return normalized;
	}

	private String trimToNull(String value) {
		return StringUtils.hasText(value) ? value.trim() : null;
	}

	private boolean parseBoolean(String value) {
		return "TRUE".equalsIgnoreCase(value);
	}

	public record StationRecord(String uniqueId, String name, String normalizedName) {
	}

	public record ToiletRecord(String location, boolean accessible, boolean babyChanging, boolean insideGateLine,
			boolean feeCharged, String type) {
	}

	private record StationScore(StationRecord station, int score) {
	}
}
