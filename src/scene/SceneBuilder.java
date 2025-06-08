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

/**
 * Utility class for building Scene objects from XML definitions.
 */
public class SceneBuilder {

    /**
     * Parses and constructs a Scene from the given XML file.
     *
     * @param file XML file containing the scene definition
     * @return the constructed Scene instance
     * @throws ParserConfigurationException if a parser cannot be created
     * @throws SAXException                 if XML parsing fails
     * @throws IOException                  if an I/O error occurs while reading the file
     */
    public static Scene loadSceneFromFile(File file)
            throws ParserConfigurationException, SAXException, IOException {
        // Set up XML parser
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(file);
        document.getDocumentElement().normalize();

        // Root element: <scene>
        Element sceneElement = document.getDocumentElement();

        // Parse background color attribute ("R G B")
        String[] bgColorComponents = sceneElement
                .getAttribute("background-color").split(" ");
        Color backgroundColor = new Color(
                Integer.parseInt(bgColorComponents[0]),
                Integer.parseInt(bgColorComponents[1]),
                Integer.parseInt(bgColorComponents[2])
        );

        // Parse ambient light element <ambient-light color="R G B"/>
        Element ambientElement =
                (Element) sceneElement.getElementsByTagName("ambient-light").item(0);
        String[] ambientColorComponents = ambientElement
                .getAttribute("color").split(" ");
        AmbientLight ambientLight = new AmbientLight(
                new Color(
                        Integer.parseInt(ambientColorComponents[0]),
                        Integer.parseInt(ambientColorComponents[1]),
                        Integer.parseInt(ambientColorComponents[2])
                )
        );

        // Parse geometry elements
        Geometries geometries = new Geometries();

        // -- Parse <sphere center="x y z" radius="r" /> --
        NodeList spheres = sceneElement.getElementsByTagName("sphere");
        for (int i = 0; i < spheres.getLength(); i++) {
            Element sphereElement = (Element) spheres.item(i);
            Point center = parsePoint(sphereElement.getAttribute("center"));
            double radius = Double.parseDouble(sphereElement.getAttribute("radius"));
            geometries.add(new Sphere(center, radius));
        }

        // -- Parse <triangle p0="x y z" p1="x y z" p2="x y z" /> --
        NodeList triangles = sceneElement.getElementsByTagName("triangle");
        for (int i = 0; i < triangles.getLength(); i++) {
            Element triangleElement = (Element) triangles.item(i);
            Point p0 = parsePoint(triangleElement.getAttribute("p0"));
            Point p1 = parsePoint(triangleElement.getAttribute("p1"));
            Point p2 = parsePoint(triangleElement.getAttribute("p2"));
            geometries.add(new Triangle(p0, p1, p2));
        }

        // Build and return the Scene
        return new Scene("XML Scene", backgroundColor, ambientLight, geometries);
    }

    /**
     * Converts a space-separated coordinate string into a Point.
     *
     * @param attribute space-delimited coordinates "x y z"
     * @return Point instance at the specified coordinates
     * @throws NumberFormatException if the string cannot be parsed as doubles
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
