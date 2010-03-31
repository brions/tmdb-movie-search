/**
 * 
 */
package org.bidea.android.moviesearch;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

/**
 * Searches themoviedb.org for a movie by title and updates the provided SQLite
 * database with the results.
 */
public class TMDbClient {
	private static final String TMDB_BASE_URL="http://api.themoviedb.org/2.1/Movie.search/en/json/c94816c61f718f3c07d74f59a2055412/";
	
	private static DefaultHttpClient http;
	private static String lastError = "";

	static {
		http = new DefaultHttpClient();
	}

	/**
	 * Clear out previous search information in preparation for another search.
	 * 
	 * @param ctx Context of this application.
	 */
	public static void prepare() {
		lastError = "";
	}
	
	/**
	 * Search TMDb for <code>title</code>.
	 * 
	 * @param title	the <code>title</code> to search for.
	 * @return <code>true</code> if the database was updated, <code>false</code> otherwise.
	 */
	public static JSONObject[] findMovie(String title) {

//		try {
//			Thread.sleep(5000);
//			return;
//		} catch (InterruptedException e1) {
//			Log.d(TMDbClient.class.getSimpleName(), e1.getMessage());
//		}
		JSONObject[] results = null;
		
		// prepare the request
		HttpGet get = new HttpGet(TMDB_BASE_URL + URLEncoder.encode(title));

		HttpResponse response = null;
		try {
			response = http.execute(get);

			// examine response
			Log.i(TMDbClient.class.getSimpleName(), "Got " + response.getStatusLine() + " from " + get.getURI().toString() );

			// get the response entity
			HttpEntity entity = response.getEntity();

			// if the entity is null, we don't need to clean up the connection
			if (entity != null) {

				// read the response into a string
				byte[] buffer = new byte[512];
				StringBuffer sb = new StringBuffer();
				InputStream is = entity.getContent();
				
				while (is.read(buffer, 0, 512) > -1) {
					String buff = new String(buffer, "UTF-8").trim();
					Log.d(TMDbClient.class.getSimpleName(), "read: " + buff);
					sb.append(buff);
					Arrays.fill(buffer, (byte)0);
				}
				
				// now parse out the document
				JSONTokener parser = new JSONTokener(sb.toString());
				
				try {
					// parse the JSONObjects out of the document until we have no more
					
					JSONArray movies = new JSONArray(parser);
					int numMovies = movies.length();
					results = new JSONObject[numMovies];
					
					for (int pos=0; pos < numMovies; pos++) {
						JSONObject movie = (JSONObject)movies.get(pos);

						results[pos] = movie;
//						// print out the JSONObject and its attributes
//						for (Iterator<String> keys = movie.keys(); keys.hasNext(); ) {
//							String key = keys.next();
//							Log.d(TMDbClient.class.getSimpleName(), key+":"+movie.get(key));
//						}
					}
				} catch (JSONException e) {
					Log.e(TMDbClient.class.getSimpleName(), "Error parsing JSON: "+e.getMessage(), e);
					lastError = e.getMessage();
				}
			}
			
		} catch (ClientProtocolException ex) {
			Log.e(TMDbClient.class.getSimpleName(), "HTTP error: "
					+ ex.getMessage());
			lastError = ex.getMessage();
		} catch (IOException ex) {
			Log.e(TMDbClient.class.getSimpleName(), "IO error: "
					+ ex.getMessage());
			lastError = ex.getMessage();
		} finally {
			// clean up our request
			if (response != null && response.getEntity() != null) {
				try {
					
					// this is to free our connection resources
					response.getEntity().consumeContent();
				} catch (IOException ex) {
					Log.w(TMDbClient.class.getSimpleName(), ex.getMessage());
				}
			}
		}
		
		return results;
	}
	
	public static String getLastError() {
		return lastError;
	}
}
