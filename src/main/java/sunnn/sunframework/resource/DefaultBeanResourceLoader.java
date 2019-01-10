package sunnn.sunframework.resource;

import sunnn.sunframework.annotation.Bean;
import sunnn.sunframework.annotation.Depend;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
            BeanDefinition definition = loadBeanDefinition(r);
            if (definition != null)
                definitions.add(definition);
        }

        BeanDefinition[] ds = new BeanDefinition[definitions.size()];
        definitions.toArray(ds);
        return ds;
    }

    private BeanDefinition loadBeanDefinition(ClassResource c) {
        Class clazz = c.getClazz();

        if (isBean(clazz)) {
            return parseBeanDefinition(clazz);
        }
        return null;
    }

    private BeanDefinition parseBeanDefinition(Class clazz) {
        Bean beanAnnotation = (Bean) clazz.getAnnotation(Bean.class);

        BeanDefinition definition = new BeanDefinition();
        definition.setClazz(clazz)
                .setBeanName(beanAnnotation.name())
                .setScope(beanAnnotation.scope())
                .setDepends(getBeanDepends(clazz));
        return definition;
    }

    private boolean isBean(Class clazz) {
        if (clazz.getAnnotation(Bean.class) == null)
            return false;
        return true;
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
}
