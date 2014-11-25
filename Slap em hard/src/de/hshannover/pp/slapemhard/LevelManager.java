package de.hshannover.pp.slapemhard;

import java.awt.Dimension;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.objects.*;

public class LevelManager {
	private static final int HEIGHT = 240;
	private ArrayList<ObjectPrototype> collisionObjects;
	private ArrayList<ObjectPrototype> maliciousObjects;
	private ArrayList<ObjectPrototype> enemies;
	private ArrayList<ObjectPrototype> powerups;
	
	private int levelTime;
	private BufferedImageReference landscapeImage;
	private ArrayList<BufferedImageReference> backgroundImages;
	private ArrayList<BufferedImageReference> foregroundImages;
	private ObjectPrototype targetArea;
	private Dimension bounds;
	private ObjectPrototype player;
	
	public void create() {
		collisionObjects = new ArrayList<ObjectPrototype>();
		maliciousObjects = new ArrayList<ObjectPrototype>();
		enemies = new ArrayList<ObjectPrototype>();
		powerups = new ArrayList<ObjectPrototype>();
		backgroundImages = new ArrayList<BufferedImageReference>();
		foregroundImages = new ArrayList<BufferedImageReference>();
		landscapeImage = null;
		targetArea = null;
		levelTime = 0;
		player = new ObjectPrototype(0,0,Player.WIDTH,Player.HEIGHT);
	}
	
