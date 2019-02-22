package com.android.thebakingapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import org.apache.commons.lang3.StringUtils;

public class IngredientAppWidgetProvider extends AppWidgetProvider {

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId, String title, String body) {

        if (StringUtils.isEmpty(title) | title == null) {
            title = "please add a recipe from the app menu to see it in widget";
        }

        if (StringUtils.isEmpty(body) | body == null) {
            body = "The Baking app";
        }

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.ingredient_app_widget);
        remoteViews.setTextViewText(R.id.tv_widgetIngredients, body);
        remoteViews.setTextViewText(R.id.tv_widgetTitle, title);

        Intent intent = new Intent(context, ItemDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

        remoteViews.setOnClickPendingIntent(R.id.tv_widgetIngredients, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId,
                    context.getString(R.string.app_name),
                    context.getString(R.string.default_widget_text));
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // Perform any action when one or more AppWidget instances have been deleted
    }

    @Override
    public void onEnabled(Context context) {
        // Perform any action when an AppWidget for this provider is instantiated
    }

    @Override
    public void onDisabled(Context context) {
        // Perform any action when the last AppWidget instance for this provider is deleted
    }
}
