package org.freegeekarkansas;

import oshi.*;
import oshi.hardware.*;
import oshi.software.os.OperatingSystem;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class HardwareGrabber {
    public static BuildInfo getHardware() {
        SystemInfo si = new SystemInfo();
        OperatingSystem os = si.getOperatingSystem();
        HardwareAbstractionLayer hw = si.getHardware();
        CentralProcessor hcpu = hw.getProcessor();
        List<GraphicsCard> gpus = hw.getGraphicsCards();
        GlobalMemory mem = hw.getMemory();
        List<HWDiskStore> disks = hw.getDiskStores();

//        var a = si.getHardware().getMemory().getPhysicalMemory();
        BuildInfo build = new BuildInfo();

        String cpuStr = hcpu.getProcessorIdentifier().getName();
        System.out.println("Detected CPU: " + cpuStr);
        ArrayList<CPU> cpus = new ArrayList<>();
        for(String term : cpuStr.split(" ")) {
            ArrayList<CPU> temp = DBManager.getCpuFromString("%" + term + "%");
            if(temp.size() < 5) {
                cpus.addAll(temp);
            }
        }

        if(cpus.isEmpty()) {
            System.out.println("Something went wrong finding cpu information!");
            System.out.println("Price shown will not be correct");
            build.cpu = new CPU();
            build.cpu.clock = 1;
            build.cpu.turbo = 1;
            build.cpu.cores = 1;
            build.cpu.threads = 1;
            build.cpu.passmark = 1;
            build.cpu.name = "Not found in passmark";
            build.cpu.year = 2020;
        } else if(cpus.size() == 1) {
            build.cpu = cpus.getFirst();
        } else {
            System.out.println("Detected multiple possible matches, please select correct option: ");
            for (int i = 0; i < cpus.size(); i++) {
                System.out.println("[" + (i + 1) + "] - " + cpus.get(i).name);
            }
            build.cpu = cpus.get(new Scanner(System.in).nextInt() - 1);
        }

        build.sn = si.getHardware().getComputerSystem().getSerialNumber();

        build.ram = Math.round(((double) mem.getTotal() / 107374182.4)) / 10.0;
        try {
            build.ramType = mem.getPhysicalMemory().getFirst().getMemoryType();
        } catch(Exception e) {
            System.out.println("Failed to get RAM type, please enter manually: ");
            System.out.println("1 - DDR3");
            System.out.println("2 - DDR4 (default)");
            System.out.println("3 - DDR5");

            switch(new Scanner(System.in).nextLine()) {
                case "1":
                    build.ramType = "DDR3";
                    break;

                case "3":
                    build.ramType = "DDR5";
                    break;

                default:
                    build.ramType = "DDR4";
                    break;
            }
        }
        System.out.println();


//        build.storage = new ArrayList<String>();
        build.diskSizes = new ArrayList<>();
        build.diskTypes = new ArrayList<>();
        for(HWDiskStore disk : disks) {
            Scanner scan = new Scanner(System.in);
            System.out.println("Current disk: " + disk.getModel());
            System.out.println("Please select disk type: ");
            System.out.println("1 - hdd");
            System.out.println("2 - ssd");
            System.out.println("3 - nvme");
            System.out.println("4 - skip drive (default)");
            boolean skip = false;
            switch(scan.nextLine()) {
                case "1":
                    build.diskTypes.add(1);
                    break;

                case "2":
                    build.diskTypes.add(2);
                    break;

                case "3":
                    build.diskTypes.add(3);
                    break;

                default:
                    skip = true;
                    break;
            }
            if(!skip) {
                build.diskSizes.add((int)(Math.round(((double) disk.getSize() / 107374182.4)) / 10));
            }
            System.out.println();
        }

        build.gpu = gpus.getFirst().getName();
        build.os = os.toString();

        return build;
    }

    public static void getPrice(BuildInfo build) {
        Scanner scan = new Scanner(System.in);

        double ramPrice = build.ram;
        switch(build.ramType) {
            case "DDR3":
                ramPrice *= 1.5;
                break;

            case "DDR4":
                ramPrice *= 2.5;
                break;

            case "DDR5":
                ramPrice *= 6;
                break;
        }

        double drivePrice = 0;
        for(int i = 0; i < build.diskTypes.size(); i++) {
            switch(build.diskTypes.get(i)) {
                case 1:
                    drivePrice += build.diskSizes.get(i) * 0.02;
                    break;

                case 2:
                    drivePrice += build.diskSizes.get(i) * 0.08;
                    break;

                case 3:
                    drivePrice += build.diskSizes.get(i) * 0.1;
                    break;
            }
        }

        System.out.println("Current GPU detected: " + build.gpu);
        System.out.println("Please enter GPU price (default 0): ");
        int gpuPrice = 0;
        try {
            gpuPrice = Integer.parseInt(scan.nextLine());
            System.out.println(gpuPrice);
        } catch(Exception e) {
//            System.out.println("Error parsing input, defaulting GPU price to 0");
        }
        System.out.println();

        boolean laptop = false;
        System.out.println("Is this a (L)aptop or a (D)esktop? (default is desktop)");
        try {
            if(new Scanner(System.in).nextLine().equalsIgnoreCase("l")) {
                gpuPrice += 10;
                laptop = true;

                boolean good = false;
                System.out.println();
                System.out.println("Please enter laptop screen size: ");
                build.screenSize = scan.nextLine();
                System.out.println();
                System.out.println("Please enter laptop battery duration in hours (or press enter to leave blank): ");
                build.batteryLife = scan.nextLine();
                System.out.println();
                System.out.println("Please enter laptop battery health percentage (or press enter to leave blank): ");
                build.batteryHealth = scan.nextLine();
            }
        } catch(Exception e) {
            System.out.println("Error parsing input, defaulting to desktop");
        }
        System.out.println();

        System.out.println("Please enter computer name/description: ");
        build.description = scan.nextLine();
        if(build.description.isEmpty()) {
            build.description = "Computer Name";
        }
        System.out.println();

        System.out.println("Detected S/N [" + build.sn + "], please enter new S/N or leave blank to keep default: ");
        String sn = scan.nextLine();
        if(!sn.isEmpty()) {
            build.sn = sn;
            System.out.println("Keeping default S/N");
        }
        System.out.println();

        System.out.println("Please enter any other relevant notes about the computer: ");
        build.hardwareNotes = scan.nextLine();
        if(build.hardwareNotes.isEmpty()) {
            build.hardwareNotes = "N/A";
        }
        System.out.println();

        System.out.println("Please enter any other notes about the software:");
        build.softwareNotes = scan.nextLine();
        if(build.softwareNotes.isEmpty()) {
            build.softwareNotes = "N/A";
        }
        System.out.println();

        System.out.println("Please enter builder name: ");
        build.builder = scan.nextLine();
        System.out.println();

        build.buildDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy"));


        double yearPrice = (build.cpu.year - 2012) * ((laptop) ? 6 : 10);
        double corePrice = build.cpu.cores * (yearPrice * 0.025);
        double threadPrice = (build.cpu.threads - build.cpu.cores) * 0.75;
        double passmark = build.cpu.passmark;
        double turbo = (build.cpu.turbo == -1) ? build.cpu.clock : build.cpu.turbo;


        double osModifier = 0;
        double temp = ((((corePrice + threadPrice) * turbo) + yearPrice) + ramPrice + drivePrice + gpuPrice);
        if(build.os.toLowerCase().contains("microsoft")) {
            osModifier = 0;
        } else if(build.os.toLowerCase().contains("linux")) {
            osModifier = ((0.85 * temp) - temp);
        } else if(build.os.toLowerCase().contains("macos")) {
            osModifier = ((1.2 * temp) - temp);
        }
        System.out.println(build.os);
        System.out.println();

        double cpuPrice = (laptop) ?
                (((corePrice + threadPrice) * turbo) + yearPrice) * (passmark/5813) :
                (((corePrice + threadPrice) * turbo) + yearPrice) * ((passmark/9530) * 0.67);

        double finalPrice = (cpuPrice + ramPrice + drivePrice + gpuPrice + osModifier) + 40;
        System.out.println("laptop: " + laptop);
        System.out.println("cpuPrice: " + cpuPrice);
        System.out.println("corePrice: " + corePrice);
        System.out.println("threadPrice: " + threadPrice);
        System.out.println("turbo: " + turbo);
        System.out.println("yearPrice: " + yearPrice);
        System.out.println("passmark: " + passmark);

        System.out.println("ramPrice: " + ramPrice);
        System.out.println("drivePrice: " + drivePrice);
        System.out.println("gpuPrice: " + gpuPrice);
        System.out.println("osModifier: " + osModifier);

        build.price = (int) Math.floor(finalPrice);
    }
}
