package spiddish.androidgridimagesearch;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

public class SettingsActivity extends Activity {
	private SearchSettings searchSettings;
	private EditText etSiteFilter;
	private Spinner spinnerImageType;
	private Spinner spinnerImageSize;
	private Spinner spinnerColorFilter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settings);
		searchSettings = (SearchSettings) getIntent().getSerializableExtra("settings");
		setupInputs();
		setupAdapters();
		setupListeners();
		setupSearchSettings();
	}

	private void setupSearchSettings() {
		String setting;
		setting = searchSettings.getColorFilter();
		if (setting != null) 
			setSpinnerToValue(spinnerColorFilter, setting);
		setting = searchSettings.getImageType();
		if (setting != null) 
			setSpinnerToValue(spinnerImageType, setting);
		setting = searchSettings.getImageSize();
		if (setting != null) 
			setSpinnerToValue(spinnerImageSize, setting);
		setting = searchSettings.getSiteFilter();
		if (setting != null)
			etSiteFilter.setText(setting);
	}

	public void setSpinnerToValue(Spinner spinner, String value) {
		int index = 0;
		SpinnerAdapter adapter = spinner.getAdapter();
		for (int i = 0; i < adapter.getCount(); i++) {
			if (adapter.getItem(i).equals(value)) {
				index = i;
			}
		}
		spinner.setSelection(index);
	}
	
	private void setupListeners() {
		// color listener
		spinnerColorFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				searchSettings.setColorFilter(parent.getItemAtPosition(pos).toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		// image size listener
		spinnerImageSize.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				searchSettings.setImageSize(parent.getItemAtPosition(pos).toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
		// image color listener
		spinnerImageType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
				searchSettings.setImageType(parent.getItemAtPosition(pos).toString());
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent) { }
		});
	}

	private void setupAdapters() {
		// color filter adapter
		ArrayAdapter<CharSequence> adapterColorFilter = ArrayAdapter.createFromResource(this, R.array.color_filter_array, android.R.layout.simple_spinner_item);
		adapterColorFilter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerColorFilter.setAdapter(adapterColorFilter);
		// image type adapter
		ArrayAdapter<CharSequence> adapterImageType = ArrayAdapter.createFromResource(this, R.array.image_type_array, android.R.layout.simple_spinner_item);
		adapterImageType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerImageType.setAdapter(adapterImageType);
		// image size adapter
		ArrayAdapter<CharSequence> adapterImageSize = ArrayAdapter.createFromResource(this, R.array.image_size_array, android.R.layout.simple_spinner_item);
		adapterImageSize.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerImageSize.setAdapter(adapterImageSize);
	}

	private void setupInputs() {
		etSiteFilter = (EditText) findViewById(R.id.etSiteFilter);
		spinnerColorFilter = (Spinner) findViewById(R.id.spinnerColorFilter);
		spinnerImageSize = (Spinner) findViewById(R.id.spinnerImageSize);
		spinnerImageType = (Spinner) findViewById(R.id.spinnerImageType);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	@Override
	public void finish() {
		searchSettings.setSiteFilter(etSiteFilter.getText().toString());
		Intent data = new Intent();
		data.putExtra("settings", searchSettings);
		setResult(RESULT_OK, data);
		super.finish();
	}
	
}
