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
public interface Visitor<R, E extends Throwable> {


    R visitImportedData(ImportedData aBean)
            throws E
            ;

    R visitImportedType(ImportedType aBean)
            throws E
            ;

    R visitMessage(ImportedType.Message aBean)
            throws E
            ;

    R visitChoiceElement(ChoiceElement aBean)
            throws E
            ;

    R visitComplexObject(ComplexObject aBean)
            throws E
            ;

    R visitLocalElement(ComplexObject.LocalElement aBean)
            throws E
            ;

    R visitHasJAXBElement(HasJAXBElement aBean)
            throws E
            ;

    R visitProblem(Problem aBean)
            throws E
            ;

    R visitRecursive(Recursive aBean)
            throws E
            ;

    R visitTSimpleRequest(TSimpleRequest aBean)
            throws E
            ;

    R visitTSimpleResponse(TSimpleResponse aBean)
            throws E
            ;

}
