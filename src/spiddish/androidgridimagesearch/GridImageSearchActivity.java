package spiddish.androidgridimagesearch;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

public class GridImageSearchActivity extends Activity {
	EditText etQuery;
	Button btnSearch;
	Button btnMore;
	GridView gvResults;
	ArrayList<ImageResult> imageResults = new ArrayList<ImageResult>();
	ImageResultArrayAdapter imageAdapter;
	SearchSettings searchSettings = new SearchSettings();
	int offset = 0;
	private static final int REQUEST_CODE = 20;
	String currentQuery;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_grid_image_search);
		setupViews();
		imageAdapter = new ImageResultArrayAdapter(this, R.layout.item_image_result, imageResults);
		gvResults.setAdapter(imageAdapter);
		gvResults.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View parent, int position, long rowId) {
				Intent i = new Intent(getApplicationContext(), ImageDisplayActivity.class);
				ImageResult imageResult = imageResults.get(position);
				//i.putExtra("url", imageResult.getFullUrl());
				i.putExtra("result", imageResult);
				startActivity(i);
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.grid_image_search, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case R.id.miSettings:
	    	editSettings();
	    	Toast.makeText(this, "Menu Item 1 selected", Toast.LENGTH_SHORT).show();
	    	break;
	    default:
	    	break;
	    }

	    return true;
	}

	private void editSettings() {
		Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
		i.putExtra("settings", searchSettings);
		startActivityForResult(i, REQUEST_CODE);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
			searchSettings = (SearchSettings) data.getSerializableExtra("settings");
			Toast.makeText(this, "activity returned", Toast.LENGTH_SHORT).show();
		} 
	}

	public void onImageSearch(View v) {
		currentQuery = etQuery.getText().toString();
		Toast.makeText(this,  "searching for: " + currentQuery, Toast.LENGTH_SHORT).show();
		AsyncHttpClient client = new AsyncHttpClient();
		offset = 0;
		client.get(createUrl(currentQuery, offset, searchSettings), 
					new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults;
				try {
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageResults.clear();
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
					btnMore.setVisibility(View.VISIBLE);
					Log.d("DEBUG", imageResults.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void onMoreImages(View v) {
		AsyncHttpClient client = new AsyncHttpClient();
		offset += 3;
		client.get(createUrl(currentQuery, offset, searchSettings), 
					new JsonHttpResponseHandler() {
			@Override
			public void onSuccess(JSONObject response) {
				JSONArray imageJsonResults;
				try {
					imageJsonResults = response.getJSONObject("responseData").getJSONArray("results");
					imageAdapter.addAll(ImageResult.fromJSONArray(imageJsonResults));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private String createUrl(String query, int start, SearchSettings settings) {
		String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&rsz=3";
		url += "&q=" + Uri.encode(query);
		url += "&start=" + start;
		String colorFilter = settings.getColorFilter();
		if (colorFilter != null && !colorFilter.equals("none"))
			url += "&imgcolor=" + settings.getColorFilter();
		String imageSize = settings.getImageSize();
		if (imageSize != null && !imageSize.equals("all"))
			url += "&imgsz=" + settings.getImageSize();
		String imageType = settings.getImageType();
		if (imageType != null && !imageType.equals("all"))
			url += "&imgtype=" + settings.getImageType();
		String siteFilter = settings.getSiteFilter();
		if (siteFilter != null && !siteFilter.trim().equals(""))
			url += "&as_sitesearch=" + settings.getSiteFilter();
		Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
		
		return url;
	}

	public void setupViews() {
		etQuery = (EditText) findViewById(R.id.etQuery);
		btnSearch = (Button) findViewById(R.id.btnSearch);
		gvResults = (GridView) findViewById(R.id.gvResults);
		btnMore = (Button) findViewById(R.id.btnMore);
		btnMore.setVisibility(View.INVISIBLE);
	}
}
