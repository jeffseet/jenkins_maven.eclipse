package com.sddevops.jenkins_maven.eclipse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONObject;

public class SongCollection {

	private static final Logger logger = Logger.getLogger(SongCollection.class.getName());
	private static final int DEFAULT_CAPACITY = 20;

	private final List<Song> songs;

	public SongCollection() {
		this.songs = new ArrayList<>(DEFAULT_CAPACITY);
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

	protected String fetchSongJson() {
		String urlString = "https://mocki.io/v1/36c94419-b141-4cfd-96fa-327f4872aca6";
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
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
}