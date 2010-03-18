package org.bidea.android.moviesearch;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.EditText;

public class FindMovie extends Activity {

	private static final int PROGRESS_DIALOG = 0;
	private static final int ERROR_DIALOG = 1;
	
	private Button searchButton;
	private EditText searchText;
	
	private TMDbClient tmdbClient;
	private String errorMessage = "";
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        tmdbClient = new TMDbClient();
        
        searchButton = (Button) findViewById(R.id.search_button);
        searchText = (EditText) findViewById(R.id.search_text);
        
        // register the search button
        searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!tmdbClient.findMovie(searchText.getText().toString())) {
					showDialog(ERROR_DIALOG);
				}
			}
		});
    }
    
    @Override
    protected Dialog onCreateDialog(int id) {
    	Dialog dialog;
    	switch(id) {
    	case PROGRESS_DIALOG:
    		dialog = ProgressDialog.show(FindMovie.this, "",
    				"Searching TMDb...", true);
    		break;
    	case ERROR_DIALOG:
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(tmdbClient.getLastError())
    			.setCancelable(true)
    			.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
    		dialog = builder.create();
    		break;
    	default:
    		dialog = null;
    	}
    	return dialog;
    }
    
	/* (non-Javadoc)
	 * @see android.app.Activity#onContextItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		return super.onContextItemSelected(item);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateContextMenu(android.view.ContextMenu, android.view.View, android.view.ContextMenu.ContextMenuInfo)
	 */
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// TODO Auto-generated method stub
		super.onCreateContextMenu(menu, v, menuInfo);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle)
	 */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
    
	
}