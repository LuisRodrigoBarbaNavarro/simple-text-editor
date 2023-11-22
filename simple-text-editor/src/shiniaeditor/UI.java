package shiniaeditor;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.DefaultEditorKit;

import shiniaeditor.UI;

public class UI extends JFrame implements ActionListener
{
    private static final long serialVersionUID = 1L;
    
	private final String[] dragDropExtensionFilter = {".txt", ".dat", ".log", ".xml", ".mf", ".html"};
    
	private final JTextArea textArea;
    private final JTextArea errorArea;
    private final JMenuBar menuBar;
    private final JComboBox <String> fontType;
    private final JComboBox <Integer> fontSize;
    private final JMenu menuFile, menuEdit, menuFind, menuAbout;
    private final JMenuItem newFile, openFile, saveFile, saveAsFile, print, close, cut, copy, paste, clearFile, selectAll, quickFind,
    aboutMe, aboutSoftware, wordWrap, compileText;
    private final JToolBar mainToolbar;
    private final JButton newButton, openButton, saveButton, clearButton, quickButton, aboutMeButton, aboutButton, closeButton, boldButton, italicButton;
    private final Action selectAllAction;

    private final ImageIcon boldIcon = new ImageIcon(UI.class.getResource("icons/bold.png"));
    private final ImageIcon italicIcon = new ImageIcon("icons/italic.png");

    private final ImageIcon newIcon = new ImageIcon(UI.class.getResource("icons/new.png"));
    private final ImageIcon openIcon = new ImageIcon(UI.class.getResource("icons/open.png"));
    private final ImageIcon saveIcon = new ImageIcon(UI.class.getResource("icons/save.png"));
    private final ImageIcon saveAsIcon = new ImageIcon(UI.class.getResource("icons/saveas.png"));
    private final ImageIcon printIcon = new ImageIcon(UI.class.getResource("icons/print.png"));
    private final ImageIcon closeIcon = new ImageIcon(UI.class.getResource("icons/close.png"));

    private final ImageIcon clearIcon = new ImageIcon(UI.class.getResource("icons/clear.png"));
    private final ImageIcon cutIcon = new ImageIcon(UI.class.getResource("icons/cut.png"));
    private final ImageIcon copyIcon = new ImageIcon(UI.class.getResource("icons/copy.png"));
    private final ImageIcon pasteIcon = new ImageIcon(UI.class.getResource("icons/paste.png"));
    private final ImageIcon selectAllIcon = new ImageIcon(UI.class.getResource("icons/selectall.png"));
    private final ImageIcon wordwrapIcon = new ImageIcon(UI.class.getResource("icons/wordwrap.png"));
    private final ImageIcon compileIcon = new ImageIcon(UI.class.getResource("icons/compile.png"));
    private final ImageIcon searchIcon = new ImageIcon(UI.class.getResource("icons/search.png"));

    private final ImageIcon aboutMeIcon = new ImageIcon(UI.class.getResource("icons/about_me.png"));
    private final ImageIcon aboutIcon = new ImageIcon(UI.class.getResource("icons/about.png"));

    private SupportedKeywords kw = new SupportedKeywords();
    private HighlightText languageHighlighter = new HighlightText(Color.GRAY);
    
    AutoComplete autocomplete;
    
    private boolean hasListener = false;
    private boolean edit = false;

    private String removeWhitespaceAndNewlines(String input) {
        // Utiliza expresiones regulares para eliminar tabulaciones, espacios en blanco y saltos de línea
    	return input.replaceAll("[\\t\\r\\n]+", "");
    }
    
    private int getIterations(String input) {
    	Matcher matcher = Pattern.compile("again\\((\\d+)\\)#").matcher(input);
		return matcher.find() != false ? Integer.parseInt(matcher.group(1)) : 1 ;
    }
    
