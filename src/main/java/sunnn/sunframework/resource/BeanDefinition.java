package sunnn.sunframework.resource;

import sunnn.sunframework.bean.BeanType;

public class BeanDefinition extends Resource {

//    public static final String TYPE_DEPENDS = "_type";

    private String beanName;

    private Class clazz;

    private String[] depends;

    private BeanType scope;

    public BeanDefinition setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public BeanDefinition setClazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public BeanDefinition setDepends(String[] depends) {
        this.depends = depends;
        return this;
    }

    public BeanDefinition setScope(BeanType scope) {
        this.scope = scope;
        return this;
    }

    public String getBeanName() {
        return beanName;
    }

    public Class getClazz() {
        return clazz;
    }

    public String[] getDepends() {
        return depends;
    }

    public BeanType getScope() {
        return scope;
    }
}
