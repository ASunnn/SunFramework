package sunnn.sunframework.container;

import sunnn.sunframework.bean.BeanProcessor;

public interface ConfigurableBeanContainer extends BeanContainer {

    BeanProcessor[] getBeanProcessors();
}
