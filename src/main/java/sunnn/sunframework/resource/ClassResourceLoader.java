package sunnn.sunframework.resource;

import java.io.IOException;

public interface ClassResourceLoader {

    ClassResource[] loadResources(String path) throws ClassNotFoundException;
}
