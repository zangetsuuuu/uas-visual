/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 * @author rafif
 */
public class AuthService {

    public boolean register(String username, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            // Periksa apakah username sudah digunakan
            String checkUserQuery = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement checkUserStmt = connection.prepareStatement(checkUserQuery);
            checkUserStmt.setString(1, username);
            ResultSet resultSet = checkUserStmt.executeQuery();
            resultSet.next();
            if (resultSet.getInt(1) > 0) {
                return false;
            }

            // Tambahkan pengguna baru
            String insertUserQuery = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement insertUserStmt = connection.prepareStatement(insertUserQuery);
            insertUserStmt.setString(1, username);
            insertUserStmt.setString(2, password);
            insertUserStmt.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserId(String username) {
        try (Connection connection = DBConnection.getConnection()) {
            String query = "SELECT id FROM users WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt("id");
            } else {
                return -1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
}
