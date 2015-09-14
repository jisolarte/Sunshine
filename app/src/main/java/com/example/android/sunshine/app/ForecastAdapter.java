package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Julio on 5/29/2015.
 */
public class ForecastAdapter extends CursorAdapter{

    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;
    private boolean mUseTodayLayout;

    public void setmUseTodayLayout(boolean mUseTodayLayout) {
        this.mUseTodayLayout = mUseTodayLayout;
    }

        public ForecastAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            boolean isMetric = Utility.isMetric(mContext);
            String highLowStr = Utility.formatTemperature(mContext, high, isMetric) + "/" + Utility.formatTemperature(mContext, low, isMetric);
            return highLowStr;
        }

        /*
            This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
            string.
         */
        private String convertCursorRowToUXFormat(Cursor cursor) {
            // get row indices for our cursor

            String highAndLow = formatHighLows(
                    cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                    cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));

            return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                    " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                    " - " + highAndLow;
        }

    @Override
    public int getItemViewType(int position) {
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    /**
     * Copy/paste note: Replace existing newView() method in ForecastAdapter with this one.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        // TODO: Determine layoutId from viewType
        if(viewType == VIEW_TYPE_TODAY){
            layoutId = R.layout.list_item_forecast_today;
        }else if(viewType == VIEW_TYPE_FUTURE_DAY){
            layoutId = R.layout.list_item_forecast;
        }
       View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }
        /*
            This is where we fill-in the views with the contents of the cursor.
         */
        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            // our view is pretty simple here --- just a text view
            // we'll keep the UI functional with a simple (and slow!) binding.
            ViewHolder viewHolder = (ViewHolder)view.getTag();
            // Read weather icon ID from cursor
            int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);
            // Use placeholder image for now
            if(getItemViewType(cursor.getPosition())==VIEW_TYPE_TODAY) {
                viewHolder.iconView.setImageResource(Utility.getArtResourceForWeatherCondition(weatherId));
            }else if(getItemViewType(cursor.getPosition())==VIEW_TYPE_FUTURE_DAY){
                viewHolder.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));
            }

            // TODO Read date from cursor
            long date = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);

            viewHolder.dateView.setText(Utility.getFriendlyDayString(context,date));
            // TODO Read weather forecast from cursor
            String forecastDesc = cursor.getString(ForecastFragment.COL_WEATHER_DESC);

            viewHolder.descriptionView.setText(forecastDesc);
            // Read user preference for metric or imperial temperature units
            boolean isMetric = Utility.isMetric(context);

            // Read high temperature from cursor
            double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
            viewHolder.highTempView.setText(Utility.formatTemperature(mContext, high, isMetric));


            // TODO Read low temperature from cursor
            double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
            viewHolder.lowTempView.setText(Utility.formatTemperature(mContext, low, isMetric));


        }

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;

        public ViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.list_item_icon);
            dateView = (TextView) view.findViewById(R.id.list_item_date_textView);
            descriptionView = (TextView) view.findViewById(R.id.list_item_weather_textView);
            highTempView = (TextView) view.findViewById(R.id.list_item_high_textView);
            lowTempView = (TextView) view.findViewById(R.id.list_item_low_textView);
        }
    }


}

