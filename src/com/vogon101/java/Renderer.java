package com.vogon101.java;

import com.colofabrix.scala.geometry.shapes.Circle;
import com.colofabrix.scala.tankwar.Bullet;
import com.colofabrix.scala.tankwar.Tank;
import com.colofabrix.scala.tankwar.World;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import scala.collection.mutable.ListBuffer;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by Freddie on 15/03/2015.
 */
public class Renderer {

    private double width = 0d, height = 0d;

    private String title = "TankWar 2.0";

    private World world;

    public Renderer(World world) {
        this.world = world;

        this.width = world.arena().width();
        this.height = world.arena().height();


        try {
            initGL();
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
        setCamera();
    }

    public void update() {
        setCamera();
        drawBG();
        drawTanks();
        drawBullets();

        Display.sync(20);

        Display.update();
    }

    private void initGL() throws LWJGLException {
        Display.setDisplayMode(new DisplayMode((int) width, (int) height));
        Display.create();
        Display.setTitle(title);
    }

    private void drawBG() {
        glPushMatrix();
        {
            glTranslated(0, 0, 0);
            glColor3d(0.5, 1, 0.3);
            glBegin(GL_QUADS);
            {
                glVertex2d(0, 0);
                glVertex2d(0, height);
                glVertex2d(width, height);
                glVertex2d(width, 0);
            }
            glEnd();
        }
        glPopMatrix();
    }

    private void cleanUp() {
        System.out.println("EXIT");
        Display.destroy();
        System.exit(0);
    }

    private void setCamera() {
        glClearColor(1f, 1f, 1f, 1.0f);
        // Clear
        glClear(GL_COLOR_BUFFER_BIT);
        // Modify projection matrix - 2d projection
        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, width, 0, height, -1, 1);

        // Modify modelview matrix
        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();

        GL11.glViewport(0, 0, (int) width, (int) height);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        glLoadIdentity();

    }

    private void drawTanks() {

        ListBuffer<Tank> tanks = world.tanks();

        for (int i = 0; i < tanks.length(); i++) {
            drawTank(tanks.apply(i));
        }


    }

    private void drawTank(Tank tank) {
        if (tank.isDead())
            return;

        glPushMatrix();
        {


            glTranslated(tank.position().x(), tank.position().y(), 0);
            glColor3d(1, 0, 0);

            double size = ((Circle) tank.boundary()).radius();


            glRotated(tank.rotation().t() * 57.2957795, 0, 0, 1);
            glBegin(GL_QUADS);
            {
                glVertex2d(-size, -size);
                glVertex2d(-size, size);
                glVertex2d(size, size);
                glVertex2d(size, -size);
            }
            glEnd();
            glBegin(GL_QUADS);
            {
                glVertex2d(-2, 2);
                glVertex2d(-10, 10);
                glVertex2d(20, 20);
                glVertex2d(20, -10);
            }
            glEnd();


        }
        glPopMatrix();

    }

    private void drawBullets() {

        ListBuffer<Bullet> bullets = world.bullets().clone();

        for (int i = 0; i < bullets.size(); i++) {
            drawBullet(bullets.apply(i));
        }

    }

    private void drawBullet(Bullet bullet) {
        glPushMatrix();
        {
            glTranslated(bullet.position().x(), bullet.position().y(), 0);
            //glRotated(50, 1, 0, 0);
            glColor3d(0, 0, 1);
            glBegin(GL_QUADS);
            {
                glVertex2d(-5, -5);
                glVertex2d(-5, 5);
                glVertex2d(5, 5);
                glVertex2d(5, -5);
            }
            glEnd();

        }
        glPopMatrix();
    }


}
