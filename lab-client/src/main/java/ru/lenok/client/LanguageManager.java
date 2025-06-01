package ru.lenok.client;

import java.util.*;

public class LanguageManager {
    private static final LanguageManager INSTANCE = new LanguageManager();
    private Locale current = new Locale("ru");
    private final Map<String, Locale> locales = Map.of(
            "Русский", new Locale("ru"),
            "Македонски", new Locale("mk"),
            "Shqip", new Locale("sq"),
            "English (NZ)", new Locale("en", "NZ")
    );
    private final Map<Locale, Map<String, String>> translations = new HashMap<>();

    private LanguageManager() {
        addLocale("ru", Map.of(
                "title.login", "Вход / Регистрация",
                "label.login", "Логин",
                "label.password", "Пароль",
                "label.register", "Регистрация",
                "button.login", "Войти",
                "error.empty_fields", "Заполните все поля"
        ));
        addLocale("mk", Map.of(
                "title.login", "Најава / Регистрација",
                "label.login", "Корисничко име",
                "label.password", "Лозинка",
                "label.register", "Регистрација",
                "button.login", "Најави се",
                "error.empty_fields", "Пополнете ги сите полиња"
        ));
        addLocale("sq", Map.of(
                "title.login", "Hyrje / Regjistrim",
                "label.login", "Përdoruesi",
                "label.password", "Fjalëkalimi",
                "label.register", "Regjistrohu",
                "button.login", "Hyr",
                "error.empty_fields", "Ju lutem plotësoni të gjitha fushat"
        ));
        addLocale(new Locale("en", "NZ"), Map.of(
                "title.login", "Login / Register",
                "label.login", "Login",
                "label.password", "Password",
                "label.register", "Register",
                "button.login", "Login",
                "error.empty_fields", "Please fill in all fields",
                "user_label", "User"
        ));
    }

    public static LanguageManager getInstance() {
        return INSTANCE;
    }

    public void setLanguage(String name) {
        current = locales.getOrDefault(name, new Locale("ru"));
    }

    public String getCurrentLanguageName() {
        return locales.entrySet().stream()
                .filter(e -> e.getValue().equals(current))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse("Русский");
    }

    public String get(String key) {
        return translations.getOrDefault(current, translations.get(new Locale("ru"))).getOrDefault(key, key);
    }

    private void addLocale(String lang, Map<String, String> map) {
        translations.put(new Locale(lang), map);
    }

    private void addLocale(Locale locale, Map<String, String> map) {
        translations.put(locale, map);
    }
}
