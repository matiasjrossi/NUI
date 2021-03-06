/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ar.edu.unicen.nui.views.gl;

import ar.edu.unicen.nui.model.Tile;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;
import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;

/**
 *
 * @author matias
 */
public class GLRenderer implements GLEventListener {
    
    private BufferedImage lastCapturedFrame;
    private ArrayList<BufferedImage> lastAvailableFrames;
    private Texture waitingForCameraTexture, noCameraTexture;
//    private Marker[] detectedMarkers;
    private HashMap<Integer, Color> colors;
    private HashSet<Tile> tiles;
    private Mode mode;
    private int width, height;
    
    public enum Mode {WAITING_FOR_DEVICE, DEVICE_SELECTOR, NO_DEVICES_AVAILABLE, READY}; 

    public GLRenderer() {
        mode = Mode.WAITING_FOR_DEVICE;
        colors = new HashMap<Integer, Color>();
    }

    /* Public interface */
    public void setMode(Mode mode) {
        this.mode = mode;
    }
    
    public void setLastCapturedFrame(BufferedImage lastCapturedFrame) {
        this.lastCapturedFrame = lastCapturedFrame;
    }

    public void setLastAvailableFrames(ArrayList<BufferedImage> lastAvailableFrames) {
        this.lastAvailableFrames = lastAvailableFrames;
    }
    
    public void setTiles(Collection<Tile> tiles) {
        this.tiles = new HashSet<Tile>(tiles);
    }
    
//    public void setDetectedMarkers(Marker[] detectedMarkers) {
//        this.detectedMarkers = detectedMarkers;
//    }
    
    
    /* Internal Events */
    @Override
    public void init(GLAutoDrawable glad) {
        GL2 gl2 = glad.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glLoadIdentity();
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glLoadIdentity();
        try {
            waitingForCameraTexture = AWTTextureIO.newTexture(gl2.getGLProfile(), ImageIO.read(getClass().getResource("resources/waiting-for-camera.png")), false);
            noCameraTexture = AWTTextureIO.newTexture(gl2.getGLProfile(), ImageIO.read(getClass().getResource("resources/no-camera.png")), false);
        } catch (IOException ex) {
            Logger.getLogger(GLRenderer.class.getName()).log(Level.SEVERE, "Failed to load textures", ex);
        }
//        gl2.glScalef(-1.0f, 1.0f, 1.0f); // Mirror
    }

    @Override
    public void dispose(GLAutoDrawable glad) {
        if (waitingForCameraTexture != null) {
            waitingForCameraTexture.destroy(glad.getGL().getGL2());
        }
    }
    
    @Override
    public void reshape(GLAutoDrawable glad, int x, int y, int width, int height) {
        glad.getGL().getGL2().glViewport(0, 0, width, height);
        this.width = width;
        this.height = height;
    }

    @Override
    public void display(GLAutoDrawable glad) {
        GL2 gl2 = glad.getGL().getGL2();
        gl2.glClear(GL2.GL_DEPTH_BUFFER_BIT | GL2.GL_COLOR_BUFFER_BIT);
        
        switch (mode) {
            case WAITING_FOR_DEVICE:
                renderMessageFromTexture(glad, waitingForCameraTexture);
                break;
            case DEVICE_SELECTOR:
                break;
            case NO_DEVICES_AVAILABLE:
                renderMessageFromTexture(glad, noCameraTexture);
                break;
            case READY:
                // Set projection
                gl2.glMatrixMode(GL2.GL_PROJECTION);
                gl2.glPushMatrix();
                float horizontalScale = Math.max(
                        ((float) width * (float) lastCapturedFrame.getHeight()) / 
                        ((float) height * (float) lastCapturedFrame.getWidth())
                    , 1.0f);;
                float verticalScale = Math.max(
                        ((float) height * (float) lastCapturedFrame.getWidth()) / 
                        ((float) width * (float) lastCapturedFrame.getHeight())
                    , 1.0f);;
                gl2.glOrtho(-horizontalScale, horizontalScale, -verticalScale, verticalScale, -1.0, 1.0);
                
                // Render capture
                renderBackgroundImage(glad);
                // Render overlays
                renderTiles(glad);
                // Restore projection
                gl2.glMatrixMode(GL2.GL_PROJECTION);
                gl2.glPopMatrix();
                // Picken-pack
                drawTeapot(glad);
                break;
        }
    }

