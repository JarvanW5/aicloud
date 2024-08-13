package org.spring.aicloud.util.idempotent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义幂等性判断注解
 */
@Target(ElementType.METHOD)      // 方法注解
@Retention(RetentionPolicy.RUNTIME)        // 程序运行期间有效
public @interface Idempotent {

    /**
     * 幂等性判断的时长
     * @return
     */
    int time() default 60;
}
