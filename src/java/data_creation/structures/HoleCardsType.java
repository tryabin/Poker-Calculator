package data_creation.structures;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public enum HoleCardsType {
    PAIR(""),
    SUITED("s"),
    OFFSUIT("o");

    private String abbreviation;

    private static final Map<String, HoleCardsType> ENUM_MAP;

    static {
        Map<String, HoleCardsType> map = new ConcurrentHashMap<String, HoleCardsType>();
        for (HoleCardsType instance : HoleCardsType.values()) {
            map.put(instance.toString().toLowerCase(), instance);
        }
        ENUM_MAP = Collections.unmodifiableMap(map);
    }


    HoleCardsType(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    @Override
    public String toString() {
        return abbreviation;
    }

    public static HoleCardsType get(String name) {
        return ENUM_MAP.get(name.toLowerCase());
    }
}
