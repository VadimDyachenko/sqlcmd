package ua.com.vadim.SQLcmd.controller;

import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;
import ua.com.vadim.SQLcmd.view.View;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

class LocaleSelector {
    private final Map<String, Locale> supportedLocale = new HashMap<>();

    private RunParameters runParameters;
    private View view;

    LocaleSelector(RunParameters runParameters, View view) {
        this.runParameters = runParameters;
        this.view = view;
        setSupportedLocale();
    }

    private void setSupportedLocale() {
        supportedLocale.put("ru", new Locale("ru", "RU"));
        supportedLocale.put("en", Locale.ENGLISH);
    }

    void setDefaultLocale() {
        String locale = runParameters.getInterfaceLanguage();
        try {
            setLocale(locale);
        } catch (UnsupportedLanguageException e) {
            view.writeMessage("Unsupported language parameter in sqlcmd.properties file.");
            view.writeMessage("Exit the program and change it to one of available variant: " +
                    supportedLocale.keySet().toString());
            view.writeMessage("Current interface language setting to [en]\n");
        }
    }

    private void setLocale(String locale) throws UnsupportedLanguageException{
        if (supportedLocale.containsKey(locale)) {
            Locale.setDefault(supportedLocale.get(locale));
        } else {
            throw new UnsupportedLanguageException();
        }
    }
}
