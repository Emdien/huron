package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.services.OntologyUtils;
import es.um.dis.tecnomod.huron.services.RDFUtils;

/**
 * This class calculates the ratio of number of classes with no names to the number of all classes.
 * 
 * @author fjredondo
 */
public class ClassesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric {
	
	/** The Constant METRIC_NAME. */
	private static final String METRIC_NAME = "Classes with no name";
	
	/**
	 * Get the entities from the ontology and obtain the annotations number of each entity to calculate the metric ratio.
	 * Also, saves the totalEntities and numberOfEntitiesWithNoAnnotation.
	 * Writes the following fields: Metric, Class (IRI) and WithNoName (boolean), in a file.
	 * @return The metric ratio
	 */
	@Override
	public MetricResult calculateAll() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tClass\tWithNoName\n");
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfClassesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLClass owlClass : super.getOntology().classesInSignature().collect(Collectors.toList())){
			if (owlClass.isOWLNothing() || owlClass.isOWLThing() || OntologyUtils.isObsolete(owlClass, getOntology())) {
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlClass);
			if (localNumberOfNames == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), true));
				rdfModel.createResource(owlClass.getIRI().toString()).addLiteral(metricProperty, true);
				RDFUtils.createIssue(rdfModel, metricProperty, owlClass, String.format("The entity %s does not have any name.", owlClass.getIRI().toQuotedString()));
				numberOfClassesWithNoName++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlClass.toStringID(), false));
				rdfModel.createResource(owlClass.getIRI().toString()).addLiteral(metricProperty, false);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfClassesWithNoName)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
		});
		return new MetricResult(metricValue, rdfModel);	
	}


	/**
	 * Get the name of the metric
	 * @return String
	 */
	@Override
	public String getName() {
		return METRIC_NAME;
	}


	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "ClassesWithNoNameMetric";
	}
}