    /***************/
    
    /* Rendering methods */
    private void renderMessageFromTexture(GLAutoDrawable glad, Texture texture) {
        GL2 gl2 = glad.getGL().getGL2();
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();
        gl2.glLoadIdentity();
        drawTexture(gl2, texture, false);
        gl2.glPopMatrix();
    }
    
    private void renderBackgroundImage(GLAutoDrawable glad) {
        GL2 gl2 = glad.getGL().getGL2();
        Texture backgroundTexture;
        backgroundTexture = AWTTextureIO.newTexture(gl2.getGLProfile(), lastCapturedFrame, false);
        drawTexture(gl2, backgroundTexture, true);
        backgroundTexture.destroy(gl2);
    }
    
    private void renderTiles(GLAutoDrawable glad) {
        GL2 gl2 = glad.getGL().getGL2();
        // Reset transformations
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();
        // invert Y axis sign to account for image coordinates to model coordinates
        gl2.glTranslatef(-1.0f, -1.0f * -1.0f, 0.0f);
        gl2.glScalef(2.0f/(float)width, -1.0f * 2.0f/(float)height, 1.0f);
        
        // Set coloring
        gl2.glPushAttrib(
                GL2.GL_LIGHTING_BIT |
                GL2.GL_COLOR_BUFFER_BIT |
                GL2.GL_CURRENT_BIT | 
                GL2.GL_DEPTH_BUFFER_BIT |
                GL2.GL_ENABLE_BIT);
        gl2.glDisable(GL2.GL_LIGHTING);

//        for (Marker marker: detectedMarkers) {
//            if (!colors.containsKey(marker.id))
//                colous.put(marker.id, new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
//            gl2.glColor3fv(colors.get(marker.id).getComponents(null), 0);
//
//            gl2.glBegin(GL2.GL_LINE_STRIP);
//
//            gl2.glColor3fv(new float[]{1.0f, 0.0f, 0.0f}, 0);
//            gl2.glVertex3d(marker.corners[0], marker.corners[1], 1.0d);
//            gl2.glVertex3d(marker.corners[2], marker.corners[3], 1.0d);
//
//            gl2.glColor3fv(colours.get(marker.id), 0);
//            gl2.glVertex3d(marker.corners[4], marker.corners[5], 1.0d);
//            gl2.glVertex3d(marker.corners[6], marker.corners[7], 1.0d);
//            gl2.glVertex3d(marker.corners[0], marker.corners[1], 1.0d);
//            gl2.glEnd();
//        }
        
        for (Tile tile: tiles) {
            if (!colors.containsKey(tile.getId()))
                colors.put(tile.getId(), new Color((float)Math.random(), (float)Math.random(), (float)Math.random()));
            gl2.glColor3fv(colors.get(tile.getId()).getComponents(null), 0);

            gl2.glBegin(GL2.GL_LINES);            
            gl2.glVertex3f(tile.getCenterX(), tile.getCenterY(), 1.0f);
            gl2.glVertex3f(tile.getCenterX() + tile.getUpX(), tile.getCenterY() + tile.getUpY(), 1.0f);
            gl2.glEnd();
            
            TextRenderer renderer = new TextRenderer(new Font("SansSerif", Font.BOLD, 16));
            renderer.beginRendering(glad.getWidth(), glad.getHeight());
            renderer.setColor(colors.get(tile.getId()));
            renderer.draw(Double.toString(tile.getAngle()), (int) (tile.getCenterX() + tile.getUpX()), glad.getHeight() - (int) (tile.getCenterY() + tile.getUpY()));
            renderer.draw("(" + tile.getId() + ")", (int) tile.getCenterX(), glad.getHeight() - (int) tile.getCenterY());
            renderer.endRendering();
        }

        gl2.glPopMatrix();
        gl2.glPopAttrib();

    }
    
