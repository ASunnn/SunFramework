package sunnn.sunframework.resource;


public interface BeanResourceLoader extends ResourceLoader {

    BeanDefinition[] loadResources(String path) throws Exception;
}
