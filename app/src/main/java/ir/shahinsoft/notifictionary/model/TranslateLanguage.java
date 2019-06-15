package ir.shahinsoft.notifictionary.model;

/**
 * Created by shayan4shayan on 3/19/18.
 */

public class TranslateLanguage {
    private String name;
    private String shortName;

    public TranslateLanguage(String name, String shortName) {

        this.name = name;
        this.shortName = shortName;
    }

    public String get() {
        return shortName;
    }

    @Override
    public String toString() {
        return name;
    }
}
