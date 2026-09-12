package com.example.demo.support;

import java.lang.reflect.Method;
import java.util.List;
import org.junit.jupiter.api.DisplayNameGenerator;

public class BddDisplayNameGenerator implements DisplayNameGenerator {

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        When when = testClass.getAnnotation(When.class);
        if (when != null) {
            return when.value();
        }

        return testClass.getSimpleName();
    }

    @Override
    public String generateDisplayNameForNestedClass(List<Class<?>> enclosingInstanceTypes, Class<?> nestedClass) {
        When when = nestedClass.getAnnotation(When.class);
        if (when != null) {
            return when.value();
        }

        return nestedClass.getSimpleName();
    }

    @Override
    public String generateDisplayNameForMethod(
            List<Class<?>> enclosingInstanceTypes, Class<?> testClass, Method testMethod) {
        When when = testMethod.getAnnotation(When.class);
        Should should = testMethod.getAnnotation(Should.class);

        // When과 Should 둘 다 쓰면 띄어쓰기로 이어서 표시한다.
        if (when != null && should != null) {
            return when.value() + " " + should.value();
        }

        if (when != null) {
            return when.value();
        }

        if (should != null) {
            return should.value();
        }

        return testMethod.getName();
    }
}
