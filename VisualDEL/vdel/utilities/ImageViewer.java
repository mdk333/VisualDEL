/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vdel.utilities;

/**
 *
 * @author byear
 */
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

public class ImageViewer
{
  
  public ImageViewer(final String filename) throws Exception
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      @Override
      public void run()
      {
        JFrame editorFrame = new JFrame("Image Demo");
        editorFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        
        BufferedImage image = null;
        try
        {
          image = ImageIO.read(new File(filename));
        }
        catch (IOException e)
        {
          
        }
        
        ImageIcon theImageHandle = new ImageIcon(image);
        JLabel imageHolder = new JLabel();
        imageHolder.setIcon(theImageHandle);
        editorFrame.getContentPane().add(imageHolder, BorderLayout.CENTER);

        editorFrame.pack();
        editorFrame.setLocationRelativeTo(null);
        editorFrame.setVisible(true);
      }
    });
  }
}
