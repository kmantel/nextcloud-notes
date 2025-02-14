package it.niedermann.owncloud.notes.android.activity;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.niedermann.owncloud.notes.R;
import it.niedermann.owncloud.notes.android.appwidget.SingleNoteWidget;
import it.niedermann.owncloud.notes.model.DBNote;
import it.niedermann.owncloud.notes.model.Item;
import it.niedermann.owncloud.notes.util.ExceptionHandler;
import it.niedermann.owncloud.notes.util.Notes;

public class SelectSingleNoteActivity extends NotesListViewActivity {

    @BindView(R.id.fab_create)
    View fabCreate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setUncaughtExceptionHandler(new ExceptionHandler(this));
        setResult(Activity.RESULT_CANCELED);

        ButterKnife.bind(this);
        fabCreate.setVisibility(View.GONE);

        toolbar.setTitle(R.string.activity_select_single_note);
        swipeRefreshLayout.setEnabled(false);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onNoteClick(int position, View v) {
        Item item = adapter.getItem(position);
        DBNote note = (DBNote) item;
        long noteID = note.getId();
        final Bundle extras = getIntent().getExtras();

        if (extras == null) {
            finish();
        }

        assert extras != null;
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        SharedPreferences.Editor sp = PreferenceManager.getDefaultSharedPreferences(this).edit();

        sp.putLong(SingleNoteWidget.WIDGET_KEY + appWidgetId, noteID);
        sp.putLong(SingleNoteWidget.ACCOUNT_ID_KEY + appWidgetId, note.getAccountId());
        sp.putBoolean(SingleNoteWidget.DARK_THEME_KEY + appWidgetId, Notes.getAppTheme(getApplicationContext()));
        sp.apply();

        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null,
                                        getApplicationContext(), SingleNoteWidget.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        setResult(RESULT_OK, updateIntent);
        getApplicationContext().sendBroadcast(updateIntent);
        finish();
    }
}
