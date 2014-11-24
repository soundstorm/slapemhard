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
import de.hshannover.pp.slapemhard.objects.*;

public class Level {
	private ArrayList<CollisionObject> collisionObjects = new ArrayList<CollisionObject>();
	private ArrayList<CollisionObject> maliciousObjects = new ArrayList<CollisionObject>();
	private ArrayList<Person> enemies = new ArrayList<Person>();
	private ArrayList<Bullet> bullets = new ArrayList<Bullet>();
	private ArrayList<PowerUp> powerups = new ArrayList<PowerUp>();
	private Game game;
	private int levelTime;
	private BufferedImage landscapeImage;
	private ArrayList<BufferedImage> backgroundImages = new ArrayList<BufferedImage>();
	private ArrayList<BufferedImage> foregroundImages = new ArrayList<BufferedImage>();
	private int width;
	private Dimension bounds;
	private long startTime;
	private Rectangle targetArea;
	private boolean completed;
	private boolean created;
	
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
			
			nList = doc.getElementsByTagName("MaliciousObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					maliciousObjects
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
			game.getPlayer().setLeft(false);
			game.getPlayer().setRight(false);
			game.getPlayer().setFire(false);
			game.getPlayer().setJump(false);
			
			nList = doc.getElementsByTagName("Enemy");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					Person enemy = new Person(game,
						Integer.parseInt(eElement.getAttribute("health")),
						new Dimension(
							Integer.parseInt(eElement.getAttribute("x")),
							Integer.parseInt(eElement.getAttribute("y"))
						),
						Person.PersonName.values()[Integer.parseInt(eElement.getAttribute("look"))]
					);
					enemy.setPower(Integer.parseInt(eElement.getAttribute("power")));
					enemy.setWeapon(new Weapon(game, new BulletType(BulletType.BulletName.values()[Integer.parseInt(eElement.getAttribute("weapon"))])));
					enemies.add(enemy);
				}
			}
			
			final Element targetElement = (Element)doc.getElementsByTagName("Target").item(0);
			targetArea = new Rectangle(
					Integer.parseInt(targetElement.getAttribute("x")),
					Integer.parseInt(targetElement.getAttribute("y")),
					Integer.parseInt(targetElement.getAttribute("width")),
					Integer.parseInt(targetElement.getAttribute("height"))
			);
			
			//Add Background images
			BufferedImageLoader bL = new BufferedImageLoader();
			nList = doc.getElementsByTagName("BackgroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					backgroundImages
						.add(bL.getImage("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			//Add Foreground images
			nList = doc.getElementsByTagName("ForegroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					foregroundImages
						.add(bL.getImage("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			
			//Add main landscape
			Element landscapeElement = (Element)doc.getElementsByTagName("LandscapeImage").item(0);
			landscapeImage = bL.getImage("levels/level_"+level+"/"+landscapeElement.getAttribute("src"));
			width = landscapeImage.getWidth();
			this.bounds = new Dimension(width,game.getGameSize().height);
			created = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public boolean isCreated() {
		return created;
	}
	/**
	 * Runs the level
	 */
	public void start() {
		startTime = System.currentTimeMillis();
		for (Person e : enemies) {
			//e.setAutonomous();
			e.init();
		}
	}
	/**
	 * Stops all Threads
	 */
	public void stop() {
		/*System.out.println("Stopping mover");
		for (Person p : game.getEnemies()) {
			p.stop();
		}*/
	}
	/**
	 * Returns if Level is completed
	 * @return if Level is completed
	 */
	public boolean isCompleted() {
		return completed;
	}
	
	public long getRemainingTime() {
		long tR = levelTime*100-(System.currentTimeMillis()-startTime)/10;
		if (tR < 0) {
			synchronized(game) {
				game.notify();
			}
		}
		return (tR > 0)?tR:0;
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
	
	public ArrayList<CollisionObject> getMaliciousObjects() {
		return maliciousObjects;
	}
	
	public ArrayList<PowerUp> getPowerUps() {
		return powerups;
	}

	public Player getPlayer() {
		return game.getPlayer();
	}
	
	public Rectangle getTargetArea() {
		return targetArea;
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
	public void setCompleted() {
		completed = true;
	}
}
