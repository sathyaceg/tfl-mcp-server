package com.example.tflmcpserver.model.tfl.journey;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TflDisambiguationPlaceWire {

	private String commonName;
	private String placeType;
	private String naptanId;
	private List<String> modes;
	private Double lat;
	private Double lon;

	public TflDisambiguationPlaceWire() {
	}

	public TflDisambiguationPlaceWire(String commonName, String placeType, String naptanId, List<String> modes,
			Double lat, Double lon) {
		this.commonName = commonName;
		this.placeType = placeType;
		this.naptanId = naptanId;
		this.modes = modes;
		this.lat = lat;
		this.lon = lon;
	}

	public String getCommonName() {
		return commonName;
	}

	public void setCommonName(String commonName) {
		this.commonName = commonName;
	}

	public String getPlaceType() {
		return placeType;
	}

	public void setPlaceType(String placeType) {
		this.placeType = placeType;
	}

	public String getNaptanId() {
		return naptanId;
	}

	public void setNaptanId(String naptanId) {
		this.naptanId = naptanId;
	}

	public List<String> getModes() {
		return modes;
	}

	public void setModes(List<String> modes) {
		this.modes = modes;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}
}
