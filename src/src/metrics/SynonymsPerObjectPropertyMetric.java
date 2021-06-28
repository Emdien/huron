package metrics;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;

import um.ontoenrich.config.LaInputParameters;

/**
 * The Class SynonymsPerObjectPropertyMetric.
 */
public class SynonymsPerObjectPropertyMetric extends AnnotationsPerEntityAbstractMetric{
	
	/** The Constant NAME. */
	private static final String NAME = "Synonyms per object property";

	/* (non-Javadoc)
	 * @see metrics.Metric#calculate()
	 */
	@Override
	public double calculate() throws OWLOntologyCreationException, FileNotFoundException, IOException, Exception {
		super.writeToDetailedOutputFile("Metric\tObject Property\tMetric Value\n");
		int numberOfSynonyms = 0;
		int numberOfEntities = 0;
		for(OWLObjectProperty objectProperty : super.getOntology().getObjectPropertiesInSignature()){
			int localNumberOfSynonyms = getNumberOfSynonyms(objectProperty);
			super.writeToDetailedOutputFile(String.format(Locale.ROOT, "%s\t%s\t%d\n", this.getName(), objectProperty.toStringID(), localNumberOfSynonyms));
			numberOfSynonyms = numberOfSynonyms + localNumberOfSynonyms;
			numberOfEntities ++;
		}
		return ((double) (numberOfSynonyms)) / numberOfEntities;
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#setParameters(um.ontoenrich.config.LaInputParameters)
	 */
	@Override
	public void setParameters(LaInputParameters parameters) {
		// Not used
		
	}

	/* (non-Javadoc)
	 * @see metrics.Metric#getName()
	 */
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return NAME;
	}
}


