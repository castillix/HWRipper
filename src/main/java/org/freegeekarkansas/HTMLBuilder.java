package org.freegeekarkansas;

import java.io.FileReader;
import java.util.Scanner;

public class HTMLBuilder {
    public static void main(String[] args) {
        BuildInfo build = HardwareGrabber.getHardware();
        buildHTML(build);
        System.out.println();
    }

    public static void buildHTML(BuildInfo build) {
        FileReader fr = new FileReader("BuildSheet.html");
        Scanner scan = new Scanner(fr);
        StringBuilder sb = new StringBuilder();
        while(scan.hasNextLine()) {
            sb.append(scan.nextLine()).append("\n");
        }
        String html = sb.toString();

        html = html.replace("%CPU_CLOCK%", Double.toString(build.cpu.clock));
    }
}
