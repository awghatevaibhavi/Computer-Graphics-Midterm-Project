//
//  Rasterizer.java
//
//  Created by Joe Geigel on 1/21/10.
//  Copyright 2010 Rochester Institute of Technology. All rights reserved.
//
//  Contributor:  VAIBHAVI AWGHATE
//

///
// 
// This is a class that performas rasterization algorithms
//
///

import java.util.*;

public class Rasterizer {

	///
	// number of scanlines
	///

	int n_scanlines;

	///
	// Constructor
	//
	// @param n - number of scanlines
	//
	///

	Rasterizer(int n) {
		n_scanlines = n;
	}

	///
	// Draw a filled polygon in the simpleCanvas C.
	//
	// The polygon has n distinct vertices. The
	// coordinates of the vertices making up the polygon are stored in the
	// x and y arrays. The ith vertex will have coordinate (x[i], y[i])
	//
	// You are to add the implementation here using only calls
	// to C.setPixel()
	///

	public void drawPolygon(int n, float x[], float y[], simpleCanvas C) {
		// YOUR IMPLEMENTATION GOES HERE
		float max = 0;
		float min = 0;
		float dx = 0;
		float slope = 0;
		max = y[0];
		min = y[0];
		//loop for finding max value of y vertices to make edge table
		for (int index = 0; index < n; index++) {
			if (y[index] > max) 
				max = y[index];
			if (y[index] < min)
				min = y[index];
		}
		Node[] edge_table = new Node[(int) (max + 1)];
		ArrayList<Node> active_list = new ArrayList<Node>();
		Node current = new Node(0, 0, 0, null);
		//code to create edge table
		for (int i = 0; i < n; i++) {
			if (i != n - 1) {
				if (y[i] == y[i + 1])
					continue;
				else if (y[i] < y[i + 1]) {
					dx = x[i + 1] - x[i];
					if (dx < 0) {
						dx = Math.abs(dx);
						slope = (y[i + 1] - y[i]) / dx;
						slope = -1 * slope;
					} else
						slope = (y[i + 1] - y[i]) / dx;
					Node newnode = new Node(y[i + 1], x[i], 1 / slope, null);
					//if the node is first to be added in edge table  
					if (edge_table[(int) y[i]] == null) {
						edge_table[(int) y[i]] = newnode;
					} else {
						current = edge_table[(int) y[i]];
						while (current.link != null)
							current = current.link;
						current.link = newnode;
					}
				} else {
					dx = x[i + 1] - x[i];
					if (dx < 0) {
						dx = Math.abs(dx);
						slope = (y[i + 1] - y[i]) / dx;
						slope = -1 * slope;
					} else {
						slope = (y[i + 1] - y[i]) / dx;
					}
					Node newnode = new Node(y[i], x[i + 1], 1 / slope, null);
					if (edge_table[(int) y[i + 1]] == null) {
						edge_table[(int) y[i + 1]] = newnode;
					} else {
						current = edge_table[(int) y[i + 1]];
						while (current.link != null) {
							current = current.link;
						}
						current.link = newnode;
					}
				}
			} 
			// code to create a edge table for first and last vertex
			else {
				if (y[i] == y[0])
					continue;
				else if (y[i] < y[0]) {
					dx = x[0] - x[i];
					if (dx < 0) {
						dx = Math.abs(dx);
						slope = (y[0] - y[i]) / dx;
						slope = -1 * slope;
					} else {
						slope = (y[0] - y[i]) / dx;
					}
					Node newnode = new Node(y[0], x[i], 1 / slope, null);
					if (edge_table[(int) y[i]] == null) {
						edge_table[(int) y[i]] = newnode;
					} else {
						current = edge_table[(int) y[i]];
						while (current.link != null) {
							current = current.link;
						}
						current.link = newnode;
					}
				} else {
					dx = x[0] - x[i];
					if (dx < 0) {
						dx = Math.abs(dx);
						slope = (y[0] - y[i]) / dx;
						slope = -1 * slope;
					} else
						slope = (y[0] - y[i]) / dx;
					Node newnode = new Node(y[i], x[0], 1 / slope, null);
					if (edge_table[(int) y[0]] == null) {
						edge_table[(int) y[0]] = newnode;
					} else {
						current = edge_table[(int) y[0]];
						while (current.link != null)
							current = current.link;
						current.link = newnode;
					}
				}
			}
		}
		//code to create active list
		int yi = (int)min;
		//loop starts from minimum value of y vertex
		while (yi < max-1 ) {
			if (edge_table[ yi] != null) {
				while (edge_table[ yi] != null) {
					//nodes are being added to active list for a particular ymin value 
					Node newnode = new Node(0, 0, 0, null);
					newnode = edge_table[ yi];
					active_list.add(newnode);
					edge_table[ yi] = edge_table[yi].link;
				}
				/*active list is being sorted on x values and if
				 *x values are same, sorted on slope values
				 */
				Collections.sort(active_list, new Sorting());
			} else 
				yi++;
			/*if x values are same for adjacent nodes, then respective
			 *slopes are added and y value is incremented 
			 */
			for (int i = 1; i < active_list.size(); i = i + 2) {
				
				if (active_list.get(i - 1).x == active_list.get(i).x) {
					active_list.get(i).x = (active_list.get(i).x + active_list.get(i).slope);
					active_list.get(i - 1).x = (active_list.get(i - 1).x + active_list.get(i - 1).slope);
					yi++;
				}
				//pixels are plotted between the x values of adjacent nodes
				for (int xi = (int) Math.ceil(active_list.get(i-1).x); xi < (int) Math
						.floor(active_list.get(i).x); xi++) {
					C.setPixel(xi, (int)yi);
				}
			}
			for (int j = 0; j < active_list.size(); j++) {
				//condition to check if for any node ymax is reached or not 
				if (yi == (int)active_list.get(j).ymax) {
					active_list.remove(j);
					j--;
					 int yj = (int) yi;
					//nodes are added to active list when yi = ymin of any edge
					while (edge_table[ yj] != null) {
						Node newnode = new Node(0, 0, 0, null);
						newnode = edge_table[ yj];
						active_list.add(newnode);
						edge_table[ yj] = edge_table[ yj].link;
					}
					//again the active list is sorted
					Collections.sort(active_list, new Sorting());
				} else {
					/*if ymax is not reached, then respective slopes are 
					 *added to x values and the loop is continued
					 */
					active_list.get(j).x = (active_list.get(j).x + active_list.get(j).slope);
				
				}
			}
			
		}
	}
}
//class for linked list
class Node {
	float ymax;
	float x;
	float slope;
	Node link;
	public Node(float y, float _x, float _slope, Node _link) {
		link = _link;
		ymax = y;
		x = _x;
		slope = _slope;
	}
}

class Sorting implements Comparator< Node>{
	public int compare ( Node n1, Node n2){
		if ( n1.x > n2.x)
			return 1;
		else if ( n1.x == n2.x){
			if ( n1.slope > n2.slope)
				return 1;
			else return -1;
		}
		else
			return -1;
	}
}

