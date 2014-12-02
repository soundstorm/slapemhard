package de.hshannover.pp.slapemhard;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.listener.Mouse;
import de.hshannover.pp.slapemhard.objects.ObjectPrototype;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.PowerUp;

public class LevelDesigner {
	private Menu menu;
	private LevelManager levelManager;
	private boolean dialogOpen;
	private int tool;
	private int object;
	
	private enum objectTypes {
		PLAYER,
		TARGETAREA,
		ENEMY,
		POWERUP,
		COLLISIONOBJECT,
		MALICIOUSOBJECT
	};
	
	Mouse mouse;
	private ObjectPrototype activeObject;
	private objectTypes activeGroup;
	private Point objectOffset = new Point();
	private ObjectPrototype newObject;
	private Point newObjectOrigin;
	private JFrame frame = new JFrame();
	private JSpinner power = new JSpinner(new SpinnerNumberModel(0,0,4,1));
	private JSpinner health = new JSpinner(new SpinnerNumberModel(20,20,100,5));
	private JSpinner look = new JSpinner(new SpinnerNumberModel(0,0,10,1));
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox weapon = new JComboBox(new String[] {"Handgun","Rocketlauncher","Machinegun"});
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private JComboBox powerup = new JComboBox(new String[] {"MediKit","Coin","Magazine","Live","Invincible"});
	private JButton applyButton = new JButton("Apply");
	private JButton resetButton = new JButton("Reset");
	private JSlider offsetSlider = new JSlider(JSlider.HORIZONTAL,0,1,0);
	
	public LevelDesigner(Menu menu) {
		this.menu = menu;
		levelManager = new LevelManager();
		levelManager.load(1);
		
		offsetSlider.setMaximum(levelManager.getWidth()-SlapEmHard.WIDTH);
		JPanel panel = new JPanel();
		panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		panel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.fill = GridBagConstraints.HORIZONTAL;
		menu.getFrame().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		c.gridy = 0;
		c.gridwidth = 6;
		panel.add(new JLabel("Selected Object:"),c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(new JLabel("Power:"),c);
		c.gridwidth = 4;
		panel.add(power,c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(new JLabel("Health:"),c);
		c.gridwidth = 4;
		panel.add(health,c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(new JLabel("Look:"),c);
		c.gridwidth = 4;
		panel.add(look,c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(new JLabel("Weapon:"),c);
		c.gridwidth = 4;
		panel.add(weapon,c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(new JLabel("Type:"),c);
		c.gridwidth = 4;
		panel.add(powerup,c);
		c.gridy++;
		c.gridwidth = 2;
		panel.add(resetButton,c);
		c.gridwidth = 4;
		panel.add(applyButton,c);
		c.gridy++;
		
		applyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				activeObject.setPower((int)power.getValue());
				activeObject.setHealth((int)health.getValue());
				activeObject.setWeapon(weapon.getSelectedIndex());
				activeObject.setLook((int)look.getValue());
				activeObject.setType(powerup.getSelectedIndex());
			}
		});
		resetButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateGui();
			}
		});
		
