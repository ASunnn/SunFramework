package sunnn.sunframework.context;

import sunnn.sunframework.container.BeanContainer;
import sunnn.sunframework.container.DefaultBeanContainer;
import sunnn.sunframework.exception.BeanException;
import sunnn.sunframework.resource.*;

abstract class AbstractApplicationContext implements ApplicationContext {

    private ResourceLoader resourceLoader = new DefaultBeanResourceLoader();

    private BeanContainer beanContainer = new DefaultBeanContainer();

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanContainer.getBean(beanName);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return beanContainer.getBeanDefinition(beanName);
    }

    @Override
    public Class getBeanType(String beanName) {
        return beanContainer.getBeanType(beanName);
    }

    @Override
    public void registerBean(String beanName, BeanDefinition beanDefinition) throws BeanException {
        beanContainer.registerBean(beanName, beanDefinition);
    }

    @Override
    public int count() {
        return beanContainer.count();
    }

    @Override
    public void shutdown() {
        beanContainer.shutdown();
    }

    @Override
    public Resource[] loadResources(String path) throws Exception {
        return resourceLoader.loadResources(path);
    }
}
