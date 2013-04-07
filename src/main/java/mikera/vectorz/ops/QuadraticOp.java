package mikera.vectorz.ops;

import mikera.vectorz.AVector;
import mikera.vectorz.Op;

public final class QuadraticOp extends APolynomialOp {
	private final double a;
	private final double b;
	private final double c;
	
	private QuadraticOp(double a,double b, double c) {
		this.a=a;
		this.b=b;
		this.c=c;
	}
	
	public static Op create(double a,double b, double c) {
		if (a==0.0) {
			return LinearOp.create(b,c);
		}
		return new QuadraticOp(a,b,c);
	}
	
	@Override
	public final double apply(double x) {
		return (a*x*x)+(b*x)+c;
	}
	
	@Override
	public double applyInverse(double y) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public void applyTo(AVector v) {
		int len=v.length();
		for (int i=0; i<len; i++) {
			double x=v.get(i);
			v.set(i,apply(x));
		}	
	}
	
	@Override
	public void applyTo(double[] data) {
		for (int i=0; i<data.length; i++) {
			double x=data[i];
			data[i]=apply(x);
		}
	}
	
	@Override
	public void applyTo(double[] data, int start,int length) {
		for (int i=0; i<length; i++) {
			double x=data[i+start];
			data[i+start]=apply(x);
		}	
	}
	
	@Override
	public double averageValue() {
		return apply(-2.0*b/a)+a;
	}
	
	@Override
	public boolean hasDerivative() {
		return true;
	}
	
	@Override
	public double derivative(double x) {
		return 2.0*a*x+b;
	}
	
	@Override
	public double derivativeForOutput(double y) {
		return b;
	}
	
	@Override
	public boolean hasInverse() {
		return false;
	}
	
	public Op compose(ALinearOp op) {
		double f=op.getFactor();
		double g=op.getConstant();
		
		return QuadraticOp.create(a*f*f,(2*a*f*g+f*b),a*g*g+b*g+c);
	}
	
	@Override
	public Op compose(Op op) {
		if (op instanceof ALinearOp) {
			return compose((ALinearOp) op);
		}
		return super.compose(op);
	}
}
