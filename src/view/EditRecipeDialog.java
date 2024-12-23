/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package view;

import service.RecipeService;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import javax.swing.border.EmptyBorder;

/**
 *
 * @author rafif
 */
public class EditRecipeDialog extends JDialog {

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
    private int recipeId;
    private int userId;

    public EditRecipeDialog(Frame parent, int recipeId, String recipeName, String ingredients, String instructions, RecipeService recipeController, int userId) {
        super(parent, "Edit Resep", true);
        this.recipeId = recipeId;
        this.recipeController = recipeController;
        this.userId = userId;
        setSize(500, 500);
        setLocationRelativeTo(parent);

        // Initialize components
        recipeNameField = new JTextField(recipeName, 30);
        ingredientsListModel = new DefaultListModel<>();
        instructionsListModel = new DefaultListModel<>();

        // Populate ingredients and instructions models
        for (String ingredient : ingredients.split(", ")) {
            ingredientsListModel.addElement(ingredient);
        }
        for (String instruction : instructions.split("\n")) {
            instructionsListModel.addElement(instruction);
        }

        ingredientsList = new JList<>(ingredientsListModel);
        ingredientField = new JTextField(20);
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
        JButton removeIngredientButton = new JButton("Hapus");
        addIngredientPanel.add(addIngredientButton);
        addIngredientPanel.add(removeIngredientButton);
        ingredientsPanel.add(addIngredientPanel, BorderLayout.SOUTH);
        tabbedPane.addTab("Bahan-bahan", ingredientsPanel);

        // Instructions panel
        JPanel instructionsPanel = new JPanel(new BorderLayout());
        instructionsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        instructionsPanel.add(new JScrollPane(instructionsList), BorderLayout.CENTER);
        JPanel addInstructionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addInstructionPanel.add(new JLabel("Tambah petunjuk:"));
        addInstructionPanel.add(instructionField);
        JButton addInstructionButton = new JButton("Tambah");
        JButton removeInstructionButton = new JButton("Hapus");
        addInstructionPanel.add(addInstructionButton);
        addInstructionPanel.add(removeInstructionButton);
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

        removeIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = ingredientsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    ingredientsListModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Pilih bahan yang ingin dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
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

        removeInstructionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedIndex = instructionsList.getSelectedIndex();
                if (selectedIndex != -1) {
                    instructionsListModel.remove(selectedIndex);
                } else {
                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Pilih petunjuk yang ingin dihapus.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String recipeName = recipeNameField.getText().trim();
                if (recipeName.isEmpty()) {
                    JOptionPane.showMessageDialog(EditRecipeDialog.this, "Nama resep tidak boleh kosong.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Mengumpulkan bahan dan instruksi menjadi list
                List<String> ingredientsList = new ArrayList<>();
                for (int i = 0; i < ingredientsListModel.size(); i++) {
                    ingredientsList.add(ingredientsListModel.getElementAt(i));
                }

                List<String> instructionsList = new ArrayList<>();
                for (int i = 0; i < instructionsListModel.size(); i++) {
                    instructionsList.add(instructionsListModel.getElementAt(i));
                }

                // Memanggil metode updateRecipe dengan parameter yang sudah diubah
                recipeController.updateRecipe(recipeId, recipeName, ingredientsList, instructionsList, userId);
                JOptionPane.showMessageDialog(EditRecipeDialog.this, "Resep berhasil diperbarui!");
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
