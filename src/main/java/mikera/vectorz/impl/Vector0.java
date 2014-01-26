package mikera.vectorz.impl;

import java.io.ObjectStreamException;

import mikera.arrayz.INDArray;
import mikera.arrayz.impl.IDense;
import mikera.vectorz.AVector;
import mikera.vectorz.Vector;
import mikera.vectorz.util.ErrorMessages;

/**
 * Special singleton zero length vector class.
 * 
 * Mainly for convenience when doing vector construction / appending etc.
 * 
 * @author Mike
 */
public final class Vector0 extends APrimitiveVector implements IDense {
	private static final long serialVersionUID = -8153360223054646075L;

	public Vector0() {
	}

	public static Vector0 of() {
		return INSTANCE;
	}

	public static Vector0 of(double... values) {
		if (values.length != 0)
			throw new IllegalArgumentException(
					"Vector0 cannot have components!");
		return INSTANCE;
	}

	public static Vector0 INSTANCE = new Vector0();

	@Override
	public int length() {
		return 0;
	}

	@Override
	public double elementSum() {
		return 0.0;
	}
	
	@Override
	public double elementProduct() {
		return 1.0;
	}

	@Override
	public long nonZeroCount() {
		return 0;
	}

	@Override
	public double get(int i) {
		throw new IndexOutOfBoundsException(
				"Attempt to get on zero length vector!");
	}

	@Override
	public void set(int i, double value) {
		throw new IndexOutOfBoundsException(
				"Attempt to set on zero length vector!");
	}

	@Override
	public Vector0 clone() {
		return this;
	}

	@Override
	public boolean isMutable() {
		// i.e is immutable
		return false;
	}
	
	@Override
	public boolean isFullyMutable() {
		// i.e there are no immutable elements!
		return true;
	}

	@Override
	public int hashCode() {
		// 1 is hashcode for zero-length double array
		return 1;
	}

	@Override
	public boolean isZero() {
		return true;
	}

	@Override
	public double magnitudeSquared() {
		return 0.0;
	}

	@Override
	public double magnitude() {
		return 0.0;
	}

	@Override
	public AVector join(AVector v) {
		return v;
	}

	@Override
	public Vector0 immutable() {
		return this;
	}
	
	@Override
	public Vector dense() {
		return Vector.EMPTY;
	}
	
	@Override
	public Vector0 subVector(int start, int length) {
		if ((start==0)&&(length==0)) return this;
		throw new IndexOutOfBoundsException(ErrorMessages.invalidRange(this, start, length));
	}

	/**
	 * readResolve method to ensure we always use the singleton
	 */
	private Object readResolve() throws ObjectStreamException {
		return INSTANCE;
	}

	@Override
	public Vector0 exactClone() {
		// immutable, so return self
		return this;
	}
}
