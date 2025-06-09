package ru.lenok.client;

import javafx.scene.control.Button;

import java.util.*;

public class LanguageManager {
    private static final LanguageManager INSTANCE = new LanguageManager();
    private Locale current = new Locale("ru");
    private ResourceBundle bundle;

    private final Map<String, Locale> locales = Map.of(
            "Русский", new Locale("ru"),
            "English (NZ)", new Locale("en", "NZ"),
            "Македонски", new Locale("mk"),
            "Shqip", new Locale("sq")
    );

    private LanguageManager() {
        loadBundle(current);
    }

    public static LanguageManager getInstance() {
        return INSTANCE;
    }

    private void loadBundle(Locale locale) {
        current = locale;
        Locale.setDefault(locale);
        bundle = ResourceBundle.getBundle("messages", locale);
    }

    public void setLanguage(String name) {
        Locale locale = locales.getOrDefault(name, new Locale("ru"));
        loadBundle(locale);
    }

    public String get(String key) {
        return bundle.containsKey(key) ? bundle.getString(key) : key;
    }

    public String getCurrentLanguageName() {
        return locales.entrySet().stream()
                .filter(e -> e.getValue().equals(current))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Русский");
    }

    public Set<String> getAllLanguages() {
        return locales.keySet();
    }
}
