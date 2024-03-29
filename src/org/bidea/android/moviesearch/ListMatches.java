/**
 * MovieSearch by Brion Swanson is licensed under a Creative Commons Attribution-Noncommercial 3.0 United States License.
 * Based on a work at http://bidea.org/software/
 *
 * <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/"><img alt="Creative Commons License" style="border-width:0" src="http://i.creativecommons.org/l/by-nc/3.0/us/88x31.png" /></a><br /><span xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://purl.org/dc/dcmitype/Text" property="dc:title" rel="dc:type">MovieSearch</span> by <a xmlns:cc="http://creativecommons.org/ns#" href="http://bidea.org" property="cc:attributionName" rel="cc:attributionURL">Brion Swanson</a> is licensed under a <a rel="license" href="http://creativecommons.org/licenses/by-nc/3.0/us/">Creative Commons Attribution-Noncommercial 3.0 United States License</a>.<br />Based on a work at <a xmlns:dc="http://purl.org/dc/elements/1.1/" href="http://bidea.org/software/" rel="dc:source">bidea.org</a>.
 */
package org.bidea.android.moviesearch;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ListMatches extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.title_list);
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			String[] movie_titles = (String[])extras.get("titles");
			ArrayAdapter<String> titles = new ArrayAdapter<String>(this, R.layout.title_row, movie_titles);
			setListAdapter(titles);
		}
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		// TODO Auto-generated method stub
		super.onListItemClick(l, v, position, id);
	}
}
