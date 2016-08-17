//
//  cgCanvas.java 20155
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 Rochester Institute of Technology. All rights reserved.
//
//  Contributor:  VAIBHAVI AWGHATE
//

///
// This is a simple canvas class for adding functionality for the
// 2D portion of Computer Graphics.
//
///

import Jama.Matrix;
import java.util.*;

public class cgCanvas extends simpleCanvas {

    ///
    // Constructor
    //
    // @param w width of canvas
    // @param h height of canvas
    ///

	
    cgCanvas (int w, int h)
    {
        super (w, h);
        // YOUR IMPLEMENTATION HERE if you need to modify the constructor
    }

    ///
    // addPoly - Adds and stores a polygon to the canvas.  Note that this
    //           method does not draw the polygon, but merely stores it for
    //           later draw.  Drawing is initiated by the draw() method.
    //
    //           Returns a unique integer id for the polygon.
    //
    // @param x - Array of x coords of the vertices of the polygon to be added
    // @param y - Array of y coords of the vertices of the polygin to be added
    // @param n - Number of verticies in polygon
    //
    // @return a unique integer identifier for the polygon
    ///
    
    addPolyClass[] ad = new addPolyClass[12];
    static int id = 0;
    double[][] identity_Matrix = {{1, 0, 0}, {0, 1, 0 }, {0, 0, 1}};
    Matrix CurrentTransform ;
    Matrix Polygon_transform ;
    float lly, llx, urx, ury;
    int xvmin, yvmin, xvmax, yvmax;
    float[] outx = new float[50];
    float[] outy = new float[50];
    public int addPoly (float x[], float y[], int n)
    {
        // YOUR IMPLEMENTATION HERE

        // REMEMBER TO RETURN A UNIQUE ID FOR THE POLYGON
    	id++;
    	ad[id-1] = new addPolyClass(x, y, n);
    	//id is the unique ID for input polygon
        return id-1;
    }

    ///
    // drawPoly - Draw the polygon with the given id.  Should draw the
    //        polygon after applying the current transformation on the
    //        vertices of the polygon.
    //
    // @param polyID - the ID of the polygon to be drawn
    ///

    public void drawPoly (int polyID)
    {
        // YOUR IMPLEMENTATION HERE
    	
    	int n;
    	n = ad[polyID].n;
    	int wl = 0;
    	//x and y arrays to store x and y coordinate
    	float x[] = new float[n];
    	float y[] = new float[n];
    	for( int i = 0; i < n; i++ ){
    		x[i] =  ad[polyID].x[i];
    		y[i] =  ad[polyID].y[i];
    	}
    	//x and y coordinate are being stored in a matrix
    	double[][] mat1 = new double[3][n];
    	for( int i = 0; i < n; i++ ){
    		mat1[0][i] =  (double)x[i];
    		mat1[1][i] =  (double)y[i];
    		mat1[2][i] = 1;
    	}
    	Polygon_transform = new Matrix(mat1);
    	//Transformation being carried out on polygon matrix
    	Polygon_transform = CurrentTransform.times(Polygon_transform);
    	mat1 = Polygon_transform.getArray();
    	//x and y coordinates are being extracted from matrix in x and y arrays.
    	for( int i =0; i < 2 ; i++){
    		for ( int j = 0; j< n ; j++){
    			if(i == 0)  
    				x[j] = (float) mat1[i][j];
    			else
    				y[j] = (float) mat1[i][j];
    		}
    	}
    	clipper C = new clipper();
    	//clipping is done on the polygon
    	wl = C.clipPolygon(n, x, y, outx, outy, llx, lly, urx, ury);
    	double[][] mat = new double[3][wl];
    	x = new float[wl];
    	y = new float[wl];
    	//x and y coordinates after clipping are stored in new matrix 
    	for( int i = 0; i < wl; i++ ){
    		mat[0][i] =  (double)outx[i];
    		mat[1][i] =  (double)outy[i];
    		mat[2][i] = 1;
    	}
    	Polygon_transform = new Matrix(mat);
    	//viewport transformation is applied on polygon matrix
    	Matrix T = new Matrix(new double[][]{{1, 0, xvmin},{0, 1, yvmin},{0, 0, 1}});
    	Matrix S = new Matrix ( new double[][]{{(xvmax - xvmin)/(urx-llx),0, 0}, {0, (yvmax-yvmin)/(ury-lly), 0}, {0, 0, 1}});
    	Matrix T1 = new Matrix (new double [][]{{1, 0, -llx}, {0, 1, -lly}, {0, 0, 1}});
    	Matrix viewing_transform ;
    	viewing_transform = (T.times(S)).times(T1);
    	Polygon_transform = viewing_transform.times(Polygon_transform);
    	mat = Polygon_transform.getArray();
    	for( int i =0; i < 2 ; i++){
    		for ( int j = 0; j< wl ; j++){
    			if(i == 0)
    				x[j] = (float) mat[i][j];
    			else
    				y[j] = (float) mat[i][j];
    		}
    	}
    	Rasterizer R = new Rasterizer(500);
    	// x and y coordinates after viewport transformation are sent to drawPolygon function
    	R.drawPolygon(wl, x,  y, this);
    }

