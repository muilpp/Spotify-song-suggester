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

			List<Result> resultsSongName;
			resultsSongName = DetectLanguage.detect(songName);
			if (resultsSongName.isEmpty()) return false;
			Result resultSongName = resultsSongName.get(0);

			if (spanishLocales.contains(resultSongName.language)) {
				return true;
			}

			for (Artist artist : artistList) {
				List<Result> resultsArtistName;
				resultsArtistName = DetectLanguage.detect(artist.getArtistName());

				if (resultsArtistName.isEmpty() || spanishSingersToNotRemove.contains(artist.getArtistName()))
					break;
				Result resultArtistName = resultsArtistName.get(0);

				if (spanishLocales.contains(resultArtistName.language)) {
					return true;
				}
			}
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
}