package de.hshannover.pp.slapemhard;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.listener.Mouse;
import de.hshannover.pp.slapemhard.objects.ObjectPrototype;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.PowerUp;

public class LevelDesigner {
	private Menu menu;
	private LevelManager levelManager;
	private boolean dialogOpen;
	private static BufferedImage gui = (new BufferedImageLoader()).getImage("images/editor_gui.png");
	private int tool;
	private int object;
	private Rectangle tools[] = {
			new Rectangle(5,5,20,20),
			new Rectangle(25,5,20,20),
			new Rectangle(45,5,20,20)
	};
	private Rectangle objects[] = {
			new Rectangle(68,1,13,13),
			new Rectangle(81,1,13,13),
			new Rectangle(68,14,13,13),
			new Rectangle(81,14,13,13),
			new Rectangle(68,27,13,13),
			new Rectangle(81,27,13,13)
	};
	private Rectangle fileOperations[] = {
			new Rectangle(6,27,13,13),
			new Rectangle(19,27,13,13),
			new Rectangle(32,27,13,13)
	};
	private enum objectTypes {
		PLAYER,
		TARGETAREA,
		ENEMY,
		POWERUP,
		COLLISIONOBJECT,
		MALICIOUSOBJECT
	};
	
	int xoffset;
	Mouse mouse;
	private ObjectPrototype activeObject;
	private objectTypes activeGroup;
	private Point objectOffset = new Point();
	private ObjectPrototype newObject;
	private Point newObjectOrigin;

	// private CollisionObject activeObject;
	@SuppressWarnings("deprecation")
	public LevelDesigner(Menu menu) {
		this.menu = menu;
		levelManager = new LevelManager();
		levelManager.load(1);
		menu.getFrame().setCursor(Cursor.CROSSHAIR_CURSOR);
	}
	
	public void mouseWheel(int wheelRotation) {
		if (xoffset+wheelRotation >= 0 && xoffset+SlapEmHard.WIDTH+wheelRotation <= levelManager.getWidth()) {
			xoffset += wheelRotation;
		}
	}
	
