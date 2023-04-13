package net.syl.dandelion.support.aop.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Anchor {

    /**
     * 业务ID
     * 必填
     * SpEL表达式
     */
    String bizId();

    /**
     * 结果
     * 必填
     * SpEL表达式
     */
    String response();

    /**
     * 收信人
     * 选填
     * SpEL表达式
     */
    String receivers() default "";

    /**
     * 模板ID
     * 选填
     * SpEL表达式
     */
    String templateId() default "";

    /**
     * 标签
     * 选填
     */
    String tag() default "'anchor'";

}
