package a107.cardmore.util.constant;

public enum CardCompany {
    KB("1001"),
    SAMSUNG("1002"),
    LOTTE("1003"),
    WOORI("1004"),
    SHINHAN("1005"),
    HYUNDAI("1006"),
    BC("1007"),
    NH("1008"),
    HANA("1009"),
    IBK("1010");

    private final String value;

    CardCompany(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
