package org.example.visitor;

import jakarta.annotation.Generated;

import org.example.imported.ImportedData;
import org.example.imported.ImportedType;
import org.example.simple.ChoiceElement;
import org.example.simple.ComplexObject;
import org.example.simple.HasJAXBElement;
import org.example.simple.Problem;
import org.example.simple.Recursive;
import org.example.simple.TSimpleRequest;
import org.example.simple.TSimpleResponse;

@Generated("Generated by jaxb-visitor")
public interface Traverser<E extends Throwable> {


    void traverse(ImportedData aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ImportedType aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ImportedType.Message aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ChoiceElement aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ComplexObject aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(ComplexObject.LocalElement aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(HasJAXBElement aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Problem aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(Recursive aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(TSimpleRequest aBean, Visitor<?, E> aVisitor)
            throws E
            ;

    void traverse(TSimpleResponse aBean, Visitor<?, E> aVisitor)
            throws E
            ;

}