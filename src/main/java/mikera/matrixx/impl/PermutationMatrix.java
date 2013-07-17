package mikera.matrixx.impl;

import mikera.indexz.Index;
import mikera.matrixx.AMatrix;
import mikera.matrixx.Matrix;
import mikera.vectorz.AVector;
import mikera.vectorz.impl.AxisVector;
import mikera.vectorz.util.VectorzException;

public final class PermutationMatrix extends AMatrix {
	private final Index perm;
	private final int size;
	
	private PermutationMatrix(Index perm) {
		this.perm=perm;
		size=perm.length();
	}
	
	public static PermutationMatrix create(Index rowPermutations) {
		return new PermutationMatrix(rowPermutations.clone());
	}
	
	public static AMatrix create(int... rowPermutations) {
		return create(Index.of(rowPermutations));
	}
	
	@Override
	public boolean isFullyMutable() {
		return false;
	}
	
	@Override
	public boolean isSquare() {
		return true;
	}

	@Override
	public int rowCount() {
		return size;
	}

	@Override
	public int columnCount() {
		return size;
	}

	@Override
	public double get(int row, int column) {
		return (perm.get(row)==column)?1.0:0.0;
	}

	@Override
	public void set(int row, int column, double value) {
		throw new UnsupportedOperationException("Can't arbitrarily mutate a permutation matrix");
	}
	
	@Override
	public AxisVector getRow(int i) {
		return AxisVector.create(perm.get(i), size);
	}
	
	@Override
	public AxisVector getColumn(int j) {
		return AxisVector.create(perm.find(j), size);
	}
	
	@Override
	public void swapRows(int i, int j) {
		if (i!=j) {
			perm.swap(i, j);
		}
	}
	
	@Override
	public void swapColumns(int i, int j) {
		if (i!=j) {
			int a=perm.find(i);
			int b=perm.find(j);
			perm.swap(a, b); 
		}
	}
	
	@Override
	public void transform(AVector source, AVector dest) {
		assert(rowCount()==dest.length());
		assert(columnCount()==source.length());
		for (int i=0; i<size; i++) {
			dest.set(i,source.get(perm.get(i)));
		}
	}
	
	@Override
	public Matrix innerProduct(AMatrix a) {
		if (a instanceof Matrix) return innerProduct((Matrix)a);
		int cc=a.columnCount();
		Matrix result=Matrix.create(size,cc);
		for (int i=0; i<size; i++) {
			int dstIndex=i*cc;
			int srcRow=perm.get(i);
			for (int j=0; i<cc; j++) {
				result.data[dstIndex+j]=a.get(srcRow,j);
			}
		}
		return result;
	}
	
	@Override
	public Matrix innerProduct(Matrix a) {
		int cc=a.columnCount();
		Matrix result=Matrix.create(size,cc);
		for (int i=0; i<size; i++) {
			int srcIndex=perm.get(i)*cc;
			int dstIndex=i*cc;
			System.arraycopy(a.data,srcIndex,result.data,dstIndex,cc);
		}
		return result;
	}

	@Override
	public PermutationMatrix exactClone() {
		return new PermutationMatrix(perm.clone());
	}

	
	@Override
	public void validate() {
		super.validate();
		if (size!=perm.length()) throw new VectorzException("Whoops!");
	}
}
