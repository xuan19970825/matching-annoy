package matching.annoy;

import java.io.Closeable;
import java.util.List;

/**
 * AnnoyIndex interface, provided to aid with dependency injection in tests.
 */
public interface AnnoyIndex extends Closeable{
  /**
   * Get the vector for a given node's memory offset in the tree.
   * @param nodeOffset  node index in the ANN tree
   * @param v           output vector; overwritten.
   * @deprecated this should not be a public method
   */
  @Deprecated
  void getNodeVector(long nodeOffset, float[] v);

  /**
   * Get the vector for a given item in the tree.
   * @param itemIndex  item id
   * @param v          output vector; overwritten.
   * @deprecated use getItemVector(itemIndex)'s return value
   */
  @Deprecated
  void getItemVector(int itemIndex, float[] v);

  /**
   * Get the vector for a given item in the tree.
   * @param itemIndex  item id
   * @return item vector
   */
  float[] getItemVector(int itemIndex);

  /**
   * Look up nearest neighbors in the tree.
   * @param queryVector  find nearest neighbors for this query point
   * @param nResults     number of items to return
   * @return             list of items in descending nearness to query point
   */
  List<Integer> getNearest(float[] queryVector, int nResults);

  /**
   * Look up nearest PqEntries in the tree.
   * @param queryVector  find nearest neighbors for this query point
   * @param nResults     number of items to return
   * @return             list of PqEntries in descending nearness to query point, containing margin and node offset
   */
  List<ANNIndex.PQEntry> getNearestPqEntries(float[] queryVector, int nResults);
}
