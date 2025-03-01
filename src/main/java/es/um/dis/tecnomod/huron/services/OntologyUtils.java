package es.um.dis.tecnomod.huron.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.search.EntitySearcher;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import uk.ac.manchester.cs.owl.owlapi.OWLLiteralImplBoolean;

/**
 * The Class OntologyUtils.
 */
public class OntologyUtils {
	
	/**
	 * Checks if a class is obsolete or not.
	 *
	 * @param owlEntity the owl class
	 * @param ontology the ontology
	 * @return true, if is obsolete
	 */
	public static boolean isObsolete(OWLEntity owlEntity, OWLOntology ontology) {
		Set<OWLAnnotationAssertionAxiom> axioms = ontology.annotationAssertionAxioms(owlEntity.getIRI()).collect(Collectors.toSet());
		for (OWLAnnotationAssertionAxiom axiom : axioms) {
			IRI propertyIRI = axiom.getProperty().asOWLAnnotationProperty().getIRI();
			if (propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED.getIRI())
					|| propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED_CLASS.getIRI())
					|| propertyIRI.equals(OWLRDFVocabulary.OWL_DEPRECATED_PROPERTY.getIRI())) {
				if (axiom.getValue() instanceof OWLLiteralImplBoolean) {
					OWLLiteralImplBoolean value = (OWLLiteralImplBoolean) axiom.getValue();
					return value.parseBoolean();
				}
			}
		}
		return false;
	}
	
	public static Set<OWLAnnotationAssertionAxiom> getOWLAnnotationAssertionAxiom(OWLEntity entity, OWLOntology ontology, boolean includeImports) {
		Set<OWLAnnotationAssertionAxiom> annotationAssertionAxioms = ontology.annotationAssertionAxioms(entity.getIRI()).collect(Collectors.toSet());
		if (includeImports) {
			for (OWLOntology importedOntology : ontology.imports().collect(Collectors.toList())) {
				Set<OWLAnnotationAssertionAxiom> importedAnnotationAssertionAxioms = importedOntology.annotationAssertionAxioms(entity.getIRI()).collect(Collectors.toSet());
				annotationAssertionAxioms.addAll(importedAnnotationAssertionAxioms);
			}
		}
		return annotationAssertionAxioms;
	}
	
	public static List<String> getAnnotationValues(OWLEntity entity, OWLAnnotationProperty annotationProperty, OWLOntology ontology, boolean includeImports) {
		return getAnnotationValues(entity.getIRI(), annotationProperty.getIRI(), ontology, includeImports);
	}
	
	public static List<String> getAnnotationValues(IRI entityIRI, IRI annotationPropertyIRI, OWLOntology ontology, boolean includeImports) {
		List<String> annotationValues = new ArrayList<String>();
		if (ontology.containsAnnotationPropertyInSignature(annotationPropertyIRI)) {
			OWLAnnotationProperty annotationProperty = ontology.getOWLOntologyManager().getOWLDataFactory().getOWLAnnotationProperty(annotationPropertyIRI);
			EntitySearcher.getAnnotationObjects(entityIRI, ontology, annotationProperty).forEach(annotation -> {
				annotation.getValue().asLiteral().ifPresent(literal -> {annotationValues.add(literal.getLiteral());
				});
			});
		}
		
		return annotationValues;
	}
}
