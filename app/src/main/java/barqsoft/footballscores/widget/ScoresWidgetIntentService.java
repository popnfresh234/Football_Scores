package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Alexander on 7/2/2015.
 */
public class ScoresWidgetIntentService extends IntentService {

    private static final String LOG_TAG = ScoresWidgetProvider.class.getSimpleName();

    private static final String[] SCORE_COLUMNS = {
            DatabaseContract.scores_table.LEAGUE_COL,
            DatabaseContract.scores_table.DATE_COL,
            DatabaseContract.scores_table.TIME_COL,
            DatabaseContract.scores_table.HOME_COL,
            DatabaseContract.scores_table.AWAY_COL,
            DatabaseContract.scores_table.HOME_GOALS_COL,
            DatabaseContract.scores_table.AWAY_GOALS_COL,
            DatabaseContract.scores_table.MATCH_ID,
            DatabaseContract.scores_table.MATCH_DAY
    };

    private static final int INDEX_LEAGUE = 0;
    private static final int INDEX_MATCH_DATE = 1;
    private static final int INDEX_MATCH_TIME = 2;
    private static final int INDEX_HOME = 3;
    private static final int INDEX_AWAY = 4;
    private static final int INDEX_HOME_GOALS = 5;
    private static final int INDEX_AWAY_GOALS = 6;
    private static final int INDEX_MATCH_ID = 7;
    private static final int INDEX_MATCH_DAY = 8;

    public ScoresWidgetIntentService() {
        super("ScoresWidgetIntentService");
    }

    private String[] mFragmentDate = new String[1];

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ScoresWidgetProvider.class));

        Date fragmentdate = new Date(System.currentTimeMillis()+(86400000));
        String dateSt = "March 3, 2015";// start jd
        try {
            DateFormat format = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH);
            fragmentdate = format.parse(dateSt);
        }catch (Exception e)
        {
            e.printStackTrace();
        }//end jd
        SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");

        mFragmentDate[0] = mformat.format(fragmentdate);

        //Get score data from Content Provider
        Cursor data = getContentResolver().query(DatabaseContract.scores_table.buildScoreWithDate(), SCORE_COLUMNS, null, mFragmentDate, null);

        if (data == null) {
            Log.i(LOG_TAG, "null cursor");
            return;
        }
        if (!data.moveToFirst()) {
            Log.i(LOG_TAG, "!data.moveToFirst");
            data.close();
            return;
        }

        //extract data
        String homeName = data.getString(INDEX_HOME);
        int homeCrestId = Utilies.getTeamCrestByTeamName(homeName);

        int homeGoals = data.getInt(INDEX_HOME_GOALS);
        int awayGoals = data.getInt(INDEX_AWAY_GOALS);
        String score = Utilies.getScores(homeGoals, awayGoals);

        String awayName = data.getString(INDEX_AWAY);
        int awayCrestId = Utilies.getTeamCrestByTeamName(awayName);

        String matchTime = data.getString(INDEX_MATCH_TIME);
        data.close();

        for (int appWidgetId : appWidgetIds) {
            Log.i(LOG_TAG, "updateWidget");
            int layoutId = R.layout.widget;

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            //Add data
            views.setImageViewResource(R.id.widget_home_crest, homeCrestId);
            views.setTextViewText(R.id.widget_home_name, homeName);

            views.setTextViewText(R.id.widget_score_textview, score);

            views.setImageViewResource(R.id.widget_away_crest, awayCrestId);
            views.setTextViewText(R.id.widget_away_name, awayName);

            views.setTextViewText(R.id.widget_data_textview, matchTime);

            //update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }
}
