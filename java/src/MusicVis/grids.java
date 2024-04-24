package MusicVis;

public class grids {
    int lastColorChange = 0;
    int colorChangeInterval = 1500;
    int currentColor, nextColor;
    MyVisual mv;
    public grids(MyVisual mv) {
        this.mv = mv;
    }


    void render() {
        mv.pushStyle();
        mv.hint(mv.DISABLE_DEPTH_TEST);

        mv.stroke(0);
        defineLights();
        
        if (mv.millis() - lastColorChange > colorChangeInterval) {
            currentColor = nextColor;
            nextColor = mv.color(mv.random(70,500), mv.random(70,500), mv.random(70,500));
            lastColorChange = mv.millis();
        }

        float amt = (float)(mv.millis() - lastColorChange) / colorChangeInterval;

        for (int x = 0; x <= mv.width; x += 60) {
            for (int y = 0; y <= mv.height; y += 60) {
                mv.pushMatrix();
                mv.translate(x, y);
                mv.rotateY(mv.map(mv.mouseX, 0, mv.width, 0, mv.PI));
                mv.rotateX(mv.map(mv.mouseY, 0, mv.height, 0, mv.PI));
                mv.box(90);
                mv.fill(mv.lerpColor(currentColor, nextColor, amt));
                mv.popMatrix();
            }
        }
        mv.hint(mv.ENABLE_DEPTH_TEST);
        mv.popStyle();
    }

    
    void defineLights() {
        mv.ambientLight(0, 0, 100);
        // Orange point light on the right
        mv.pointLight(150, 100, 0,   // Color
                   200, -150, 0); // Position
    
        // Blue directional light from the left
        mv.directionalLight(0, 102, 255, // Color
                         1, 0, 0);    // The x-, y-, z-axis direction
    
        // Yellow spotlight from the front
        mv.spotLight(255, 255, 109,  // Color
                0f, 40f, 200f,     // Position
                0f, -0.5f,-0.5f,  // Direction
                (float) (mv.PI / 2), 2f);     // Angle, concentration
        }
}
