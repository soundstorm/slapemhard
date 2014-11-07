package de.hshannover.pp.slapemhard;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.*;

import org.w3c.dom.*;

import de.hshannover.pp.slapemhard.images.BufferedImageLoader;
import de.hshannover.pp.slapemhard.listener.LevelKeyboardListener;
import de.hshannover.pp.slapemhard.objects.Bullet;
import de.hshannover.pp.slapemhard.objects.CollisionObject;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.Player;
import de.hshannover.pp.slapemhard.objects.PowerUp;
import de.hshannover.pp.slapemhard.threads.DrawLevelThread;
import de.hshannover.pp.slapemhard.threads.MoveThread;

public class Level {

	private ArrayList<CollisionObject> collisionObjects = new ArrayList<CollisionObject>();
	private ArrayList<Person> enemies = new ArrayList<Person>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
	private Game game;
	private MoveThread mover;
	private int levelTime;
	private BufferedImage landscapeImage;
	private ArrayList<BufferedImage> backgroundImages = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> foregroundImages = new ArrayList<BufferedImage>();
	private int width;
	private Dimension bounds;
	private DrawLevelThread drawThread;
	private long startTime;
	
	public Level(Game game, int level) {
		this.game = game;
		try {
			InputStream fXmlFile = this.getClass().getResourceAsStream(("/res/levels/level_"+level+".xml"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			levelTime = Integer.parseInt(doc.getDocumentElement().getAttribute("time"));

			//Add collision objects
			NodeList nList = doc.getElementsByTagName("CollisionObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					collisionObjects
						.add(new CollisionObject(
								game,
								new Rectangle(
										Integer.parseInt(eElement.getAttribute("x")),
										Integer.parseInt(eElement.getAttribute("y")),
										Integer.parseInt(eElement.getAttribute("width")),
										Integer.parseInt(eElement.getAttribute("height"))
								)
						));
				}
			}
			
			nList = doc.getElementsByTagName("PowerUp");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					powerups
						.add(new PowerUp(
								game,
								new Dimension(
										Integer.parseInt(eElement.getAttribute("x")),
										Integer.parseInt(eElement.getAttribute("y"))
								),
								Integer.parseInt(eElement.getAttribute("type"))
						));
					System.out.println("Added Powerup.");
				}
			}
			
			//Place player
			Element playerElement = (Element)doc.getElementsByTagName("Player").item(0);
			game.getPlayer().setPosition(Integer.parseInt(playerElement.getAttribute("x")), Integer.parseInt(playerElement.getAttribute("y")));
			
			//Add Background images
			BufferedImageLoader bL = new BufferedImageLoader();
			nList = doc.getElementsByTagName("BackgroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					backgroundImages
						.add(Integer.parseInt(eElement.getAttribute("layer")), bL.getImage("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			//Add Foreground images
			nList = doc.getElementsByTagName("ForegroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					foregroundImages
						.add(Integer.parseInt(eElement.getAttribute("layer")), bL.getImage("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			
			//Add main landscape
			Element landscapeElement = (Element)doc.getElementsByTagName("LandscapeImage").item(0);
			landscapeImage = bL.getImage("levels/level_"+level+"/"+landscapeElement.getAttribute("src"));
			width = landscapeImage.getWidth();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.bounds = new Dimension(width,game.getGameSize().height);
	}
	
	public void start() {
		System.out.println("Starting level");
		drawThread = new DrawLevelThread(this);
		drawThread.setPreferredSize(new Dimension(game.getFrame().getWidth(), game.getFrame().getHeight()));
		drawThread.setBounds(new Rectangle(0,0,game.getFrame().getWidth(), game.getFrame().getHeight()));
		game.getFrame().add(drawThread);
		drawThread.addKeyListener(new LevelKeyboardListener(this));
		drawThread.start();
		System.out.println("Started drawThread");
		mover = new MoveThread(this);
		mover.start();
		System.out.println("Started mover");
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		System.out.println("Stopping mover");
		mover.interrupt();
		System.out.println("Stopping drawThread");
		drawThread.interrupt();
		System.out.println("Stopped");
		game.getFrame().remove(drawThread);
	}
	
	public boolean done() {
		return false;
	}
	
	public String getRemainingTime() {
		return ""+(levelTime*100-(System.currentTimeMillis()-startTime)/10);
	}
	
	public boolean timeUp() {
		return System.currentTimeMillis() > startTime+levelTime*1000;
	}
	
	public BufferedImage getLandscapeImage() {
		return landscapeImage;
	}
	
	public ArrayList<Person> getEnemies() {
		return enemies;
	}

	public ArrayList<Bullet> getBullets() {
		return bullets;
	}

	public ArrayList<CollisionObject> getCollisionObjects() {
		return collisionObjects;
	}
	
	public ArrayList<PowerUp> getPowerUps() {
		return powerups;
	}

	public Player getPlayer() {
		return game.getPlayer();
	}

	public MoveThread getMoveThread() {
		return mover;
	}

	public Font getFont() {
		return game.getFont();
	}

	public ArrayList<BufferedImage> getBackgroundImages() {
		return backgroundImages;
	}
	
	public Dimension getBounds() {
		return bounds;
	}

	public Game getGame() {
		return game;
	}

	public ArrayList<BufferedImage> getForegroundImages() {
		return foregroundImages;
	}
}
