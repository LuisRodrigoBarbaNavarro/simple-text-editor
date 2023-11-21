package shiniaeditor;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.text.*;
import java.awt.*;

public class EjemploResaltadorTextArea extends JFrame 
{
    private static final long serialVersionUID = 1L;
    
	private JTextArea areaTexto;
    private Highlighter resaltador;

    public EjemploResaltadorTextArea()
    {
        setTitle("Ejemplo de Resaltado en JTextArea - Shinia");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        initComponents();
        
        setSize(600, 400);
        setLocationRelativeTo(null);
    }

    private void initComponents()
    {
        // Configuración del JTextArea y el JScrollPane
        areaTexto = new JTextArea();
        areaTexto.setLineWrap(true);
        areaTexto.getDocument().addDocumentListener(new EscuchaDocumento());

        JScrollPane panelDesplazamiento = new JScrollPane(areaTexto);
        resaltador = areaTexto.getHighlighter();

        getContentPane().add(panelDesplazamiento, BorderLayout.CENTER);
    }

    private class EscuchaDocumento implements DocumentListener
    {
        @Override
        public void insertUpdate(DocumentEvent e) { procesarTexto(); }

        @Override
        public void removeUpdate(DocumentEvent e) { procesarTexto(); }

        @Override
        public void changedUpdate(DocumentEvent e) { procesarTexto(); }

        private void procesarTexto()
        {
            // Obtener el texto del JTextArea
            String texto = areaTexto.getText();
            
            // Obtener la longitud del texto
            int longitud = texto.length();
           
            // Inicializar el último índice
            int ultimoIndice = -1;

            try
            {
                // Eliminar resaltados existentes
                Highlighter.Highlight[] resaltados = resaltador.getHighlights();
                for (Highlighter.Highlight resaltado : resaltados) resaltador.removeHighlight(resaltado);

                // Buscar y resaltar ocurrencias de vocales
                for (int i = 0; i < longitud; i++)
                {
                    // Obtener el carácter actual
                    char caracterActual = texto.charAt(i);

                    // Verificar si el carácter actual es una vocal
                    if ("aeiou".indexOf(caracterActual) != -1)
                    {
                        // Obtener el inicio del resaltado
                        int inicio = i;
                        
                        // Obtener el fin del resaltado (Inicialmente hasta el final del texto)
                        int fin = longitud;

                        // Buscar el siguiente '#' después de la vocal
                        int siguienteIndiceNumeral = texto.indexOf('#', i);
                        if (siguienteIndiceNumeral != -1)
                        {
                            // Actualizar el último índice y el fin del resaltado
                            ultimoIndice = siguienteIndiceNumeral;
                            fin = ultimoIndice;
                        }

                        // Asignar colores diferentes a cada vocal
                        Color colorResaltado;
                        switch (caracterActual)
                        {
                            case 'a':
                                colorResaltado = Color.YELLOW;
                                break;
                            case 'e':
                                colorResaltado = Color.GREEN;
                                break;
                            case 'i':
                                colorResaltado = Color.BLUE;
                                break;
                            case 'o':
                                colorResaltado = Color.ORANGE;
                                break;
                            case 'u':
                                colorResaltado = Color.MAGENTA;
                                break;
                            default:
                                colorResaltado = Color.BLACK;
                        }

                        // Aplicar el resaltado
                        resaltador.addHighlight(inicio, fin, new DefaultHighlighter.DefaultHighlightPainter(colorResaltado));
                    }
                }
            } catch (BadLocationException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new EjemploResaltadorTextArea().setVisible(true));
    }
}

