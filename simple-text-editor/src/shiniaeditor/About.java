package shiniaeditor;

import javax.swing.*;

import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class About
{
    private final JFrame frame;
    private final JPanel panel;
    private final JLabel text;
    
    private String contentText;
    
    public About(UI ui)
    {
        panel = new JPanel(new FlowLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        
        frame = new JFrame();

        frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                frame.dispose();
            }
        });

        frame.setVisible(true);
        frame.setSize(300,125);
        frame.setLocationRelativeTo(ui);
        
        text = new JLabel();
    }
    
    public void me()
    {
        frame.setTitle("About Me - " + "Shinia Editor");
        
        contentText =
        """
		<html>
		<body>
		  <p style="font-weight: bold; color: blue;">
		    Integrantes Del Equipo: <br />
		    <span style="font-weight: normal; color: blue;">Barba Navarro Luis Rodrigo</span><br />
		    <span style="font-weight: normal; color: blue;">Garcia Aguirre Carlos Enrique</span><br />
		    <span style="font-weight: normal; color: blue;">Geraldo Armenta Angel Uriel</span><br />
		  </p>
		</body>
		</html>
        """;

        text.setText(contentText);
        panel.add(text);
        frame.add(panel);
    }

    public void software()
    {
    	frame.setTitle("About Software - " + "Shinia Editor");
    	
    	contentText =
        """
		<html>
		<body>
		  <p style="font-weight: bold; color: blue;">
		    Aplicación de Editor de Texto para Compilador y Analizador Semántico: <br />
		    <span style="font-weight: normal; color: blue;">Este editor de texto está diseñado para la elaboración de un compilador y analizador semántico.</span><br />
		  </p>
		</body>
		</html>

        """;

    	text.setText(contentText);
    	panel.add(text);
    	frame.add(panel);
    }
}