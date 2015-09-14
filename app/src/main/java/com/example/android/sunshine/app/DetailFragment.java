package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{


    private static final int DETAIL_LOADER = 5;
    TextView tv;
    String forecastUri;
    ShareActionProvider mShareActionProvider;
    String mForecast;
    ViewHolder viewHolder;
    static final String DETAIL_URI = "URI";
    private Uri mUri;

    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_WEATHER_ID = 0;
    static final int COL_WEATHER_DATE = 1;
    static final int COL_WEATHER_DESC = 2;
    static final int COL_WEATHER_MAX_TEMP = 3;
    static final int COL_WEATHER_MIN_TEMP = 4;
    static final int COL_WEATHER_HUMIDITY = 5;
    static final int COL_WEATHER_WIND = 6;
    static final int COL_WEATHER_DEGREES = 7;
    static final int COL_WEATHER_PRESSURE = 8;
    static final int COL_WEATHER_CONDITION_ID = 9;



    public DetailFragment() {
    setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Intent intent = getActivity().getIntent();
        Bundle arguments = getArguments();

        if(arguments!=null){
            mUri = arguments.getParcelable(DETAIL_URI);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);


        viewHolder = new ViewHolder(rootView);
//        if (intent!= null){
//            forecastUri = intent.getDataString();
//
//        }

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detailfragment,menu);

        MenuItem menuItem = menu.findItem(R.id.action_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        if(mForecast!=null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    private Intent createShareIntent(){

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mForecast + " #SunshineApp");
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
//        Intent intent = getActivity().getIntent();
//        if(intent == null || intent.getData()==null){
//            return null;
//        }
        if (null!=mUri){
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );
        }
//        CursorLoader cursorLoader = new CursorLoader(getActivity(),
//                intent.getData(),FORECAST_COLUMNS,null,null,null);

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
          if(!data.moveToFirst()){
              return;
          }
        String dateString = Utility.formatDate(data.getLong(COL_WEATHER_DATE));
       // Toast.makeText(getActivity(),dateString,Toast.LENGTH_SHORT).show();
        viewHolder.dayTextView.setText(Utility.getDayName(getActivity(), data.getLong(COL_WEATHER_DATE)));
        viewHolder.dateTextView.setText(Utility.getFormattedMonthDay(getActivity(), data.getLong(COL_WEATHER_DATE)));

        int weatherId = data.getInt(COL_WEATHER_CONDITION_ID);
        viewHolder.detailIcon.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));

        viewHolder.descTextView.setText(data.getString(COL_WEATHER_DESC));
        viewHolder.humidityTextView.setText("Humidity: "+String.valueOf(data.getDouble(COL_WEATHER_HUMIDITY))+" %");
        viewHolder.pressureTextView.setText("Pressure: "+String.valueOf(data.getDouble(COL_WEATHER_PRESSURE))+" hPa");
        viewHolder.windTextView.setText(Utility.getFormattedWind(getActivity(),
                data.getFloat(COL_WEATHER_WIND),
                data.getFloat(COL_WEATHER_DEGREES)));

        boolean isMetric = Utility.isMetric(getActivity());

        viewHolder.highTextView.setText(Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MAX_TEMP),isMetric));
        viewHolder.lowTextView.setText(Utility.formatTemperature(getActivity(),
                data.getDouble(COL_WEATHER_MIN_TEMP),isMetric));
        String weatherDesc = data.getString(COL_WEATHER_DESC);
        String high = Utility.formatTemperature(getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);
        String low = Utility.formatTemperature(getActivity(),data.getDouble(COL_WEATHER_MIN_TEMP),isMetric);
        mForecast = String.format("%s - %s - %s/%s",dateString,weatherDesc,high,low);
        //tv.setText(mForecast);
        if(mShareActionProvider != null){
            mShareActionProvider.setShareIntent(createShareIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    public void onLocationChanged(String newlocation) {
        Uri uri = mUri;
        if(null != uri){
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updateUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newlocation,date);
            mUri = updateUri;
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);
        }

    }

    public static class ViewHolder{
        public final TextView dayTextView;
        public final TextView dateTextView;
        public final TextView highTextView;
        public final TextView lowTextView;
        public final TextView humidityTextView;
        public final TextView windTextView;
        public final TextView pressureTextView;
        public final TextView descTextView;
        public final ImageView detailIcon;

        public ViewHolder (View view){
            dayTextView = (TextView)view.findViewById(R.id.detail_day_textview);
            dateTextView = (TextView)view.findViewById(R.id.detail_date_textView);
            highTextView = (TextView)view.findViewById(R.id.detail_high_textView);
            lowTextView = (TextView)view.findViewById(R.id.detail_low_textView);
            descTextView = (TextView)view.findViewById(R.id.detail_description_textView);
            detailIcon = (ImageView)view.findViewById(R.id.detail_icon);
            humidityTextView = (TextView)view.findViewById(R.id.detail_humidity_textView);
            windTextView = (TextView)view.findViewById(R.id.detail_wind_textView);
            pressureTextView = (TextView)view.findViewById(R.id.detail_pressure_textView);
        }
    }
}
