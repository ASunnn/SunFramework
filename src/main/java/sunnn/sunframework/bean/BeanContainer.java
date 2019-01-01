package sunnn.sunframework.bean;

public interface BeanContainer {

    Object getBean(String beanName);

    Class getBeanType(String beanName);

    void registerBean(String beanName, BeanDefinition beanDefinition);

    int count();

    void shutdown();
}
