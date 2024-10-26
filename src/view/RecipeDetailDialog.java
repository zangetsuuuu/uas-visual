package view;

import service.RecipeService;
import javax.swing.*;
import java.awt.*;

/**
 *
 * @author rafif
 */
public class RecipeDetailDialog extends JDialog {

    private final JLabel recipeIdLabel;
    private final JLabel recipeNameLabel;
    private final JTextArea ingredientsTextArea;
    private final JTextArea instructionsTextArea;
    private final RecipeService recipeController;
    private final int recipeId;

    public RecipeDetailDialog(Frame parent, int recipeId, String recipeName, String ingredients, String instructions, RecipeService recipeController) {
        super(parent, "Detail Resep", true);
        this.recipeId = recipeId;
        this.recipeController = recipeController;
        setLayout(new BorderLayout(10, 10));
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Buat label dan text area
        recipeIdLabel = new JLabel("ID Resep: " + recipeId);
        recipeIdLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        recipeNameLabel = new JLabel("Nama Resep: " + recipeName);
        recipeNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        ingredientsTextArea = new JTextArea(ingredients);
        ingredientsTextArea.setLineWrap(true);
        ingredientsTextArea.setWrapStyleWord(true);
        ingredientsTextArea.setEditable(false);
        ingredientsTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        ingredientsTextArea.setBorder(BorderFactory.createTitledBorder("Bahan-bahan"));

        instructionsTextArea = new JTextArea(instructions);
        instructionsTextArea.setLineWrap(true);
        instructionsTextArea.setWrapStyleWord(true);
        instructionsTextArea.setEditable(false);
        instructionsTextArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        instructionsTextArea.setBorder(BorderFactory.createTitledBorder("Petunjuk"));

        // Gunakan panel dengan GridBagLayout untuk tata letak yang lebih baik
        JPanel detailPanel = new JPanel(new GridBagLayout());
        detailPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Tambahkan padding di sekitar panel
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // Tambahkan komponen ke panel
        detailPanel.add(recipeIdLabel, gbc);
        gbc.gridy++;
        detailPanel.add(recipeNameLabel, gbc);
        gbc.gridy++;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 0.5;
        detailPanel.add(new JScrollPane(ingredientsTextArea), gbc);
        gbc.gridy++;
        detailPanel.add(new JScrollPane(instructionsTextArea), gbc);

        // Tambahkan panel detail ke dialog
        add(detailPanel, BorderLayout.CENTER);

        // Buat panel tombol
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editButton = new JButton("Edit");
        JButton deleteButton = new JButton("Hapus");
        JButton closeButton = new JButton("Tutup");

        // Tambahkan aksi untuk tombol "Edit"
        editButton.addActionListener(e -> {
            MainFrame mainFrame = new MainFrame();
            EditRecipeDialog editDialog = new EditRecipeDialog(mainFrame, recipeId, recipeName, ingredients, instructions, recipeController);
            editDialog.setVisible(true);
            dispose(); // Tutup dialog detail setelah pengeditan
        });

        // Tambahkan aksi untuk tombol "Hapus"
        deleteButton.addActionListener(e -> {
            int confirmation = JOptionPane.showConfirmDialog(this, "Apakah Anda yakin ingin menghapus resep ini?", "Konfirmasi Hapus", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                recipeController.deleteRecipe(recipeId);
                JOptionPane.showMessageDialog(this, "Resep berhasil dihapus!");
                dispose(); // Tutup dialog setelah penghapusan
            }
        });

        // Tambahkan aksi untuk tombol "Tutup"
        closeButton.addActionListener(e -> dispose());

        // Tambahkan tombol ke panel
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(closeButton);

        // Tambahkan panel tombol ke dialog
        add(buttonPanel, BorderLayout.SOUTH);

        // Atur ukuran dialog dan lokasi
        setSize(800, 700);
        setLocationRelativeTo(parent);
    }
}