	public void render(Graphics g) {
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, SlapEmHard.WIDTH, SlapEmHard.HEIGHT);
		if (levelManager.getLandscapeImage() == null) {
			g.setColor(Color.WHITE);
			drawStringCentered(g, "LOAD LANDSCAPE IMAGE",
					(SlapEmHard.HEIGHT - 8) / 2);
		} else {
			Graphics2D g2d = (Graphics2D) g;
			// g2d.scale(.5, .5);

			int xoffset = this.xoffset;

			for (BufferedImageReference bI : levelManager.getBackgroundImages()) {
				try {
					g.drawImage(bI.getImage(), -xoffset
							* (bI.getImage().getWidth() - SlapEmHard.WIDTH)
							/ (levelManager.getWidth() - SlapEmHard.WIDTH), 0,
							null);
				} catch (NullPointerException e) {
				}
			}
			g.drawImage(levelManager.getLandscapeImage().getImage(), -xoffset, 0, null);
			for (BufferedImageReference bI : levelManager.getForegroundImages()) {
				try {
					g.drawImage(bI.getImage(), -xoffset
							* (bI.getImage().getWidth() - SlapEmHard.WIDTH)
							/ (levelManager.getWidth() - SlapEmHard.WIDTH), 0,
							null);
				} catch (NullPointerException e) {
				}
			}
			g2d.translate(-xoffset, 0);

			g.setColor(new Color(204, 102, 0, 150));
			for (ObjectPrototype o : levelManager.getCollisionObjects()) {
				try {
					drawPrototype(g, o);
				} catch (NullPointerException e) {
				}
			}
			g.setColor(new Color(255, 0, 0, 150));
			for (ObjectPrototype o : levelManager.getMaliciousObjects()) {
				try {
					drawPrototype(g, o);
				} catch (NullPointerException e) {
				}
			}
			g.setColor(new Color(255, 204, 51, 150));
			for (ObjectPrototype o : levelManager.getPowerups()) {
				try {
					drawPrototype(g, o);
				} catch (NullPointerException e) {
				}
			}
			g.setColor(new Color(255, 100, 100, 150));
			for (ObjectPrototype o : levelManager.getEnemies()) {
				try {
					drawPrototype(g, o);
				} catch (NullPointerException e) {
				}
			}
			g.setColor(new Color(0, 0, 0, 150));
			try {
				drawPrototype(g, levelManager.getTargetArea());
			} catch (NullPointerException e) {
			}
			g.setColor(new Color(0, 255, 0, 150));
			drawPrototype(g,levelManager.getPlayer());
			
			g.setColor(new Color(255, 255, 255, 150));
			if (newObject != null) {
				drawPrototype(g,newObject);
			}
			
			g2d.translate(xoffset, 0);
			// Draw UI
			g.setColor(new Color(0, 0, 255, 150));
			g.fillRect(0, 0, 100, 50);
			g.setColor(new Color(0, 255, 0, 150));
			fillRect(g,tools[tool]);
			fillRect(g,objects[object]);
			g.drawImage(gui, 0, 0, 100, 50, null);
			g.setColor(Color.WHITE);
			g.drawLine(5, 45, 95, 45);
			if (levelManager.getWidth() >= SlapEmHard.WIDTH) {
				int xpos = xoffset*90/(levelManager.getWidth()-SlapEmHard.WIDTH);
				g.drawLine(5+xpos, 42, 5+xpos, 48);
			}
		}
	}

	private String openFileDialog() {
		if (dialogOpen) return null;
		dialogOpen = true;
		JFrame jf = new JFrame();
		FileDialog fd = new FileDialog(jf, "Choose Image", FileDialog.LOAD);
		fd.setVisible(true);
		String file = fd.getFile();
		String dir = fd.getDirectory();
		if (file != null) {
			String filename = file.toLowerCase();
			if (filename.endsWith("jpg") | filename.endsWith("jpeg")
					| filename.endsWith("png") | filename.endsWith("bmp")) {
				System.out.println(dir+file);
				dialogOpen = false;
				return dir+file;
			} else {
				System.out.println("Filetype is not valid.");
			}
		} else {
			System.out.println("Cancelled");
		}
		dialogOpen = false;
		return null;
	}
	
	private void fillRect(Graphics g, Rectangle r) {
		g.fillRect(r.x, r.y, r.width, r.height);
	}
	
	private void drawPrototype(Graphics g, ObjectPrototype r) {
		Color c = g.getColor();
		g.fillRect(r.x, r.y, r.width, r.height);
		g.setColor(r == activeObject?Color.RED:Color.WHITE);
		g.drawRect(r.x, r.y, r.width, r.height);
		g.setColor(c);
	}

	private void drawStringCentered(Graphics g, String string, int y) {
		g.drawString(string, (SlapEmHard.WIDTH - g.getFontMetrics()
				.stringWidth(string)) / 2, y);
	}

	public void tick() {
		// ..
	}
	
	private Point getCoord(Point p) {
		return new Point(
				(int)(p.x/menu.getScale()),
				(int)(p.y/menu.getScale())
		);
	}
	
	@SuppressWarnings("deprecation")
	public void mousePressed(MouseEvent e) {
		Point coord = getCoord(e.getPoint());
		this.activeGroup = null;
		this.activeObject = null;
		for (int i = 0; i < fileOperations.length; i++) {
			if (fileOperations[i].contains(coord)) {
				switch (i) {
				case 0:
					String url = openFileDialog();
					BufferedImageReference tmp = new BufferedImageReference(url,true);
					if (tmp.getImage() == null) {
						return;
					}
					if (tmp.getImage().getHeight() < SlapEmHard.HEIGHT) {
						JOptionPane.showMessageDialog(menu.getFrame(), "Image is too small. Minumum height is "+SlapEmHard.HEIGHT);
						return;
					}
					levelManager.create();
					levelManager.setLandscapeImage(tmp);
					break;
				case 1:
					levelManager.load();
					break;
				case 2:
					levelManager.save();
				}
				newObject = null;
				return;
			}
		}
		for (int i = 0; i < tools.length; i++) {
			if (tools[i].contains(coord)) {
				tool = i;
				newObject = null;
				if (tool == 0) {
					menu.getFrame().setCursor(Cursor.CROSSHAIR_CURSOR);
				} else if (tool == 1) {
					menu.getFrame().setCursor(Cursor.DEFAULT_CURSOR);
				} else {
					menu.getFrame().setCursor(Cursor.E_RESIZE_CURSOR);
				}
				return;
			}
		}
		for (int i = 0; i < objects.length; i++) {
			if (objects[i].contains(coord)) {
				object = i;
				newObject = null;
				return;
			}
		}
		coord.x += xoffset;
		if (tool == 0 && e.getButton() == MouseEvent.BUTTON1) {
			switch (object) {
				case 0:
					levelManager.getPlayer().setLocation(coord);
					activeObject = levelManager.getPlayer();
					activeGroup = objectTypes.PLAYER;
					return;
				case 1: case 4: case 5:
					newObject = new ObjectPrototype(coord.x, coord.y, 1, 1);
					newObjectOrigin = coord;
					return;
				case 2:
					activeObject = new ObjectPrototype(coord.x, coord.y, Person.WIDTH, Person.HEIGHT);
					levelManager.getEnemies().add(activeObject);
					activeGroup = objectTypes.ENEMY;
					return;
				case 3:
					activeObject = new ObjectPrototype(coord.x, coord.y, PowerUp.WIDTH, PowerUp.HEIGHT);
					levelManager.getPowerups().add(activeObject);
					activeGroup = objectTypes.POWERUP;
					return;
			}
		}
		newObject = null;
		for (ObjectPrototype o : levelManager.getCollisionObjects()) {
			if (o.contains(coord)) {
				this.activeObject = o;
				this.activeGroup = objectTypes.COLLISIONOBJECT;
				break;
			}
		}
		if (activeGroup == null)
		for (ObjectPrototype o : levelManager.getMaliciousObjects()) {
			if (o.contains(coord)) {
				this.activeObject = o;
				this.activeGroup = objectTypes.MALICIOUSOBJECT;
				break;
			}
		}
		if (activeGroup == null)
		for (ObjectPrototype o : levelManager.getPowerups()) {
			if (o.contains(coord)) {
				this.activeObject = o;
				this.activeGroup = objectTypes.POWERUP;
				break;
			}
		}
		if (activeGroup == null)
		for (ObjectPrototype o : levelManager.getEnemies()) {
			if (o.contains(coord)) {
				this.activeObject = o;
				this.activeGroup = objectTypes.ENEMY;
				break;
			}
		}
		if (activeGroup == null)
		if (levelManager.getPlayer().contains(coord)) {
			this.activeObject = levelManager.getPlayer();
			this.activeGroup = objectTypes.PLAYER;
		}
		if (activeGroup == null)
		if (levelManager.getTargetArea().contains(coord)) {
			this.activeObject = levelManager.getTargetArea();
			this.activeGroup = objectTypes.TARGETAREA;
		}
		if (activeGroup != null) {
			objectOffset.x = coord.x-activeObject.x;
			objectOffset.y = coord.y-activeObject.y;
			if (e.getButton() == MouseEvent.BUTTON3) {
				JPanel panel = new JPanel();
				panel.setLayout(new GridLayout(0,2));
				switch (activeGroup) {
				case ENEMY:
					panel.add(new JLabel("Power"));
					JSpinner power = new JSpinner(new SpinnerNumberModel(activeObject.getPower(),0,4,1));
					panel.add(power);
					panel.add(new JLabel("Look"));
					JSpinner look = new JSpinner(new SpinnerNumberModel(activeObject.getLook(),0,10,1));
					panel.add(look);
					panel.add(new JLabel("Weapon"));
					@SuppressWarnings({ "rawtypes", "unchecked" })
					JComboBox weapon = new JComboBox(new String[] {"Handgun","Rocketlauncher","Machinegun"});
					weapon.setSelectedIndex(activeObject.getWeapon());
					panel.add(weapon);
					if (JOptionPane.showConfirmDialog(null, panel, "Enemy", JOptionPane.OK_CANCEL_OPTION,-1,null) == JOptionPane.OK_OPTION) {
						activeObject.setPower((int)power.getValue());
						activeObject.setLook((int)look.getValue());
						activeObject.setWeapon((int)weapon.getSelectedIndex());
					}
					break;
				case POWERUP:
					panel.add(new JLabel("Type"));
					@SuppressWarnings({ "rawtypes", "unchecked" })
					JComboBox powerup = new JComboBox(new String[] {"MediKit","Coin","Magazine","Live","Invincible"});
					powerup.setSelectedIndex(activeObject.getType());
					panel.add(powerup);
					if (JOptionPane.showConfirmDialog(null, panel, "Enemy", JOptionPane.OK_CANCEL_OPTION,-1,null) == JOptionPane.OK_OPTION) {
						activeObject.setType((int)powerup.getSelectedIndex());
					}
					break;
				default:
					break;
				}
				activeObject = null;
				activeGroup = null;
			}
			
		}
	}

	public void mouseReleased(MouseEvent e) {
		if (newObject != null) {
			if (newObject.height <= 1 | newObject.width <= 1) {
				newObject = null;
				return;
			}
			switch (object) {
			case 1:
				levelManager.setTargetArea(newObject);
				activeGroup = objectTypes.TARGETAREA;
				break;
			case 4:
				levelManager.getCollisionObjects().add(newObject);
				activeGroup = objectTypes.COLLISIONOBJECT;
				break;
			case 5:
				levelManager.getMaliciousObjects().add(newObject);
				activeGroup = objectTypes.MALICIOUSOBJECT;
				break;
			}
			activeObject = newObject;
			newObject = null;
		}
	}

	public void mouseDragged(MouseEvent e) {
		Point coord = getCoord(e.getPoint());
		coord.x += xoffset;
		if (tool == 0) {
			try {
				if (coord.x < newObjectOrigin.x) {
					newObject.x = coord.x;
				}
				if (coord.y < newObjectOrigin.y) {
					newObject.y = coord.y;
				}
				newObject.width = Math.abs(coord.x-newObjectOrigin.x);
				newObject.height = Math.abs(coord.y-newObjectOrigin.y);
			} catch (NullPointerException ex) {}
		} else {
			coord.x -= objectOffset.x;
			coord.y -= objectOffset.y;
			try {
				this.activeObject.setLocation(coord);
			} catch (NullPointerException ex) {}
		}
	}

	public void keyEvent(int keyCode) {
		switch (keyCode) {
			case KeyEvent.VK_UP:
				try {
					if (activeObject.y > 0) {
						activeObject.y--;
					}
				} catch (NullPointerException e){}
				break;
			case KeyEvent.VK_DOWN:
				try {
					if (activeObject.y+activeObject.height < SlapEmHard.HEIGHT) {
						activeObject.y++;
					}
				} catch (NullPointerException e){}
				break;
			case KeyEvent.VK_LEFT:
				try {
					if (activeObject.x > 0) {
						activeObject.x--;
					}
				} catch (NullPointerException e){}
				break;
			case KeyEvent.VK_RIGHT:
				try {
					if (activeObject.x+activeObject.width < levelManager.getWidth()) {
						activeObject.x++;
					}
				} catch (NullPointerException e){}
				break;
			case KeyEvent.VK_BACK_SPACE:
				try {
					getActiveCollection().remove(activeObject);
				} catch (NullPointerException e) {}
				break;
			case KeyEvent.VK_S:
				levelManager.save();
				break;
			case KeyEvent.VK_O:
				levelManager.load();
				break;
		}
	}
	
	private ArrayList<ObjectPrototype> getActiveCollection() {
		switch (this.activeGroup) {
			case COLLISIONOBJECT:
				return levelManager.getCollisionObjects();
			case ENEMY:
				return levelManager.getEnemies();
			case MALICIOUSOBJECT:
				return levelManager.getMaliciousObjects();
			case POWERUP:
				return levelManager.getPowerups();
			default:
				return null;
		}
	}
}
