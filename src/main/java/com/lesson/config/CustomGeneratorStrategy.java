package com.lesson.config;

import org.jooq.codegen.DefaultGeneratorStrategy;
import org.jooq.meta.Definition;

public class CustomGeneratorStrategy extends DefaultGeneratorStrategy {
    @Override
    public String getJavaIdentifier(Definition definition) {
        String name = super.getJavaIdentifier(definition);
        // 将下划线命名转换为驼峰命名
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        for (char c : name.toCharArray()) {
            if (c == '_') {
                nextUpper = true;
            } else {
                result.append(nextUpper ? Character.toUpperCase(c) : c);
                nextUpper = false;
            }
        }
        return result.toString();
    }
} 