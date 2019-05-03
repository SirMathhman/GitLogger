package com.meti;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Date;
import java.util.Scanner;

class Main {
    public static void main(String[] args) {
        try {
            run();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void run() throws IOException {
        String username = getUsername();
        Process process = buildProcess(username);
        write(getLogPath(),
                gatherLinesFrom(printErrorPath(process).getInputStream())
        );
    }

    private static Process printErrorPath(Process process) throws IOException {
        String error = gatherLinesFrom(process.getErrorStream()).trim();
        if (error.length() != 0) {
            System.err.println(error);
        }
        return process;
    }

    private static void write(Path path, String output) throws IOException {
        PrintWriter writer = new PrintWriter(Files.newOutputStream(Files.createFile(path)));
        writer.println(output);
        writer.flush();
        writer.close();
    }

    private static String gatherLinesFrom(InputStream inputStream) throws IOException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        reader.lines().peek(builder::append).forEach(s -> builder.append("\n"));
        reader.close();
        return builder.toString();
    }

    private static String getUsername() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter in your Git Username.");
        String username = scanner.nextLine();
        scanner.close();
        return username;
    }

    private static Process buildProcess(String username) throws IOException {
        return Runtime.getRuntime().exec("git log --author=" + username);
    }

    private static Path getLogPath() {
        Path path = Paths.get("log--" + buildDate() + ".txt");
        return Files.exists(path)
                ? getLogPath()
                : path;
    }

    private static String buildDate() {
        return Date.from(Instant.now())
                .toString()
                .replace(":", "_")
                .replace(" ", "");
    }
}
