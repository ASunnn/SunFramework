package sunnn.sunframework.bean;

import sunnn.sunframework.exception.BeanException;
import sunnn.sunframework.resource.BeanDefinition;

public interface BeanContainer {

    String FACTORY_BEAN = "&";

    Object getBean(String beanName) throws Exception;

    BeanDefinition getBeanDefinition(String beanName);

    Class getBeanType(String beanName);

    void registerBean(String beanName, BeanDefinition beanDefinition) throws BeanException;

    int count();

    void shutdown();
}
