package matching.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import matching.Generator;
import matching.similarity.Similarity;

public class BaselineList implements Container {

	private int listNum;
	private int capacity;
	private List<float[]> list;

	public BaselineList(int listNum) {
		this.capacity = listNum * 2;
		this.listNum = listNum;
		list = new ArrayList<float[]>(capacity);
		fillListUpWithVectors();
	}

	// Use this method if you already have data
	public BaselineList(ArrayList<float[]> list) {
		this.listNum = list.size();
		this.capacity = listNum * 2;
		this.list = new ArrayList<float[]>(list);
	}

	private void fillListUpWithVectors() {
		System.out.println("Filling the list up");
		for (int i = 0; i < listNum; i++) {
			add(Generator.genVector());
		}
		System.out.println("Finish filling the list");
	}

	@Override
	public synchronized float[] search(float[] target) {
		if (size() == 0) {
			return null;
		}

		float[] maxSimVector = list.get(0);
		float maxSim = Similarity.cosineSim(target, maxSimVector);

		for (int i = 1, l = size(); i < l; i++) {
			float[] vector = list.get(i);
			float similarity = Similarity.cosineSim(target, vector);

			if (similarity > maxSim) {
				maxSim = similarity;
				maxSimVector = vector;
			}
		}
		return maxSimVector;
	}

	@Override
	public synchronized boolean add(float[] target) {
		return list.add(target);
	}

	@Override
	public synchronized boolean modify(int index , float[] newVector) {
		return list.set(index, newVector) != null;
	}

	@Override
	public synchronized boolean delete(float[] target) {

		int matchIndex = -1;
		for (int i = 0, l = list.size(); i < l; i++) {
			if (list.get(i)[0] == target[0]) {
				matchIndex = i;
				float[] vector = list.get(i);
				for (int j = 0; j < vector.length; j++) {
					if (vector[j] != target[j]) {
						matchIndex = -1;
						break;
					}
				}
			}
		}
		if (matchIndex != -1) {
			list.remove(list.get(matchIndex));
			return true;
		} else {
			return false;
		}
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public int capacity() {
		return capacity;
	}

	@Override
	public Iterator<float[]> iterator() {
		return list.iterator();
	}

	@Override
	public float[] getVector(int index) {
		return list.get(index);
	}
}
