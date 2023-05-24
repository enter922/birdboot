package Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(value= ElementType.TYPE)//注解应用范围
@Retention(value = RetentionPolicy.RUNTIME)//
public @interface Controller {
}
