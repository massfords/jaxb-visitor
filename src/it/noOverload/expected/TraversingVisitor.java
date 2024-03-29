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
public class TraversingVisitor<R, E extends Throwable>
        implements Visitor<R, E> {

    private boolean traverseFirst;
    private Visitor<R, E> visitor;
    private Traverser<E> traverser;
    private TraversingVisitorProgressMonitor progressMonitor;

    public TraversingVisitor(Traverser<E> aTraverser, Visitor<R, E> aVisitor) {
        traverser = aTraverser;
        visitor = aVisitor;
    }

    public boolean getTraverseFirst() {
        return traverseFirst;
    }

    public void setTraverseFirst(boolean aVisitor) {
        traverseFirst = aVisitor;
    }

    public Visitor<R, E> getVisitor() {
        return visitor;
    }

    public void setVisitor(Visitor<R, E> aVisitor) {
        visitor = aVisitor;
    }

    public Traverser<E> getTraverser() {
        return traverser;
    }

    public void setTraverser(Traverser<E> aVisitor) {
        traverser = aVisitor;
    }

    public TraversingVisitorProgressMonitor getProgressMonitor() {
        return progressMonitor;
    }

    public void setProgressMonitor(TraversingVisitorProgressMonitor aVisitor) {
        progressMonitor = aVisitor;
    }

    @Override
    public R visitImportedData(ImportedData aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseImportedData(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseImportedData(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitImportedType(ImportedType aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseImportedType(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseImportedType(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitMessage(ImportedType.Message aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseMessage(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseMessage(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitChoiceElement(ChoiceElement aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseChoiceElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseChoiceElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitComplexObject(ComplexObject aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseComplexObject(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseComplexObject(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitLocalElement(ComplexObject.LocalElement aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseLocalElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseLocalElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitHasJAXBElement(HasJAXBElement aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseHasJAXBElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseHasJAXBElement(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitProblem(Problem aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseProblem(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseProblem(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitRecursive(Recursive aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseRecursive(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseRecursive(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitTSimpleRequest(TSimpleRequest aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseTSimpleRequest(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseTSimpleRequest(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

    @Override
    public R visitTSimpleResponse(TSimpleResponse aBean)
            throws E {
        if (traverseFirst == true) {
            getTraverser().traverseTSimpleResponse(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        R returnVal;
        returnVal = aBean.accept(getVisitor());
        if (progressMonitor != null) {
            progressMonitor.visited(aBean);
        }
        if (traverseFirst == false) {
            getTraverser().traverseTSimpleResponse(aBean, this);
            if (progressMonitor != null) {
                progressMonitor.traversed(aBean);
            }
        }
        return returnVal;
    }

}
