package sunnn.sunframework.context;

import sunnn.sunframework.resource.BeanDefinition;
import sunnn.sunframework.resource.Resource;
import sunnn.sunframework.util.PropertiesUtil;

public class DefaultApplicationContext extends AbstractApplicationContext {

    public DefaultApplicationContext() throws Exception {
        init();
    }

    private void init() throws Exception {
        PropertiesUtil ppUtil = PropertiesUtil.getInstance();

        String basePackage = ppUtil.basePackage();

        Resource[] resources = loadResources(basePackage);

        for (Resource r : resources) {
            BeanDefinition definition = (BeanDefinition) r;
            registerBean(definition.getBeanName(), definition);
        }
    }
}
