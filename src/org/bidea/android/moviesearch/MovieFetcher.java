/**
 * MovieSearch by Brion Swanson is licensed under a Creative Commons Attribution-Noncommercial 3.0 United States License.
 * Based on a work at http://bidea.org/software/
 *
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc/3.0/us/88x31.png" /></a><br /><span xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://purl.org/dc/dcmitype/Text" property="dc:title" rel="dc:type">MovieSearch</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://bidea.org" property="cc:attributionName" rel="cc:attributionURL">Brion Swanson</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/">Creative Commons Attribution-Noncommercial 3.0 United States License</a>.<br />Based on a work at <a xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://bidea.org/software/" rel="dc:source">bidea.org</a>.
 */
package org.bidea.android.moviesearch;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.util.Log;


/**
 * Class to fetch movie data on a separate thread.
 */
public class MovieFetcher extends Thread {
	
	private Handler handler = null;
	private String searchString = "";
	
	/** The following three arrays are associated by position */
	/** Note: three native arrays are faster than a set of nested Maps
	 *  or a multi-dimensional array */
	private JSONObject[] movieObjects;
	private String[] moviePosters;
	private String[] movieTitles;
	
	
	/**
	 * Creates a new MovieFetcher with a Hander to report when it's finished
	 * 
	 * @param handler the handler to notify when finished running
	 * @param searchString the string to search for
	 */
	public MovieFetcher(Handler handler, String searchString) {
		this.handler = handler;
		this.searchString = searchString;
	}
	
	@Override
	public void run() {
		TMDbClient.prepare();
		movieObjects = TMDbClient.findMovie(searchString);
		sortResults();
		handler.sendEmptyMessage(0);
	}
	
	/**
	 * Orders the results by rank + score and breaks out the title/poster
	 */
	private void sortResults() {
		HashMap<Double, Integer> orderedMap = new HashMap<Double, Integer>();
		
		for (int pos=0; pos<movieObjects.length; pos++){
			try {
				orderedMap.put(calculateOrder(movieObjects[pos].getString("score"), movieObjects[pos].getString("rating"), movieObjects[pos].getString("popularity")), 
							Integer.valueOf(pos));
			} catch (JSONException ex) {
				Log.e(MovieFetcher.class.getSimpleName(), "failed to sort results: " + ex.getMessage());
			}
		}
		
		// Sort the keys of the maps
		List<Double> sortedKeys = new LinkedList<Double>(orderedMap.keySet());
		Collections.sort(sortedKeys);
		
		// run through the list of keys and place them into a sorted list of movies
		LinkedList<JSONObject> sortedMovies = new LinkedList<JSONObject>();
		for ( Double key : sortedKeys )
		{
			sortedMovies.add(movieObjects[orderedMap.get(key)]);
		}
		
		//DEBUG
		Log.d(MovieFetcher.class.getSimpleName(), "Movie order before sorting...");
		for ( JSONObject movie : movieObjects ) {
			try {
				Log.d(MovieFetcher.class.getSimpleName(), movie.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		sortedMovies.toArray(movieObjects);
		//DEBUG
		Log.d(MovieFetcher.class.getSimpleName(), "Movie order AFTER sorting...");
		for ( JSONObject movie : movieObjects ) {
			try {
				Log.d(MovieFetcher.class.getSimpleName(), movie.getString("name"));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Double calculateOrder(String score, String ranking, String popularity) {
		Double dScore = Double.valueOf(score);
		Double dRank = Double.valueOf(ranking);
		Double dPop = Double.valueOf(popularity);
		
		return Double.valueOf(dScore.doubleValue() + dRank.doubleValue() + dPop.doubleValue());
	}
}
