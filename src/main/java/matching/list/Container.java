package matching.list;

import java.util.Iterator;

public interface Container {

	float[] search(float[] vector);

	boolean add(float[] vector);

	boolean modify(int index, float[] newVector);

	boolean delete(float[] vector);

	int size();

	int capacity();

	Iterator<float[]> iterator();

	float[] getVector(int index);
	
}
