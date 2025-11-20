package org.freegeekarkansas;

import java.io.FileReader;
import java.util.List;
import java.util.Scanner;

public class HTMLBuilder {
    public static void main(String[] args) throws Exception {
        BuildInfo build = HardwareGrabber.getHardware();
        buildHTML(build);
        System.out.println();
    }

    public static String buildHTML(BuildInfo build) throws Exception {
        FileReader fr = new FileReader("BuildSheet.html");
        Scanner scan = new Scanner(fr);
        StringBuilder sb = new StringBuilder();
        while(scan.hasNextLine()) {
            sb.append(scan.nextLine()).append("\n");
        }
        String html = sb.toString();

        
        html = html.replace("%CPU_NAME%", build.cpu.name);
        html = html.replace("%CPU_CORES%", Integer.toString(build.cpu.cores));
        html = html.replace("%CPU_THREADS%", Integer.toString(build.cpu.threads));
        html = html.replace("%CPU_CLOCK%", Double.toString(build.cpu.clock));
        html = html.replace("%PRICE%", Integer.toString(build.price));
        html = html.replace("%DESCRIPTION%", build.description);
        html = html.replace("%SN%", build.sn);
        html = html.replace("%RAM_AMNT%", Integer.toString((int)Math.ceil(build.ram)));

        StringBuilder allDisksBuilder = new StringBuilder();
        for(int i = 0; i < build.diskTypes.size(); i++) {
            allDisksBuilder.append(getStorageString(build.diskSizes, build.diskTypes, i));
            if(i < build.diskTypes.size() - 1) {
                allDisksBuilder.append(", ");
            }
        }
        html = html.replace("%STORAGE_AMNT%", allDisksBuilder.toString());
        html = html.replace("%SCREEN_SIZE%", build.screenSize);
        html = html.replace("%BATTERY_LIFE%", build.batteryLife);
        html = html.replace("%BATTERY_HEALTH%", build.batteryHealth);
        html = html.replace("%HARDWARE_NOTES%", build.hardwareNotes);
        html = html.replace("%OS_NAME%", build.os.substring(build.os.indexOf(" "))/*.split("build")[0]*/);
        html = html.replace("%SOFTWARE_NOTES%", build.softwareNotes);
        html = html.replace("%BUILDER%", build.builder);
        html = html.replace("%BUILD_DATE%", build.buildDate);

        return html;
    }

    public static String getStorageString(List<Integer> diskSizes, List<Integer> diskTypes, int i) {
        return diskSizes.get(i) + " GB " + ((diskTypes.get(i) == 1) ? "HDD" : (diskTypes.get(i) == 2) ? "SSD" : "NVMe");
    }
}