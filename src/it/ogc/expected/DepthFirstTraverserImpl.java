package org.example.visitor;

import jakarta.annotation.Generated;
import jakarta.xml.bind.JAXBElement;

import net.opengis.fes._2.AbstractIdType;
import net.opengis.fes._2.AdditionalOperatorsType;
import net.opengis.fes._2.ArgumentType;
import net.opengis.fes._2.ArgumentsType;
import net.opengis.fes._2.AvailableFunctionType;
import net.opengis.fes._2.AvailableFunctionsType;
import net.opengis.fes._2.BBOXType;
import net.opengis.fes._2.BinaryComparisonOpType;
import net.opengis.fes._2.BinaryLogicOpType;
import net.opengis.fes._2.BinarySpatialOpType;
import net.opengis.fes._2.BinaryTemporalOpType;
import net.opengis.fes._2.ComparisonOperatorType;
import net.opengis.fes._2.ComparisonOperatorsType;
import net.opengis.fes._2.ConformanceType;
import net.opengis.fes._2.DistanceBufferType;
import net.opengis.fes._2.ExtendedCapabilitiesType;
import net.opengis.fes._2.ExtensionOperatorType;
import net.opengis.fes._2.FilterCapabilities;
import net.opengis.fes._2.FilterType;
import net.opengis.fes._2.FunctionType;
import net.opengis.fes._2.GeometryOperandsType;
import net.opengis.fes._2.IdCapabilitiesType;
import net.opengis.fes._2.LiteralType;
import net.opengis.fes._2.LogicalOperators;
import net.opengis.fes._2.LowerBoundaryType;
import net.opengis.fes._2.MeasureType;
import net.opengis.fes._2.PropertyIsBetweenType;
import net.opengis.fes._2.PropertyIsLikeType;
import net.opengis.fes._2.PropertyIsNilType;
import net.opengis.fes._2.PropertyIsNullType;
import net.opengis.fes._2.ResourceIdType;
import net.opengis.fes._2.ResourceIdentifierType;
import net.opengis.fes._2.ScalarCapabilitiesType;
import net.opengis.fes._2.SortByType;
import net.opengis.fes._2.SortPropertyType;
import net.opengis.fes._2.SpatialCapabilitiesType;
import net.opengis.fes._2.SpatialOperatorType;
import net.opengis.fes._2.SpatialOperatorsType;
import net.opengis.fes._2.TemporalCapabilitiesType;
import net.opengis.fes._2.TemporalOperandsType;
import net.opengis.fes._2.TemporalOperatorType;
import net.opengis.fes._2.TemporalOperatorsType;
import net.opengis.fes._2.UnaryLogicOpType;
import net.opengis.fes._2.UpperBoundaryType;
import net.opengis.ows._1.AbstractReferenceBaseType;
import net.opengis.ows._1.AcceptFormatsType;
import net.opengis.ows._1.AcceptVersionsType;
import net.opengis.ows._1.AddressType;
import net.opengis.ows._1.AllowedValues;
import net.opengis.ows._1.AnyValue;
import net.opengis.ows._1.BasicIdentificationType;
import net.opengis.ows._1.BoundingBoxType;
import net.opengis.ows._1.CapabilitiesBaseType;
import net.opengis.ows._1.CodeType;
import net.opengis.ows._1.ContactType;
import net.opengis.ows._1.ContentsBaseType;
import net.opengis.ows._1.DCP;
import net.opengis.ows._1.DatasetDescriptionSummaryBaseType;
import net.opengis.ows._1.DescriptionType;
import net.opengis.ows._1.DomainMetadataType;
import net.opengis.ows._1.DomainType;
import net.opengis.ows._1.ExceptionReport;
import net.opengis.ows._1.ExceptionType;
import net.opengis.ows._1.GetCapabilitiesType;
import net.opengis.ows._1.GetResourceByIdType;
import net.opengis.ows._1.HTTP;
import net.opengis.ows._1.IdentificationType;
import net.opengis.ows._1.KeywordsType;
import net.opengis.ows._1.LanguageStringType;
import net.opengis.ows._1.ManifestType;
import net.opengis.ows._1.MetadataType;
import net.opengis.ows._1.NoValues;
import net.opengis.ows._1.OnlineResourceType;
import net.opengis.ows._1.Operation;
import net.opengis.ows._1.OperationsMetadata;
import net.opengis.ows._1.RangeType;
import net.opengis.ows._1.ReferenceGroupType;
import net.opengis.ows._1.ReferenceType;
import net.opengis.ows._1.RequestMethodType;
import net.opengis.ows._1.ResponsiblePartySubsetType;
import net.opengis.ows._1.ResponsiblePartyType;
import net.opengis.ows._1.SectionsType;
import net.opengis.ows._1.ServiceIdentification;
import net.opengis.ows._1.ServiceProvider;
import net.opengis.ows._1.ServiceReferenceType;
import net.opengis.ows._1.TelephoneType;
import net.opengis.ows._1.UnNamedDomainType;
import net.opengis.ows._1.ValueType;
import net.opengis.ows._1.ValuesReference;
import net.opengis.ows._1.WGS84BoundingBoxType;

