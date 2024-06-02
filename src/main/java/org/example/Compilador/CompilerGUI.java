package org.example.Compilador;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class CompilerGUI extends JFrame {
    private JTextArea codeTextArea;
    private JTextArea resultTextArea;
    private SymbolTable symbolTable;
    private ErrorManager errorManager;

    public CompilerGUI() {
        setTitle("Compilador");
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        symbolTable = new SymbolTable();
        errorManager = new ErrorManager();

        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel inputPanel = createInputPanel();
        JPanel resultPanel = createResultPanel();
        JPanel buttonPanel = createButtonPanel();

        mainPanel.add(inputPanel, BorderLayout.WEST);
        mainPanel.add(resultPanel, BorderLayout.EAST);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new BorderLayout());
        JLabel codeLabel = new JLabel("Ingrese el código:");
        codeTextArea = new JTextArea(20, 40);
        codeTextArea.getDocument().addDocumentListener(new HighlightDocumentListener());
        JScrollPane codeScrollPane = new JScrollPane(codeTextArea);
        inputPanel.add(codeLabel, BorderLayout.NORTH);
        inputPanel.add(codeScrollPane, BorderLayout.CENTER);
        return inputPanel;
    }

    private JPanel createResultPanel() {
        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = new JLabel("Resultado del análisis:");
        resultTextArea = new JTextArea(20, 40);
        JScrollPane resultScrollPane = new JScrollPane(resultTextArea);
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(resultScrollPane, BorderLayout.CENTER);
        return resultPanel;
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel();
        JButton analyzeButton = new JButton("Analizar");
        analyzeButton.addActionListener(e -> analyzeCode());
        buttonPanel.add(analyzeButton);
        return buttonPanel;
    }

    private void analyzeCode() {
        String code = codeTextArea.getText();
        resultTextArea.setText("");

        LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer(code);
        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(lexicalAnalyzer);
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable, errorManager);

        lexicalAnalyzer.analyze();
        syntaxAnalyzer.analyze();
        semanticAnalyzer.analyze();

        StringBuilder resultText = new StringBuilder();
        if (!errorManager.hasErrors()) {
            resultText.append("El código no contiene errores.\n\nTokens:\n");
            lexicalAnalyzer.getTokens().forEach(token -> resultText.append(token).append("\n"));
        } else {
            resultText.append("Se encontraron errores en el código:\n");
            errorManager.getErrors().forEach(error -> {
                resultText.append(error).append("\n");
                highlightError(error.getLine());
            });
        }
        resultTextArea.setText(resultText.toString());
    }

    private void highlightError(int line) {
        try {
            int start = codeTextArea.getLineStartOffset(line - 1);
            int end = codeTextArea.getLineEndOffset(line - 1);
            codeTextArea.getHighlighter().addHighlight(start, end, new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CompilerGUI().setVisible(true));
    }
}

class HighlightDocumentListener implements DocumentListener {
    @Override
    public void insertUpdate(DocumentEvent e) {
        removeHighlights(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        removeHighlights(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        removeHighlights(e);
    }

    private void removeHighlights(DocumentEvent e) {
        DefaultHighlighter highlighter = (DefaultHighlighter) e.getDocument().getDefaultRootElement();
        Highlighter.Highlight[] highlights = highlighter.getHighlights();
        for (Highlighter.Highlight highlight : highlights) {
            if (highlight.getPainter() instanceof DefaultHighlighter.DefaultHighlightPainter) {
                highlighter.removeHighlight(highlight);
            }
        }
    }
}

class LexicalAnalyzer {
    private String code;
    private List<Token> tokens;

    public LexicalAnalyzer(String code) {
        this.code = code;
        tokens = new ArrayList<>();
    }

    public void analyze() {
        // Implement lexical analysis here
    }

    public List<Token> getTokens() {
        return tokens;
    }
}

class SyntaxAnalyzer {
    private LexicalAnalyzer lexicalAnalyzer;

    public SyntaxAnalyzer(LexicalAnalyzer lexicalAnalyzer) {
        this.lexicalAnalyzer = lexicalAnalyzer;
    }

    public void analyze() {
        // Implement syntax analysis here
    }
}

class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private ErrorManager errorManager;

    public SemanticAnalyzer(SymbolTable symbolTable, ErrorManager errorManager) {
        this.symbolTable = symbolTable;
        this.errorManager = errorManager;
    }

    public void analyze() {
        // Implement semantic analysis here
    }
}

class Token {
    private String lexeme;
    private TokenType type;

    public Token(String lexeme, TokenType type) {
        this.lexeme = lexeme;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Lexeme: " + lexeme + ", Type: " + type;
    }
}

enum TokenType {
    RESERVED_WORD, LOGICAL_EXPRESSION, MATHEMATICAL_EXPRESSION, VARIABLE, CONSTANT,
    FUNCTION, CLASS, LOOP, CONDITIONAL, CRUD
}

class SymbolTable {
    // Implement symbol table here
}

class ErrorManager {
    private List<Error> errors;

    public ErrorManager() {
        errors = new ArrayList<>();
    }

    public void addError(Error error) {
        errors.add(error);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<Error> getErrors() {
        return errors;
    }
}

class Error {
    private String message;
    private int line;

    public Error(String message, int line) {
        this.message = message;
        this.line = line;
    }

    public int getLine() {
        return line;
    }

    @Override
    public String toString() {
        return "Error en línea " + line + ": " + message;
    }
}
