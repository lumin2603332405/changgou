package com.changgou.search.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 描述:
 * 项目名：changgou-parent
 * 包名：com.changgou.search.annotation
 * 作者：lusir
 * 日期：2020-02-05  18:46
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MySearchAnnotation {
}
