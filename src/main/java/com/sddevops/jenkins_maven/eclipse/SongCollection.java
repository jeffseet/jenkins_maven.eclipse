package com.sddevops.jenkins_maven.eclipse;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

public class SongCollection {

	private static final int DEFAULT_CAPACITY = 20;
	private final List<Song> songs;
	private final int capacity;

	public SongCollection() {
		this(DEFAULT_CAPACITY);
	}

	public SongCollection(int capacity) {
		this.capacity = capacity;
		this.songs = new ArrayList<>();
	}

	public List<Song> getSongs() {
		return new ArrayList<>(songs); // Defensive copy
	}

	public boolean addSong(Song song) {
		if (songs.size() < capacity) {
			return songs.add(song);
		}
		return false;
	}

	public List<Song> sortSongsByTitle() {
		songs.sort(Song.titleComparator);
		return songs;
	}

	public List<Song> sortSongsBySongLength() {
		songs.sort(Song.songLengthComparator);
		return songs;
	}

	public Song findSongById(String id) {
		return songs.stream().filter(s -> s.getId().equals(id)).findFirst().orElse(null);
	}

	public Song findSongByTitle(String title) {
		return songs.stream().filter(s -> s.getTitle().equalsIgnoreCase(title)).findFirst().orElse(null);
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
			}
		} catch (Exception e) {
			System.err.println("Failed to fetch song JSON: " + e.getMessage());
		}
		return null;
	}

	public Song fetchSongOfTheDay() {
		try {
			String jsonStr = fetchSongJson();
			if (jsonStr == null)
				return null;

			JSONObject json = new JSONObject(jsonStr);
			String id = json.getString("id");
			String title = json.getString("title");
			String artiste = json.getString("artiste");
			double length = json.getDouble("songLength");

			if ("Taylor Swift".equals(artiste)) {
				artiste = "TS";
			} else if ("Bruno Mars".equals(artiste)) {
				artiste = "BM";
			}

			Song song = new Song(id, title, artiste, length);
			addSong(song);
			return song;

		} catch (Exception e) {
			System.err.println("Error parsing song of the day: " + e.getMessage());
			return null;
		}
	}
}
