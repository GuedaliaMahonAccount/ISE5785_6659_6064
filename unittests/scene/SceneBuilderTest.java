package scene;

import lighting.AmbientLight;
import org.junit.jupiter.api.Test;
import primitives.Color;

import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

public class SceneBuilderTest {

    @Test
    public void testLoadSceneFromFile() throws Exception {
        File file = new File("unittests/scene/renderTestTwoColors.xml");
        Scene scene = SceneBuilder.loadSceneFromFile(file);
        assertNotNull(scene);
        assertEquals("XML Scene", scene.getName());
        assertEquals(new Color(75, 127, 190), scene.getBackground());
        assertEquals(new AmbientLight(new Color(255, 191, 191)), scene.getAmbientLight());
        assertFalse(scene.getGeometries().isEmpty());
    }
}
