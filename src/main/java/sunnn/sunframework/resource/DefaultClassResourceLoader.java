package sunnn.sunframework.resource;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DefaultClassResourceLoader implements ClassResourceLoader {

    @Override
    public ClassResource[] loadResources(String path) throws ClassNotFoundException {
        URL url = ClassLoader.getSystemClassLoader()
                .getResource(path.replace(".", "/"));
        if (url == null)
            return null;

        File p = new File(url.getPath());
        if (!p.exists())
            return null;

        return doLoadResources(p, path);
    }

    private ClassResource[] doLoadResources(File path, String pkg) throws ClassNotFoundException {
        List<File> classFiles = loadClassFile(path);

        ClassResource[] resources = new ClassResource[classFiles.size()];

        for (int i = 0; i < resources.length; ++i) {
            resources[i] = loadClass(classFiles.get(i), pkg);
        }

        return resources;
    }

    private List<File> loadClassFile(File path) {
        File[] files =
                path.listFiles(pathname -> pathname.isDirectory() || pathname.getName().endsWith(".class"));
        List<File> classFile = new ArrayList<>();

        // 提前返回
        if (files == null)
            return classFile;

        for (File f : files) {
            if (f.isDirectory())
                classFile.addAll(loadClassFile(f));
            else
                classFile.add(f);
        }
        return classFile;
    }

    private ClassResource loadClass(File file, String pkg) throws ClassNotFoundException {
        String className = file.getPath().replace(File.separatorChar, '.')
                .substring(0, file.getPath().lastIndexOf('.'));

        String fullClassName = className.substring(className.indexOf(pkg));
        Class clazz = ClassLoader.getSystemClassLoader().loadClass(fullClassName);
        return new ClassResource(className, clazz);
    }
}
