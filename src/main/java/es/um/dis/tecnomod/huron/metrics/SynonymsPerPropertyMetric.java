package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.services.OntologyUtils;

/**
 * The Class SynonymsPerPropertyMetric.
 */
public class SynonymsPerPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Synonyms per property";
	
	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public MetricResult calculateAll() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tProperty\tMetric Value\n");
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfSynonyms = 0;
		int totalProperties = 0;
		
		for(OWLObjectProperty owlObjectProperty : super.getOntology().objectPropertiesInSignature().collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlObjectProperty, getOntology()) || owlObjectProperty.isOWLTopObjectProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlObjectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlObjectProperty.toStringID(), localNumberOfSynonyms));
			rdfModel.createResource(owlObjectProperty.getIRI().toString()).addLiteral(metricProperty, localNumberOfSynonyms);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlDataProperty, getOntology()) || owlDataProperty.isOWLTopDataProperty()){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlDataProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlDataProperty.toStringID(), localNumberOfSynonyms));
			rdfModel.createResource(owlDataProperty.getIRI().toString()).addLiteral(metricProperty, localNumberOfSynonyms);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		for(OWLAnnotationProperty owlAnnotationProperty : super.getOntology().annotationPropertiesInSignature().collect(Collectors.toList())){
			if(OntologyUtils.isObsolete(owlAnnotationProperty, getOntology())){
				continue;
			}
			totalProperties++;
			int localNumberOfSynonyms = getNumberOfSynonyms(owlAnnotationProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), owlAnnotationProperty.toStringID(), localNumberOfSynonyms));
			rdfModel.createResource(owlAnnotationProperty.getIRI().toString()).addLiteral(metricProperty, localNumberOfSynonyms);
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
		}
		
		double metricValue = ((double) (numberOfSynonyms)) / totalProperties;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
		});
		return new MetricResult(metricValue, rdfModel);
	
	}


	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "SynonymsPerPropertyMetric";
	}

}
