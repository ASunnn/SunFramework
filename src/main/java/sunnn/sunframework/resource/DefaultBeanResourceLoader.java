package sunnn.sunframework.resource;

import sunnn.sunframework.annotation.Bean;
import sunnn.sunframework.annotation.Beans;
import sunnn.sunframework.annotation.Depend;
import sunnn.sunframework.annotation.Depends;
import sunnn.sunframework.bean.BeanType;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DefaultBeanResourceLoader implements BeanResourceLoader {

    private ClassResourceLoader classResourceLoader;

    public DefaultBeanResourceLoader() {
        classResourceLoader = new DefaultClassResourceLoader();
    }

    @Override
    public BeanDefinition[] loadResources(String path) throws Exception {
        ClassResource[] resources = classResourceLoader.loadResources(path);

        List<BeanDefinition> definitions = new ArrayList<>();
        for (ClassResource r : resources) {
            BeanDefinition[] definition = loadBeanDefinitions(r);

            if (definition != null)
                Collections.addAll(definitions, definition);
        }

        BeanDefinition[] ds = new BeanDefinition[definitions.size()];
        definitions.toArray(ds);
        return ds;
    }

    private BeanDefinition[] loadBeanDefinitions(ClassResource c) {
        Class clazz = c.getClazz();

        // 判断是哪一种类型的bean声明方式
        if (isBean(clazz)) {
            return new BeanDefinition[]{parseBeanDefinition(clazz)};
        } else if (isBeans(clazz)) {
            return parseBeanDefinitions(clazz);
        }

        return null;
    }

    private boolean isBean(Class clazz) {
        if (clazz.getAnnotation(Bean.class) != null)
            return true;
        return false;
    }

    private boolean isBeans(Class clazz) {
        return clazz.getAnnotation(Beans.class) != null;
    }

    private BeanDefinition parseBeanDefinition(Class clazz) {
        Bean beanAnnotation = (Bean) clazz.getAnnotation(Bean.class);

        BeanDefinition definition = new BeanDefinition();
        definition.setClazz(clazz)
                .setBeanName(beanAnnotation.name())
                .setScope(beanAnnotation.scope())
                .setDepends(getBeanDepends(clazz))
                .setUseBeans(false);
        return definition;
    }

    private BeanDefinition[] parseBeanDefinitions(Class clazz) {
        List<BeanDefinition> definitions = new ArrayList<>();

        BeanDefinition beansDefinition = new BeanDefinition()
                .setBeanName(clazz.getName())
                .setClazz(clazz)
                .setScope(BeanType.SINGLETON)
                .setUseBeans(false);
        definitions.add(beansDefinition);

        Method[] methods = clazz.getDeclaredMethods();
        for (Method method : methods) {
            Bean beanAnnotation = method.getAnnotation(Bean.class);

            if (beanAnnotation != null) {
                Class c = method.getReturnType();

                String[] dep1 = getBeanDepends(method);
                String[] dep2 = getBeanDepends(c);
                String[] dep = new String[dep1.length + dep2.length + 1];

                dep[0] = clazz.getName();
                for (int i = 0, j = 1; i < dep1.length || i < dep2.length; ++i) {
                    if (i < dep1.length)
                        dep[j++] = dep1[i];
                    if (i < dep2.length)
                        dep[j++] = dep2[i];
                }

                BeanDefinition definition = new BeanDefinition()
                        .setBeanName(beanAnnotation.name())
                        .setScope(beanAnnotation.scope())
                        .setClazz(c)
                        .setMethod(method)
                        .setUseBeans(true)
                        .setDepends(dep);
                definitions.add(definition);
            }
        }
        BeanDefinition[] d = new BeanDefinition[definitions.size()];
        definitions.toArray(d);
        return d;
    }

    private String[] getBeanDepends(Class clazz) {
        Field[] fields = clazz.getDeclaredFields();

        List<String> depends = new ArrayList<>();
        for (Field f : fields) {
            Depend depend = f.getAnnotation(Depend.class);
            if (depend != null)
                depends.add(depend.name());
        }

        String[] d = new String[depends.size()];
        depends.toArray(d);
        return d;
    }

    private String[] getBeanDepends(Method method) {
        Depends dependsAnnotation = method.getAnnotation(Depends.class);

        return dependsAnnotation == null ?
                new String[0]
                : dependsAnnotation.name();
    }
}
