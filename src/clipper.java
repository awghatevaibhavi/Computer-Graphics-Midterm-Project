//
//  Clipper.java
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 Rochester Institute of Technology. All rights reserved.
//
//  Contributor:  VAIBHAVI AWGHATE
//

///
// Object for performing clipping
//
///

public class clipper {

	///
	// clipPolygon
	//
	// Clip the polygon with vertex count in and vertices inx/iny
	// against the rectangular clipping region specified by lower-left corner
	// (llx,lly) and upper-right corner (urx,ury). The resulting vertices are
	// placed in outx/outy.
	//
	// The routine should return the the vertex count of the polygon
	// resulting from the clipping.
	//
	// @param in the number of vertices in the polygon to be clipped
	// @param inx - x coords of vertices of polygon to be clipped.
	// @param iny - y coords of vertices of polygon to be clipped.
	// @param outx - x coords of vertices of polygon resulting after clipping.
	// @param outy - y coords of vertices of polygon resulting after clipping.
	// @param llx - x coord of lower left of clipping rectangle.
	// @param lly - y coord of lower left of clipping rectangle.
	// @param urx - x coord of upper right of clipping rectangle.
	// @param ury - y coord of upper right of clipping rectangle.
	//
	// @return number of vertices in the polygon resulting after clipping
	//
	///
	public int clipPolygon(int in, float inx[], float iny[], float outx[], float outy[], float llx, float lly,
			float urx, float ury) {
		// YOUR IMPLEMENTATION GOES HERE
		float lower_left_x = llx, lower_left_y = lly, upper_right_x = urx, upper_right_y = ury ;
		float outx1[] = new float [50];
        float outy1[] = new float [50];
        float outx2[] = new float [50];
        float outy2[] = new float [50];
        float outx3[] = new float [50];
        float outy3[] = new float [50];
		int w=0;
		//Sutherland Hodgeman polygon clipping algorithm for each edge of clipping boundary
		w = SHPC (in, inx, iny, outx1, outy1, llx, lly, urx, lly, lower_left_x, lower_left_y, upper_right_x, upper_right_y );
		w = SHPC (w, outx1, outy1, outx2, outy2, urx, lly, urx, ury , lower_left_x, lower_left_y, upper_right_x, upper_right_y);
		w = SHPC (w, outx2, outy2, outx3, outy3, urx, ury, llx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y );
		w = SHPC (w, outx3, outy3, outx, outy, llx, ury, llx, lly, lower_left_x, lower_left_y, upper_right_x, upper_right_y );
		return w;
		 // should return number of vertices in clipped poly.
	}
	
