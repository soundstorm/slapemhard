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
import de.hshannover.pp.slapemhard.threads.DrawLevelThread;
import de.hshannover.pp.slapemhard.threads.MoveThread;

public class Level {

	private ArrayList<CollisionObject> collisionObjects = new ArrayList<CollisionObject>();
	private ArrayList<Person> enemies = new ArrayList<Person>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private Game game;
	private MoveThread mover;
	private boolean paused;
	private BufferedImage landscapeImage;
	private ArrayList<BufferedImage> backgroundImages = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> foregroundImages = new ArrayList<BufferedImage>();
	private int width;
	private Dimension bounds;
	
	public Level(Game game, int level) {
		this.game = game;
		try {
			InputStream fXmlFile = this.getClass().getResourceAsStream(("/res/levels/level_"+level+".xml"));
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();
			
			System.out.println("Root element :"+doc.getDocumentElement().getNodeName());

			System.out.println("----------------------------");

			NodeList nList = doc.getElementsByTagName("CollisionObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
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
								)));
					System.out.println("x : "
							+ eElement.getAttribute("x"));
					System.out.println("y : "
							+ eElement.getAttribute("y"));
					System.out.println("width : "
							+ eElement.getAttribute("width"));
					System.out.println("height : "
							+ eElement.getAttribute("height"));
				}
			}
			Element playerElement = (Element)doc.getElementsByTagName("Player").item(0);
			game.getPlayer().setPosition(Integer.parseInt(playerElement.getAttribute("x")), Integer.parseInt(playerElement.getAttribute("y")));
			
			BufferedImageLoader bL = new BufferedImageLoader();
			nList = doc.getElementsByTagName("BackgroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				System.out.println("\nCurrent Element :" + nNode.getNodeName());
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					backgroundImages
						.add(Integer.parseInt(eElement.getAttribute("layer")), bL.getImage("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			
			
			Element landscapeElement = (Element)doc.getElementsByTagName("LandscapeImage").item(0);
			landscapeImage = bL.getImage("levels/level_"+level+"/"+landscapeElement.getAttribute("src"));
			width = landscapeImage.getWidth();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		DrawLevelThread drawThread = new DrawLevelThread(this);
		drawThread.setPreferredSize(new Dimension(game.getFrame().getWidth(), game.getFrame().getHeight()));
		drawThread.setBounds(new Rectangle(0,0,game.getFrame().getWidth(), game.getFrame().getHeight()));
		game.getFrame().add(drawThread);
		drawThread.addKeyListener(new LevelKeyboardListener(this));
		mover = new MoveThread(this);
		drawThread.start();
		mover.start();
		bounds = new Dimension(width,game.getGameSize().height);
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
}
