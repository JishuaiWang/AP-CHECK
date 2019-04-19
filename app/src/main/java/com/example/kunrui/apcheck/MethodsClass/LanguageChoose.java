package com.example.kunrui.apcheck.MethodsClass;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import java.util.Locale;

public class LanguageChoose {
    public void selectLanguage(String language, Resources resources) {
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        switch (language) {
            case "zh":
                System.out.println("切换为CHINA");
                configuration.locale = Locale.SIMPLIFIED_CHINESE;
                break;
            case "jp":
                System.out.println("切换为JAPANESE");
                configuration.locale = Locale.JAPANESE;
                break;
            case "en":
                System.out.println("切换为英语");
                configuration.locale = Locale.ENGLISH;
                break;
            case "fr":
                System.out.println("切换为法语");
                configuration.locale = Locale.FRENCH;
                break;
            default:
                System.out.println("默认为英语");
                configuration.locale = Locale.ENGLISH;
                break;
        }
        resources.updateConfiguration(configuration, displayMetrics);
    }
}
