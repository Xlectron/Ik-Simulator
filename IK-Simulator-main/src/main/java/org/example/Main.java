package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main extends JFrame {

    Panel panel = new Panel();
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main().setVisible(true);
            }
        });
    }

    public Main() {
        initComponents();
    }

    public void initComponents() {
        this.setPreferredSize(new Dimension(panel.width,panel.height));
        this.add(panel);
        this.pack();
        this.setVisible(true);
    }
}

class Panel extends JPanel {
    int x = 0;
    int y = 0;
    int width = 1800;
    int height = 1300;
    int offset = width/2;


    ArmModel armModel = new ArmModel();
    double LOWERARMSTARTANGLE = Math.toRadians(armModel.LOWERARMSTARTANGLE);
    double UPPERARMSTARTANGLE = Math.toRadians(armModel.UPPERARMSTARTANGLE);
    public Panel(){
        this.setVisible(true);
        this.addMouseMotionListener(new MouseAdapter() {
            public void mouseMoved(MouseEvent e) {
                x = e.getX();
                y = e.getY();
                repaint();
            }
        });
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        g.setColor(Color.red);

        int adjX = x-this.getX()-offset;
        int adjY = (this.getHeight()/2)-y;
        double[] angles = armModel.calculateMotorPositions(adjX, adjY);
        g.drawLine(this.getX()+offset,(this.getHeight()/2),x,y);
        int ex = (int) (Math.cos(armModel.encoderToRadians(angles[0], false)+ LOWERARMSTARTANGLE)*armModel.LOWERARMLENGTH);
        int ey = (int) (Math.sin(armModel.encoderToRadians(angles[0], false)+ LOWERARMSTARTANGLE)*armModel.LOWERARMLENGTH);
        g.setColor(new Color(0,0,0));
        //((Graphics2D)g).setStroke(new Stroke(5));
        g.drawLine(this.getX()+offset,(this.getHeight()/2),ex+this.getX()+offset,(this.getHeight()/2)-ey);
        int wx = (int) (Math.cos(armModel.encoderToRadians(angles[1], true)+UPPERARMSTARTANGLE)*armModel.UPPERARMLENGTH);
        int wy = (int) (Math.sin(armModel.encoderToRadians(angles[1], true)+UPPERARMSTARTANGLE)*armModel.UPPERARMLENGTH);
        g.drawLine(ex+this.getX()+offset,(this.getHeight()/2)-ey,ex+wx+this.getX()+offset,(this.getHeight()/2)-wy-ey);
        g.drawLine(ex+wx+this.getX()+offset,(this.getHeight()/2)-wy-ey,
                ex+wx+this.getX()+offset  + (int)(Math.cos(armModel.encoderToRadians(angles[1], true)+UPPERARMSTARTANGLE + angles[2]) * 30),
                (this.getHeight()/2)-wy-ey - (int)(Math.sin(armModel.encoderToRadians(angles[1], true)+UPPERARMSTARTANGLE + angles[2]) * 30)
        );
        System.out.println(angles[0] + " " + angles[1]);
    }
}