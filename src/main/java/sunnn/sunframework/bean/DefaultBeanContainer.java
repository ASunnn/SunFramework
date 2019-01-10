package sunnn.sunframework.bean;

import sunnn.sunframework.annotation.Depend;
import sunnn.sunframework.annotation.Depends;
import sunnn.sunframework.exception.CanNotFindBeanException;
import sunnn.sunframework.exception.CircularDependsException;
import sunnn.sunframework.exception.DuplicateBeanNameException;
import sunnn.sunframework.resource.BeanDefinition;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DefaultBeanContainer implements ConfigurableBeanContainer {

    private List<BeanProcessor> beanProcessors = new ArrayList<>();

    private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();

    private Map<String, Object> createdSingletonBeanObject = new ConcurrentHashMap<>();

    /**
     * Map<A, B>
     * A → B
     * A 依赖于 B
     */
    private Map<String, String> dependentBean = new ConcurrentHashMap<>();

    @Override
    public Object getBean(String beanName) throws Exception {
        String currentBeanName = beanNameTransfer(beanName);

        return doGetBean(currentBeanName, beanName.startsWith(BeanContainer.FACTORY_BEAN));
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null)
            return null;
        return beanDefinition;
    }

    @Override
    public Class getBeanType(String beanName) {
        BeanDefinition beanDefinition = beanDefinitionMap.get(beanName);
        if (beanDefinition == null)
            return null;
        return beanDefinition.getClazz();
    }

    @Override
    public void registerBean(String beanName, BeanDefinition beanDefinition) throws DuplicateBeanNameException {
        if (isIllegalBeanName(beanName))
            throw new IllegalArgumentException("Illegal Bean Name '" + beanName + "'");
        // 先检查Map里有没有这个bean，有的话就报错——重复bean
        // 没有就sei进去
        if (beanDefinitionMap.containsKey(beanName))
            throw new DuplicateBeanNameException("Bean Container Already Has Beans Called '" + beanName + "'");

        beanDefinitionMap.put(beanName, beanDefinition);
    }

    @Override
    public int count() {
        return beanDefinitionMap.size();
    }

    @Override
    public void shutdown() {

    }

    @Override
    public BeanProcessor[] getBeanProcessors() {
        return new BeanProcessor[0];
    }

    private boolean isIllegalBeanName(String beanName) {
        return beanName.startsWith(BeanContainer.FACTORY_BEAN);
    }

    private String beanNameTransfer(String originalBeanName) {
        if (originalBeanName == null)
            throw new NullPointerException("Bean Name Cannot Be NULL");

        if (originalBeanName.startsWith(BeanContainer.FACTORY_BEAN)) {
            return  originalBeanName.substring(BeanContainer.FACTORY_BEAN.length());
        }
        return originalBeanName;
    }

    /**
     * 记得考虑创建bean的线程同步
     */
    private Object doGetBean(String beanName, boolean getFactoryBean) throws Exception {
        BeanDefinition definition = getBeanDefinition(beanName);
        if (definition == null) {
            throw new CanNotFindBeanException("Can Not Find Bean Called '" + beanName + "'");
        }

        BeanType type = definition.getScope();
        Object bean;
        if (type.equals(BeanType.SINGLETON)) {
            // 从缓存中获取已经创建好的单例bean
            bean = getSingletonBeanFromCache(beanName);

            if (bean != null) { // 如果获取成功，返回需要的实例
                return getObjectFromBean(bean, getFactoryBean);
            }
            bean = createSingletonBeanInstance(definition);
            createdSingletonBeanObject.put(beanName, bean);
            return getObjectFromBean(bean, getFactoryBean);
        } else {
            // 非单例bean
            bean = createBeanInstance(definition);
            return getObjectFromBean(bean, getFactoryBean);
        }
    }

    private Object getSingletonBeanFromCache(String beanName) {
        return createdSingletonBeanObject.get(beanName);
    }

    private Object getObjectFromBean(Object bean, boolean getFactoryBean) throws Exception {
        if (getFactoryBean)
            return bean;

        if (bean instanceof FactoryBean) {
            return ((FactoryBean) bean).getObject();
        } else
            return bean;
    }

    private Object createSingletonBeanInstance(BeanDefinition beanDefinition) throws Exception {
        synchronized (this) {
            // 在次检查获得锁之前有没有被其他线程创建好了bean
            Object bean = getSingletonBeanFromCache(beanDefinition.getBeanName());
            if (bean != null)
                return bean;

            return createBeanInstance(beanDefinition);
        }
    }

    private Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
        /*
            初始化Bean依赖
         */
        String[] depends = beanDefinition.getDepends();
        if (depends != null && depends.length > 0) {
            for (String dep : depends) {
                String d = dependentBean.get(dep);
                if (d != null && d.equals(beanDefinition.getBeanName())) {
                    throw new CircularDependsException(
                            "Circular Depends-on Relationship Between '" + beanDefinition.getBeanName()  + "' And '" + d + "'");
                }
                dependentBean.put(beanDefinition.getBeanName(), dep);
                getBean(dep);
            }
        }
        /*
            创建bean实例
         */
        Object bean = doCreateInstance(beanDefinition);
        /*
            如果bean实现了BeanEvent接口，处理
         */
        if (bean instanceof BeanEvent)
            ((BeanEvent) bean).init();
        /*
            依赖注入:
            1.找到适合的setter依赖注入
            2.找不到用反射强行设值
         */
        autowireBean(bean, beanDefinition);
        /*
            执行BeanProcessor
         */
        for (BeanProcessor processor : beanProcessors) {
            processor.processor(bean);
        }

        return bean;
    }


    private Object doCreateInstance(BeanDefinition definition) throws Exception {
        Object bean = null;

        if (definition.isUseBeans()) {
            // 检查可见性
            Method method = definition.getMethod();
            if (!Modifier.isPublic(method.getModifiers())) {
                throw new IllegalAccessException("Can Not Access Bean Method : " + definition.getBeanName());
            }

            // 获取依赖的传入参数
            Depends dependsAnnotation = method.getAnnotation(Depends.class);
            String[] depends = dependsAnnotation == null ?
                    new String[0]
                    : dependsAnnotation.name();
            // 获取参数实例
            Object[] dependObjects = new Object[depends.length];
            for (int i = 0; i < depends.length; ++i) {
                dependObjects[i] = getBean(depends[i]);
            }

            bean = method.invoke(getBean(definition.getDepends()[0]), dependObjects);
        } else {
            // 检查可见性
            Class clazz = definition.getClazz();
            if (!Modifier.isPublic(clazz.getModifiers())) {
                throw new IllegalAccessException("Can Not Access Bean Constructor : " + definition.getBeanName());
            }
            // 获取合适的构造方法
            Constructor[] constructors = clazz.getDeclaredConstructors();

            for (Constructor c : constructors) {
                if (c.getParameterTypes().length == 0) {
                    bean = c.newInstance();
                }
            }
        }
        return bean;
    }

    private void autowireBean(Object bean, BeanDefinition beanDefinition) throws Exception {
        Class clazz = beanDefinition.getClazz();
        Field[] fields = getAutowireFields(clazz);

        for (Field field : fields) {
            String depend =
                    field.getAnnotation(Depend.class).name();

            Method method = getAutowireMethod(depend, field, clazz);
            if (method == null) {
                field.setAccessible(true);
                field.set(bean, getBean(depend));
            } else
                method.invoke(bean, getBean(depend));
        }
    }

    private Field[] getAutowireFields(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();

        List<Field> targets = new ArrayList<>();
        for (Field f : fields) {
            Depend depend = f.getAnnotation(Depend.class);
            if (depend != null) {
                targets.add(f);
            }
        }

        Field[] t = new Field[targets.size()];
        targets.toArray(t);
        return t;
    }

    private Method getAutowireMethod(String beanName, Field field, Class clazz) {
        String fieldName = field.getName();
        String methodName = "set"
                + fieldName.substring(0, 1).toUpperCase()
                + fieldName.substring(1);

        // 寻找setter
        Method target;
        try {
            target = clazz.getMethod(methodName, getBeanType(beanName));
        } catch (NoSuchMethodException e) {
            target = null;
        }
        return target;
    }

    public void addProcessor(BeanProcessor processor) {
        beanProcessors.add(processor);
    }
}
