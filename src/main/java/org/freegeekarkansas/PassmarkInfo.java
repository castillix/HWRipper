package org.freegeekarkansas;

import oshi.hardware.CentralProcessor;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class PassmarkInfo {
    String cpuName;
    String clock;
    String clockTurbo;
    int cores;
    int threads;

    int passmark;
    int passmarkSingle;

    public PassmarkInfo(CentralProcessor cpu) throws Exception {
//        this.loadPassmarkInfo(getCpuUrl(cpu.getProcessorIdentifier().getName()));
    }

    public String getCpuUrl(String cpuName) throws Exception {
        Scanner scan = new Scanner(new File("out.txt"));
        String bestLine = "";
        int bestScore = -1;

        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            int score = 0;

            String[] words = cpuName.split(" ");
            for(String word : words) {
                if(line.contains(word)) {
                    score++;
                }
            }
            if(score > bestScore) {
                bestLine = line;
                bestScore = score;
            }
        }
        return "https://www.cpubenchmark.net/" + bestLine.split("\\|\\|\\|")[0];
    }

    public static void updatePassmarkList() throws Exception {
        URL url = new URI("https://www.cpubenchmark.net/cpu_list.php").toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        Scanner scan = new Scanner(con.getInputStream());
        FileWriter fw = new FileWriter(new File("out.txt"));
        while(scan.hasNextLine()) {
            String line = scan.nextLine();
            if(line.contains("<tr id=\"cpu")) {
                line = line.substring(line.indexOf("<a href=\"") + 9, line.indexOf("</a></td><td>"));
                line = line.replace("cpu_lookup.php", "cpu.php");
                line = line.replace("&amp;", "&");
                line = line.replace("\">", "|||");
                fw.write(line);
                fw.write("\n");
            }
        }
        fw.flush();
        fw.close();
    }

    public static void loadPassmarkInfo() throws Exception {
        List<String> cpus = Files.readAllLines(Paths.get("out.txt"));
        int count = 10;
        for(String cpu : cpus) {
            System.out.println(cpu);
            URL url = new URI("https://www.cpubenchmark.net/" + cpu.split("\\|\\|\\|")[0]).toURL();
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            Scanner scan = new Scanner(con.getInputStream());
            StringBuilder totalBuilder = new StringBuilder();
            while(scan.hasNextLine()) {
                totalBuilder.append(scan.nextLine());
            }
            String total = totalBuilder.toString();

            String pms = total.substring(total.indexOf("#F48A18;\">") + 10);
            pms = pms.substring(0, pms.indexOf("</div>"));
            System.out.println(pms);

            if(total.contains("Turbo Speed")) {
                System.out.println(htmlVal("Turbo Speed:</strong> ", "</p>", total));
            } else {
                System.out.println("No Turbo Speed");
            }

            System.out.println(htmlVal("Clockspeed:</strong> ", "</p>", total));

            System.out.println(htmlVal("Cores:</strong> ", " <strong>", total));

            System.out.println(htmlVal("Threads:</strong> ", "</p>", total));

            System.out.println();
            count--;
            if(count <= 0) {
                return;
            }
        }
    }

    public static String htmlVal(String start, String end, String source) {
        String val = source.substring(source.indexOf(start) + start.length());
        val = val.substring(0, val.indexOf(end));
        return val;
    }

//    private URL getCPUUrl(String cpuName) {
//
//    }
}
