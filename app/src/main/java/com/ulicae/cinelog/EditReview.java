package com.ulicae.cinelog;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ulicae.cinelog.dto.KinoDto;
import com.ulicae.cinelog.dto.KinoService;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * CineLog Copyright 2018 Pierre Rognon
 * kinolog Copyright (C) 2017  ryan rigby
 *
 *
 * This file is part of CineLog.
 * CineLog is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CineLog is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with CineLog. If not, see <https://www.gnu.org/licenses/>.
 *
 */
public class EditReview extends AppCompatActivity {

    @BindView(R.id.kino_rating_bar)
    RatingBar rating_bar;
    @BindView(R.id.kino_review_text)
    EditText review_text;
    @BindView(R.id.kino_review_date)
    TextView review_date;

    @BindView(R.id.view_kino_title_edit)
    EditText kino_title;
    @BindView(R.id.view_kino_title_readonly)
    TextView kino_title_readonly;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.rating_picker)
    NumberPicker rating_picker;

    KinoDto kino;

    private KinoService kinoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_review);
        ButterKnife.bind(this);

        kinoService = new KinoService(((KinoApplication) getApplicationContext()).getDaoSession());

        kino = Parcels.unwrap(getIntent().getParcelableExtra("kino"));
        if (getIntent().getBooleanExtra("creation", false)) {
            setTitle(getString(R.string.title_activity_add_review_creation));
        }

        int maxRating;
        if (kino.getMaxRating() == null) {
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String defaultMaxRateValue = prefs.getString("default_max_rate_value", "5");
            maxRating = Integer.parseInt(defaultMaxRateValue);
        } else {
            maxRating = kino.getMaxRating();
        }

        String[] displayedValues = getDisplayedValues(maxRating);

        rating_picker.setMinValue(0);
        rating_picker.setMaxValue(maxRating * 2);

        rating_picker.setDisplayedValues(displayedValues);

        rating_picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                String[] displayedValues = picker.getDisplayedValues();
                float rating = Float.parseFloat(displayedValues[newVal]);
                rating_bar.setRating(rating);

                kino.setRating(rating);
            }
        });

        if (kino.getRating() != null) {
            rating_picker.setValue(getValueToDisplay(displayedValues, kino.getRating()));
            rating_bar.setRating(kino.getRating());
        }

        rating_bar.setNumStars(maxRating);
        rating_bar.setStepSize(0.5f);

        if (kino.getReview() != null) {
            review_text.setText(kino.getReview());
        }

        if (kino.getReview_date() != null) {
            String review_date_as_string = null;
            if (kino.getReview_date() != null) {
                review_date_as_string =
                        new SimpleDateFormat("dd/MM/yyyy").format(kino.getReview_date());
            }
            review_date.setText(review_date_as_string);
        }

        kino_title.setText(kino.getTitle());
        kino_title_readonly.setText(kino.getTitle());
        if (kino.getTmdbKinoId() != null) {
            kino_title.setVisibility(View.INVISIBLE);
            kino_title_readonly.setVisibility(View.VISIBLE);
        } else {
            kino_title.setVisibility(View.VISIBLE);
            kino_title_readonly.setVisibility(View.INVISIBLE);
        }

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private int getValueToDisplay(String[] displayedValues, float rating) {
        int i = 0;
        for (String value : displayedValues) {
            if (Float.parseFloat(value) == rating) {
                return i;
            }

            i++;
        }

        return 0;
    }

    @NonNull
    private String[] getDisplayedValues(int maxRating) {
        List<String> displayedValues = new ArrayList<>();

        for (int i = 0; i <= maxRating; i++) {
            displayedValues.add(String.valueOf(i));

            if (i != maxRating) {
                displayedValues.add(i + ".5");
            }
        }

        return displayedValues.toArray(new String[0]);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);


        }
    }

    public void showTimePickerDialog(View view) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");

    }

    @OnClick(R.id.fab_save)
    public void onClick() {
        if (rating_bar.getRating() == 0 && (review_text.getText().toString().equals("") || review_text.getText().toString().isEmpty())) {
            Toast t = Toast.makeText(getApplicationContext(),
                    "Error no review to save.",
                    Toast.LENGTH_LONG);
            t.show();
        } else {
            kino.setRating(rating_bar.getRating());
            kino.setReview(review_text.getText().toString());

            if (kino.getTmdbKinoId() == null) {
                kino.setTitle(kino_title.getText().toString());
            }

            if (kino.getMaxRating() == null) {
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
                String maxRating = prefs.getString("default_max_rate_value", "5");
                int maxRatingAsInt = Integer.parseInt(maxRating);
                kino.setMaxRating(maxRatingAsInt);
            }

            // TODO call a createOrUpdate instead
            kino = kinoService.createKino(kino);

            Intent returnIntent = getIntent();
            returnIntent.putExtra("kino", Parcels.wrap(kino));
            setResult(Activity.RESULT_OK, returnIntent);
            finish();
        }
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            final Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

            KinoDto kino = ((EditReview) getActivity()).kino;
            kino.setReview_date(c.getTime());
            String review_date_as_string = null;
            if (kino.getReview_date() != null) {
                review_date_as_string =
                        new SimpleDateFormat("dd/MM/yyyy").format(kino.getReview_date());
            }
            ((EditReview) getActivity()).review_date.setText(review_date_as_string);
            // Do something with the date chosen by the user
        }
    }
}