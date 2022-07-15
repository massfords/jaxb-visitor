package org.example.visitor;

import javax.annotation.Generated;
import extended.Parameter;
import extended.Parameter2;
import extendedJaxbModel.Base;
import extendedJaxbModel.Choice;
import extendedJaxbModel.ParamElem;
import extendedJaxbModel.Single;

@Generated("Generated by jaxb-visitor")
public interface Traverser<E extends Throwable> {


    void traverse(Base aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Choice aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ParamElem aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Single aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Parameter aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Parameter2 aBean, Visitor<?, E> aVisitor)
            throws E
            ;

}