    private void drawTexture(GL2 gl2, Texture texture, boolean stretch) {        
        // Reset transformations
//        gl2.glMatrixMode(GL2.GL_MODELVIEW);
//        gl2.glPushMatrix();
        
        // Prepare texture
        texture.enable(gl2);
        texture.bind(gl2);
        
        float horizontalRatio = 1.0f;
        float verticalRatio = 1.0f;
        if (!stretch) {
            horizontalRatio = (float) texture.getWidth() / (float) width;
            verticalRatio = (float) texture.getHeight() / (float) height;
        }
        
        // Render the texture
        gl2.glBegin(GL2.GL_QUADS);
        gl2.glTexCoord2f(0.0f, 1.0f);
        gl2.glVertex3f(-horizontalRatio, -verticalRatio, 1.0f);
        gl2.glTexCoord2f(1.0f, 1.0f);
        gl2.glVertex3f(horizontalRatio, -verticalRatio, 1.0f);
        gl2.glTexCoord2f(1.0f, 0.0f);
        gl2.glVertex3f(horizontalRatio, verticalRatio, 1.0f);
        gl2.glTexCoord2f(0.0f, 0.0f);
        gl2.glVertex3f(-horizontalRatio, verticalRatio, 1.0f);
        gl2.glEnd();
        
        // Restore state
        texture.disable(gl2);
//        gl2.glPopMatrix();
    }
    
    private void drawTeapot(GLAutoDrawable glad) {
        GL2 gl2 = glad.getGL().getGL2();
        // Set projection
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glPushMatrix();
        float hor = Math.max((float)width / (float)height, 1.0f);
        float ver = Math.max((float)height / (float)width, 1.0f);
        gl2.glOrtho(-hor, hor, -ver, ver, 1.0, 10.0);
        
        // Set transformations
        gl2.glMatrixMode(GL2.GL_MODELVIEW);
        gl2.glPushMatrix();
//        gl2.glLoadIdentity(); // remove mirror
        // Push the teapot back into the frustum
        gl2.glTranslatef(0.0f, 0.0f, -2.0f);
        // Position the object in the screen
        gl2.glTranslatef(0.9f * hor, -0.9f * ver, 0.0f);
        
        // Set coloring
        gl2.glPushAttrib(
                GL2.GL_LIGHTING_BIT |
                GL2.GL_COLOR_BUFFER_BIT |
                GL2.GL_CURRENT_BIT | 
                GL2.GL_DEPTH_BUFFER_BIT |
                GL2.GL_ENABLE_BIT);
        gl2.glShadeModel(GL2.GL_SMOOTH);
        gl2.glEnable(GL2.GL_DEPTH_TEST);
        
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_AMBIENT, new float[]{0.24725f, 0.1995f, 0.0745f, 1.0f}, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_DIFFUSE, new float[]{0.75164f, 0.60648f, 0.22648f, 1.0f}, 0);
        gl2.glMaterialfv(GL2.GL_FRONT_AND_BACK, GL2.GL_SPECULAR, new float[]{0.628281f, 0.555802f, 0.366065f, 1.0f}, 0);
        gl2.glMaterialf(GL2.GL_FRONT_AND_BACK, GL2.GL_SHININESS, 51.2f);
        
        // Set lighting
        gl2.glEnable(GL2.GL_LIGHTING);
        gl2.glEnable(GL2.GL_LIGHT0);        
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, new float[]{0.5f, 1.0f, 1.5f, 1.0f}, 0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, new float[]{0.3f, 0.3f, 0.3f}, 0);
        gl2.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, new float[]{0.5f, 0.5f, 0.5f}, 0);

        // Render the teapot
        GLUT glut = new GLUT();
        glut.glutSolidTeapot(0.1d);

        // Restore state
        gl2.glPopAttrib();
        gl2.glPopMatrix();
        gl2.glMatrixMode(GL2.GL_PROJECTION);
        gl2.glPopMatrix();
    }

}
