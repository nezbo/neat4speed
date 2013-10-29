package itu.jgdiejuu.network;

import java.awt.MouseInfo;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;


public class InputManager implements KeyListener{
	
	NetworkCoach nCoach;
	
	public InputManager(NetworkCoach nCoach) {
		this.nCoach = nCoach;
	}

	
	
	@Override
	public void keyPressed(KeyEvent e) {
		System.out.println("keyPressed");
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
		System.out.println("keyReleased");
		switch (e.getKeyCode()) {
			
			case KeyEvent.VK_UP: nCoach.increaseLearningrate(); System.out.println("increase");break;
			case KeyEvent.VK_DOWN: nCoach.decreaseLearningrate(); System.out.println("decrease"); break;
			case KeyEvent.VK_SPACE: nCoach.saveNetwork(); System.out.println("save"); break;
			default: break;
		}
		nCoach.setText();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

}
