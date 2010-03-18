/**
 * 
 */
package org.bidea.android.moviesearch;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.app.AlertDialog;
import android.util.Log;
import android.util.Xml.Encoding;

/**
 * Searches themoviedb.org for a movie by title and updates the provided SQLite
 * database with the results.
 * 
 * @author brion
 */
public class TMDbClient {
	private static final String TMDB_BASE_URL="http://api.themoviedb.org/2.1/Movie.search/en/xml/c94816c61f718f3c07d74f59a2055412/";
	
	private XmlPullParser parser;
	private DefaultHttpClient http;
	private String lastError = "";

	public TMDbClient() {
		try {
			parser = XmlPullParserFactory.newInstance().newPullParser();
			http = new DefaultHttpClient();
		} catch (XmlPullParserException ex) {
			Log.e(this.getClass().getSimpleName(), "Failed to create XML parser: " + ex.getMessage());
		}
	}

	/**
	 * Search TMDb for <code>title</code>.
	 * 
	 * @param title	the <code>title</code> to search for.
	 * @return <code>true</code> if the database was updated, <code>false</code> otherwise.
	 */
	public boolean findMovie(String title) {

		// prepare the request
		HttpGet get = new HttpGet(TMDB_BASE_URL + URLEncoder.encode(title));

		HttpResponse response = null;
		try {
			response = http.execute(get);

			// examine response
			Log.i(this.getClass().getSimpleName(), "Got " + response.getStatusLine() + " from " + get.getURI().toString() );

			// get the response entity
			HttpEntity entity = response.getEntity();

			// if the entity is null, we don't need to clean up the connection
			if (entity != null) {

				parser.setInput(entity.getContent(), Encoding.UTF_8.toString());
				
				while (true) {
					try {
						// pull-parse the xml tags out of the document until we have no more tags (exception)
						int type = parser.nextTag();
						
						if (type == XmlPullParser.START_TAG ) {
							
							// print out the tag and it's attributes
							Log.d(this.getClass().getSimpleName(), parser.getName());
							for (int i = 0; i < parser.getAttributeCount(); i++) {
								Log.d(this.getClass().getSimpleName(), parser.getAttributeName(i) + "=" + parser.getAttributeValue(i));
							}
							
							try {
								// pull the next text element
								Log.d(this.getClass().getSimpleName(), parser.nextText());
							} catch (XmlPullParserException ex) {
								// ignore
							}
						}
					} catch (XmlPullParserException ex) {
						// do nothing
					}
				}

			}
			
			return true;
			
		} catch (ClientProtocolException ex) {
			Log.e(this.getClass().getSimpleName(), "HTTP error: "
					+ ex.getMessage());
			lastError = ex.getMessage();
		} catch (IOException ex) {
			Log.e(this.getClass().getSimpleName(), "IO error: "
					+ ex.getMessage());
			lastError = ex.getMessage();
		} catch (XmlPullParserException ex) {
			Log.e(this.getClass().getSimpleName(), "XML error: "
					+ ex.getMessage());
			lastError = ex.getMessage();
		} finally {
			// clean up our request
			if (response != null && response.getEntity() != null) {
				try {
					
					// this is to free our connection resources
					response.getEntity().consumeContent();
				} catch (IOException ex) {
					Log.w(this.getClass().getSimpleName(), ex.getMessage());
				}
			}
		}

		return false;
	}
	
	public String getLastError() {
		return lastError;
	}
}
