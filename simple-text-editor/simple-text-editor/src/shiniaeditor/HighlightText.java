package shiniaeditor;

import javax.swing.text.*;

import shiniaeditor.HighlightText;

import java.awt.*;

public class HighlightText extends DefaultHighlighter.DefaultHighlightPainter
{

    public HighlightText(Color color)
    {
        super(color);
    }
    
    public void highLightAutomatas(JTextComponent textComp){
    	
        removeHighlights(textComp);

        Highlighter highlighter = textComp.getHighlighter();
        Document doc = textComp.getDocument();
        Highlighter.Highlight[] resaltados = highlighter.getHighlights();


        try
        {
            String texto = doc.getText(0, doc.getLength());
            
            // Obtener la longitud del texto
            int longitud = texto.length();
           
            // Inicializar el último índice
            int ultimoIndice = -1;
            
            // Eliminar resaltados existentes
            resaltados = highlighter.getHighlights();
            for (Highlighter.Highlight resaltado : resaltados) highlighter.removeHighlight(resaltado);

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
                    highlighter.addHighlight(inicio, fin, new DefaultHighlighter.DefaultHighlightPainter(colorResaltado));
                }
            }
        } catch (BadLocationException ex)
        
        {
            ex.printStackTrace();
        }
    }
    public void highLight(JTextComponent textComp, String[] pattern)
    {
        removeHighlights(textComp);

        try 
        {
            Highlighter highlighter = textComp.getHighlighter();
            Document doc = textComp.getDocument();
            
            String text = doc.getText(0, doc.getLength());
            
            for (int i = 0; i < pattern.length; i++)
            {
                int pos = 0;

                while ((pos = text.indexOf(pattern[i], pos)) >= 0) 
                {
                    highlighter.addHighlight(pos, pos + pattern[i].length(), this);
                    pos += pattern[i].length();
                }
            }
            
        } catch (BadLocationException e) {}
    }

    public void removeHighlights(JTextComponent textComp)
    {
        Highlighter highlighter = textComp.getHighlighter();
        Highlighter.Highlight[] hilites = highlighter.getHighlights();

        for (int i = 0; i < hilites.length; i++)
        {
            if (hilites[i].getPainter() instanceof HighlightText) { highlighter.removeHighlight(hilites[i]); }
        }
    }
}
