package scene;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import geometries.Geometries;
import geometries.Sphere;
import geometries.Triangle;
import lighting.AmbientLight;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import primitives.Color;
import primitives.Point;

public class SceneBuilder {

    /**
     * Loads a scene from an XML file.
     * @param file The XML file containing the scene definition.
     * @return The loaded Scene object.
     * @throws ParserConfigurationException If a parser configuration error occurs.
     * @throws SAXException If an XML parsing error occurs.
     * @throws IOException If an I/O error occurs.
     */
    public static Scene loadSceneFromFile(File file) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();

        // Extract Scene Elements
        Element sceneElement = document.getDocumentElement();

        // Get the background color
        String[] bgColorComponents = sceneElement.getAttribute("background-color").split(" ");
        Color backgroundColor = new Color(
                Integer.parseInt(bgColorComponents[0]),
                Integer.parseInt(bgColorComponents[1]),
                Integer.parseInt(bgColorComponents[2])
        );

        // Get the ambient light
        Element ambientElement = (Element) sceneElement.getElementsByTagName("ambient-light").item(0);
        String[] ambientColorComponents = ambientElement.getAttribute("color").split(" ");
        AmbientLight ambientLight = new AmbientLight(
                new Color(
                        Integer.parseInt(ambientColorComponents[0]),
                        Integer.parseInt(ambientColorComponents[1]),
                        Integer.parseInt(ambientColorComponents[2])
                )
        );

        // Get the geometries
        Geometries geometries = new Geometries();
        NodeList spheres = sceneElement.getElementsByTagName("sphere");
        for (int i = 0; i < spheres.getLength(); i++) {
            Element sphereElement = (Element) spheres.item(i);
            Point center = parsePoint(sphereElement.getAttribute("center"));
            double radius = Double.parseDouble(sphereElement.getAttribute("radius"));
            geometries.add(new Sphere(radius, center));
        }

        NodeList triangles = sceneElement.getElementsByTagName("triangle");
        for (int i = 0; i < triangles.getLength(); i++) {
            Element triangleElement = (Element) triangles.item(i);
            Point p0 = parsePoint(triangleElement.getAttribute("p0"));
            Point p1 = parsePoint(triangleElement.getAttribute("p1"));
            Point p2 = parsePoint(triangleElement.getAttribute("p2"));
            geometries.add(new Triangle(p0, p1, p2));
        }

        // Create and return the Scene
        return new Scene("XML Scene", backgroundColor, ambientLight, geometries);
    }

    /**
     * Parses a 3D point from a space-separated string.
     * @param attribute The string containing the point coordinates.
     * @return The parsed Point object.
     */
    private static Point parsePoint(String attribute) {
        String[] coords = attribute.split(" ");
        return new Point(
                Double.parseDouble(coords[0]),
                Double.parseDouble(coords[1]),
                Double.parseDouble(coords[2])
        );
    }
}
