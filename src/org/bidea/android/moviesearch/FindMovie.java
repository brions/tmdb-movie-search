/**
 * MovieSearch by Brion Swanson is licensed under a Creative Commons Attribution-Noncommercial 3.0 United States License.
 * Based on a work at http://bidea.org/software/
 *
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc/3.0/us/88x31.png" /></a><br /><span xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://purl.org/dc/dcmitype/Text" property="dc:title" rel="dc:type">MovieSearch</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://bidea.org" property="cc:attributionName" rel="cc:attributionURL">Brion Swanson</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/">Creative Commons Attribution-Noncommercial 3.0 United States License</a>.<br />Based on a work at <a xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://bidea.org/software/" rel="dc:source">bidea.org</a>.
 */
package org.bidea.android.moviesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;

public class FindMovie extends Activity {

	private static final int PROGRESS_DIALOG = 0x0;
	private static final int ERROR_DIALOG = 0x1;
	
	private Button searchButton;
	private EditText searchText;
	
	private ProgressDialog pd;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        
        setContentView(R.layout.main);
        
        searchButton = (Button) findViewById(R.id.search_button);
        searchText = (EditText) findViewById(R.id.search_text);
        
        // register the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
			
        	/** Create an anonymous click listener to show the progress dialog */
			public void onClick(View v) {
				
				// Prevent ANR timeouts by fetching the data on a separate thread
				MovieFetcher fetcher = new MovieFetcher(new Handler() {
					@Override
					public void handleMessage(Message msg) {
						pd.dismiss();
						// do list Activity here
					}
				}, searchText.getText().toString());
				
				// kick it off
				fetcher.start();

				// Since there's nothing for the user to do just now, show a progress dialog
	    		pd = ProgressDialog.show(FindMovie.this, "",
	    				"Searching TMDb...", true);
			}
		});
    }
    
    
        
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}
}