package com.vogon101.Graphics;

import com.colofabrix.scala.geometry.shapes.Circle;
import com.colofabrix.scala.math.Vector2D;
import com.colofabrix.scala.tankwar.Bullet;
import com.colofabrix.scala.tankwar.Tank;
import com.colofabrix.scala.tankwar.World;
import com.colofabrix.scala.tankwar.integration.TankEvaluator;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import scala.collection.mutable.ListBuffer;

/**
 * Temporary class
 *
 * Created by Freddie on 15/03/2015.
 */
@Deprecated
public class OldRenderer {

    private double width = 0d, height = 0d;

    private World world;

    public OldRenderer(World world) {
        this.world = world;

        this.width = world.arena().width();
        this.height = world.arena().height();

        try {
            initGL();
        } catch( LWJGLException e ) {
            e.printStackTrace();
        }

        setCamera();
    }

    public void update() {
        setCamera();
        drawBG();
        drawTanks();
        drawBullets();

        Display.sync(25);

        Display.update();
    }

    private void initGL() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode((int) width, (int) height));
        Display.create();
        Display.setTitle("TankWar");
    }

    private void drawBG() {
        GL11.glPushMatrix();
        {
            GL11.glTranslated(0, 0, 0);
            //glColor3d(0.5, 1, 0.3);   // Freddie's colours
            GL11.glColor3d(0, 0, 0);
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2d(0, 0);
                GL11.glVertex2d(0, height);
                GL11.glVertex2d(width, height);
                GL11.glVertex2d(width, 0);
            }
            GL11.glEnd();
        }
        GL11.glPopMatrix();
    }

    private void setCamera() {
        GL11.glClearColor(1f, 1f, 1f, 1.0f);
        // Clear
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        // Modify projection matrix - 2d projection
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, width, 0, height, -1, 1);

        // Modify model view matrix
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();

        GL11.glViewport(0, 0, (int) width, (int) height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glLoadIdentity();
    }

    private void drawTanks() {
        ListBuffer<Tank> tanks = world.tanks();

        for( int i = 0; i < tanks.length(); i++ ) {
            Tank tank = tanks.apply(i);

            if( !tank.isDead() )
                drawTank(tank);
        }
    }

    private void drawTank(Tank tank) {
        double size = ((Circle) tank.boundary()).radius();
        double fitness = new TankEvaluator().getFitness(tank, null);

        GL11.glPushMatrix();
        {
            // Translate the reference frame
            GL11.glTranslated(tank.position().x(), tank.position().y(), 0);
            // Rotate the reference frame
            GL11.glRotated(tank.rotation().t() * 180 / Math.PI, 0, 0, 1);
            // Set a color for the reference frame
            GL11.glColor3d(1, 2 * fitness / TankEvaluator.higherFitness(world), 0);

            GL11.glBegin(GL11.GL_TRIANGLES);
            {
                GL11.glVertex2d(size, 0.0);
                GL11.glVertex2d(-0.866025 * size, 0.5 * size);
                GL11.glVertex2d(-0.866025 * size, -0.5 * size);
            }
            GL11.glEnd();

            drawCircle(new Circle(Vector2D.origin(), size));

            // Set a color for the reference frame
            GL11.glColor3d(0.1, 0.1, 0.1);
            double sight = world.max_sight();
            drawCircle(new Circle(Vector2D.origin(), sight));
        }
        GL11.glPopMatrix();
    }

    private void drawBullets() {
        ListBuffer<Bullet> bullets = world.bullets().clone();

        for( int i = 0; i < bullets.size(); i++ )
            drawBullet(bullets.apply(i));
    }

    private void drawBullet(Bullet bullet) {
        double size = ((Circle)bullet.boundary()).radius();

        GL11.glPushMatrix();
        {
            GL11.glTranslated(bullet.position().x(), bullet.position().y(), 0);
            //glRotated(50, 1, 0, 0);
            GL11.glColor3d(0, 0, 1);
            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2d(-size, -size);
                GL11.glVertex2d(-size, size);
                GL11.glVertex2d(size, size);
                GL11.glVertex2d(size, -size);
            }
            GL11.glEnd();
        }
        GL11.glPopMatrix();
    }

    //

    /**
     * Draws an approximate Circle Circle
     *
     * Draws a circle as a regular polygon with a specific number of edges
     * Ref: http://slabode.exofire.net/circle_draw.shtml
     *
     * @param circle The circle to draw
     */
    private void drawCircle(Circle circle)
    {
        int numSegments = (int)(circle.radius() * 2.0 * Math.PI);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        for( int i = 0; i < numSegments; i++ )
        {
            double theta = 2.0 * Math.PI * (double)i / (double)numSegments;

            double x = circle.radius() * Math.cos( theta );
            double y = circle.radius() * Math.sin( theta );

            GL11.glVertex2f((float) (x + circle.center().x()), (float) (y + circle.center().y()));
        }
        GL11.glEnd();
    }
}
