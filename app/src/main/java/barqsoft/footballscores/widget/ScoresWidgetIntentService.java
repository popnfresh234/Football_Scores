package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.widget.RemoteViews;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;

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

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(LOG_TAG, "onHandleIntent");
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, ScoresWidgetProvider.class));

        //Get score data from Content Provider
        Cursor data = getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, SCORE_COLUMNS, null, null, DatabaseContract.scores_table.DATE_COL + " ASC");
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
        String home = data.getString(INDEX_HOME);
        String homeGoals = data.getString(INDEX_HOME_GOALS);
        Log.i(LOG_TAG, home);
        data.close();

        for (int appWidgetId : appWidgetIds) {
            Log.i(LOG_TAG, "updateWidget");
            int layoutId = R.layout.widget;

            RemoteViews views = new RemoteViews(getPackageName(), layoutId);

            //Add data
            views.setTextViewText(R.id.widget_home, home);
            views.setTextViewText(R.id.widget_home_goals, homeGoals);
            //update widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

        }

    }
}
