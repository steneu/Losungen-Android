package de.schalter.losungen.tabs;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import de.schalter.losungen.MainActivity;
import de.schalter.losungen.fragments.FragmentLosungTag;

/**
 * Created by marti on 27.10.2015.
 */
public class PagerAdapter extends android.support.v4.app.FragmentPagerAdapter {

    private long baseId = 0;

    //Immer 21 Tage in den Tabs anzeigen
    public static final int ITEMSBEFOR = 4;
    private final int ITEMSAFTER = 4;

    private MainActivity context;

    private List<String> titles;
    private List<Long> times;

    private long currentDate;

    public PagerAdapter(FragmentManager fm, MainActivity context) {
        super(fm);
        this.context = context;

        titles = new ArrayList<>();
        times = new ArrayList<>();

        long timeNow = System.currentTimeMillis();
        currentDate = timeNow;

        String title = "";
        long time;
        for(int i = ITEMSBEFOR * -1; i < ITEMSAFTER + 1; i++) {
            time = timeNow + (i * 1000 * 60 * 60 * 24);
            title = getTitleByTime(time);

            titles.add(title);
            times.add(time);
        }

        this.notifyDataSetChanged();

    }

    private String getTitleByTime(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        Date date = calendar.getTime();
        DateFormat df = new SimpleDateFormat("E, dd.MM");
        return df.format(date);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        return FragmentLosungTag.newFragmentLosungTag(times.get(position), context);
    }

    @Override
    public int getCount() {
        return titles.size();
    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public long getItemId(int position) {
        baseId++;
        return baseId;
    }

    public void setDate(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, day);
        long timeNow = calendar.getTimeInMillis();
        currentDate = timeNow;

        titles.clear();
        times.clear();

        String title = "";
        long time;
        for(int i = ITEMSBEFOR * -1; i < ITEMSAFTER + 1; i++) {
            time = timeNow + (i * 1000 * 60 * 60 * 24);
            title = getTitleByTime(time);

            titles.add(title);
            times.add(time);
        }
        this.notifyDataSetChanged();
    }

    public int gescrollt(int position, boolean nachRechtsGescrollt) {

        currentDate = times.get(position);

        if(nachRechtsGescrollt && (position > (titles.size() - 2))) { //Am Ende sollen immer noch 3 weitere Tage stehen
            for(int i = 0; i < (position - titles.size() + ITEMSAFTER + 4); i++) {
                long time = times.get(times.size() - 1) + (1000 * 60 * 60 * 24);
                times.add(time);
                titles.add(getTitleByTime(time));
            }

            //Error reported in Google-Play developer console
            try {
                this.notifyDataSetChanged();
            } catch (Exception ignored) {}

        } else if(position < 1) {

            for(int i = 0; i < ITEMSBEFOR; i++) {
                long time = times.get(0) - (1000 * 60 * 60 * 24);
                times.add(0, time);
                titles.add(0, getTitleByTime(time));
            }

            //Error reported in Google-Play developer console
            try {
                this.notifyDataSetChanged();
            } catch (Exception ignored) {}

            return position + ITEMSBEFOR;
        }

        return position;
    }

    public long getCurrentDate() {
        return currentDate;
    }
}
