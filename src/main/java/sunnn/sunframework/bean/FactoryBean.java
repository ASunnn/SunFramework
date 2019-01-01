package sunnn.sunframework.bean;

public interface FactoryBean<T> {

    T getObject() throws Exception;
}
