package c22376553;

import MusicVis.MyVisual;

public class wiredimension {
    MyVisual mv;
    int rows = 5000 / 30;
    int cols = 2000 / 30;
    float[][] terrain = new float[cols][rows];
    float smoothAmplitude = 0;

    public wiredimension(MyVisual mv) {
        this.mv = mv;
        smoothAmplitude = mv.random(10);
        createSlits();
    }

    float lerpedBuffer[] = new float[1024];
    float smoothedAmplitude = 0;

    // RGB colors
    int[] sunColors = {
            mv.color(212, 202, 11),
            mv.color(214, 198, 30),
            mv.color(211, 170, 26),
            mv.color(216, 157, 51),
            mv.color(217, 124, 64),
            mv.color(213, 104, 81),
            mv.color(212, 51, 98),
            mv.color(215, 29, 121),
            mv.color(217, 11, 139),
            mv.color(217, 0, 151)
    };

    int bgColor = mv.color(0, 0, 30);
    float sunRadius = 500;
    float topSlitY;
    Rectangle[] slits;

    class Rectangle {
        float x, y, w, h;

        Rectangle(float y, float h) {
            this.x = mv.width / 2 - sunRadius;
            this.w = 2 * sunRadius;
            this.y = y;
            this.h = h;
        }

        void render() {
            lerpedBuffer = new float[mv.width];
            mv.fill(bgColor);
            mv.rect(mv.width / 2 - sunRadius, y, w, h);
        }

        void update() {
            y -= smoothedAmplitude * 100;

            if (y < topSlitY) {
                y = 1080 / 2 + sunRadius;
            }

            h = mv.map(y, topSlitY + 150, 1080 / 2 + sunRadius, 1.0f, 40.0f);
        }
    }

    public void createSlits() {
        topSlitY = mv.height / 2 - (sunRadius / 4);

        slits = new Rectangle[6];

        float y = topSlitY + 1000;
        float h = 1.0f;
        for (int i = 0; i < slits.length; i++) {
            slits[i] = new Rectangle(y, h);

            y += ((mv.height / 2 + sunRadius) - topSlitY) / slits.length;
            h = mv.map(y, topSlitY, mv.height / 2 + sunRadius, 1.0f, 40.0f);
        }
    }

    public void drawSun(float sum) {
        mv.background(bgColor);

        mv.noStroke();
        mv.fill(sunColors[0]);
        mv.ellipse(mv.width / 2, mv.height / 2, 2 * sunRadius, 2 * sunRadius);

        mv.loadPixels();

        for (int i = 0; i < mv.pixels.length; i++) {

            if (mv.pixels[i] == sunColors[0]) {
                int y = i / mv.width;

                float step = mv.map(y, mv.height / 2 - sunRadius, mv.height / 2 + sunRadius, 0, 1.0f);

                mv.pixels[i] = interpolateColor(sunColors, step);
            }

        }

        mv.updatePixels();

        for (Rectangle r : slits) {
            r.update();
            r.render();
        }
    }

    public int interpolateColor(int[] arr, float step) {
        int sz = arr.length;

        if (sz == 1 || step <= 0.0f) {
            return arr[0];
        } else if (step >= 1.0f) {
            return arr[sz - 1];
        } else {
            float f = step * (sz - 1);
            int i = (int) f;
            float p = f - i;
            return mv.lerpColor(arr[i], arr[i + 1], p);
        }
    }

    public void createTerrain(float yoff) {
        for (int y = 0; y < cols; y++) {
            float xoff = 0;
            for (int x = 0; x < 25; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 150);
                xoff += 0.4f;
            }
            for (int x = 26; x < 45; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 150);
                xoff += 0.4f;
            }
            for (int x = 45; x < 50; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 80);
                xoff += 0.4f;
            }
            for (int x = 51; x < 56; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 25);
                xoff += 0.4f;
            }
            for (int x = 57; x < 61; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 80);
                xoff += 0.4f;
            }
            for (int x = 61; x < 80; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 150);
                xoff += 0.4f;
            }
            for (int x = 80; x < rows; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 150);
                xoff += 0.4f;
            }
            yoff += 0.4f;
        }

        for (int y = 0; y < 10; y++) {
            float xoff = 0;
            for (int x = 0; x < rows; x++) {
                terrain[y][x] = mv.map(mv.noise(xoff, yoff), 0, 1, -30, 100);
                xoff += 0.4f;
            }
        }
    }

    public void drawTerrain() {
        for (int y = 10; y < cols - 1; y++) {
            mv.beginShape(mv.TRIANGLE_STRIP);
            for (int x = 0; x < rows; x++) {
                mv.vertex(x * 30, y * 30, terrain[y][x]);
                mv.vertex(x * 30, (y + 1) * 30, terrain[y + 1][x]);
            }
            mv.endShape();
        }
    }

    float flying = 0;
    float enlarge = 0;

    public void render() {
        mv.background(bgColor);

        float sum = 0;

        for (int i = 0; i < mv.getAudioPlayer().mix.size(); i++) {
            sum += Math.abs(mv.getAudioPlayer().mix.get(i));
            lerpedBuffer[i] = mv.lerp(lerpedBuffer[i], mv.getAudioPlayer().mix.get(i), 0.1f);
        }

        enlarge += 0.5f;

        float zoff = enlarge;

        mv.beginShape();
        mv.translate(0, 0, -500);
        drawSun(sum);
        mv.endShape();

        mv.beginShape();
        flying -= smoothedAmplitude;

        float yoff = flying;

        createTerrain(yoff);

        mv.translate(mv.width / 6 + 10, (mv.height / 2) + 70);
        mv.rotateX(mv.PI / 2);
        mv.translate(-mv.width / 2, -mv.height / 2);
        mv.fill(0, 0, 20);
        mv.stroke(255, 20, 147);
        mv.strokeWeight(1);
        drawTerrain();
    }

    public float[] getLerpedBuffer() {
        return lerpedBuffer;
    }

    public void setLerpedBuffer(float[] lerpedBuffer) {
        this.lerpedBuffer = lerpedBuffer;
    }
}
