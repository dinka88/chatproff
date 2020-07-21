package server;

import java.io.IOException;
import java.sql.*;

public class DBAuthService implements AuthService {
    private static Connection connection;
    private static Statement stmt;

    public DBAuthService() throws IOException {
    }

    public static void connect() throws SQLException, ClassNotFoundException {
        Class.forName("org.sqlite.JDBC");
        connection = DriverManager.getConnection("jdbc:sqlite:test.db");
        stmt = connection.createStatement();
    }

    static {
        try {
            connect();
            Statement stmt = connection.createStatement();
            stmt.execute("CREATE TABLE users (\n" +
                    "    login VARCHAR (32) PRIMARY KEY,\n" +
                    "    pass  VARCHAR (32) NOT NULL,\n" +
                    "    nick  VARCHAR (32) NOT NULL" +
                    ");");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getNicknameByLoginAndPassword(String login, String password) {
        try {
            ResultSet resultSet = stmt.executeQuery("select nick from users where login = '" + login + "' and pass='" + password + "'");
            if (resultSet.next() == true) {
                return resultSet.getString("nick");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean registration(String login, String password, String nickname) {
        try (PreparedStatement pstmt = connection.prepareStatement("insert into users  (login, pass, nick) VALUES ( ?, ?, ?)")) {
            pstmt.setString(1, login);
            pstmt.setString(2, password);
            pstmt.setString(3, nickname);
            pstmt.executeUpdate();

            if (pstmt.getUpdateCount() > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

    @Override
    public boolean changeNick(String login, String newnick) {
        try (PreparedStatement pstmt = connection.prepareStatement("UPDATE users set nick = ? where login=?")) {
            pstmt.setString(1, newnick);
            pstmt.setString(2, login);
            pstmt.executeUpdate();

            if (pstmt.getUpdateCount() > 0) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return false;
    }

}
