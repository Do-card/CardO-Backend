package a107.cardmore.util.constant;

public enum MerchantCategory {
    REFUELING("REFUELING", "CG-3fa85f6425e811e"),
    MARKET("MARKET", "CG-4fa85f6425ad1d3"),
    TRAFFIC("TRAFFIC", "CG-4fa85f6455cad4a"),
    EDUCATION("EDUCATION", "CG-6dd85f6425ez11o"),
    COMMUNICATION("COMMUNICATION", "CG-7fa85f6425bc311"),
    FOREIGN("FOREIGN", "CG-8fa85f6425e1123"),
    LIFE("LIFE", "CG-9ca85f66311a23d");

    private final String key;
    private final String value;

    MerchantCategory(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}