	public boolean load(int level) {
		create(); //reset
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
						.add(new ObjectPrototype(
								Integer.parseInt(eElement.getAttribute("x")),
								Integer.parseInt(eElement.getAttribute("y")),
								Integer.parseInt(eElement.getAttribute("width")),
								Integer.parseInt(eElement.getAttribute("height"))
						));
				}
			}
			
			nList = doc.getElementsByTagName("MaliciousObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					maliciousObjects
						.add(new ObjectPrototype(
								Integer.parseInt(eElement.getAttribute("x")),
								Integer.parseInt(eElement.getAttribute("y")),
								Integer.parseInt(eElement.getAttribute("width")),
								Integer.parseInt(eElement.getAttribute("height"))
						));
				}
			}
			
			nList = doc.getElementsByTagName("PowerUp");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ObjectPrototype powerup = new ObjectPrototype(
							Integer.parseInt(eElement.getAttribute("x")),
							Integer.parseInt(eElement.getAttribute("y")),
							PowerUp.WIDTH,
							PowerUp.HEIGHT
						);
					powerup.setType(Integer.parseInt(eElement.getAttribute("type")));
					powerups.add(powerup);
					System.out.println("Added Powerup.");
				}
			}
			
			//Place player
			Element playerElement = (Element)doc.getElementsByTagName("Player").item(0);
			player = new ObjectPrototype(
				Integer.parseInt(playerElement.getAttribute("x")),
				Integer.parseInt(playerElement.getAttribute("y")),
				Player.WIDTH,
				Player.HEIGHT
			);
			
			nList = doc.getElementsByTagName("Enemy");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ObjectPrototype enemy = new ObjectPrototype(
						Integer.parseInt(eElement.getAttribute("x")),
						Integer.parseInt(eElement.getAttribute("y")),
						Person.WIDTH,
						Person.HEIGHT
					);
					enemy.setHealth(Integer.parseInt(eElement.getAttribute("health")));
					enemy.setLook(Integer.parseInt(eElement.getAttribute("look")));
					enemy.setPower(Integer.parseInt(eElement.getAttribute("power")));
					enemy.setWeapon(Integer.parseInt(eElement.getAttribute("weapon")));
					enemies.add(enemy);
				}
			}
			
			final Element targetElement = (Element)doc.getElementsByTagName("Target").item(0);
			targetArea = new ObjectPrototype(
					Integer.parseInt(targetElement.getAttribute("x")),
					Integer.parseInt(targetElement.getAttribute("y")),
					Integer.parseInt(targetElement.getAttribute("width")),
					Integer.parseInt(targetElement.getAttribute("height"))
			);
			
			//Add Background images
			//BufferedImageLoader bL = new BufferedImageLoader();
			nList = doc.getElementsByTagName("BackgroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					backgroundImages
						.add(new BufferedImageReference("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			//Add Foreground images
			nList = doc.getElementsByTagName("ForegroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					foregroundImages
						.add(new BufferedImageReference("levels/level_"+level+"/"+eElement.getAttribute("src")));
				}
			}
			
			//Add main landscape
			Element landscapeElement = (Element)doc.getElementsByTagName("LandscapeImage").item(0);
			landscapeImage = new BufferedImageReference("levels/level_"+level+"/"+landscapeElement.getAttribute("src"));
			bounds = new Dimension(landscapeImage.getImage().getWidth(),HEIGHT);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void save(int level) {
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Level");
			rootElement.setAttribute("time", ""+levelTime);
			doc.appendChild(rootElement);
			
			Element backgroundElements = doc.createElement("BackgroundImages");
			for (BufferedImageReference o : backgroundImages) {
				Element e = doc.createElement("BackgroundImage");
				e.setAttribute("src", ""+o.getPath());
				backgroundElements.appendChild(e);
			}
			rootElement.appendChild(backgroundElements);
			
			Element foregroundElements = doc.createElement("ForegroundImages");
			for (BufferedImageReference o : foregroundImages) {
				Element e = doc.createElement("ForegroundImage");
				e.setAttribute("src", ""+o.getPath());
				foregroundElements.appendChild(e);
			}
			rootElement.appendChild(foregroundElements);
			
			Element landscapeElement = doc.createElement("ForegroundImage");
			landscapeElement.setAttribute("src", ""+landscapeImage.getPath());
			rootElement.appendChild(landscapeElement);
			
			Element collisionElements = doc.createElement("CollisionObjects");
			for (ObjectPrototype o : collisionObjects) {
				Element e = doc.createElement("CollisionObject");
				e.setAttribute("x", ""+o.x);
				e.setAttribute("y", ""+o.y);
				e.setAttribute("width", ""+o.width);
				e.setAttribute("height", ""+o.height);
				collisionElements.appendChild(e);
			}
			rootElement.appendChild(collisionElements);
			
			Element maliciousElements = doc.createElement("MaliciousObjects");
			for (ObjectPrototype o : maliciousObjects) {
				Element e = doc.createElement("CollisionObject");
				e.setAttribute("x", ""+o.x);
				e.setAttribute("y", ""+o.y);
				e.setAttribute("width", ""+o.width);
				e.setAttribute("height", ""+o.height);
				collisionElements.appendChild(e);
			}
			rootElement.appendChild(maliciousElements);
			
			Element powerupElements = doc.createElement("PowerUps");
			for (ObjectPrototype o : powerups) {
				Element e = doc.createElement("PowerUp");
				e.setAttribute("x", ""+o.x);
				e.setAttribute("y", ""+o.y);
				e.setAttribute("type", ""+o.getType());
				collisionElements.appendChild(e);
			}
			rootElement.appendChild(powerupElements);
			
			Element enemyElements = doc.createElement("Enemies");
			for (ObjectPrototype o : enemies) {
				Element e = doc.createElement("Enemy");
				e.setAttribute("x", ""+o.x);
				e.setAttribute("y", ""+o.y);
				e.setAttribute("look", ""+o.getType());
				e.setAttribute("weapon", ""+o.getWeapon());
				e.setAttribute("look", ""+o.getPower());
				collisionElements.appendChild(e);
			}
			rootElement.appendChild(enemyElements);
			
			Element playerElement = doc.createElement("Player");
			playerElement.setAttribute("x", ""+player.x);
			playerElement.setAttribute("y", ""+player.y);
			collisionElements.appendChild(playerElement);
			
			Element targetElement = doc.createElement("Target");
			targetElement.setAttribute("x", ""+targetArea.x);
			targetElement.setAttribute("y", ""+targetArea.y);
			targetElement.setAttribute("width", ""+targetArea.width);
			targetElement.setAttribute("height", ""+targetArea.height);
			collisionElements.appendChild(targetElement);
		} catch (Exception e) {
			
		}
	}

	/**
	 * @return the bounds
	 */
	public Dimension getBounds() {
		return bounds;
	}

	public ArrayList<ObjectPrototype> getCollisionObjects() {
		return collisionObjects;
	}

	public ArrayList<ObjectPrototype> getMaliciousObjects() {
		return maliciousObjects;
	}

	public ArrayList<ObjectPrototype> getEnemies() {
		return enemies;
	}

	public ArrayList<ObjectPrototype> getPowerups() {
		return powerups;
	}

	public int getLevelTime() {
		return levelTime;
	}

	public BufferedImageReference getLandscapeImage() {
		return landscapeImage;
	}

	public ArrayList<BufferedImageReference> getBackgroundImages() {
		return backgroundImages;
	}

	public ArrayList<BufferedImageReference> getForegroundImages() {
		return foregroundImages;
	}

	public ObjectPrototype getTargetArea() {
		return targetArea;
	}

	public ObjectPrototype getPlayer() {
		return player;
	}
}
