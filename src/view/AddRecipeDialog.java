/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import service.RecipeService;

/**
 *
 * @author rafif
 */
public class AddRecipeDialog extends JDialog {

    private JTextField recipeNameField;
    private DefaultListModel<String> ingredientsListModel;
    private JList<String> ingredientsList;
    private JTextField ingredientField;
    private DefaultListModel<String> instructionsListModel;
    private JList<String> instructionsList;
    private JTextField instructionField;
    private JButton saveButton;
    private JButton cancelButton;
    private RecipeService recipeController;
    private int userId;

    public AddRecipeDialog(Frame parent, RecipeService recipeController, int userId) {
        super(parent, "Tambah Resep Baru", true);
        this.recipeController = recipeController;
        this.userId = userId;
        setSize(500, 500);
        setLocationRelativeTo(parent);

        // Initialize components
        recipeNameField = new JTextField(30);
        ingredientsListModel = new DefaultListModel<>();
        ingredientsList = new JList<>(ingredientsListModel);
        ingredientField = new JTextField(20);
        instructionsListModel = new DefaultListModel<>();
        instructionsList = new JList<>(instructionsListModel);
        instructionField = new JTextField(20);
        saveButton = new JButton("Simpan");
        cancelButton = new JButton("Batal");

        // Tabbed pane for Ingredients and Instructions
        JTabbedPane tabbedPane = new JTabbedPane();

        // Ingredients panel
        JPanel ingredientsPanel = new JPanel(new BorderLayout());
        ingredientsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        ingredientsPanel.add(new JScrollPane(ingredientsList), BorderLayout.CENTER);
        JPanel addIngredientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addIngredientPanel.add(new JLabel("Tambah bahan:"));
        addIngredientPanel.add(ingredientField);
        JButton addIngredientButton = new JButton("Tambah");
        addIngredientPanel.add(addIngredientButton);
        ingredientsPanel.add(addIngredientPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Bahan-bahan", ingredientsPanel);

        // Instructions panel
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructionsPanel.add(new JScrollPane(instructionsList), BorderLayout.CENTER);
        JPanel addInstructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addInstructionPanel.add(new JLabel("Tambah Petunjuk:"));
        addInstructionPanel.add(instructionField);
        JButton addInstructionButton = new JButton("Tambah");
        addInstructionPanel.add(addInstructionButton);
        instructionsPanel.add(addInstructionPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Petunjuk", instructionsPanel);

        // Layout setup
        setLayout(new BorderLayout());
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.add(new JLabel("Nama Resep:"), BorderLayout.WEST);
        topPanel.add(recipeNameField, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);
        add(tabbedPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        bottomPanel.add(saveButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);

        // Button actions
        addIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ingredient = ingredientField.getText().trim();
                if (!ingredient.isEmpty()) {
                    ingredientsListModel.addElement(ingredient);
                    ingredientField.setText("");
                }
            }
        });

        addInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String instruction = instructionField.getText().trim();
                if (!instruction.isEmpty()) {
                    instructionsListModel.addElement(instruction);
                    instructionField.setText("");
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipeName = recipeNameField.getText().trim();
                if (recipeName.isEmpty()) {
                    JOptionPane.showMessageDialog(AddRecipeDialog.this, "Nama resep tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ingredientsListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(AddRecipeDialog.this, "Daftar bahan tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (instructionsListModel.isEmpty()) {
                    JOptionPane.showMessageDialog(AddRecipeDialog.this, "Daftar petunjuk tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                StringBuilder ingredients = new StringBuilder();
                for (int i = 0; i < ingredientsListModel.size(); i++) {
                    ingredients.append(ingredientsListModel.get(i));
                    if (i < ingredientsListModel.size() - 1) {
                        ingredients.append(", ");
                    }
                }

                String[] instructions = new String[instructionsListModel.size()];
                for (int i = 0; i < instructionsListModel.size(); i++) {
                    instructions[i] = instructionsListModel.get(i);
                }

                recipeController.addRecipe(recipeName, ingredients.toString(), instructions, userId);

                JOptionPane.showMessageDialog(AddRecipeDialog.this, "Resep berhasil disimpan!");
                dispose();
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }
}
