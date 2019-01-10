package sunnn.sunframework.resource;

import sunnn.sunframework.bean.BeanType;

import java.lang.reflect.Method;

public class BeanDefinition extends Resource {

//    public static final String TYPE_DEPENDS = "_type";

    private String beanName;

    private Class clazz;

    private Method method;

    private boolean useBeans;

    private String[] depends;

//    private Map<String, InjectType> inject;

    private BeanType scope;

    public BeanDefinition setBeanName(String beanName) {
        this.beanName = beanName;
        return this;
    }

    public BeanDefinition setClazz(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public BeanDefinition setMethod(Method method) {
        this.method = method;
        return this;
    }

    public BeanDefinition setUseBeans(boolean useBeans) {
        this.useBeans = useBeans;
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

    public Method getMethod() {
        return method;
    }

    public boolean isUseBeans() {
        return useBeans;
    }

    public String[] getDepends() {
        return depends;
    }

    public BeanType getScope() {
        return scope;
    }
}
