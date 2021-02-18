package matching.similarity;

import static jdk.incubator.vector.VectorOperators.ADD;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorSpecies;

public class Similarity {

	static final VectorSpecies<Float> SPECIES = FloatVector.SPECIES_256;

	public static float cosineSim(float[] vectorA, float[] vectorB) {
		FloatVector sum = FloatVector.zero(SPECIES);
		int i = 0;
		for (; i < SPECIES.loopBound(vectorA.length); i += SPECIES.length()) {
			// FloatVector va, vb, vc;
			FloatVector va = FloatVector.fromArray(SPECIES, vectorA, i);
			FloatVector vb = FloatVector.fromArray(SPECIES, vectorB, i);
			sum = va.fma(vb, sum);// va * vb + sum
		}
		float result = sum.reduceLanes(ADD);
		for (; i < vectorA.length; i++) {
			result += vectorA[i] * vectorB[i];
		}
		return result;
	}
}
