package matching;

import java.util.Random;

public class Generator {
	private static Random rand = new Random();// [0,1)
	private final static int VEC_DIM = Runner.VEC_DIM;

	// generate a vector with VEC_DIM size
	public static float[] genVector() {
		float[] vector = new float[VEC_DIM];

		for (int i = 0; i < VEC_DIM; i++) {
			vector[i] = rand.nextFloat();
		}
		return normalize(vector);
	}

	// normalize the vector
	private static float[] normalize(float[] vector) {
		float scale = 0;
		for (int i = 0; i < vector.length; i++) {
			scale += vector[i] * vector[i];
		}
		scale = (float) (1 / Math.sqrt(scale));
		for (int k = 0; k < vector.length; k++) {
			vector[k] *= scale;
		}
		return vector;
	}

	public static float genFloat() {
		return rand.nextFloat();
	}

	public static double genDouble() {
		return rand.nextDouble();
	}

}
