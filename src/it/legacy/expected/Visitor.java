package org.example.visitor;

import javax.annotation.Generated;

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
public interface Visitor<R, E extends Throwable> {


    R visit(ImportedData aBean)
            throws E
            ;

    R visit(ImportedType aBean)
            throws E
            ;

    R visit(ImportedType.Message aBean)
            throws E
            ;

    R visit(ChoiceElement aBean)
            throws E
            ;

    R visit(ComplexObject aBean)
            throws E
            ;

    R visit(ComplexObject.LocalElement aBean)
            throws E
            ;

    R visit(HasJAXBElement aBean)
            throws E
            ;

    R visit(Problem aBean)
            throws E
            ;

    R visit(Recursive aBean)
            throws E
            ;

    R visit(TSimpleRequest aBean)
            throws E
            ;

    R visit(TSimpleResponse aBean)
            throws E
            ;

}