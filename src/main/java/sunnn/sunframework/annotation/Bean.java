package sunnn.sunframework.annotation;

import sunnn.sunframework.bean.BeanType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Bean {

    String name();

    BeanType scope() default BeanType.SINGLETON;
}
