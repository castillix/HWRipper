package org.freegeekarkansas;

import javax.xml.transform.Result;
import java.sql.*;
import java.util.ArrayList;

public class DBManager {
    public static String url = "jdbc:sqlite:cpus.db";
//    public static void main(String[] args) {
//        initTable();
//    }

//    public static void connect() {
//        try(Connection conn = DriverManager.getConnection(url)) {
//            System.out.println("Connected to db");
//        } catch(Exception e) {
//            System.out.println("Error connecting to database " + e);
//        }
//    }

    public static void insert(CPU cpu) {
        String sql =
                "INSERT OR REPLACE INTO cpus(id, year, url, name, cores, threads, clock, turbo, passmark) " +
                    "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setInt(1, cpu.id);
            st.setInt(2, cpu.year);
            st.setString(3, cpu.url);
            st.setString(4, cpu.name);
            st.setInt(5, cpu.cores);
            st.setInt(6, cpu.threads);
            st.setDouble(7, cpu.clock);
            st.setDouble(8, cpu.turbo);
            st.setInt(9, cpu.passmark);

            st.executeUpdate();
        } catch(Exception e) {
            System.out.println("Error connecting to database " + e);
        }
    }

    public static ArrayList<CPU> getCPUs() {
        String sql = "SELECT * FROM cpus";

        try(Connection conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement();
            ResultSet res = st.executeQuery(sql)) {

            ArrayList<CPU> cpus = new ArrayList<>();
            while(res.next()) {
                CPU cpu = new CPU();
                cpu.id = res.getInt("id");
                cpu.year = res.getInt("year");
                cpu.url = res.getString("url");
                cpu.name = res.getString("name");
                cpu.cores = res.getInt("cores");
                cpu.threads = res.getInt("threads");
                cpu.clock = res.getDouble("clock");
                cpu.turbo = res.getDouble("turbo");
                cpu.passmark = res.getInt("passmark");
                cpus.add(cpu);
            }

            return cpus;
        } catch(Exception e) {
            System.out.println("Error retrieving from database " + e);
        }
        return new ArrayList<>();
    }

    public static CPU getCpuFromString(String str) {
        String sql = "SELECT * FROM cpus WHERE name LIKE ? ORDER BY name";

        try(Connection conn = DriverManager.getConnection(url);
            PreparedStatement st = conn.prepareStatement(sql)) {

            st.setString(1, str);
            ResultSet res = st.executeQuery();

            if(res.next()) {
                CPU cpu = new CPU();
                cpu.id = res.getInt("id");
                cpu.year = res.getInt("year");
                cpu.url = res.getString("url");
                cpu.name = res.getString("name");
                cpu.cores = res.getInt("cores");
                cpu.threads = res.getInt("threads");
                cpu.clock = res.getDouble("clock");
                cpu.turbo = res.getDouble("turbo");
                cpu.passmark = res.getInt("passmark");

                return cpu;
            }
        } catch(Exception e) {
            System.out.println("Error retrieving from database " + e);
        }
        return new CPU();
    }

    public static void initTable() {
        String sql =
                "CREATE TABLE IF NOT EXISTS cpus (" +
                "   id INTEGER PRIMARY KEY," +
                "   year INTEGER," +
                "   url TEXT," +
                "   name TEXT," +
                "   cores INTEGER," +
                "   threads INTEGER," +
                "   clock REAL," +
                "   turbo REAL," +
                "   passmark INTEGER" +
                ");";

        try(Connection conn = DriverManager.getConnection(url);
            Statement st = conn.createStatement()) {

            st.execute(sql);
        } catch(Exception e) {
            System.out.println("Error connecting to database " + e);
        }
    }
}
