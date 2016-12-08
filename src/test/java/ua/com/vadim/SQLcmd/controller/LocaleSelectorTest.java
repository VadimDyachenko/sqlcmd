package ua.com.vadim.SQLcmd.controller;

import org.junit.Test;
import ua.com.vadim.SQLcmd.exception.UnsupportedLanguageException;

import java.util.Locale;
import java.util.Set;

import static org.junit.Assert.assertEquals;


public class LocaleSelectorTest {
    private final LocaleSelector localeSelector = new LocaleSelector();

    @Test
    public void testSetLocale() throws UnsupportedLanguageException {
        //given
        Locale.setDefault(Locale.ENGLISH);
        String locale = "ru";
        //when
        localeSelector.setLocale(locale);
        //then
        assertEquals("ru_RU", Locale.getDefault().toString());
    }

    @Test
    public void testSetEnglishLocale() throws UnsupportedLanguageException {
        //given
        Locale.setDefault(new Locale("ru", "RU"));
        //when
        localeSelector.setEnglishLocale();
        //then
        assertEquals(Locale.ENGLISH, Locale.getDefault());
    }

    @Test (expected = UnsupportedLanguageException.class)
    public void testSetLocaleUnsupportedLocale() throws UnsupportedLanguageException {
        //given
        Locale.setDefault(Locale.ENGLISH);
        String locale = "ab";
        //when
        localeSelector.setLocale(locale);
        //then
        assertEquals("ru_RU", Locale.getDefault().toString());
    }

    @Test
    public void testGetSupportedLocale() throws UnsupportedLanguageException {
        //given
        String expected = "[ru, en]";
        //when
        Set<String> supportedLocale = localeSelector.getSupportedLocale();
        //then
        assertEquals(expected, supportedLocale.toString());
    }
}