    private void compileAndPrintMessage()
    {
        // Define la expresión regular para buscar la secuencia deseada
        String regex = "inicio#again\\((\\d+)\\)#ensaje\\('([^']+)'\\)#out#";

        // Obtén el contenido completo del TextArea
        String textoCompleto = removeWhitespaceAndNewlines(textArea.getText());
        int veces = getIterations(textoCompleto);
        
        // Crea un objeto Pattern para la expresión regular
        Pattern pattern = Pattern.compile(regex);

        // Crea un objeto Matcher para buscar coincidencias
        Matcher matcher = pattern.matcher(textoCompleto);

        // Limpia el contenido actual del errorArea
        errorArea.setText("");

        // Variable para determinar si se encontró alguna coincidencia
        boolean encontrada = false;
        
        // Busca todas las coincidencias
        while (matcher.find())
        {
            // Obtiene el contenido dentro de los paréntesis en mensaje('...')
            String mensajeContenido = matcher.group(2);

            // Agrega el contenido encontrado al errorArea (textArea)
            for (int i = 0 ; i < veces ; i++) {
				errorArea.append("Mensaje encontrado: " + mensajeContenido + "\n");
			}
            errorArea.setForeground(Color.BLACK);

            // Marca que se encontró al menos una coincidencia
            encontrada = true;
        }
        
        // Si no se encontró ninguna coincidencia, muestra un mensaje de error
        if (!encontrada) {
            errorArea.setText("No se encontraron mensajes que coincidan con el patrón.");
            errorArea.setForeground(Color.RED);
        }
    }
    
    public UI()
    {	
        try 
        {
            ImageIcon image = new ImageIcon(UI.class.getResource("icons/ste.png"));
            super.setIconImage(image.getImage());
        } catch (Exception ex)
        {
            ex.printStackTrace();
        }

        setSize(800, 500);

        setTitle("Untitled | " + "Shinia Editor");

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        textArea = new JTextArea("", 0, 0);
        textArea.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        textArea.setTabSize(2);

        textArea.setLineWrap(true);

        textArea.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyReleased(KeyEvent e)
            {
                setTitle("Untitled | " + "Shinia Editor" + " | [ Length: " + textArea.getText().length()
                        + "    Lines: " + (textArea.getText() + "|").split("\n").length
                        + "    Words: " + textArea.getText().trim().split("\\s+").length + " ]");
                
                languageHighlighter.highLightAutomatas(textArea);

            }

            @Override
            public void keyPressed(KeyEvent ke)
            {
            	
                edit = true;
                
                /*
                languageHighlighter.highLight(textArea, kw.getCppKeywords());
                languageHighlighter.highLight(textArea, kw.getJavaKeywords());
                */
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        textArea.setWrapStyleWord(true);
        
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        getContentPane().setLayout(new BorderLayout());
        
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane);
        getContentPane().add(panel);
        
        errorArea = new JTextArea("", 0, 0);
        errorArea.setFont(new Font("Century Gothic", Font.PLAIN, 12));
        errorArea.setTabSize(2);
        errorArea.setLineWrap(true);
        errorArea.setEditable(false);
        JScrollPane errorScrollPane = new JScrollPane(errorArea);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollPane, errorScrollPane);
        splitPane.setDividerLocation(300); 

        getContentPane().setLayout(new BorderLayout());
        panel.add(splitPane); 
        getContentPane().add(panel);

        menuFile = new JMenu("File");
        menuEdit = new JMenu("Edit");
        menuFind = new JMenu("Search");
        menuAbout = new JMenu("About");

        newFile = new JMenuItem("New", newIcon);
        openFile = new JMenuItem("Open", openIcon);
        saveFile = new JMenuItem("Save", saveIcon);
        saveAsFile = new JMenuItem("Save As", saveAsIcon);
        print = new JMenuItem("Print", printIcon);
        close = new JMenuItem("Quit", closeIcon);
        clearFile = new JMenuItem("Clear", clearIcon);
        quickFind = new JMenuItem("Quick", searchIcon);
        aboutMe = new JMenuItem("About Me", aboutMeIcon);
        aboutSoftware = new JMenuItem("About Software", aboutIcon);

        menuBar = new JMenuBar();
        menuBar.add(menuFile);
        menuBar.add(menuEdit);
        menuBar.add(menuFind);

        menuBar.add(menuAbout);

        this.setJMenuBar(menuBar);

        selectAllAction = new SelectAllAction("Select All", clearIcon, "Select all text", new Integer(KeyEvent.VK_A), textArea);

        this.setJMenuBar(menuBar);

        newFile.addActionListener(this);  
        newFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        menuFile.add(newFile);

        openFile.addActionListener(this);
        openFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        menuFile.add(openFile);

        saveFile.addActionListener(this);
        saveFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        menuFile.add(saveFile);
        
