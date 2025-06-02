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
        addLocale("ru", Map.ofEntries(
                Map.entry("title.main", "Менеджер LabWork"),
                Map.entry("title.login", "Вход / Регистрация"),
                Map.entry("label.login", "Логин"),
                Map.entry("label.password", "Пароль"),
                Map.entry("label.register", "Регистрация"),
                Map.entry("button.login", "Войти"),
                Map.entry("error.empty_fields", "Пожалуйста, заполните все поля"),

                Map.entry("title.create_labwork", "Создание LabWork"),
                Map.entry("title.edit_labwork", "Редактирование LabWork"),

                Map.entry("label.key", "Ключ"),
                Map.entry("label.name", "Имя"),
                Map.entry("title.coordinates", "Координаты"),
                Map.entry("label.x", "X"),
                Map.entry("label.y", "Y"),
                Map.entry("label.creation_date", "Дата создания"),
                Map.entry("label.minimal_point", "Минимальный балл"),
                Map.entry("label.description", "Описание"),
                Map.entry("label.difficulty", "Сложность"),
                Map.entry("title.discipline", "Дисциплина"),
                Map.entry("label.discipline_name", "Название дисциплины"),
                Map.entry("label.practice_hours", "Часы практики"),
                Map.entry("label.owner_id", "ID владельца"),

                Map.entry("button.ok", "ОК"),
                Map.entry("button.cancel", "Отмена"),
                Map.entry("button.create", "Создать"),
                Map.entry("button.edit", "Редактировать"),
                Map.entry("button.delete", "Удалить"),
                Map.entry("button.clear", "Очистить"),

                Map.entry("user_label", "Пользователь"),

                Map.entry("error.key.empty", "Ключ не может быть пустым"),
                Map.entry("error.name.empty", "Название не может быть пустым"),
                Map.entry("error.x.invalid", "X должно быть числом"),
                Map.entry("error.y.invalid", "Y должно быть числом"),
                Map.entry("error.min_point.invalid", "Минимальный балл должен быть числом"),
                Map.entry("error.min_point.negative", "Минимальный балл не может быть отрицательным"),
                Map.entry("error.description.empty", "Описание не может быть пустым"),
                Map.entry("error.description.toolong", "Описание слишком длинное (максимум 2863 символа)"),
                Map.entry("error.difficulty.empty", "Выберите сложность"),
                Map.entry("error.discipline_name.empty", "Название дисциплины не может быть пустым"),
                Map.entry("error.practice_hours.invalid", "Часы практики должны быть числом"),

                Map.entry("error.insert", "Ошибка при сохранении"),
                Map.entry("error.exception", "Исключение")
        ));

        addLocale(new Locale("en", "NZ"), Map.ofEntries(
                Map.entry("title.main", "LabWork Manager"),
                Map.entry("title.login", "Login / Register"),
                Map.entry("label.login", "Login"),
                Map.entry("label.password", "Password"),
                Map.entry("label.register", "Register"),
                Map.entry("button.login", "Login"),
                Map.entry("error.empty_fields", "Please fill in all fields"),

                Map.entry("title.create_labwork", "Create LabWork"),
                Map.entry("title.edit_labwork", "Edit LabWork"),

                Map.entry("label.key", "Key"),
                Map.entry("label.name", "Name"),
                Map.entry("title.coordinates", "Coordinates"),
                Map.entry("label.x", "X"),
                Map.entry("label.y", "Y"),
                Map.entry("label.creation_date", "Creation Date"),
                Map.entry("label.minimal_point", "Minimal Point"),
                Map.entry("label.description", "Description"),
                Map.entry("label.difficulty", "Difficulty"),
                Map.entry("title.discipline", "Discipline"),
                Map.entry("label.discipline_name", "Discipline Name"),
                Map.entry("label.practice_hours", "Practice Hours"),
                Map.entry("label.owner_id", "Owner ID"),

                Map.entry("button.ok", "OK"),
                Map.entry("button.cancel", "Cancel"),
                Map.entry("button.create", "Create"),
                Map.entry("button.edit", "Edit"),
                Map.entry("button.delete", "Delete"),
                Map.entry("button.clear", "Clear"),

                Map.entry("user_label", "User"),

                Map.entry("error.key.empty", "Key cannot be empty"),
                Map.entry("error.name.empty", "Name cannot be empty"),
                Map.entry("error.x.invalid", "X must be a number"),
                Map.entry("error.y.invalid", "Y must be a number"),
                Map.entry("error.min_point.invalid", "Minimal point must be a number"),
                Map.entry("error.min_point.negative", "Minimal point cannot be negative"),
                Map.entry("error.description.empty", "Description cannot be empty"),
                Map.entry("error.description.toolong", "Description is too long (max 2863 characters)"),
                Map.entry("error.difficulty.empty", "Please select difficulty"),
                Map.entry("error.discipline_name.empty", "Discipline name cannot be empty"),
                Map.entry("error.practice_hours.invalid", "Practice hours must be a number"),

                Map.entry("error.insert", "Error saving"),
                Map.entry("error.exception", "Exception")
        ));

        addLocale("mk", Map.ofEntries(
                Map.entry("title.main", "Менџер на LabWork"),
                Map.entry("title.login", "Најава / Регистрација"),
                Map.entry("label.login", "Корисничко име"),
                Map.entry("label.password", "Лозинка"),
                Map.entry("label.register", "Регистрација"),
                Map.entry("button.login", "Најави се"),
                Map.entry("error.empty_fields", "Ве молиме пополнете ги сите полиња"),

                Map.entry("title.create_labwork", "Креирај LabWork"),
                Map.entry("title.edit_labwork", "Уреди LabWork"),

                Map.entry("label.key", "Клуч"),
                Map.entry("label.name", "Име"),
                Map.entry("title.coordinates", "Координати"),
                Map.entry("label.x", "X"),
                Map.entry("label.y", "Y"),
                Map.entry("label.creation_date", "Датум на креирање"),
                Map.entry("label.minimal_point", "Минимален поен"),
                Map.entry("label.description", "Опис"),
                Map.entry("label.difficulty", "Тежина"),
                Map.entry("title.discipline", "Дисциплина"),
                Map.entry("label.discipline_name", "Име на дисциплина"),
                Map.entry("label.practice_hours", "Часови практика"),
                Map.entry("label.owner_id", "ИД на сопственикот"),

                Map.entry("button.ok", "ВО РЕД"),
                Map.entry("button.cancel",   "Откажи"),
                Map.entry("button.create", "Креирај"),
                Map.entry("button.edit", "Уреди"),
                Map.entry("button.delete", "Избриши"),
                Map.entry("button.clear", "Исчисти"),

                Map.entry("user_label", "Корисник"),

                Map.entry("error.key.empty", "Клучот не смее да биде празен"),
                Map.entry("error.name.empty", "Името не смее да биде празно"),
                Map.entry("error.x.invalid", "X мора да биде број"),
                Map.entry("error.y.invalid", "Y мора да биде број"),
                Map.entry("error.min_point.invalid", "Минималниот поен мора да биде број"),
                Map.entry("error.min_point.negative", "Минималниот поен не смее да биде негативен"),
                Map.entry("error.description.empty", "Описот не смее да биде празен"),
                Map.entry("error.description.toolong", "Описот е предолг (максимум 2863 карактери)"),
                Map.entry("error.difficulty.empty", "Изберете тежина"),
                Map.entry("error.discipline_name.empty", "Името на дисциплината не смее да биде празно"),
                Map.entry("error.practice_hours.invalid", "Часовите практика мора да бидат број"),

                Map.entry("error.insert", "Грешка при зачувување"),
                Map.entry("error.exception", "Исклучок")
        ));

        addLocale("sq", Map.ofEntries(
                Map.entry("title.main", "Menaxheri i LabWork"),
                Map.entry("title.login", "Hyrje / Regjistrim"),
                Map.entry("label.login", "Përdoruesi"),
                Map.entry("label.password", "Fjalëkalimi"),
                Map.entry("label.register", "Regjistrohu"),
                Map.entry("button.login", "Hyr"),
                Map.entry("error.empty_fields", "Ju lutemi plotësoni të gjitha fushat"),

                Map.entry("title.create_labwork", "Krijo LabWork"),
                Map.entry("title.edit_labwork", "Ndrysho LabWork"),

                Map.entry("label.key", "Çelësi"),
                Map.entry("label.name", "Emri"),
                Map.entry("title.coordinates", "Koordinatat"),
                Map.entry("label.x", "X"),
                Map.entry("label.y", "Y"),
                Map.entry("label.creation_date", "Data e krijimit"),
                Map.entry("label.minimal_point", "Pikë minimale"),
                Map.entry("label.description", "Përshkrimi"),
                Map.entry("label.difficulty", "Vështirësia"),
                Map.entry("title.discipline", "Disiplina"),
                Map.entry("label.discipline_name", "Emri i disiplinës"),
                Map.entry("label.practice_hours", "Orë praktike"),
                Map.entry("label.owner_id", "ID i pronarit"),

                Map.entry("button.ok", "OK"),
                Map.entry("button.cancel", "Anulo"),
                Map.entry("button.create", "Krijo"),
                Map.entry("button.edit", "Ndrysho"),
                Map.entry("button.delete", "Fshi"),
                Map.entry("button.clear", "Pastro"),

                Map.entry("user_label", "Përdoruesi"),

                Map.entry("error.key.empty", "Çelësi nuk duhet të jetë bosh"),
                Map.entry("error.name.empty", "Emri nuk duhet të jetë bosh"),
                Map.entry("error.x.invalid", "X duhet të jetë numër"),
                Map.entry("error.y.invalid", "Y duhet të jetë numër"),
                Map.entry("error.min_point.invalid", "Pikë minimale duhet të jetë numër"),
                Map.entry("error.min_point.negative", "Pikë minimale nuk duhet të jetë negative"),
                Map.entry("error.description.empty", "Përshkrimi nuk duhet të jetë bosh"),
                Map.entry("error.description.toolong", "Përshkrimi është shumë i gjatë (maksimumi 2863 karaktere)"),
                Map.entry("error.difficulty.empty", "Zgjidhni vështirësinë"),
                Map.entry("error.discipline_name.empty", "Emri i disiplinës nuk duhet të jetë bosh"),
                Map.entry("error.practice_hours.invalid", "Orët praktike duhet të jenë numër"),

                Map.entry("error.insert", "Gabim gjatë ruajtjes"),
                Map.entry("error.exception", "Përjashtim")
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

    public Set<String> getAllLanguages() {
        return locales.keySet();
    }

    public String get(String key) {
        return translations.getOrDefault(current, translations.get(new Locale("ru"))).getOrDefault(key, key);
    }

    private void addLocale(String lang, Map<String, String> map) {
        addLocale(new Locale(lang), map);
    }

    private void addLocale(Locale locale, Map<String, String> map) {
        translations.put(locale, map);
    }

}
