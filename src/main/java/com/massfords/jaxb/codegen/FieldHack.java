package com.massfords.jaxb.codegen;


import java.lang.reflect.Field;

/**
 * I need to be able to access the field that models a list of values within a
 * generated bean to see if it has annotations on it that point to external
 * classes. This is one of the ways I discover externally mapped classes in the
 * codegen process.
 * <p>
 * Unfortunately, there is no public getter on the field that contains these
 * annotations and even worse the AbstractListField class is not public so I can't have a
 * ref to it at compile time. Instead, I use the Class.forName trickery below in
 * order to get to the class and then make the field accessible which is a hack.
 *
 * @author mford
 */
final class FieldHack {
    private FieldHack() {
    }

    private static final Field LIST_FIELD;

    static {
        try {
            Class<?> defaultAccessClass = Class.forName(
                    "com.sun.tools.xjc.generator.bean.field.AbstractListField");
            LIST_FIELD = defaultAccessClass.getDeclaredField("field");
            LIST_FIELD.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static Field getField() {
        return LIST_FIELD;
    }
}
