package com.sddevops.jenkins_maven.eclipse;

import java.util.Comparator;

public class Song {
	private String id;
	private String title;
	private String artiste;
	private double songLength;

	public Song(String id, String title, String artiste, double songLength) {
		this.id = id;
		this.title = title;
		this.artiste = artiste;
		this.songLength = songLength;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getArtiste() {
		return artiste;
	}

	public double getSongLength() {
		return songLength;
	}

	public void setArtiste(String artiste) {
		this.artiste = artiste;
	}

	// ✅ Lambda-based comparator for sorting by title (ascending)
	public static final Comparator<Song> titleComparator = Comparator.comparing(Song::getTitle);

	// ✅ Lambda-based comparator for sorting by song length (descending)
	public static final Comparator<Song> songLengthComparator = Comparator.comparingDouble(Song::getSongLength)
			.reversed();
}
