package com.sddevops.jenkins_maven.eclipse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SongCollectionTest {

	private SongCollection sc;
	private Song s1;
	private Song s2;
	private Song s3;
	private Song s4;
	private static final int SONG_COLLECTION_SIZE = 4;
	private SongCollection scWithSize;
	private SongCollection scWithSize1;

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
		scWithSize = new SongCollection(5);
		scWithSize1 = new SongCollection(1);
	}

	@AfterEach
	void tearDown() {
		sc = null;
		scWithSize = null;
		scWithSize1 = null;
	}

	@Test
	void testGetSongs() {
		List<Song> testSc = sc.getSongs();
		assertEquals(SONG_COLLECTION_SIZE, testSc.size());
	}

	@Test
	void testAddSong() {
		assertEquals(SONG_COLLECTION_SIZE, sc.getSongs().size());
		assertTrue(sc.addSong(s1));
		assertEquals(SONG_COLLECTION_SIZE + 1, sc.getSongs().size());

		assertTrue(scWithSize1.addSong(s1));
		assertFalse(scWithSize1.addSong(s2));
		assertEquals(1, scWithSize1.getSongs().size());
	}

	@Test
	void testSortSongsByTitle() {
		List<Song> sorted = sc.sortSongsByTitle();
		assertEquals("MONTERO", sorted.get(0).getTitle());
		assertEquals("Peaches", sorted.get(1).getTitle());
		assertEquals("bad guy", sorted.get(2).getTitle());
		assertEquals("good 4 u", sorted.get(3).getTitle());
	}

	@Test
	void testSortSongsBySongLength() {
		List<Song> sorted = sc.sortSongsBySongLength();
		assertEquals(3.59, sorted.get(0).getSongLength());
		assertEquals(3.18, sorted.get(1).getSongLength());
		assertEquals(3.14, sorted.get(2).getSongLength());
		assertEquals(2.3, sorted.get(3).getSongLength());
	}

	@Test
	void testFindSongById() {
		Song song = sc.findSongById("004");
		assertNotNull(song);
		assertEquals("billie eilish", song.getArtiste());

		assertNull(sc.findSongById("does-not-exist"));
	}

	@Test
	void testFindSongByTitle() {
		Song song = sc.findSongByTitle("MONTERO");
		assertNotNull(song);
		assertEquals("Lil Nas", song.getArtiste());

		assertNull(sc.findSongByTitle("unknown title"));
	}

	@Test
	void testFetchSongOfTheDay() {
		String mockJson = """
					{
						"id": "001",
						"title": "Mock Song",
						"artiste": "Mock Artist",
						"songLength": 4.25
					}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(mockJson).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNotNull(result);
		assertEquals("001", result.getId());
		assertEquals("Mock Song", result.getTitle());
		assertEquals("Mock Artist", result.getArtiste());
		assertEquals(4.25, result.getSongLength());
	}

	@Test
	void testInvalidFetchSongOfTheDay() {
		SongCollection collection = spy(new SongCollection());
		doReturn(null).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNull(result);
	}

	@Test
	void testExceptionHandlingInFetchSongOfTheDay() {
		SongCollection collection = spy(new SongCollection());
		doThrow(new RuntimeException("API failed")).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNull(result);
		assertEquals(0, collection.getSongs().size());
	}

	@Test
	void testFetchSongOfTheDay_TaylorSwift() {
		String mockJson = """
					{
						"id": "005",
						"title": "You Belong With Me",
						"artiste": "Taylor Swift",
						"songLength": 3.50
					}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(mockJson).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNotNull(result);
		assertEquals("TS", result.getArtiste());
		assertEquals(1, collection.getSongs().size());
	}

	@Test
	void testFetchSongOfTheDay_BrunoMars() {
		String mockJson = """
					{
						"id": "006",
						"title": "Just The Way You Are",
						"artiste": "Bruno Mars",
						"songLength": 3.75
					}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(mockJson).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNotNull(result);
		assertEquals("BM", result.getArtiste());
		assertEquals(1, collection.getSongs().size());
	}

	@Test
	void testFetchSongOfTheDay_OtherArtist_NotAdded() {
		String mockJson = """
					{
						"id": "007",
						"title": "Perfect",
						"artiste": "Ed Sheeran",
						"songLength": 4.20
					}
				""";

		SongCollection collection = spy(new SongCollection());
		doReturn(mockJson).when(collection).fetchSongJson();

		Song result = collection.fetchSongOfTheDay();
		assertNotNull(result);
		assertEquals("Ed Sheeran", result.getArtiste());
		assertEquals(0, collection.getSongs().size()); // Not added
	}
}