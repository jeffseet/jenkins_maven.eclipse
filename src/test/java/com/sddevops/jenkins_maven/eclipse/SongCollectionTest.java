package com.sddevops.jenkins_maven.eclipse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class SongCollectionTest {

	private SongCollection sc;
	private Song s1;
	private Song s2;
	private Song s3;
	private Song s4;
	private static final int SONG_COLLECTION_SIZE = 4;

	@BeforeEach
	void setUp() {
		sc = new SongCollection();
		s1 = new Song("001", "good 4 u", "Olivia Rodrigo", 3.59);
		s2 = new Song("002", "Peaches", "Justin Bieber", 3.18);
		s3 = new Song("003", "MONTERO", "Lil Nas", 2.3);
		s4 = new Song("004", "bad guy", "billie eilish", 3.14);
		sc.addSong(s1);
		sc.addSong(s2);
		sc.addSong(s3);
		sc.addSong(s4);
	}

	@AfterEach
	void tearDown() {
		sc = null;
	}

	@Test
	void testGetSongs() {
		assertEquals(SONG_COLLECTION_SIZE, sc.getSongs().size());
	}

	@Test
	void testAddSong() {
		Song newSong = new Song("005", "Blinding Lights", "The Weeknd", 3.2);
		sc.addSong(newSong);
		assertEquals(SONG_COLLECTION_SIZE + 1, sc.getSongs().size());
		assertTrue(sc.getSongs().contains(newSong));
	}

	@Test
	void testSortSongsByTitle() {
		List<String> sortedTitles = sc.getSongs().stream().sorted(Song.titleComparator).map(Song::getTitle)
				.collect(Collectors.toList());

		assertEquals(List.of("MONTERO", "Peaches", "bad guy", "good 4 u"), sortedTitles);
	}

	@Test
	void testSortSongsBySongLength() {
		List<Double> sortedLengths = sc.getSongs().stream().sorted(Song.songLengthComparator).map(Song::getSongLength)
				.collect(Collectors.toList());

		assertEquals(List.of(3.59, 3.18, 3.14, 2.3), sortedLengths);
	}

	@Test
	void testGetSongById() {
		Song found = sc.getSongById("004");
		assertNotNull(found);
		assertEquals("billie eilish", found.getArtiste());

		assertNull(sc.getSongById("999"));
	}

	@Test
	void testGetSongOfTheDay_validJson() {
		String mockJson = """
				{
				    "id": "010",
				    "title": "Mock Song",
				    "artiste": "Mock Artist",
				    "songLength": 4.25
				}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(mockJson).when(collection).fetchSongJson();

		Song song = collection.getSongOfTheDay();
		assertNotNull(song);
		assertEquals("010", song.getId());
		assertEquals("Mock Song", song.getTitle());
		assertEquals("Mock Artist", song.getArtiste());
		assertEquals(4.25, song.getSongLength(), 0.01);
	}

	@Test
	void testGetSongOfTheDay_nullJson() {
		SongCollection collection = spy(new SongCollection());
		doReturn(null).when(collection).fetchSongJson();

		Song song = collection.getSongOfTheDay();
		assertNull(song);
	}

	@Test
	void testGetSongOfTheDay_malformedJson() {
		String badJson = "not a json";

		SongCollection collection = spy(new SongCollection());
		doReturn(badJson).when(collection).fetchSongJson();

		Song song = collection.getSongOfTheDay();
		assertNull(song);
	}

	@Test
	void testGetSongOfTheDay_artistInitialHandling() {
		String taylorJson = """
				{
				    "id": "TS1",
				    "title": "Love Story",
				    "artiste": "Taylor Swift",
				    "songLength": 3.5
				}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(taylorJson).when(collection).fetchSongJson();

		Song song = collection.getSongOfTheDay();
		assertNotNull(song);
		assertEquals("Taylor Swift", song.getArtiste());
		assertEquals("Love Story", song.getTitle());
		assertEquals(3.5, song.getSongLength(), 0.01);
	}

	// --- New Mockito test for fetchSongJson() coverage ---

	@Test
	void testFetchSongJson_withMockedHttpURLConnection() throws Exception {
		// Prepare mock HttpURLConnection
		HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);

		// Mock the InputStream with JSON data
		String expectedJson = "{\"id\":\"999\",\"title\":\"Test Song\",\"artiste\":\"Test Artist\",\"songLength\":3.3}";
		InputStream mockInputStream = new ByteArrayInputStream(expectedJson.getBytes());

		// Configure mock behavior
		when(mockConn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
		when(mockConn.getInputStream()).thenReturn(mockInputStream);

		// Inject the connection supplier into SongCollection
		SongCollection collection = new SongCollection(() -> mockConn);

		// Call the protected method via subclass to expose it or reflection,
		// but since fetchSongJson is protected, can call directly here because same
		// package
		String jsonResult = collection.fetchSongJson();

		// Assert the JSON string returned matches expected
		assertEquals(expectedJson, jsonResult);
	}

	@Test
	void testFetchSongJson_nonOkResponseLogsWarning() throws Exception {
		HttpURLConnection mockConn = Mockito.mock(HttpURLConnection.class);
		when(mockConn.getResponseCode()).thenReturn(HttpURLConnection.HTTP_NOT_FOUND);

		SongCollection collection = new SongCollection(() -> mockConn);
		String result = collection.fetchSongJson();

		assertNull(result);
	}

	@Test
	void testFetchSongJson_throwsExceptionLogsSevere() {
		// Create SongCollection with connection factory that throws an exception
		SongCollection collection = new SongCollection(() -> {
			throw new RuntimeException("Connection error");
		});

		String result = collection.fetchSongJson();

		assertNull(result);
	}
}