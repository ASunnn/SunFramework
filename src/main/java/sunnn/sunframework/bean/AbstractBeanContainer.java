package sunnn.sunframework.bean;

import sunnn.sunframework.resource.BeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class AbstractBeanContainer implements BeanContainer {

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    @Override
    public Object getBean(String beanName) {
        return null;
    }

    @Override
    public Class getBeanType(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null)
            return null;
        return beanDefinition.getClazz();
    }

    @Override
    public void registerBean(String beanName, BeanDefinition beanDefinition) {
        // 先检查Map里有没有这个bean，有的话就报错——重复bean
        // 没有就sei进去
        if (beanDefinitionMap.containsKey(beanName)) {
            // TODO 重复的bean
        }
        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public int count() {
        return beanDefinitionMap.size();
    }

    @Override
    public void shutdown() {

    }
}