	public int SHPC ( int in, float inx[], float iny[], float outx[], float outy[], float llx, float lly,
			float urx, float ury, float lower_left_x, float lower_left_y, float upper_right_x, float upper_right_y){
		int j = 0;
		int i = 0;
		int wl =0 ;
		float sx, sy;
		float px , py ;
		if ( in == 0){
			px = inx[0];
			py = iny[0];
		}else{
			px = inx[in-1];
			py = iny[in-1];
		}
		float[] intersection = new float[2];
		//loop over each vertex in polygon
		for (j = 0; j < in ; j++) {
			sx = inx[j];
			sy = iny[j];
			//checks if the both the vertices in polygon are inside the clipping boundary
			if (inside(sx, sy, llx, lly, urx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y)) {
				if (inside(px, py, llx, lly, urx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y)) {
					outx[i] = sx;
					outy[i] = sy;
					i++;
					wl++;
				} 
				//if one vertex is inside the polygon and other is not, then it finds the intersection with the clipping region
				else {
					intersection = intersect(px, py, sx, sy, llx, lly, urx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y);
						outx[i] = intersection[0];
						outy[i] = intersection[1];
						i++;
						wl++;
						outx[i] = sx;
						outy[i] = sy;
						i++;
						wl++;
				}
			} else {
				if (inside(px, py, llx, lly, urx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y)) {
					intersection = intersect(px, py, sx, sy, llx, lly, urx, ury, lower_left_x, lower_left_y, upper_right_x, upper_right_y);
						outx[i] = intersection[0];
						outy[i] = intersection[1];
						i++;
						wl++;
					//}
				}
			}
			px = sx;
			py = sy;
		}
		//System.out.println("wl" + wl);
		return wl;
	}
	//function to check if the given vertex of edge is inside the clipping boundary or not
	public boolean inside(float sx, float sy, float llx, float lly, float urx, float ury, float lower_left_x, float lower_left_y, float upper_right_x, float upper_right_y) {
		//condition to check for horizontal clipping boundaries 
		if ( lly == ury){
			if  ((lly == lower_left_y && sy >= lower_left_y) || ( lly == upper_right_y && sy <= upper_right_y) )
				return true;
			else 
				return false;
		}
		//condition to check for vertical clipping boundaries 
		else{
			if ((llx == lower_left_x && sx >= lower_left_x) || ( llx == upper_right_x && sx <= upper_right_x))
				return true;
			else 
				return false;
		}
	}
	//function to find intersection point with the clipping boundaries
	public float[] intersect(float px, float py, float sx, float sy, float llx, float lly, float urx, float ury, float lower_left_x, float lower_left_y, float upper_right_x, float upper_right_y) {
		float ix = 0, iy = 0;
		float slope;
		float dy, dx;
		//to find intersection point with the horizontal clipping boundaries
		if ( lly == ury){
			if (lly == lower_left_y){
				//if the edge to be clipped is vertical, then only y coordinate is changed
				if ( sx == px){
					ix = sx;
					iy = lly;
				}
				//else, from slope of edge to be clipped, x coordinate of intersection point is found
				else{
					dx = sx - px;
					dy = sy - py;
					if ( dx < 0){
						dx = Math.abs(dx);
						slope = dy/dx;
						slope = -1 * slope;
					}
					else
						slope = dy/dx;
					ix = ((sx * slope) + lly - sy)/slope;
					iy = lly;
				}
			}
			if ( lly == upper_right_y){
				//if the edge to be clipped is vertical, then only y coordinate is changed
				if ( sx == px){
					ix = sx;
					iy = lly;
				}
				//else, from slope of edge to be clipped, x coordinate of intersection point is found
				else{
					dx = sx - px;
					dy = sy - py;
					if ( dx < 0){
						dx = Math.abs(dx);
						slope = dy/dx;
						slope = -1 * slope;
					}
					else
						slope = dy/dx;
					ix = ((sx * slope) + ury - sy)/slope;
					iy = ury;
				}
			}
		}
		//to find intersection point with the vertical clipping boundaries
		else{
			if ( llx == lower_left_x){
				//if the edge to be clipped is horizontal, then only x coordinate is changed
				if ( sy == py){
					ix = llx;
					iy = py;
				}
				//else, from slope of edge to be clipped, y coordinate of intersection point is found
				else{
					dx = sx - px;
					dy = sy - py;
					if ( dx < 0){
						dx = Math.abs(dx);
						slope = dy/dx;
						slope = -1 * slope;
					}
					else
						slope = dy/dx;
					ix = llx;
					iy = py - (px * slope) + (ix * slope);
					
				}
			}
			if ( llx == upper_right_x){
				//if the edge to be clipped is horizontal, then only x coordinate is changed
				if ( sy == py){
					ix = llx;
					iy = py;
				}
				//else, from slope of edge to be clipped, y coordinate of intersection point is found
				else{
					dx = sx - px;
					dy = sy - py;
					if ( dx < 0){
						dx = Math.abs(dx);
						slope = dy/dx;
						slope = -1 * slope;
					}
					else
						slope = dy/dx;
					ix = llx;
					iy = py - (px * slope) + (ix * slope);
					
				}
			}
		}
		return new float[] { ix, iy };
	}

}
