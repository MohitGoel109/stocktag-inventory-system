/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * NOTE: This is the original legacy implementation, kept for historical reference.
 * The hardcoded credentials that were originally here have been removed before
 * publishing this repository — set DB_USER / DB_PASSWORD as environment variables
 * (or your own constants) before running this legacy app locally.
 * The actively maintained version of this project is inventory-api/ + inventory-web/.
 *
 * @author Lenovo
 */
public class ConnectionProvider {

    public static Connection getCon(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            String user = System.getenv().getOrDefault("DB_USER", "root");
            String password = System.getenv().getOrDefault("DB_PASSWORD", "");
            Connection con = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/inventory?useSSL=false", user, password);
            return con;
        }
        catch(Exception e){
            System.out.println(e);
            return null;
        }
        }
    }