    ///
    // clearTransform - Set the current transformation to the identity matrix.
    ///

    public void clearTransform()
    {
        // YOUR IMPLEMENTATION HERE
    	double[][] identity_Matrix = {{1, 0, 0}, {0, 1, 0 }, {0, 0, 1}};
    	CurrentTransform = new Matrix(identity_Matrix);
    }

    ///
    // translate - Add a translation to the current transformation by
    //             pre-multiplying the appropriate translation matrix to
    //             the current transformation matrix.
    //
    // @param x - Amount of translation in x
    // @param y - Amount of translation in y
    ///

    public void translate (float x, float y)
    {
        // YOUR IMPLEMENTATION HERE
    	Matrix translate_Matrix = new Matrix(new double[][]{{1, 0, x }, {0, 1, y }, {0, 0, 1}});
    	CurrentTransform = translate_Matrix.times(CurrentTransform);
    }

    ///
    // rotate - Add a rotation to the current transformation by
    //          pre-multiplying the appropriate rotation matrix to the
    //          current transformation matrix.
    //
    // @param degrees - Amount of rotation in degrees
    ///

    public void rotate (float degrees)
    {
        // YOUR IMPLEMENTATION HERE
    	double radians = Math.toRadians(degrees);
    	Matrix rotate_Matrix = new Matrix(new double[][]{{Math.cos(radians), -Math.sin(radians), 0 }, 
    													 {Math.sin(radians), Math.cos(radians), 0 },
    													 {0, 0, 1}});
    	CurrentTransform = rotate_Matrix.times(CurrentTransform);
    }

    ///
    // scale - Add a scale to the current transformation by pre-multiplying
    //         the appropriate scaling matrix to the current transformation
    //         matrix.
    //
    // @param x - Amount of scaling in x
    // @param y - Amount of scaling in y
    ///

    public void scale (float x, float y)
    {
        // YOUR IMPLEMENTATION HERE
    	Matrix scale_Matrix = new Matrix(new double[][]{{x, 0, 0 }, {0, y, 0 }, {0, 0, 1}});
    	CurrentTransform = scale_Matrix.times(CurrentTransform);
    }

    ///
    // setClipWindow - defines the clip window
    //
    // @param bottom - y coord of bottom edge of clip window (in world coords)
    // @param top - y coord of top edge of clip window (in world coords)
    // @param left - x coord of left edge of clip window (in world coords)
    // @param right - x coord of right edge of clip window (in world coords)
    ///

    public void setClipWindow (float bottom, float top, float left, float right)
    {
        // YOUR IMPLEMENTATION HERE
    	lly = bottom;
    	llx = left;
    	urx = right;
    	ury = top;
    }

    ///
    // setViewport - defines the viewport
    //
    // @param xmin - x coord of lower left of view window (in screen coords)
    // @param ymin - y coord of lower left of view window (in screen coords)
    // @param width - width of view window (in world coords)
    // @param height - width of view window (in world coords)
    ///

    public void setViewport (int x, int y, int width, int height)
    {
        // YOUR IMPLEMENTATION HERE
    	xvmin = x;
    	yvmin = y;
    	xvmax = x + width;
    	yvmax = y + height;
    }

}
//class for storing x, y arrays and n for each polygon
class addPolyClass{
	float x[] = new float[12];
	float y[] = new float[12];
	int n;
	public addPolyClass(float[] x, float[] y, int n){
		this.x = Arrays.copyOf(x, n);
		this.y = Arrays.copyOf(y, n);
		this.n = n;
	}
}


