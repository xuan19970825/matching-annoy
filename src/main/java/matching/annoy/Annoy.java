package matching.annoy;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import matching.Runner;

public class Annoy {

	private AtomicReference<ANNIndex> atomicRef;
	IndexType indexType;
	String indexPath;
	ANNIndex annIndex = null;

	// TODO : new an ANNIndex and set atomicRef;
	public Annoy(String indexPath, IndexType indexType) {

	}

	//get nearest items
	public List<Integer> search(float[] target) {
		return annIndex.getNearest(target, Runner.nn);
	}

	// TODO : set atomicRef to a new ANNIndex
	public synchronized void update() {

	}

}
