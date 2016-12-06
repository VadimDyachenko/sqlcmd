package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class LocaleSelector {
    private final Map<String, Locale> supportedLocale = new HashMap<>();

    public LocaleSelector() {
        setSupportedLocale();
    }

    private void setSupportedLocale() {
        supportedLocale.put("ru", new Locale("ru", "RU"));
        supportedLocale.put("en", Locale.ENGLISH);
    }

    public void setLocale(String locale) throws UnsupportedLanguageException {
        if (supportedLocale.containsKey(locale)) {
            Locale.setDefault(supportedLocale.get(locale));
        } else {
            throw new UnsupportedLanguageException();
        }
    }

    Set<String> getSupportedLocale() {
        return supportedLocale.keySet();
    }

    public void setEnglishLocale() {
        Locale.setDefault(Locale.ENGLISH);
    }
}
