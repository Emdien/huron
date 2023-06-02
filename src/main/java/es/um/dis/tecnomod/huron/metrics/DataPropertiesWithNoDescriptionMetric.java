package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class DataPropertiesWithNoDescriptionMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no description";
	
	@Override
	public MetricResult calculateAll() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tDataProperty\tWithNoDescription\n");
		Model rdfModel = ModelFactory.createDefaultModel();
		Property metricProperty = rdfModel.createProperty(this.getIRI());
		int numberOfDataPropertiesWithNoDescription = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfDescriptions = this.getNumberOfDescriptions(owlDataProperty);
			if (localNumberOfDescriptions == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), true));
				rdfModel.createResource(owlDataProperty.getIRI().toString()).addLiteral(metricProperty, true);
				RDFUtils.createIssue(rdfModel, metricProperty, owlDataProperty, String.format("The entity %s does not have any description.", owlDataProperty.getIRI().toQuotedString()));
				numberOfDataPropertiesWithNoDescription++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), false));
				rdfModel.createResource(owlDataProperty.getIRI().toString()).addLiteral(metricProperty, false);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDataPropertiesWithNoDescription)) / numberOfEntities;
		this.getOntology().getOntologyID().getOntologyIRI().ifPresent(ontologyIRI -> {
			rdfModel.createResource(ontologyIRI.toString()).addLiteral(metricProperty, metricValue);
		});
		return new MetricResult(metricValue, rdfModel);		
	}

	@Override
	public String getName() {
		return METRIC_NAME;
	}

	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "DataPropertiesWithNoDescriptionMetric";
	}

}
