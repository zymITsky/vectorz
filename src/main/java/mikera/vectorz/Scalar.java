package mikera.vectorz;


public class Scalar extends AScalar {
	public double value;

	public Scalar(double value) {
		this.value = value;
	}

	public static Scalar create(double value) {
		return new Scalar(value);
	}

	public static Scalar create(AScalar a) {
		return create(a.get());
	}

	@Override
	public double get() {
		return value;
	}

	@Override
	public void set(double value) {
		this.value = value;
	}

	@Override
	public void abs() {
		value = Math.abs(value);
	}

	@Override
	public void add(double d) {
		value += d;
	}

	@Override
	public void sub(double d) {
		value -= d;
	}

	@Override
	public void add(AScalar s) {
		value += s.get();
	}

	@Override
	public void multiply(double factor) {
		value *= factor;
	}

	@Override
	public void negate() {
		value = -value;
	}
	
	@Override
	public void scaleAdd(double factor, double constant) {
		value = value*factor + constant;
	}

	@Override
	public boolean isView() {
		return false;
	}

	@Override
	public Scalar clone() {
		return new Scalar(value);
	}

	@Override
	public void getElements(double[] dest, int offset) {
		dest[offset] = value;
	}

	@Override
	public Scalar exactClone() {
		return clone();
	}

}