package com.sddevops.jenkins_maven.eclipse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.logging.Logger;

import org.json.JSONObject;

public class SongCollection {

	private static final Logger logger = Logger.getLogger(SongCollection.class.getName());
	private static final int DEFAULT_CAPACITY = 20;
	private final List<Song> songs;
	private String jsonApiUrl = "https://mocki.io/v1/36c94419-b141-4cfd-96fa-327f4872aca6";

	// Supplier to provide HttpURLConnection - can be mocked for tests
	private Supplier<HttpURLConnection> connectionSupplier;

	// Default constructor uses real URL connection
	public SongCollection() {
		this.songs = new ArrayList<>(DEFAULT_CAPACITY);
		this.connectionSupplier = this::createHttpURLConnection;
	}

	// Constructor with custom connection supplier for testing
	public SongCollection(Supplier<HttpURLConnection> connectionSupplier) {
		this.songs = new ArrayList<>(DEFAULT_CAPACITY);
		this.connectionSupplier = connectionSupplier;
	}

	// Creates the real HttpURLConnection from jsonApiUrl
	private HttpURLConnection createHttpURLConnection() {
		try {
			URL url = new URL(jsonApiUrl);
			return (HttpURLConnection) url.openConnection();
		} catch (Exception e) {
			logger.severe("Failed to create HttpURLConnection: " + e.getMessage());
			return null;
		}
	}

	public List<Song> getSongs() {
		return songs;
	}

	public void addSong(Song song) {
		if (song != null) {
			songs.add(song);
		}
	}

	public Song getSongById(String id) {
		for (Song song : songs) {
			if (song.getId().equals(id)) {
				return song;
			}
		}
		return null;
	}

	public Song getSongOfTheDay() {
		String json = fetchSongJson();
		if (json != null) {
			try {
				JSONObject obj = new JSONObject(json);
				String id = obj.getString("id");
				String title = obj.getString("title");
				String artiste = obj.getString("artiste");
				double length = obj.getDouble("songLength");
				return new Song(id, title, artiste, length);
			} catch (Exception e) {
				logger.severe("Error parsing JSON: " + e.getMessage());
			}
		}
		return null;
	}

	/**
	 * Fetch JSON from remote API. Protected so can be overridden or mocked for
	 * testing.
	 */
	protected String fetchSongJson() {
		try {
			HttpURLConnection conn = connectionSupplier.get();
			if (conn == null) {
				logger.severe("HttpURLConnection is null");
				return null;
			}
			conn.setRequestMethod("GET");

			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				try (BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
					StringBuilder response = new StringBuilder();
					String inputLine;
					while ((inputLine = in.readLine()) != null) {
						response.append(inputLine);
					}
					return response.toString();
				}
			} else {
				logger.warning("Non-OK response from API: " + conn.getResponseCode());
			}
		} catch (Exception e) {
			logger.severe("Failed to fetch song JSON: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Allows setting a custom URL for the song JSON API.
	 */
	public void setJsonApiUrl(String jsonApiUrl) {
		this.jsonApiUrl = jsonApiUrl;
	}
}