package sunnn.sunframework.bean;

import sunnn.sunframework.resource.BeanDefinition;

public interface BeanContainer {

    Object getBean(String beanName);

    Class getBeanType(String beanName);

    void registerBean(String beanName, BeanDefinition beanDefinition);

    int count();

    void shutdown();
}
