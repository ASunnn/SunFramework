package sunnn.sunframework.resource;

public class ClassResource extends Resource {

    private String name;

    private Class clazz;

    public ClassResource(String name, Class clazz) {
        this.name = name;
        this.clazz = clazz;
    }

    public String getName() {
        return name;
    }

    public Class getClazz() {
        return clazz;
    }
}
