package de.hshannover.pp.slapemhard;

import java.awt.FileDialog;
import java.awt.GridLayout;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hshannover.pp.slapemhard.images.BufferedImageReference;
import de.hshannover.pp.slapemhard.objects.ObjectPrototype;
import de.hshannover.pp.slapemhard.objects.Person;
import de.hshannover.pp.slapemhard.objects.Player;
import de.hshannover.pp.slapemhard.objects.PowerUp;

/**
 * @author	Patrick Defayay<br />
 * 			Andre Schmidt<br />
 * 			Steffen Schulz<br />
 * 			Luca Zimmermann
 */
public class LevelManager {
	private ArrayList<ObjectPrototype> collisionObjects;
	private ArrayList<ObjectPrototype> maliciousObjects;
	private ArrayList<ObjectPrototype> enemies;
	private ArrayList<ObjectPrototype> powerups;

	private int levelTime;
	private BufferedImageReference landscapeImage;
	private ArrayList<BufferedImageReference> backgroundImages;
	private ArrayList<BufferedImageReference> foregroundImages;
	private ObjectPrototype targetArea;
	private int width;
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
		player = new ObjectPrototype(0, 0, Player.WIDTH, Player.HEIGHT);
	}
	
	public boolean load() {
		JFrame jf = new JFrame();
		FileDialog fd = new FileDialog(jf, "Choose Level", FileDialog.LOAD);
		fd.setVisible(true);
		String file = fd.getFile();
		String dir = fd.getDirectory();
		if (file == null)
			return false;
		if (file.endsWith(".xml"))
			return load(dir, true);
		return false;
	}

	public boolean load(int level) {
		return load("levels/level_" + level + "/", false);
	}

	public boolean load(String basedir, boolean absolute) {
		create(); // reset
		try {
			InputStream fXmlFile;
			if (absolute) {
				fXmlFile = new FileInputStream(basedir + "level.xml");
			} else {
				fXmlFile = this.getClass().getResourceAsStream(
						"/res/" + basedir + "level.xml");
			}
			if (fXmlFile == null)
				return false;
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(fXmlFile);
			doc.getDocumentElement().normalize();

			levelTime = Integer.parseInt(doc.getDocumentElement().getAttribute(
					"time"));

			// Add collision objects
			NodeList nList = doc.getElementsByTagName("CollisionObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					collisionObjects.add(new ObjectPrototype(Integer
							.parseInt(eElement.getAttribute("x")), Integer
							.parseInt(eElement.getAttribute("y")), Integer
							.parseInt(eElement.getAttribute("width")), Integer
							.parseInt(eElement.getAttribute("height"))));
				}
			}

			nList = doc.getElementsByTagName("MaliciousObject");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					maliciousObjects.add(new ObjectPrototype(Integer
							.parseInt(eElement.getAttribute("x")), Integer
							.parseInt(eElement.getAttribute("y")), Integer
							.parseInt(eElement.getAttribute("width")), Integer
							.parseInt(eElement.getAttribute("height"))));
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
							PowerUp.WIDTH, PowerUp.HEIGHT);
					powerup.setType(Integer.parseInt(eElement
							.getAttribute("type")));
					powerups.add(powerup);
					System.out.println("Added Powerup.");
				}
			}

			// Place player
			Element playerElement = (Element) doc
					.getElementsByTagName("Player").item(0);
			player = new ObjectPrototype(Integer.parseInt(playerElement
					.getAttribute("x")), Integer.parseInt(playerElement
					.getAttribute("y")), Player.WIDTH, Player.HEIGHT);

			nList = doc.getElementsByTagName("Enemy");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					ObjectPrototype enemy = new ObjectPrototype(
							Integer.parseInt(eElement.getAttribute("x")),
							Integer.parseInt(eElement.getAttribute("y")),
							Person.WIDTH, Person.HEIGHT);
					enemy.setHealth(Integer.parseInt(eElement
							.getAttribute("health")));
					enemy.setLook(Integer.parseInt(eElement
							.getAttribute("look")));
					enemy.setPower(Integer.parseInt(eElement
							.getAttribute("power")));
					enemy.setWeapon(Integer.parseInt(eElement
							.getAttribute("weapon")));
					enemies.add(enemy);
				}
			}

			final Element targetElement = (Element) doc.getElementsByTagName(
					"Target").item(0);
			targetArea = new ObjectPrototype(Integer.parseInt(targetElement
					.getAttribute("x")), Integer.parseInt(targetElement
					.getAttribute("y")), Integer.parseInt(targetElement
					.getAttribute("width")), Integer.parseInt(targetElement
					.getAttribute("height")));

			// Add Background images
			// BufferedImageLoader bL = new BufferedImageLoader();
			nList = doc.getElementsByTagName("BackgroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (absolute)
						backgroundImages.add(new BufferedImageReference(basedir
								+ eElement.getAttribute("src"), true));
					else
						backgroundImages.add(new BufferedImageReference(basedir
								+ eElement.getAttribute("src")));
				}
			}
			// Add Foreground images
			nList = doc.getElementsByTagName("ForegroundImage");
			for (int temp = 0; temp < nList.getLength(); temp++) {
				Node nNode = nList.item(temp);
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					if (absolute)
						foregroundImages.add(new BufferedImageReference(basedir
								+ eElement.getAttribute("src"), true));
					else
						foregroundImages.add(new BufferedImageReference(basedir
								+ eElement.getAttribute("src")));
				}
			}

			// Add main landscape
			Element landscapeElement = (Element) doc.getElementsByTagName(
					"LandscapeImage").item(0);
			if (absolute)
				landscapeImage = new BufferedImageReference(basedir
						+ landscapeElement.getAttribute("src"), true);
			else
				landscapeImage = new BufferedImageReference(basedir
						+ landscapeElement.getAttribute("src"));
			width = landscapeImage.getImage().getWidth();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public void save() {
		int level;
		String basePath;
		JPanel panel = new JPanel();
		panel.setLayout(new GridLayout(0, 2));
		panel.add(new JLabel("Level Number:"));
		JSpinner levelSelector = new JSpinner(new SpinnerNumberModel(1, 1, 100,
				1));
		panel.add(levelSelector);
		if (JOptionPane.showConfirmDialog(null, panel, "Save",
				JOptionPane.OK_CANCEL_OPTION, -1, null) != JOptionPane.OK_OPTION) {
			return;
		}
		level = (int) levelSelector.getValue();
		basePath = "SlapEmHardDesigner/level_" + level + "/";
		File f = new File(basePath);
		if (f.isDirectory()) {
			if (JOptionPane
					.showConfirmDialog(
							null,
							"Directory already exists. You are about to overwrite files.",
							"Save", JOptionPane.OK_CANCEL_OPTION, -1, null) != JOptionPane.OK_OPTION) {
				return;
			}
		} else {
			new File("SlapEmHardDesigner/").mkdir();
			if (!f.mkdir()) {
				return;
			}
		}
		File xmlFile = new File(basePath + "level.xml");
		try {
			xmlFile.createNewFile();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			DocumentBuilderFactory docFactory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("Level");
			rootElement.setAttribute("time", "" + levelTime);
			doc.appendChild(rootElement);

			Element backgroundElements = doc.createElement("BackgroundImages");
			for (BufferedImageReference o : backgroundImages) {
				Element e = doc.createElement("BackgroundImage");
				e.setAttribute("src", new File(o.getPath()).getName());
				backgroundElements.appendChild(e);
			}
			rootElement.appendChild(backgroundElements);

			Element foregroundElements = doc.createElement("ForegroundImages");
			for (BufferedImageReference o : foregroundImages) {
				Element e = doc.createElement("ForegroundImage");
				e.setAttribute("src", new File(o.getPath()).getName());
				foregroundElements.appendChild(e);
			}
			rootElement.appendChild(foregroundElements);

			Element landscapeElement = doc.createElement("LandscapeImage");
			landscapeElement.setAttribute("src", new File(landscapeImage.getPath()).getName());
			rootElement.appendChild(landscapeElement);

			Element collisionElements = doc.createElement("CollisionObjects");
			for (ObjectPrototype o : collisionObjects) {
				Element e = doc.createElement("CollisionObject");
				e.setAttribute("x", "" + o.x);
				e.setAttribute("y", "" + o.y);
				e.setAttribute("width", "" + o.width);
				e.setAttribute("height", "" + o.height);
				collisionElements.appendChild(e);
			}
			rootElement.appendChild(collisionElements);

			Element maliciousElements = doc.createElement("MaliciousObjects");
			for (ObjectPrototype o : maliciousObjects) {
				Element e = doc.createElement("MaliciousObject");
				e.setAttribute("x", "" + o.x);
				e.setAttribute("y", "" + o.y);
				e.setAttribute("width", "" + o.width);
				e.setAttribute("height", "" + o.height);
				maliciousElements.appendChild(e);
			}
			rootElement.appendChild(maliciousElements);

			Element powerupElements = doc.createElement("PowerUps");
			for (ObjectPrototype o : powerups) {
				Element e = doc.createElement("PowerUp");
				e.setAttribute("x", "" + o.x);
				e.setAttribute("y", "" + o.y);
				e.setAttribute("type", "" + o.getType());
				powerupElements.appendChild(e);
			}
			rootElement.appendChild(powerupElements);

			Element enemyElements = doc.createElement("Enemies");
			for (ObjectPrototype o : enemies) {
				Element e = doc.createElement("Enemy");
				e.setAttribute("x", "" + o.x);
				e.setAttribute("y", "" + o.y);
				e.setAttribute("look", "" + o.getType());
				e.setAttribute("weapon", "" + o.getWeapon());
				e.setAttribute("power", "" + o.getPower());
				e.setAttribute("health", "" + o.getHealth());
				enemyElements.appendChild(e);
			}
			rootElement.appendChild(enemyElements);

			Element playerElement = doc.createElement("Player");
			playerElement.setAttribute("x", "" + player.x);
			playerElement.setAttribute("y", "" + player.y);
			collisionElements.appendChild(playerElement);

			Element targetElement = doc.createElement("Target");
			targetElement.setAttribute("x", "" + targetArea.x);
			targetElement.setAttribute("y", "" + targetArea.y);
			targetElement.setAttribute("width", "" + targetArea.width);
			targetElement.setAttribute("height", "" + targetArea.height);
			collisionElements.appendChild(targetElement);

			TransformerFactory transformerFactory = TransformerFactory
					.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(xmlFile);
			transformer.transform(source, result);

			for (BufferedImageReference o : backgroundImages) {
				copyFile(o.getPath(), basePath);
			}
			for (BufferedImageReference o : foregroundImages) {
				copyFile(o.getPath(), basePath);
			}
			copyFile(landscapeImage.getPath(), basePath);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void copyFile(String inputPath, String baseDir) {
		File iF = new File(inputPath);
		File oF = new File(baseDir + iF.getName());
		if (iF.equals(oF))
			return;
		FileInputStream iS = null;
		FileOutputStream oS = null;
		try {
			oF.createNewFile();
			iS = new FileInputStream(iF);
			oS = new FileOutputStream(oF);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = iS.read(buffer)) > 0) {
				oS.write(buffer, 0, length);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				iS.close();
				oS.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return the bounds
	 */
	public int getWidth() {
		return width;
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

	public void setLandscapeImage(BufferedImageReference image) {
		landscapeImage = image;
		if (image.getImage() != null)
			width = image.getImage().getWidth();
		else
			width = 0;
	}

	public void setTargetArea(ObjectPrototype o) {
		targetArea = o;
	}
}
