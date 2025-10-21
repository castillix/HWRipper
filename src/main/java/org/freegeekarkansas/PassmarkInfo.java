package org.freegeekarkansas;

import oshi.hardware.CentralProcessor;

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.spec.ECField;
import java.util.ArrayList;
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
        // load all cpu urls
        List<String> cpus = Files.readAllLines(Paths.get("out.txt"));
        ArrayList<CPU> allCPUs = new ArrayList<>();

        for(String cpuInfo : cpus) {
//        for(int i = 0; i < 10; i++) {
//            String cpuInfo = cpus.get(i);

            int err = 0;
            CPU cpu = new CPU();

            // set cpu object url/name
            cpu.id = Integer.parseInt(cpuInfo.split("\\|\\|\\|")[0].split("id=")[1]);
            cpu.url = "https://www.cpubenchmark.net/" + cpuInfo.split("\\|\\|\\|")[0];
            cpu.name = cpuInfo.split("\\|\\|\\|")[1];
            URL url = new URI(cpu.url).toURL();

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");

            // get cpu info http as a single string
            Scanner scan = new Scanner(con.getInputStream());
            StringBuilder totalBuilder = new StringBuilder();
            while(scan.hasNextLine()) {
                totalBuilder.append(scan.nextLine());
            }
            String total = totalBuilder.toString();

            // get cpu year
            cpu.year = Integer.parseInt(htmlVal("CPU First Seen on Charts:</strong> ", "</p>", total).split(" ")[1]);

            // narrow down passmark score
            String pms = total.substring(total.indexOf("#F48A18;\">") + 10);
            try {
                cpu.passmark = Integer.parseInt(pms.substring(0, pms.indexOf("</div>")));
            } catch(Exception E) {
                cpu.passmark = -1;
                err = 400;
            }

            // narrow down turbo or write no turbo
            if(total.contains("Turbo Speed")) {
                cpu.turbo = Double.parseDouble(htmlVal("Turbo Speed:</strong> ", " GHz</p>", total));
            } else {
                cpu.turbo = -1;
            }
//            System.out.println(htmlVal("Clockspeed:</strong> ", "</p>", total));
            try {
                cpu.clock = Double.parseDouble(htmlVal("Clockspeed:</strong> ", " GHz</p>", total));
            } catch(Exception e) {
                cpu.clock = -1;
            }

//            System.out.println(htmlVal("Cores:</strong> ", " <strong>", total));
            try {
                cpu.cores = Integer.parseInt(htmlVal("Cores:</strong> ", " ", total));
            } catch(Exception e) {
                cpu.cores = -1;
            }

//            System.out.println(htmlVal("Threads:</strong> ", "</p>", total));
            try {
                cpu.threads = Integer.parseInt(htmlVal("Threads:</strong> ", "</p>", total));
            } catch(Exception e) {
                cpu.threads = -1;
            }

//            System.out.println();
//            count--;
//            if(count <= 0) {
//                return;
//            }
            allCPUs.add(cpu);
            System.out.println(cpu.name + " retrieved[" + cpus.indexOf(cpuInfo) + "]: " + err);
        }

        DBManager.initTable();
        for(CPU cpu : allCPUs) {
            DBManager.insert(cpu);
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
