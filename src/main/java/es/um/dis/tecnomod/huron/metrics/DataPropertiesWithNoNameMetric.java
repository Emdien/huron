package es.um.dis.tecnomod.huron.metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.OWL;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import es.um.dis.tecnomod.huron.dto.MetricResult;
import es.um.dis.tecnomod.huron.namespaces.Namespaces;
import es.um.dis.tecnomod.huron.rdf_builder.RDFConstants;
import es.um.dis.tecnomod.huron.services.RDFUtils;

public class DataPropertiesWithNoNameMetric extends AnnotationsPerEntityAbstractMetric {

	/** The Constant NAME. */
	private static final String METRIC_NAME = "DataProperties with no name";
	
	@Override
	public MetricResult calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tDataProperty\tWithNoName\n");
		String ontologyIRI = RDFUtils.getOntologyIRI(getOntology());
		Calendar timestamp = Calendar.getInstance();
		Model rdfModel = ModelFactory.createDefaultModel();
		int numberOfDataPropertiesWithNoName = 0;
		int numberOfEntities = 0;
		for(OWLDataProperty owlDataProperty : super.getOntology().dataPropertiesInSignature().collect(Collectors.toList())){
			if(owlDataProperty.isOWLTopDataProperty()){
				continue;
			}			
			int localNumberOfNames = getNumberOfNames(owlDataProperty);
			if (localNumberOfNames == 0) {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), true));
				RDFUtils.createObservation(rdfModel, ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(true), timestamp);
				numberOfDataPropertiesWithNoName++;
			}else {
				super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%b\n", this.getName(), owlDataProperty.toStringID(), false));
				RDFUtils.createObservation(rdfModel, ontologyIRI, owlDataProperty.getIRI().toString(), OWL.DatatypeProperty.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Boolean(false), timestamp);
			}
			numberOfEntities ++;
		}
		
		double metricValue = ((double) (numberOfDataPropertiesWithNoName)) / numberOfEntities;
		RDFUtils.createObservation(rdfModel, ontologyIRI, ontologyIRI, OWL.Ontology.getURI(), getObservablePropertyIRI(), getIRI(), getInstrumentIRI(), getUnitOfMeasureIRI(), new Double(metricValue), timestamp);

		return new MetricResult(metricValue, rdfModel);	
	}


	@Override
	public String getName() {
		return METRIC_NAME;
	}
	
	@Override
	public String getIRI() {
		return Namespaces.OQUO_NS + "DataPropertiesWithNoNameMetric";
	}


	@Override
	public String getObservablePropertyIRI() {
		return RDFConstants.NAMES;
	}
}