@Generated("Generated by jaxb-visitor")
public class DepthFirstTraverserImpl<E extends Throwable>
        implements Traverser<E> {


    @Override
    public void traverse(AdditionalOperatorsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ExtensionOperatorType bean : aBean.getOperator()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ArgumentType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getMetadata() != null) {
            aBean.getMetadata().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ArgumentsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ArgumentType bean : aBean.getArgument()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(AvailableFunctionType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getMetadata() != null) {
            aBean.getMetadata().accept(aVisitor);
        }
        if (aBean.getArguments() != null) {
            aBean.getArguments().accept(aVisitor);
        }
    }

    @Override
    public void traverse(AvailableFunctionsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (AvailableFunctionType bean : aBean.getFunction()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(BBOXType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
        if (aBean.getAny() instanceof Visitable) {
            ((Visitable) aBean.getAny()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(BinaryComparisonOpType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<?> obj : aBean.getExpression()) {
            if (obj.getValue() instanceof Visitable) {
                ((Visitable) obj.getValue()).accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(BinaryLogicOpType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<?> obj : aBean.getComparisonOpsOrSpatialOpsOrTemporalOps()) {
            if (obj.getValue() instanceof Visitable) {
                ((Visitable) obj.getValue()).accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(BinarySpatialOpType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
        if (aBean.getAny() instanceof Visitable) {
            ((Visitable) aBean.getAny()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(BinaryTemporalOpType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
        if (aBean.getAny() instanceof Visitable) {
            ((Visitable) aBean.getAny()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(ComparisonOperatorType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ComparisonOperatorsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ComparisonOperatorType bean : aBean.getComparisonOperator()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ConformanceType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (DomainType bean : aBean.getConstraint()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(DistanceBufferType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
        if (aBean.getAny() instanceof Visitable) {
            ((Visitable) aBean.getAny()).accept(aVisitor);
        }
        if (aBean.getDistance() != null) {
            aBean.getDistance().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ExtendedCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getAdditionalOperators() != null) {
            aBean.getAdditionalOperators().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ExtensionOperatorType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(FilterCapabilities aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getConformance() != null) {
            aBean.getConformance().accept(aVisitor);
        }
        if (aBean.getIdCapabilities() != null) {
            aBean.getIdCapabilities().accept(aVisitor);
        }
        if (aBean.getScalarCapabilities() != null) {
            aBean.getScalarCapabilities().accept(aVisitor);
        }
        if (aBean.getSpatialCapabilities() != null) {
            aBean.getSpatialCapabilities().accept(aVisitor);
        }
        if (aBean.getTemporalCapabilities() != null) {
            aBean.getTemporalCapabilities().accept(aVisitor);
        }
        if (aBean.getFunctions() != null) {
            aBean.getFunctions().accept(aVisitor);
        }
        if (aBean.getExtendedCapabilities() != null) {
            aBean.getExtendedCapabilities().accept(aVisitor);
        }
    }

    @Override
    public void traverse(FilterType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getComparisonOps() != null) {
            aBean.getComparisonOps().getValue().accept(aVisitor);
        }
        if (aBean.getSpatialOps() != null) {
            aBean.getSpatialOps().getValue().accept(aVisitor);
        }
        if (aBean.getTemporalOps() != null) {
            aBean.getTemporalOps().getValue().accept(aVisitor);
        }
        if (aBean.getLogicOps() != null) {
            aBean.getLogicOps().getValue().accept(aVisitor);
        }
        if (aBean.getExtensionOps() != null) {
            aBean.getExtensionOps().accept(aVisitor);
        }
        if (aBean.getFunction() != null) {
            aBean.getFunction().accept(aVisitor);
        }
        for (JAXBElement<? extends AbstractIdType> obj : aBean.getId()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(FunctionType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<?> obj : aBean.getExpression()) {
            if (obj.getValue() instanceof Visitable) {
                ((Visitable) obj.getValue()).accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(GeometryOperandsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (GeometryOperandsType.GeometryOperand bean : aBean.getGeometryOperand()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(GeometryOperandsType.GeometryOperand aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(IdCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ResourceIdentifierType bean : aBean.getResourceIdentifier()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(LiteralType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (Object bean : aBean.getContent()) {
            if (bean instanceof Visitable) {
                ((Visitable) bean).accept(aVisitor);
            } else {
                if (bean instanceof JAXBElement<?>) {
                    if (((JAXBElement<?>) bean).getValue() instanceof Visitable) {
                        ((Visitable) ((JAXBElement<?>) bean).getValue()).accept(aVisitor);
                    }
                }
            }
        }
    }

    @Override
    public void traverse(LogicalOperators aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(LowerBoundaryType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(MeasureType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(PropertyIsBetweenType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
        if (aBean.getLowerBoundary() != null) {
            aBean.getLowerBoundary().accept(aVisitor);
        }
        if (aBean.getUpperBoundary() != null) {
            aBean.getUpperBoundary().accept(aVisitor);
        }
    }

    @Override
    public void traverse(PropertyIsLikeType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<?> obj : aBean.getExpression()) {
            if (obj.getValue() instanceof Visitable) {
                ((Visitable) obj.getValue()).accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(PropertyIsNilType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(PropertyIsNullType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(ResourceIdType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ResourceIdentifierType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getMetadata() != null) {
            aBean.getMetadata().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ScalarCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getLogicalOperators() != null) {
            aBean.getLogicalOperators().accept(aVisitor);
        }
        if (aBean.getComparisonOperators() != null) {
            aBean.getComparisonOperators().accept(aVisitor);
        }
    }

    @Override
    public void traverse(SortByType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (SortPropertyType bean : aBean.getSortProperty()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(SortPropertyType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(SpatialCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getGeometryOperands() != null) {
            aBean.getGeometryOperands().accept(aVisitor);
        }
        if (aBean.getSpatialOperators() != null) {
            aBean.getSpatialOperators().accept(aVisitor);
        }
    }

    @Override
    public void traverse(SpatialOperatorType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getGeometryOperands() != null) {
            aBean.getGeometryOperands().accept(aVisitor);
        }
    }

    @Override
    public void traverse(SpatialOperatorsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (SpatialOperatorType bean : aBean.getSpatialOperator()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(TemporalCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getTemporalOperands() != null) {
            aBean.getTemporalOperands().accept(aVisitor);
        }
        if (aBean.getTemporalOperators() != null) {
            aBean.getTemporalOperators().accept(aVisitor);
        }
    }

    @Override
    public void traverse(TemporalOperandsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (TemporalOperandsType.TemporalOperand bean : aBean.getTemporalOperand()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(TemporalOperandsType.TemporalOperand aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(TemporalOperatorType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getTemporalOperands() != null) {
            aBean.getTemporalOperands().accept(aVisitor);
        }
    }

    @Override
    public void traverse(TemporalOperatorsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (TemporalOperatorType bean : aBean.getTemporalOperator()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(UnaryLogicOpType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getComparisonOps() != null) {
            aBean.getComparisonOps().getValue().accept(aVisitor);
        }
        if (aBean.getSpatialOps() != null) {
            aBean.getSpatialOps().getValue().accept(aVisitor);
        }
        if (aBean.getTemporalOps() != null) {
            aBean.getTemporalOps().getValue().accept(aVisitor);
        }
        if (aBean.getLogicOps() != null) {
            aBean.getLogicOps().getValue().accept(aVisitor);
        }
        if (aBean.getExtensionOps() != null) {
            aBean.getExtensionOps().accept(aVisitor);
        }
        if (aBean.getFunction() != null) {
            aBean.getFunction().accept(aVisitor);
        }
        for (JAXBElement<? extends AbstractIdType> obj : aBean.getId()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(UpperBoundaryType aBean, Visitor<?, E> aVisitor)
            throws E {
        if ((aBean.getExpression() != null) && (aBean.getExpression().getValue() instanceof Visitable)) {
            ((Visitable) aBean.getExpression().getValue()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(AbstractReferenceBaseType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(AcceptFormatsType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(AcceptVersionsType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(AddressType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(AllowedValues aBean, Visitor<?, E> aVisitor)
            throws E {
        for (Object bean : aBean.getValueOrRange()) {
            if (bean instanceof Visitable) {
                ((Visitable) bean).accept(aVisitor);
            } else {
                if (bean instanceof JAXBElement<?>) {
                    if (((JAXBElement<?>) bean).getValue() instanceof Visitable) {
                        ((Visitable) ((JAXBElement<?>) bean).getValue()).accept(aVisitor);
                    }
                }
            }
        }
    }

    @Override
    public void traverse(AnyValue aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(BasicIdentificationType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(BoundingBoxType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(CapabilitiesBaseType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getServiceIdentification() != null) {
            aBean.getServiceIdentification().accept(aVisitor);
        }
        if (aBean.getServiceProvider() != null) {
            aBean.getServiceProvider().accept(aVisitor);
        }
        if (aBean.getOperationsMetadata() != null) {
            aBean.getOperationsMetadata().accept(aVisitor);
        }
    }

    @Override
    public void traverse(CodeType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ContactType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getPhone() != null) {
            aBean.getPhone().accept(aVisitor);
        }
        if (aBean.getAddress() != null) {
            aBean.getAddress().accept(aVisitor);
        }
        if (aBean.getOnlineResource() != null) {
            aBean.getOnlineResource().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ContentsBaseType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (DatasetDescriptionSummaryBaseType bean : aBean.getDatasetDescriptionSummary()) {
            bean.accept(aVisitor);
        }
        for (MetadataType bean : aBean.getOtherSource()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(DCP aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getHTTP() != null) {
            aBean.getHTTP().accept(aVisitor);
        }
    }

    @Override
    public void traverse(DatasetDescriptionSummaryBaseType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (WGS84BoundingBoxType bean : aBean.getWGS84BoundingBox()) {
            bean.accept(aVisitor);
        }
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (JAXBElement<? extends BoundingBoxType> obj : aBean.getBoundingBox()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
        for (DatasetDescriptionSummaryBaseType bean : aBean.getDatasetDescriptionSummary()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(DescriptionType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(DomainMetadataType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(DomainType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getAllowedValues() != null) {
            aBean.getAllowedValues().accept(aVisitor);
        }
        if (aBean.getAnyValue() != null) {
            aBean.getAnyValue().accept(aVisitor);
        }
        if (aBean.getNoValues() != null) {
            aBean.getNoValues().accept(aVisitor);
        }
        if (aBean.getValuesReference() != null) {
            aBean.getValuesReference().accept(aVisitor);
        }
        if (aBean.getDefaultValue() != null) {
            aBean.getDefaultValue().accept(aVisitor);
        }
        if (aBean.getMeaning() != null) {
            aBean.getMeaning().accept(aVisitor);
        }
        if (aBean.getDataType() != null) {
            aBean.getDataType().accept(aVisitor);
        }
        if (aBean.getUOM() != null) {
            aBean.getUOM().accept(aVisitor);
        }
        if (aBean.getReferenceSystem() != null) {
            aBean.getReferenceSystem().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ExceptionReport aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ExceptionType bean : aBean.getException()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ExceptionType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(GetCapabilitiesType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getAcceptVersions() != null) {
            aBean.getAcceptVersions().accept(aVisitor);
        }
        if (aBean.getSections() != null) {
            aBean.getSections().accept(aVisitor);
        }
        if (aBean.getAcceptFormats() != null) {
            aBean.getAcceptFormats().accept(aVisitor);
        }
    }

    @Override
    public void traverse(GetResourceByIdType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(HTTP aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<RequestMethodType> obj : aBean.getGetOrPost()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
    }

    @Override
    public void traverse(IdentificationType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<? extends BoundingBoxType> obj : aBean.getBoundingBox()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(KeywordsType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (LanguageStringType bean : aBean.getKeyword()) {
            bean.accept(aVisitor);
        }
        if (aBean.getType() != null) {
            aBean.getType().accept(aVisitor);
        }
    }

    @Override
    public void traverse(LanguageStringType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ManifestType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (ReferenceGroupType bean : aBean.getReferenceGroup()) {
            bean.accept(aVisitor);
        }
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(MetadataType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getAbstractMetaData() instanceof Visitable) {
            ((Visitable) aBean.getAbstractMetaData()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(NoValues aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(OnlineResourceType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(Operation aBean, Visitor<?, E> aVisitor)
            throws E {
        for (DCP bean : aBean.getDCP()) {
            bean.accept(aVisitor);
        }
        for (DomainType bean : aBean.getParameter()) {
            bean.accept(aVisitor);
        }
        for (DomainType bean : aBean.getConstraint()) {
            bean.accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(OperationsMetadata aBean, Visitor<?, E> aVisitor)
            throws E {
        for (Operation bean : aBean.getOperation()) {
            bean.accept(aVisitor);
        }
        for (DomainType bean : aBean.getParameter()) {
            bean.accept(aVisitor);
        }
        for (DomainType bean : aBean.getConstraint()) {
            bean.accept(aVisitor);
        }
        if (aBean.getExtendedCapabilities() instanceof Visitable) {
            ((Visitable) aBean.getExtendedCapabilities()).accept(aVisitor);
        }
    }

    @Override
    public void traverse(RangeType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getMinimumValue() != null) {
            aBean.getMinimumValue().accept(aVisitor);
        }
        if (aBean.getMaximumValue() != null) {
            aBean.getMaximumValue().accept(aVisitor);
        }
        if (aBean.getSpacing() != null) {
            aBean.getSpacing().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ReferenceGroupType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (JAXBElement<? extends AbstractReferenceBaseType> obj : aBean.getAbstractReferenceBase()) {
            if (obj.getValue() != null) {
                obj.getValue().accept(aVisitor);
            }
        }
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ReferenceType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(RequestMethodType aBean, Visitor<?, E> aVisitor)
            throws E {
        for (DomainType bean : aBean.getConstraint()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ResponsiblePartySubsetType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getContactInfo() != null) {
            aBean.getContactInfo().accept(aVisitor);
        }
        if (aBean.getRole() != null) {
            aBean.getRole().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ResponsiblePartyType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getContactInfo() != null) {
            aBean.getContactInfo().accept(aVisitor);
        }
        if (aBean.getRole() != null) {
            aBean.getRole().accept(aVisitor);
        }
    }

    @Override
    public void traverse(SectionsType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ServiceIdentification aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getServiceType() != null) {
            aBean.getServiceType().accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getTitle()) {
            bean.accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (KeywordsType bean : aBean.getKeywords()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ServiceProvider aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getProviderSite() != null) {
            aBean.getProviderSite().accept(aVisitor);
        }
        if (aBean.getServiceContact() != null) {
            aBean.getServiceContact().accept(aVisitor);
        }
    }

    @Override
    public void traverse(ServiceReferenceType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getRequestMessage() instanceof Visitable) {
            ((Visitable) aBean.getRequestMessage()).accept(aVisitor);
        }
        if (aBean.getIdentifier() != null) {
            aBean.getIdentifier().accept(aVisitor);
        }
        for (LanguageStringType bean : aBean.getAbstract()) {
            bean.accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(TelephoneType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(UnNamedDomainType aBean, Visitor<?, E> aVisitor)
            throws E {
        if (aBean.getAllowedValues() != null) {
            aBean.getAllowedValues().accept(aVisitor);
        }
        if (aBean.getAnyValue() != null) {
            aBean.getAnyValue().accept(aVisitor);
        }
        if (aBean.getNoValues() != null) {
            aBean.getNoValues().accept(aVisitor);
        }
        if (aBean.getValuesReference() != null) {
            aBean.getValuesReference().accept(aVisitor);
        }
        if (aBean.getDefaultValue() != null) {
            aBean.getDefaultValue().accept(aVisitor);
        }
        if (aBean.getMeaning() != null) {
            aBean.getMeaning().accept(aVisitor);
        }
        if (aBean.getDataType() != null) {
            aBean.getDataType().accept(aVisitor);
        }
        if (aBean.getUOM() != null) {
            aBean.getUOM().accept(aVisitor);
        }
        if (aBean.getReferenceSystem() != null) {
            aBean.getReferenceSystem().accept(aVisitor);
        }
        for (MetadataType bean : aBean.getMetadata()) {
            bean.accept(aVisitor);
        }
    }

    @Override
    public void traverse(ValueType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(ValuesReference aBean, Visitor<?, E> aVisitor)
            throws E {
    }

    @Override
    public void traverse(WGS84BoundingBoxType aBean, Visitor<?, E> aVisitor)
            throws E {
    }

}