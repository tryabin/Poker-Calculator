package data_creation.structures;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public enum Rank {
    TWO("2"),
    THREE("3"),
    FOUR("4"),
    FIVE("5"),
    SIX("6"),
    SEVEN("7"),
    EIGHT("8"),
    NINE("9"),
    TEN("T"),
    JACK("J"),
    QUEEN("Q"),
    KING("K"),
    ACE("A");

    private String abbreviation;

    private static final Map<String, Rank> ENUM_MAP;

    static {
        Map<String, Rank> map = new ConcurrentHashMap<>();
        for (Rank instance : Rank.values()) {
            map.put(instance.toString(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }


    Rank(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }

    public static Rank get(String name) {
        return ENUM_MAP.get(name);
    }

    public static Set<String> getAbbreviations() {
        return ENUM_MAP.keySet();
    }
}
