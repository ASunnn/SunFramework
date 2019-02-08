package sunnn.sunframework.annotation;

import sunnn.sunframework.bean.BeanType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Controller {

    String name();

    BeanType scope() default BeanType.SINGLETON;

    String handlerServlet() default "dispatcherServlet";
}