		final JButton toolButtons[] = {
				new JButton("Draw"),
				new JButton("Edit"),
				new JButton("Resize")
		};
		final JButton objectButtons[] = {
				new JButton("Start"),
				new JButton("Target"),
				new JButton("Enemy"),
				new JButton("PowerUp"),
				new JButton("Collision"),
				new JButton("Trap")
		};
		c.gridy++;
		c.gridwidth = 6;
		panel.add(new JSeparator(),c);
		c.gridy++;
		panel.add(new JLabel("Tools"),c);
		c.gridwidth = 2;
		c.gridy++;
		final Menu fmenu = this.menu;
		toolButtons[0].setEnabled(false);
		for (int i = 0; i < toolButtons.length; i++) {
			final int a = i;
			toolButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					System.out.println(e.getID());
					tool = a;
					newObject = null;
					if (tool == 0) {
						fmenu.getFrame().setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
					} else if (tool == 1) {
						fmenu.getFrame().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					} else {
						fmenu.getFrame().setCursor(new Cursor(Cursor.E_RESIZE_CURSOR));
					}
					for (int i = 0; i < toolButtons.length; i++) {
						toolButtons[i].setEnabled(i!=a);
					}
				}
			});
			panel.add(toolButtons[i],c);
		}
		c.gridy++;
		c.gridwidth = 6;
		panel.add(new JSeparator(),c);
		c.gridy++;
		panel.add(new JLabel("Objects"),c);
		c.gridwidth = 2;
		objectButtons[0].setEnabled(false);
		for (int i = 0; i < objectButtons.length; i++) {
			final int a = i;
			objectButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					object = a;
					newObject = null;
					for (int i = 0; i < objectButtons.length; i++) {
						objectButtons[i].setEnabled(a!=i);
					}
				}
			});
			if (i%3 == 0)
				c.gridy++;
			panel.add(objectButtons[i],c);
		}
		c.gridy++;
		c.gridwidth = 6;
		panel.add(new JSeparator(),c);
		c.gridy++;
		panel.add(offsetSlider,c);
		c.gridy++;
		panel.add(new JSeparator(),c);
		c.gridy++;
		c.gridwidth = 2;
		JButton saveButton = new JButton("Save");
		saveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				levelManager.save();
			}
		});
		panel.add(saveButton,c);
		//c.gridx += 2;
		JButton loadButton = new JButton("Open");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				levelManager.load();
				offsetSlider.setMaximum(levelManager.getWidth()-SlapEmHard.WIDTH);
			}
		});
		panel.add(loadButton,c);
		//c.gridx += 2;
		JButton newButton = new JButton("New");
		newButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				createLevel();
				offsetSlider.setMaximum(levelManager.getWidth()-SlapEmHard.WIDTH);
			}
		});
		panel.add(newButton,c);
		
		frame.add(panel);
		frame.pack();
		System.out.println(offsetSlider.getSize().toString());
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
		updateGui();
	}
	
	private void updateGui() {
		if (activeGroup == objectTypes.POWERUP) {
			powerup.setEnabled(true);
			powerup.setSelectedIndex(activeObject.getType());
		} else {
			powerup.setEnabled(false);
		}
		if (activeGroup == objectTypes.ENEMY) {
			power.setEnabled(true);
			power.setValue(activeObject.getPower());
			health.setEnabled(true);
			health.setValue(activeObject.getHealth());
			weapon.setEnabled(true);
			weapon.setSelectedIndex(activeObject.getWeapon());
			look.setEnabled(true);
			look.setValue(activeObject.getLook());
		} else {
			power.setEnabled(false);
			health.setEnabled(false);
			weapon.setEnabled(false);
			look.setEnabled(false);
		}
		if (activeObject == null) {
			applyButton.setEnabled(false);
			resetButton.setEnabled(false);
		} else {
			applyButton.setEnabled(true);
			resetButton.setEnabled(true);
		}
	}
	
	public void mouseWheel(int wheelRotation) {
		if (offsetSlider.getValue()+wheelRotation >= 0 && offsetSlider.getValue()+wheelRotation <= offsetSlider.getMaximum()) {
			offsetSlider.setValue(offsetSlider.getValue()+wheelRotation);
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

			int xoffset = offsetSlider.getValue();

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
	
	private void createLevel() {
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
	}
	
	public void mousePressed(MouseEvent e) {
		if (levelManager.getLandscapeImage() == null || levelManager.getLandscapeImage().getImage() == null) {
			createLevel();
		}
		Point coord = getCoord(e.getPoint());
		this.activeGroup = null;
		this.activeObject = null;
		coord.x += offsetSlider.getValue();
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
		if (levelManager.getTargetArea() != null && levelManager.getTargetArea().contains(coord)) {
			this.activeObject = levelManager.getTargetArea();
			this.activeGroup = objectTypes.TARGETAREA;
		}
		if (activeGroup != null) {
			objectOffset.x = coord.x-activeObject.x;
			objectOffset.y = coord.y-activeObject.y;
		}
		updateGui();
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
			updateGui();
			newObject = null;
		}
	}

	public void mouseDragged(MouseEvent e) {
		Point coord = getCoord(e.getPoint());
		coord.x += offsetSlider.getValue();
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
