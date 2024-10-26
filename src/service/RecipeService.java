/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import config.DBConnection;

/**
 *
 * @author rafif
 */
public class RecipeService {

    private Map<Integer, String> recipeInstructions = new HashMap<>();

    public void loadRecipes(DefaultTableModel model) {
        try (Connection connection = DBConnection.getConnection()) {
            model.setRowCount(0);
            String query = "SELECT r.id, r.name, GROUP_CONCAT(i.name SEPARATOR ', ') AS ingredients, r.created_at "
                    + "FROM recipes r "
                    + "LEFT JOIN ingredients i ON r.id = i.recipe_id "
                    + "GROUP BY r.id, r.name, r.created_at";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            int num = 1;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String ingredients = resultSet.getString("ingredients");
                String createdAt = resultSet.getString("created_at");

                model.addRow(new Object[]{num, name, ingredients, createdAt});

                loadInstructions(id);

                num++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadInstructions(int recipeId) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT description FROM instructions WHERE recipe_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, recipeId);
            ResultSet resultSet = preparedStatement.executeQuery();

            StringBuilder instructions = new StringBuilder();

            while (resultSet.next()) {
                String description = resultSet.getString("description");
                instructions.append(description).append("\n");
            }

            recipeInstructions.put(recipeId, instructions.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getInstructions(int recipeId) {
        return recipeInstructions.get(recipeId);
    }

    public void searchRecipe(DefaultTableModel model, String searchKeyword) {
        try (Connection connection = DBConnection.getConnection()) {
            model.setRowCount(0); // Kosongkan model tabel
            String query = "SELECT r.id, r.name, GROUP_CONCAT(i.name SEPARATOR ', ') AS ingredients, r.created_at "
                    + "FROM recipes r "
                    + "LEFT JOIN ingredients i ON r.id = i.recipe_id "
                    + "WHERE r.name LIKE ? "
                    + "GROUP BY r.id, r.name, r.created_at";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, "%" + searchKeyword + "%");
            ResultSet resultSet = preparedStatement.executeQuery();

            int num = 1;

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String ingredients = resultSet.getString("ingredients");
                String createdAt = resultSet.getString("created_at");

                model.addRow(new Object[]{num, name, ingredients, createdAt}); // Tambahkan data ke model tabel

                loadInstructions(id);

                num++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addRecipe(String name, String ingredients, String[] instructions) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            String recipeQuery = "INSERT INTO recipes (name) VALUES (?)";
            PreparedStatement recipeStatement = connection.prepareStatement(recipeQuery, Statement.RETURN_GENERATED_KEYS);
            recipeStatement.setString(1, name);
            recipeStatement.executeUpdate();

            ResultSet generatedKeys = recipeStatement.getGeneratedKeys();
            int recipeId = -1;
            if (generatedKeys.next()) {
                recipeId = generatedKeys.getInt(1);
            }

            if (recipeId != -1 && ingredients != null && !ingredients.isEmpty()) {
                String[] ingredientArray = ingredients.split(",");
                for (String ingredient : ingredientArray) {
                    addIngredientToRecipe(recipeId, ingredient.trim(), connection);
                }
            }

            // Menambahkan instruksi jika ada
            if (recipeId != -1 && instructions != null && instructions.length > 0) {
                for (String instruction : instructions) {
                    addInstructionToRecipe(recipeId, instruction.trim(), connection);
                }
            }

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try (Connection connection = DBConnection.getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }

    private void addIngredientToRecipe(int recipeId, String ingredient, Connection connection) {
        try {
            String ingredientQuery = "INSERT INTO ingredients (recipe_id, name) VALUES (?, ?)";
            PreparedStatement ingredientStatement = connection.prepareStatement(ingredientQuery);
            ingredientStatement.setInt(1, recipeId);
            ingredientStatement.setString(2, ingredient);
            ingredientStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addInstructionToRecipe(int recipeId, String instruction, Connection connection) {
        try {
            String instructionQuery = "INSERT INTO instructions (recipe_id, description) VALUES (?, ?)";
            PreparedStatement instructionStatement = connection.prepareStatement(instructionQuery);
            instructionStatement.setInt(1, recipeId);
            instructionStatement.setString(2, instruction);
            instructionStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateRecipe(int recipeId, String newName, List<String> newIngredients, List<String> newInstructions) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            // Update nama resep
            String updateRecipeQuery = "UPDATE recipes SET name = ? WHERE id = ?";
            try (PreparedStatement updateRecipeStmt = connection.prepareStatement(updateRecipeQuery)) {
                updateRecipeStmt.setString(1, newName);
                updateRecipeStmt.setInt(2, recipeId);
                updateRecipeStmt.executeUpdate();
            }

            // Update bahan-bahan
            updateIngredients(connection, recipeId, newIngredients);

            // Update petunjuk
            updateInstructions(connection, recipeId, newInstructions);

            // Commit transaksi jika semua update berhasil
            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try (Connection connection = DBConnection.getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }

    private void updateIngredients(Connection connection, int recipeId, List<String> newIngredients) throws SQLException {
        // Ambil semua bahan yang sudah ada dari database
        String selectIngredientsQuery = "SELECT name FROM ingredients WHERE recipe_id = ?";
        Set<String> existingIngredients = new HashSet<>();
        try (PreparedStatement selectStmt = connection.prepareStatement(selectIngredientsQuery)) {
            selectStmt.setInt(1, recipeId);
            ResultSet resultSet = selectStmt.executeQuery();
            while (resultSet.next()) {
                existingIngredients.add(resultSet.getString("name"));
            }
        }

        // Hapus bahan yang tidak ada di daftar baru
        for (String existingIngredient : existingIngredients) {
            if (!newIngredients.contains(existingIngredient)) {
                String deleteIngredientQuery = "DELETE FROM ingredients WHERE recipe_id = ? AND name = ?";
                try (PreparedStatement deleteStmt = connection.prepareStatement(deleteIngredientQuery)) {
                    deleteStmt.setInt(1, recipeId);
                    deleteStmt.setString(2, existingIngredient);
                    deleteStmt.executeUpdate();
                }
            }
        }

        // Tambah atau update bahan yang baru
        for (String newIngredient : newIngredients) {
            if (!existingIngredients.contains(newIngredient)) {
                String insertIngredientQuery = "INSERT INTO ingredients (recipe_id, name) VALUES (?, ?)";
                try (PreparedStatement insertStmt = connection.prepareStatement(insertIngredientQuery)) {
                    insertStmt.setInt(1, recipeId);
                    insertStmt.setString(2, newIngredient);
                    insertStmt.executeUpdate();
                }
            }
        }
    }

    private void updateInstructions(Connection connection, int recipeId, List<String> newInstructions) throws SQLException {
        // Hapus semua instruksi yang ada untuk resep ini
        String deleteInstructionsQuery = "DELETE FROM instructions WHERE recipe_id = ?";
        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteInstructionsQuery)) {
            deleteStmt.setInt(1, recipeId);
            deleteStmt.executeUpdate();
        }

        // Tambah instruksi baru dari daftar newInstructions tanpa step_number
        String insertInstructionQuery = "INSERT INTO instructions (recipe_id, description) VALUES (?, ?)";
        try (PreparedStatement insertStmt = connection.prepareStatement(insertInstructionQuery)) {
            for (String newInstruction : newInstructions) {
                insertStmt.setInt(1, recipeId);
                insertStmt.setString(2, newInstruction);
                insertStmt.addBatch();
            }
            insertStmt.executeBatch();
        }
    }

    public void deleteRecipe(int recipeId) {
        try (Connection connection = DBConnection.getConnection()) {
            connection.setAutoCommit(false);

            deleteIngredientsByRecipeId(recipeId);

            String query = "DELETE FROM recipes WHERE id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, recipeId);
            preparedStatement.executeUpdate();

            connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            try (Connection connection = DBConnection.getConnection()) {
                connection.rollback();
            } catch (SQLException rollbackException) {
                rollbackException.printStackTrace();
            }
        }
    }

    private void deleteIngredientsByRecipeId(int recipeId) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "DELETE FROM ingredients WHERE recipe_id = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, recipeId);
            preparedStatement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
