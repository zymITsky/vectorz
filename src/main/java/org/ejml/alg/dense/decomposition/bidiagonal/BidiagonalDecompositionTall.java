/*
 * Copyright (c) 2009-2012, Peter Abeles. All Rights Reserved.
 *
 * This file is part of Efficient Java Matrix Library (EJML).
 *
 * EJML is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * EJML is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with EJML.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.ejml.alg.dense.decomposition.bidiagonal;

import org.ejml.UtilEjml;
import org.ejml.data.DenseMatrix64F;
import org.ejml.factory.DecompositionFactory;
import org.ejml.factory.QRPDecomposition;
import org.ejml.ops.CommonOps;


/**
 * <p>
 * {@link BidiagonalDecomposition} specifically designed for tall matrices.
 * First step is to perform QR decomposition on the input matrix.  Then R is decomposed using
 * a bidiagonal decomposition.  By performing the bidiagonal decomposition on the smaller matrix
 * computations can be saved if m/n > 5/3 and if U is NOT needed.
 * </p>
 *
 * <p>
 * A = [Q<sub>1</sub> Q<sub>2</sub>][U1 0; 0 I] [B1;0] V<sup>T</sup><br>
 * U=[Q<sub>1</sub>*U1 Q<sub>2</sub>]<br>
 * B=[B1;0]<br>
 * A = U*B*V<sup>T</sup>
 * </p>
 *
 * <p>
 * A QRP decomposition is used internally.  That decomposition relies an a fixed threshold for selecting singular
 * values and is known to be less stable than SVD.  There is the potential for a degregation of stability
 * by using BidiagonalDecompositionTall instead of BidiagonalDecomposition. A few simple tests have shown
 * that loss in stability to be insignificant.
 * </p>
 *
 * <p>
 * See page 404 in "Fundamentals of Matrix Computations", 2nd by David S. Watkins.
 * </p>
 *
 *
 * @author Peter Abeles
 */
// TODO optimize this code
public class BidiagonalDecompositionTall
        implements BidiagonalDecomposition<DenseMatrix64F>
{
    QRPDecomposition<DenseMatrix64F> decompQRP = DecompositionFactory.qrp(500, 100); // todo this should be passed in
    BidiagonalDecomposition<DenseMatrix64F> decompBi = new BidiagonalDecompositionRow();

    DenseMatrix64F B = new DenseMatrix64F(1,1);

    // number of rows
    int m;
    // number of column
    int n;
    // min(m,n)
    int min;

    @Override
    public void getDiagonal(double[] diag, double[] off) {
        diag[0] = B.get(0);
        for( int i = 1; i < n; i++ ) {
            diag[i] = B.unsafe_get(i,i);
            off[i-1] = B.unsafe_get(i-1,i);
        }
    }

    @Override
    public DenseMatrix64F getB(DenseMatrix64F B, boolean compact) {
        B = BidiagonalDecompositionRow.handleB(B,compact, m, n,min);

        B.set(0,0,this.B.get(0,0));
        for( int i = 1; i < min; i++ ) {
            B.set(i,i, this.B.get(i,i));
            B.set(i-1,i, this.B.get(i-1,i));
        }
        if( n > m)
            B.set(min-1,min,this.B.get(min-1,min));

        return B;
    }

    @Override
    public DenseMatrix64F getU(DenseMatrix64F U, boolean transpose, boolean compact) {
        U = BidiagonalDecompositionRow.handleU(U,false,compact, m, n,min);

        if( compact ) {
            // U = Q*U1
            DenseMatrix64F Q1 = decompQRP.getQ(null,true);
            DenseMatrix64F U1 = decompBi.getU(null,false,true);
            CommonOps.mult(Q1,U1,U);
        } else {
           // U = [Q1*U1 Q2]
            DenseMatrix64F Q = decompQRP.getQ(U,false);
            DenseMatrix64F U1 = decompBi.getU(null,false,true);
            DenseMatrix64F Q1 = CommonOps.extract(Q,0,Q.rows,0,min);
            DenseMatrix64F tmp = new DenseMatrix64F(Q1.rows,U1.cols);
            CommonOps.mult(Q1,U1,tmp);
            CommonOps.insert(tmp,Q,0,0);
        }

        if( transpose )
            CommonOps.transpose(U);

        return U;
    }

    @Override
    public DenseMatrix64F getV(DenseMatrix64F V, boolean transpose, boolean compact) {
        return decompBi.getV(V,transpose,compact);
    }

    @Override
    public boolean decompose(DenseMatrix64F orig) {

        decompQRP.setSingularThreshold(CommonOps.elementMaxAbs(orig)* UtilEjml.EPS);
        if( !decompQRP.decompose(orig) ) {
            return false;
        }

        m = orig.rows;
        n = orig.cols;
        min = Math.min(m, n);
        B.reshape(min, n,false);

        decompQRP.getR(B,true);

        // apply the column pivots.
        // TODO this is horribly inefficient
        DenseMatrix64F result = new DenseMatrix64F(min,n);
        DenseMatrix64F P = decompQRP.getPivotMatrix(null);
        CommonOps.multTransB(B, P, result);
        B.set(result);

        return decompBi.decompose(B);
    }

    @Override
    public boolean inputModified() {
        return decompQRP.inputModified();
    }
}
