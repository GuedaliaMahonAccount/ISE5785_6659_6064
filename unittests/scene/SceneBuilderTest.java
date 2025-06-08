package scene;

import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link SceneBuilder} to verify XML-based scene loading.
 */
public class SceneBuilderTest {

    /**
     * Tests loading a scene from an XML file containing two background colors.
     * Verifies that the scene is not null, has the expected name "XML Scene",
     * correct background color, correct ambient light color, and non-empty geometries.
     *
     * @throws Exception if file parsing or I/O fails
     */
    @Test
    public void testLoadSceneFromFile() throws Exception {
        // Arrange: specify the XML file path for the test scene
        File file = new File("unittests/scene/renderTestTwoColors.xml");

        // Act: load the scene using SceneBuilder
        Scene scene = SceneBuilder.loadSceneFromFile(file);

        // Assert: scene object should be created
        assertNotNull(scene, "Scene should be loaded and not null");

        // Assert: verify scene name matches expected default
        assertEquals("XML Scene", scene.getName(), "Scene name should match 'XML Scene'");

        // Assert: verify background color parsed correctly
        assertEquals(new Color(75, 127, 190), scene.getBackground(),
                "Background color should match values from XML");

        // Assert: verify ambient light color parsed correctly
        assertEquals(new AmbientLight(new Color(255, 191, 191)), scene.getAmbientLight(),
                "Ambient light color should match values from XML");

        // Assert: geometries collection should not be empty after parsing
        assertFalse(scene.getGeometries().isEmpty(),
                "Geometries list should contain parsed objects");
    }
}
