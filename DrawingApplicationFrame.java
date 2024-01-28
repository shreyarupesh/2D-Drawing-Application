/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package java2ddrawingapplication;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

/**
*
* @author acv
*/
public class DrawingApplicationFrame extends JFrame
{


    private JPanel firstPanel = new JPanel();
    private JPanel secondPanel = new JPanel();
    private JPanel topPanel = new JPanel();
            
    private DrawPanel drawPanel = new DrawPanel();
    
    private JLabel shape = new JLabel("Shape:");
    private JComboBox shapeType = new JComboBox();
    private JButton firstColor = new JButton("1st Color...");
    private JButton secondColor = new JButton("2nd Color...");
    private JButton undoButton = new JButton("Undo");
    private JButton clearButton = new JButton("Clear");
    
    private JLabel options = new JLabel("Options:");
    private JCheckBox filled = new JCheckBox("Filled");
    private JCheckBox gradient = new JCheckBox("Use Gradient");
    private JCheckBox dashed = new JCheckBox("Dashed");
    private JLabel width = new JLabel("Line Width:");
    private JSpinner lineWidth = new JSpinner();
    private JLabel dLength = new JLabel("Dash Length:");
    private JSpinner dashLength = new JSpinner();
    

    private Color firstColorChosen = Color.LIGHT_GRAY;
    private Color secondColorChosen = Color.LIGHT_GRAY;

    private JLabel statusLabel = new JLabel("(0, 0)");
    
    private ArrayList<MyShapes> shapes;
    
    public DrawingApplicationFrame()
    {
        shapes = new ArrayList<MyShapes>();

        firstPanel.add(shape);
        shapeType.addItem("Line");
        shapeType.addItem("Oval");
        shapeType.addItem("Rectangle");
        firstPanel.add(shapeType);
        firstPanel.add(firstColor);
        firstPanel.add(secondColor);
        firstPanel.add(undoButton);
        firstPanel.add(clearButton);
        
        secondPanel.add(options);
        secondPanel.add(filled);
        secondPanel.add(gradient);
        secondPanel.add(dashed);
        secondPanel.add(width);
 
        lineWidth.setValue(1);
        dashLength.setValue(1);
        secondPanel.add(lineWidth);
        secondPanel.add(dLength);
        secondPanel.add(dashLength);
        

        topPanel.setBackground(Color.CYAN);
        firstPanel.setBackground(Color.CYAN);
        secondPanel.setBackground(Color.CYAN);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(firstPanel, BorderLayout.NORTH);
        topPanel.add(secondPanel, BorderLayout.SOUTH);
        


        this.setLayout(new BorderLayout());
        this.add(topPanel, BorderLayout.NORTH);
        drawPanel.setBackground(Color.WHITE);
        this.add(drawPanel, BorderLayout.CENTER);
        this.add(statusLabel, BorderLayout.SOUTH);
        
        ButtonEventHandler eventHandler = new ButtonEventHandler();
        firstColor.addActionListener(eventHandler);
        secondColor.addActionListener(eventHandler);
        undoButton.addActionListener(eventHandler);
        clearButton.addActionListener(eventHandler);
        
    }


    private class ButtonEventHandler implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            switch (e.getActionCommand()) {
                case "1st Color...":
                    firstColorChosen = JColorChooser.showDialog(topPanel, "Choose a color", drawPanel.getGraphics().getColor());
                    if (firstColorChosen == null) {
                        firstColorChosen = Color.LIGHT_GRAY;
                    }
                    break;
                case "2nd Color...":
                    secondColorChosen = JColorChooser.showDialog(topPanel, "Choose a color", drawPanel.getGraphics().getColor());
                    if (secondColorChosen == null) {
                        secondColorChosen = Color.LIGHT_GRAY;
                    }
                    break;
                case "Undo":
                    if (shapes.size() > 0) {    // if there are shapes in the arraylist, remove the last one                 
                        shapes.remove(shapes.size() - 1);
                    }
                    repaint(); // then repaint so everything is painted except for the last one
                    break;
                case "Clear":
                    shapes.clear(); // clear the arraylist
                    repaint(); // call repaint so that it repaints all the shapes (which are none)
                    break;
            }
        }
    }

    private class DrawPanel extends JPanel
    {

        public DrawPanel()
        {
            MouseHandler mHandler = new MouseHandler();

            this.addMouseMotionListener(mHandler);
            this.addMouseListener(mHandler);
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;

            for (MyShapes shape : shapes) {
                shape.draw(g2d);
            }
        }


        private class MouseHandler extends MouseAdapter implements MouseMotionListener
        {

            public void mousePressed(MouseEvent event)
            {

                Paint paint;
                if (gradient.isSelected()) {
                    paint = new GradientPaint(0, 0, firstColorChosen, 50, 50, secondColorChosen, true); // use format specified in lab description to create a gradient paint object
                }
                else {
                    paint = new GradientPaint(0, 0, firstColorChosen, 50, 50, firstColorChosen); // create a normal paint object
                }
                

                BasicStroke stroke;
                if (dashed.isSelected()) {
                    stroke = new BasicStroke((int)lineWidth.getValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10, new float[] {(int)dashLength.getValue()}, 0); 
                }
                else {
                    stroke = new BasicStroke((int)lineWidth.getValue(), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                }
                
                
                switch (shapeType.getSelectedItem().toString()) {
                    case "Line":
                        MyLine newLine = new MyLine(event.getPoint(), event.getPoint(), paint, stroke);
                        shapes.add(newLine); 
                        break;
                    case "Oval":
                        MyOval newOval = new MyOval(event.getPoint(), event.getPoint(), paint, stroke, filled.isSelected());
                        shapes.add(newOval); 
                        break;
                    case "Rectangle":
                        MyRectangle newRect = new MyRectangle(event.getPoint(), event.getPoint(), paint, stroke, filled.isSelected()); 
                        shapes.add(newRect);
                        break;
                }
                
                
                repaint();
            }

            public void mouseReleased(MouseEvent event)
            {
                
                repaint();
            }

            @Override
            public void mouseDragged(MouseEvent event)
            {
                
                statusLabel.setText(String.format("(%s, %s)", event.getX(), event.getY()));
                
                shapes.get(shapes.size() - 1).setEndPoint(event.getPoint());
               
                repaint();
            }

            @Override
            public void mouseMoved(MouseEvent event)
            {

                statusLabel.setText(String.format("(%s, %s)", event.getX(), event.getY()));
            }
        }

    }
}