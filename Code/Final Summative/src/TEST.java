import tapplet.*;
import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.lang.*;

public class TEST extends TApplet {
	// Settings
	static final double rotRate = 4;
	static final double moveRate = 0.05;
	static final int screenX = 400, screenY = 400;
	static final double FOV = 90;
	static final double renderQuality = 30.0;
	static final int fps = 60;
	static final int maxBrightness = 200;
	
	// Data
//	static char[][] grid = new char[5][5];
	static char[][] grid = {
		{' ', ' ', '*', '*', '*', ' '},
		{' ', '*', '*', ' ', '*', ' '},
		{' ', ' ', ' ', ' ', '*', ' '},
		{' ', ' ', '*', ' ', '*', ' '},
		{'*', ' ', '*', ' ', ' ', ' '},
		{' ', ' ', ' ', '*', ' ', ' '},
	};
//	static char[][] grid = {
//			{'*', '*', '*', ' ', ' ', ' ', ' ', '*', '*'},
//			{' ', ' ', ' ', ' ', '*', '*', ' ', ' ', ' '},
//			{' ', '*', '*', '*', ' ', '*', ' ', '*', ' '},
//			{' ', ' ', '*', '*', ' ', '*', ' ', ' ', ' '},
//			{' ', ' ', '*', ' ', '*', '*', '*', ' ', '*'},
//			{'*', ' ', ' ', '*', '*', '*', '*', '*', ' '},
//			{'*', ' ', ' ', '*', ' ', ' ', '*', '*', ' '},
//			{' ', ' ', ' ', '*', ' ', ' ', ' ', ' ', '*'},
//			{' ', ' ', '*', '*', ' ', ' ', '*', ' ', '*'},
//	};
	// Variables
	static Coord player = new Coord(4.5, 4.5);
	static double pRot = 0;// r%360+360
	static ArrayList<Coord> blocks = new ArrayList<Coord>();
	
	public static void main(String[] args) {
		
		new TEST();
	}
	
	public void init() {
		// Setup
		setFPS(fps);
		for (int i = 0; i < grid.length; i++) {
			for (int j = 0; j < grid[0].length; j++) {
				if (grid[i][j] == '*') {
					double inc = 1.0/renderQuality;
					if (i+1 >= grid.length || grid[i+1][j] != '*') {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+1, j+idx));
						}
					}
					if (i-1 <= 0 || grid[i-1][j] != '*') {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i, j+idx));
						}
					}
					if (j+1 >= grid[0].length || grid[i][j+1] != '*') {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+idx, j+1));
						}
					}
					if (j-1 <= 0 || grid[i][j-1] != '*') {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+idx, j));
						}
					}
				} else {
					double inc = 1.0/renderQuality;
					if (i == 0) {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i, j+idx));
						}
					}
					if (i == grid.length-1) {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+1, j+idx));
						}
					}
					if (j == 0) {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+idx, j));
						}
					}
					if (j == grid[0].length-1) {
						for (double idx = 0; idx <= 1; idx += inc) {
							blocks.add(new Coord(i+idx, j+1));
						}
					}
				}
			}
		}
		
		for (int i = 0; i < blocks.size(); i++) {
			System.out.println("x :"+blocks.get(i).x+" y: "+blocks.get(i).y);
		}
		
		setSize(screenX, screenY);
		Graphics g = getScreenBuffer();
	}
	
	public void movie(Graphics g) {
		// Display
		g.setColor(Color.black);
		g.fillRect(0, 0, screenX, screenY);
		
		// render
		Comparator<Coord> cmp = (a, b) -> Double.compare(dist(player, b), dist(player, a));
		Collections.sort(blocks, cmp);
		for (int i = 0; i < blocks.size(); i++) {
			drawSprite(blocks.get(i));
		}
		repaint();
	}
	
	public void drawSprite(Coord t) {
		Graphics g = getScreenBuffer();
		
		// calc
		double bRot = pRot+FOV/2;
		double bD = dist(player, t);// radius of view circle
		double bX = player.x-Math.sin(Math.toRadians(bRot))*bD, bY = player.y-Math.cos(Math.toRadians(bRot))*bD;
		Coord b = new Coord(bX, bY);
		double bA = dist(player, t), bB = dist(player, b), bC = dist(t, b);
		double bDeg = Math.acos(-(bC*bC-bA*bA-bB*bB)/(2.0*bA*bB));
		
		double bZRot = 45-pRot;
		double bOX = player.x-Math.sin(Math.toRadians(bZRot))*bD, bOY = player.y+Math.cos(Math.toRadians(bZRot))*bD;
		Coord bO = new Coord(bOX, bOY);
		double bZA = dist(player, t), bZB = dist(player, bO), bZC = dist(t, bO);
		double bZDeg = Math.acos(-(bZC*bZC-bZA*bZA-bZB*bZB)/(2.0*bZA*bZB));
		
		double tmpDist = dist(player, t);
//				bDeg = Math.toDegrees(bDeg);
		
		// DEBUG
		System.out.println("("+player.x+", "+player.y+"): "+pRot);
//				System.out.println("bRot: " +bRot);
//		System.out.println("bDeg: "+Math.toDegrees(bDeg));
//		System.out.println("bZDeg: "+Math.toDegrees(bZDeg));
//				System.out.println("bDeg/tmpDist: "+(bDeg/tmpDist));
//				System.out.println("arcL: "+(bDeg*tmpDist));
		
		// Display
		if (bDeg <= Math.toRadians(90) && bZDeg <= Math.toRadians(90)) {
			int bCC = Math.min(maxBrightness, (int)(maxBrightness/bD/2.5));
			drawRect(bDeg*250, 200, 50/tmpDist*6/renderQuality, 200/tmpDist, new Color(bCC, bCC, bCC), true);
		}
	}
	
	public void keyDown(KeyEvent e) {
		char k = e.getKeyChar();
		double px = player.x, py= player.y;
		switch(k) {
			case 'a':
				pRot -= rotRate;
				break;
			case 'd':
				pRot += rotRate;
				break;
			case 'w':
				px = player.x - Math.cos(Math.toRadians(pRot))*moveRate;
				py = player.y + Math.sin(Math.toRadians(pRot))*moveRate;
				break;
			case 's':
				px = player.x + Math.cos(Math.toRadians(pRot))*moveRate;
				py = player.y - Math.sin(Math.toRadians(pRot))*moveRate;
				break;
		}
		player.x = px;
		player.y = py;
		player.x = Math.max(0.0, player.x);
		player.x = Math.min(grid.length, player.x);
		player.y = Math.max(0.0, player.y);
		player.y = Math.min(grid[0].length, player.y);
	}
	
	public double rotConv(double r) {
		return r%360+360;
	}
	
	public double dist(Coord a, Coord b) {
		double xDist = Math.abs(a.x-b.x), yDist = Math.abs(a.y-b.y);
		return Math.sqrt(xDist*xDist + yDist*yDist);
	}
	
	public void drawRect(double cx, double cy, double w, double h, Color c, boolean fill) {
		// Setup
		Graphics g = getScreenBuffer();
		g.setColor(c);
		int dx = (int)Math.round(cx-w/2), dy = (int)Math.round(cy-h/2);
		
		// Draw
		if (fill) {// fillRect
			g.fillRect(dx, dy, (int)w, (int)h);
		} else {// drawRect (hollow)
			g.drawRect(dx, dy, (int)w, (int)h);
		}
	}
}