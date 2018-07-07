package xyz.spotifyrecommender.model.helper;

import static xyz.spotifyrecommender.model.Constant.LANGUAGE_DETECTOR_KEY;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.detectlanguage.DetectLanguage;
import com.detectlanguage.Result;
import com.detectlanguage.errors.APIError;

import xyz.spotifyrecommender.model.webservicedata.Artist;
import xyz.spotifyrecommender.model.webservicedata.Track;

public class LanguageHelper {
    private static final Logger LOGGER = Logger.getLogger(LanguageHelper.class.getName());

	public static boolean isSpanish(String songName, List<Artist> artistList) {
		DetectLanguage.apiKey = LANGUAGE_DETECTOR_KEY;
		// https://github.com/detectlanguage/detectlanguage-java

		try {
			List<String> spanishLocales = new ArrayList<>();
			spanishLocales.add("es");
			spanishLocales.add("ca");
			spanishLocales.add("gn");
			
			List<String> spanishSingersToNotRemove = new ArrayList<>();
			spanishSingersToNotRemove.add("Manu Chao");
			spanishSingersToNotRemove.add("Txarango");

			List<Result> resultsSongName;
			resultsSongName = DetectLanguage.detect(songName);
			if (resultsSongName.isEmpty()) return false;
			Result resultSongName = resultsSongName.get(0);

//			LOGGER.log(Level.INFO, "Language [{0}]", resultSongName.language);
//			System.out.println("Is reliable: " + resultSongName.isReliable);
//			System.out.println("Confidence: " + resultSongName.confidence);
//
//			LOGGER.log(Level.INFO, "Song name [{0}]", songName);
			if (spanishLocales.contains(resultSongName.language)) {
//				LOGGER.log(Level.INFO, "Remove song [{0}]", songName);
				return true;
			}

			for (Artist artist : artistList) {
				List<Result> resultsArtistName;
				resultsArtistName = DetectLanguage.detect(artist.getArtistName());

				if (resultsArtistName.isEmpty() || spanishSingersToNotRemove.contains(artist.getArtistName()))
					break;
				Result resultArtistName = resultsArtistName.get(0);
//				System.out.println("Language: " + resultArtistName.language);
//				System.out.println("Is reliable: " + resultArtistName.isReliable);
//				System.out.println("Confidence: " + resultArtistName.confidence);
//				LOGGER.log(Level.INFO, "Artist name [{0}]", artist.getArtistName());

				if (spanishLocales.contains(resultArtistName.language)) {
//					LOGGER.log(Level.INFO, "Remove artist [{0}]", artist.getArtistName());
					return true;
				}
			}
//			LOGGER.log(Level.INFO, "---------------------------------------------");

//			return spanishLocales.contains(result.language) ? true : false;
		} catch (APIError e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		return false;
	}

	public static void removeSpanishSongs(Set<Track> recs) {
		Set<Track> trackSet = new HashSet<Track>();
        for (Track track : recs) {
        	if (!LanguageHelper.isSpanish(track.getSongName(), track.artistList())) {
        		trackSet.add(track);
        	}
        }

        recs.clear();
        recs.addAll(trackSet);
	}

	public static void main(String[] args) {
		List<Artist> artistList = new ArrayList<>();
		Artist artist = new Artist();
		artist.setArtistName("Los Auténticos Decandentes");
		artistList.add(artist);
		isSpanish("Auténtica - Remasterized 2001", artistList);
	}
}