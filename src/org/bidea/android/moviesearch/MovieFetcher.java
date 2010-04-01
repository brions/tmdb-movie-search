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
	private JSONObject[] movieObjects = null;
	private String[] moviePosters = null;
	private String[] movieTitles = null;
	
	
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
		// clear out any previous search
		prepare();
		
		// find the movie(s) if any
		movieObjects = TMDbClient.findMovie(searchString);

		//create the other (empty?) lists
		moviePosters = new String[movieObjects.length];
		movieTitles = new String[movieObjects.length];
		
		// sort the results
		sortResults();
		
		// notify the calling thread that we're complete
		handler.sendEmptyMessage(0);
	}
	
	/**
	 * @return the list of JSONObjects representing the movies
	 */
	public JSONObject[] getMoviesObjects() {
		return movieObjects == null ? new JSONObject[0] : movieObjects;
	}
	
	/**
	 * @return the list of movie poster URLs
	 */
	public String[] getMoviePosters() {
		return moviePosters == null ? new String[0] : movieTitles;
	}
	
	/**
	 * @return the list of movie titles
	 */
	public String[] getMovieTitles() {
		return movieTitles == null ? new String[0] : movieTitles;
	}
	
	/**
	 * Clean things up before a new search
	 */
	private void prepare() {
		TMDbClient.prepare();
		movieObjects = null;
		moviePosters = null;
		movieTitles = null;
	}
	
	/**
	 * Orders the results by rank + score and breaks out the title/poster
	 */
	private void sortResults() {
		HashMap<SortRating, Integer> orderedMap = new HashMap<SortRating, Integer>();
		
		for (int pos=0; pos<movieObjects.length; pos++){
			try {
				orderedMap.put(new SortRating(movieObjects[pos].getDouble("score"), 
											  movieObjects[pos].getDouble("rating"), 
											  movieObjects[pos].getDouble("popularity")), 
							   Integer.valueOf(pos));
			} catch (JSONException ex) {
				Log.e(MovieFetcher.class.getSimpleName(), "failed to sort results: " + ex.getMessage());
			}
		}
		
		// Sort the keys of the maps
		List<SortRating> sortedKeys = new LinkedList<SortRating>(orderedMap.keySet());
		Collections.sort(sortedKeys);
		
		// run through the list of keys and place them into a sorted list of movies
		LinkedList<JSONObject> sortedMovies = new LinkedList<JSONObject>();
		for ( SortRating key : sortedKeys )
		{
			sortedMovies.add(movieObjects[orderedMap.get(key)]);
		}
		
		sortedMovies.toArray(movieObjects);
		
		int position = 0;
		for ( JSONObject movie : movieObjects ) {
			try {
				if (movie != null) {
					Log.d(MovieFetcher.class.getSimpleName(), movie.getString("name"));
					// put the poster URL and title in their respective lists
					movieTitles[position] = movie.getString("name");
					moviePosters[position] = movie.getJSONArray("posters").getJSONObject(0).getJSONObject("image").getString("url");
				}
			} catch (JSONException e) {
				Log.i(MovieFetcher.class.getSimpleName(), e.getMessage());
			}
			position++;
		}
	}
	
	/**
	 * Comparable object to deal with rating ties.
	 */
	private class SortRating implements Comparable<SortRating> {
		double score;
		double rank;
		double popularity;
		
		/**
		 * Create a new SortRating with three rating components
		 */
		SortRating(double score, double rank, double popularity) {
			this.score = score;
			this.rank = rank;
			this.popularity = popularity;
		}
		
		/**
		 * Compare in ascending order: score, score + rank, score + rank + popularity
		 */
		public int compareTo(SortRating another) {
			int rating = (int)(another.score - this.score);
			
			if (rating == 0) {
				rating = (int)((another.score + another.rank) - (this.score + this.rank));
			}
			
			if (rating == 0) {
				rating = (int)((another.score + another.rank + another.popularity) - 
						 (this.score + this.rank + this.popularity));
			}
			
			return rating;
		}
		
		// make sure SortRatings are equal if they have identical values
		@Override
		public boolean equals(Object o) {
			if (o==null || !(o instanceof SortRating))
				return false;
			SortRating other = (SortRating)o;
			return (other.score == score && other.rank == rank && other.popularity == popularity);
		}
		
		// make sure identical values hash to the same thing
		@Override
		public int hashCode() {
			return (""+score+rank+popularity).hashCode();
		}
	}
}
