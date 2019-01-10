package sunnn.sunframework.util;

import sunnn.sunframework.exception.IllegalConfigException;
import sunnn.sunframework.exception.LoadResourceException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    private enum PropertiesItem {
        BASE_PACKAGE("sun-framework.base-package");

        private String value;

        PropertiesItem(String value){
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    private static final String[] resources = {
            "application.yml",
            "application.yaml",
            "application.properties"
    };

    private static final PropertiesUtil instance = new PropertiesUtil();

    private Properties properties;

    private PropertiesUtil() {
        loadProperties();
    }

    private void loadProperties() {
        for (String r : resources) {
            try {
                InputStream is = ClassLoader.getSystemClassLoader().getResourceAsStream(r);
                if (is == null)
                    continue;
                properties = new Properties();
                properties.load(is);
            } catch (IOException e) {
                throw new LoadResourceException("Can Not Load Resource : " + r);
            }
        }

        checkProperties();
    }

    private void checkProperties() {
        if (properties == null) {
            throw new LoadResourceException("Can Not Load Resource");
        }

        HashSet<String> checked = new HashSet<>(4);
        /*
            检查读取到的配置信息，主要是进行查重
         */
        for (Object o : properties.entrySet()) {
            Map.Entry entry = (Map.Entry) o;
            String key = (String) entry.getKey();
//            String value = (String) entry.getValue();
            if (checked.contains(key)) {
                throw new IllegalConfigException("Duplicate Config Item '" + key + "'");
            }
            checked.add(key);
        }
        /*
            检查用户的配置中有没有遗漏。
            如果有遗漏，且该配置不提供默认值，报错
            如果遗漏的配置可以用默认值代替，向properties中补上该配置
         */
        if (!checked.contains(PropertiesItem.BASE_PACKAGE.value)) {
            throw new IllegalConfigException("Can Not Find Config Item '" + PropertiesItem.BASE_PACKAGE.value + "'");
        }
    }

    public String basePackage() {
        if (properties == null){
            return null;
        }
        return properties.getProperty(PropertiesItem.BASE_PACKAGE.value);
    }

    public static PropertiesUtil getInstance() {
        return instance;
    }
}
