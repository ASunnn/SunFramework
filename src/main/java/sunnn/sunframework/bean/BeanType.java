package sunnn.sunframework.bean;

public enum BeanType {

    SINGLETON(1 << 0),
    PROTOTYPE(1 << 1);

    private int type;

    BeanType(int code) {
        this.type = code;
    }

    public int getType() {
        return type;
    }
}
