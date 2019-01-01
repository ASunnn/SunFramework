package sunnn.sunframework.util;

import sunnn.sunframework.bean.BeanDefinition;

public interface ResourceLoader {

    BeanDefinition[] loadResources();

    BeanDefinition[] loadResources(String path);
}
