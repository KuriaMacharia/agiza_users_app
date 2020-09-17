package com.agiza.center.app;

import android.app.Activity;

import android.content.Intent;


public class themeUtils

{

    public static int cTheme;

    public final static int BLACK = 1;

    public final static int LIGHT = 0;
    public final static int GOLD = 2;


    public static void changeToTheme(Activity activity, int theme)

    {

        cTheme = theme;

        activity.finish();
        activity.startActivity(new Intent(activity, activity.getClass()));


    }

    public static void onActivityCreateSetTheme(Activity activity)

    {

        switch (cTheme)

        {

            default:
            case LIGHT:

                activity.setTheme(R.style.Light);

                break;

            case BLACK:

                activity.setTheme(R.style.Black);

                break;

            case GOLD:

                activity.setTheme(R.style.Gold);

                break;

        }

    }

}