        saveAsFile.addActionListener(this);
        saveAsFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_MASK));
        menuFile.add(saveAsFile);
        
        print.addActionListener(this);
        print.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_MASK));
        menuFile.add(print);
        
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_MASK));
        close.addActionListener(this);
        menuFile.add(close);

        selectAll = new JMenuItem(selectAllAction);
        selectAll.setText("Select All");
        selectAll.setIcon(selectAllIcon);
        selectAll.setToolTipText("Select All");
        selectAll.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        menuEdit.add(selectAll);

        clearFile.addActionListener(this);
        clearFile.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_K, InputEvent.CTRL_MASK));
        menuEdit.add(clearFile);

        cut = new JMenuItem(new DefaultEditorKit.CutAction());
        cut.setText("Cut");
        cut.setIcon(cutIcon);
        cut.setToolTipText("Cut");
        cut.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        menuEdit.add(cut);

        wordWrap = new JMenuItem();
        wordWrap.setText("Word Wrap");
        wordWrap.setIcon(wordwrapIcon);
        wordWrap.setToolTipText("Word Wrap");
        
        wordWrap.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        menuEdit.add(wordWrap);

        wordWrap.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                textArea.setLineWrap(!textArea.getLineWrap());
            }
        });

        copy = new JMenuItem(new DefaultEditorKit.CopyAction());
        copy.setText("Copy");
        copy.setIcon(copyIcon);
        copy.setToolTipText("Copy");
        copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        menuEdit.add(copy);

        paste = new JMenuItem(new DefaultEditorKit.PasteAction());
        paste.setText("Paste");
        paste.setIcon(pasteIcon);
        paste.setToolTipText("Paste");
        paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        menuEdit.add(paste);
        
        compileText = new JMenuItem();
        compileText.setText("Compile");
        compileText.setIcon(compileIcon);
        compileText.setToolTipText("Compile");
        
        compileText.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        menuEdit.add(compileText);
        
        compileText.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                compileAndPrintMessage();
            }
        });
        
        quickFind.addActionListener(this);
        quickFind.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F, InputEvent.CTRL_MASK));
        menuFind.add(quickFind);

        aboutMe.addActionListener(this);
        aboutMe.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
        menuAbout.add(aboutMe);

        aboutSoftware.addActionListener(this);
        aboutSoftware.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0));
        menuAbout.add(aboutSoftware);

        mainToolbar = new JToolBar();
        this.add(mainToolbar, BorderLayout.NORTH);
        
        newButton = new JButton(newIcon);
        newButton.setToolTipText("New");
        newButton.addActionListener(this);
        mainToolbar.add(newButton);
        mainToolbar.addSeparator();

        openButton = new JButton(openIcon);
        openButton.setToolTipText("Open");
        openButton.addActionListener(this);
        mainToolbar.add(openButton);
        mainToolbar.addSeparator();

        saveButton = new JButton(saveIcon);
        saveButton.setToolTipText("Save");
        saveButton.addActionListener(this);
        mainToolbar.add(saveButton);
        mainToolbar.addSeparator();

        clearButton = new JButton(clearIcon);
        clearButton.setToolTipText("Clear All");
        clearButton.addActionListener(this);
        mainToolbar.add(clearButton);
        mainToolbar.addSeparator();

        quickButton = new JButton(searchIcon);
        quickButton.setToolTipText("Quick Search");
        quickButton.addActionListener(this);
        mainToolbar.add(quickButton);
        mainToolbar.addSeparator();

        aboutMeButton = new JButton(aboutMeIcon);
        aboutMeButton.setToolTipText("About Me");
        aboutMeButton.addActionListener(this);
        mainToolbar.add(aboutMeButton);
        mainToolbar.addSeparator();

        aboutButton = new JButton(aboutIcon);
        aboutButton.setToolTipText("About NotePad PH");
        aboutButton.addActionListener(this);
        mainToolbar.add(aboutButton);
        mainToolbar.addSeparator();

        closeButton = new JButton(closeIcon);
        closeButton.setToolTipText("Quit");
        closeButton.addActionListener(this);
        mainToolbar.add(closeButton);
        mainToolbar.addSeparator();

        boldButton = new JButton(boldIcon);
        boldButton.setToolTipText("Bold");
        boldButton.addActionListener(this);
        mainToolbar.add(boldButton);
        mainToolbar.addSeparator();

        italicButton = new JButton(italicIcon);
        italicButton.setToolTipText("Italic");
        italicButton.addActionListener(this);
        mainToolbar.add(italicButton);
        mainToolbar.addSeparator();

        fontType = new JComboBox<String>();

        String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();

        for (String font : fonts) fontType.addItem(font);
        
        fontType.setMaximumSize(new Dimension(170, 30));
        fontType.setToolTipText("Font Type");
        mainToolbar.add(fontType);
        mainToolbar.addSeparator();

        fontType.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                String p = fontType.getSelectedItem().toString();
                int s = textArea.getFont().getSize();
                
                textArea.setFont(new Font(p, Font.PLAIN, s));
            }
        });

        fontSize = new JComboBox<Integer>();

        for (int i = 5; i <= 100; i++) fontSize.addItem(i);
        
        fontSize.setMaximumSize(new Dimension(70, 30));
        fontSize.setToolTipText("Font Size");
        
        mainToolbar.add(fontSize);

        fontSize.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent ev)
            {
                String sizeValue = fontSize.getSelectedItem().toString();
                int sizeOfFont = Integer.parseInt(sizeValue);
                String fontFamily = textArea.getFont().getFamily();

                Font font1 = new Font(fontFamily, Font.PLAIN, sizeOfFont);
                textArea.setFont(font1);
            }
        });
    }

    @Override
    protected void processWindowEvent(WindowEvent e)
    {
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            if (edit)
            {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
                if (n == 0)
                {
                    saveFile();
                    this.dispose();
                } else if (n == 1)
                {
                    this.dispose();
                }
            } else
            {
                System.exit(99);
            }
        }
    }

    protected JTextArea getEditor()
    {
        return textArea;
    }

    public void enableAutoComplete(File file)
    {
        if (hasListener)
        {
            textArea.getDocument().removeDocumentListener(autocomplete);
            hasListener = false;
        }

        ArrayList<String> arrayList;
        String[] list = kw.getSupportedLanguages();

        for (int i = 0; i < list.length; i++)
        {
            if (file.getName().endsWith(list[i]))
            {
                switch (i)
                {
                    case 0:
                    {
                        String[] jk = kw.getJavaKeywords();
                        arrayList = kw.setKeywords(jk);
                        autocomplete = new AutoComplete(this, arrayList);
                        textArea.getDocument().addDocumentListener(autocomplete);
                        hasListener = true;
                    }
                    case 1:
                    {
                        String[] ck = kw.getCppKeywords();
                        arrayList = kw.setKeywords(ck);
                        autocomplete = new AutoComplete(this, arrayList);
                        textArea.getDocument().addDocumentListener(autocomplete);
                        hasListener = true;
                    }
                }
            }
        }
    }

    public void actionPerformed(ActionEvent e)
    {
        if (e.getSource() == close || e.getSource() == closeButton)
        {
            if (edit)
            {
                Object[] options = {"Save and exit", "No Save and exit", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0) 
                {
                    saveFile();
                    this.dispose();
                } else if (n == 1)
                {
                    this.dispose();
                }
            } else
            {
                this.dispose();
            }
        }
        else if (e.getSource() == newFile || e.getSource() == newButton)
        {
            if (edit)
            {
                Object[] options = {"Save", "No Save", "Return"};
                int n = JOptionPane.showOptionDialog(this, "Do you want to save the file at first ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0)
                {
                    saveFile();
                    edit = false;
                } else if (n == 1)
                {
                    edit = false;
                    FEdit.clear(textArea);
                }
            } else
            {
                FEdit.clear(textArea);
            }

        }
        else if (e.getSource() == openFile || e.getSource() == openButton)
        {
            JFileChooser open = new JFileChooser();
            if( !(textArea.getText().equals("")) )
            {
                saveFile();
            }

            int option = open.showOpenDialog(this);

            if (option == JFileChooser.APPROVE_OPTION)
            {
                FEdit.clear(textArea); 
                try
                {
                    File openFile = open.getSelectedFile();
                    setTitle(openFile.getName() + " | " + "Shinia Editor");
                    Scanner scan = new Scanner(new FileReader(openFile.getPath()));
                    while (scan.hasNext()) {
                        textArea.append(scan.nextLine() + "\n");
                    }

                    enableAutoComplete(openFile);
                } catch (Exception ex)
                {
                    System.err.println(ex.getMessage());
                }
            }

        }
        else if (e.getSource() == saveFile || e.getSource() == saveButton)
        {
            saveFile();
        }
        else if (e.getSource() == boldButton)
        {
            if (textArea.getFont().getStyle() == Font.BOLD)
            {
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else
            {
                textArea.setFont(textArea.getFont().deriveFont(Font.BOLD));
            }
        }
        else if (e.getSource() == italicButton)
        {
            if (textArea.getFont().getStyle() == Font.ITALIC)
            {
                textArea.setFont(textArea.getFont().deriveFont(Font.PLAIN));
            } else
            {
                textArea.setFont(textArea.getFont().deriveFont(Font.ITALIC));
            }
        }

        if (e.getSource() == clearFile || e.getSource() == clearButton)
        {

            Object[] options = {"Yes", "No"};
            int n = JOptionPane.showOptionDialog(this, "Are you sure to clear the text Area ?", "Question",
                    JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
            if (n == 0) {
                FEdit.clear(textArea);
            }
        }

        if (e.getSource() == quickFind || e.getSource() == quickButton)
        {
            new Find(textArea);
        } 
        else if (e.getSource() == aboutMe || e.getSource() == aboutMeButton)
        {
            new About(this).me();
        }
        else if (e.getSource() == aboutSoftware || e.getSource() == aboutButton)
        {
            new About(this).software();
        }
    }

    class SelectAllAction extends AbstractAction
    {

        private static final long serialVersionUID = 1L;

        public SelectAllAction(String text, ImageIcon icon, String desc, Integer mnemonic, final JTextArea textArea)
        {
            super(text, icon);
            putValue(SHORT_DESCRIPTION, desc);
            putValue(MNEMONIC_KEY, mnemonic);
        }

        public void actionPerformed(ActionEvent e)
        {
            textArea.selectAll();
        }
    }

    private void saveFile()
    {
        JFileChooser fileChoose = new JFileChooser();
        int option = fileChoose.showSaveDialog(this);

        if (option == JFileChooser.APPROVE_OPTION)
        {
            try
            {
                File openFile = fileChoose.getSelectedFile();
                setTitle(openFile.getName() + " | " + "Shinia Editor");

                BufferedWriter out = new BufferedWriter(new FileWriter(openFile.getPath()));
                out.write(textArea.getText());
                out.close();

                enableAutoComplete(openFile);
                edit = false;
            } catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
    }
    
    DropTargetListener dropTargetListener = new DropTargetListener()
    {
        @Override
        public void dragEnter(DropTargetDragEvent e) {}

        @Override
        public void dragExit(DropTargetEvent e) {}

        @Override
        public void dragOver(DropTargetDragEvent e) {}

        @Override
        public void drop(DropTargetDropEvent e)
        {
            if (edit)
            {
                Object[] options = {"Save", "No Save", "Return"};
                int n = JOptionPane.showOptionDialog(UI.this, "Do you want to save the file at first ?", "Question",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[2]);
                if (n == 0)
                {
                    UI.this.saveFile();
                    edit = false;
                } else if (n == 1)
                {
                    edit = false;
                    FEdit.clear(textArea);
                } else if (n == 2)
                {
                    e.rejectDrop();
                    return;
                }
            }
            try
            {
                Transferable tr = e.getTransferable();
                DataFlavor[] flavors = tr.getTransferDataFlavors();
                for (DataFlavor flavor : flavors)
                {
                    if (flavor.isFlavorJavaFileListType())
                    {
                        e.acceptDrop(e.getDropAction());

                        try
                        {
                            String fileName = tr.getTransferData(flavor).toString().replace("[", "").replace("]", "");

                            boolean extensionAllowed = false;
                            for (String s : dragDropExtensionFilter)
                            {
                                if (fileName.endsWith(s))
                                {
                                    extensionAllowed = true;
                                    break;
                                }
                            }
                            if (!extensionAllowed)
                            {
                                JOptionPane.showMessageDialog(UI.this, "This file is not allowed for drag & drop", "Error", JOptionPane.ERROR_MESSAGE);

                            } else
                            {
                                FileInputStream fis = new FileInputStream(new File(fileName));
                                byte[] ba = new byte[fis.available()];
                                fis.read(ba);
                                textArea.setText(new String(ba));
                                fis.close();
                            }
                        } catch (Exception ex)
                        {
                            ex.printStackTrace();
                        }
                        e.dropComplete(true);
                        return;
                    }
                }
            } catch (Throwable t)
            {
                t.printStackTrace();
            }
            e.rejectDrop();
        }

        @Override
        public void dropActionChanged(DropTargetDragEvent e){}
    };
}
