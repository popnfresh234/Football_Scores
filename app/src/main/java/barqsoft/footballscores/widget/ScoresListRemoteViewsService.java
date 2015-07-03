package barqsoft.footballscores.widget;

import android.annotation.TargetApi;
import android.content.Intent;
import android.database.Cursor;
import android.os.Binder;
import android.os.Build;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import barqsoft.footballscores.DatabaseContract;
import barqsoft.footballscores.R;
import barqsoft.footballscores.Utilies;

/**
 * Created by Alexander on 7/4/2015.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ScoresListRemoteViewsService extends RemoteViewsService {

    private static final String LOG_TAG = ScoresListRemoteViewsService.class.getSimpleName();

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

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {

            }

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }
                final long identityToken = Binder.clearCallingIdentity();
                data = getContentResolver().query(DatabaseContract.BASE_CONTENT_URI, SCORE_COLUMNS, null, null, null, null);
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION || data == null || !data.moveToPosition(position)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(), R.layout.widget);

                String homeName = data.getString(INDEX_HOME);
                int homeCrestId = Utilies.getTeamCrestByTeamName(homeName);

                int homeGoals = data.getInt(INDEX_HOME_GOALS);
                int awayGoals = data.getInt(INDEX_AWAY_GOALS);
                String score = Utilies.getScores(homeGoals, awayGoals);

                String awayName = data.getString(INDEX_AWAY);
                int awayCrestId = Utilies.getTeamCrestByTeamName(awayName);

                String matchTime = data.getString(INDEX_MATCH_TIME);

                //Add data
                views.setImageViewResource(R.id.widget_home_crest, homeCrestId);
                views.setTextViewText(R.id.widget_home_name, homeName);

                views.setTextViewText(R.id.widget_score_textview, score);

                views.setImageViewResource(R.id.widget_away_crest, awayCrestId);
                views.setTextViewText(R.id.widget_away_name, awayName);

                views.setTextViewText(R.id.widget_data_textview, matchTime);

                Intent fillInIntent = new Intent();
                views.setOnClickFillInIntent(R.id.widget_list_row, fillInIntent);

                return views;

            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                return 0;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
