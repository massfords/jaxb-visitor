package com.massfords.jaxb;


import java.lang.reflect.Field;

/**
 * I need to be able to access the field that models a list of values within a
 * generated bean to see if it has annotations on it that point to external
 * classes. This is one of the ways I discover externally mapped classes in the
 * codegen process.
 *
 * Unfortunately, there is no public getter on the field that contains these
 * annotations and even worse the AbstractListField class is not public so I can't have a
 * ref to it at compile time. Instead, I use the Class.forName trickery below in
 * order to get to the class and then make the field accessible which is a hack.
 *
 * @author mford
 */
class FieldHack {
    static Field listField;
    static {
        try {
            Class<?> defaultAccessClass = Class.forName(
                    "com.sun.tools.xjc.generator.bean.field.AbstractListField");
            listField = defaultAccessClass.getDeclaredField("field");
            listField.setAccessible(true);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
