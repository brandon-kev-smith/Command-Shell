import java.io.*;
import java.lang.*;
import java.util.ArrayList;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.text.SimpleDateFormat;

public class Assign3 {

    private static long timeSpent= 0;

    public static void main(String[] args)throws IOException {
        String commandLine;

        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        ArrayList<String> hist = new ArrayList<>();

        while (true) {
            String currentDir = System.getProperty("user.dir");
            System.out.print("[" + currentDir + "]: ");
            commandLine = console.readLine();

            if (commandLine.equals("")){
                continue;
            }
            else if(commandLine.equals("exit")){
                System.exit(0);
            }

            String[] params = splitCommand(commandLine);
            hist.add(commandLine);

            execute(params, hist);

        }
    }

    public static void execute(String[] params, ArrayList<String> hist){
        if (params.length > 4 && params[2].equals("|")){
            pipe(params);
        }
        else if(params[0].equals("history")) { history(hist); }
        else if (params[0].equals("list")){ list(); }

        else if (params[0].equals("^")){
            if (params.length < 2){
                System.out.println(params[0] + " requires an argument");
            }
            repeat(hist, params);

        }
        else if (params[0].equals("ptime")){
            float f = (float)timeSpent/1000;
            System.out.printf("total time in child processes: %.4f seconds%n", f);
        }
        else if (params[0].equals("cd")){
            changeDir(params);
        }
        else{
            execProcess(params);
        }
    }

    public static void execProcess(String[] args) {

        boolean waitFor = true;

        if (args[args.length-1].equals("&") && args.length > 1) {
            String[] nArgs = new String[args.length-1];
            for (int i = 0; i < args.length - 1; i++){
                nArgs[i] = args[i];
            }
            args = nArgs;
            waitFor = false;
        }
        ProcessBuilder pb = new ProcessBuilder(args); //takes (executable, parameters)
        File currentDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        pb.directory(currentDir);

        pb.redirectInput(ProcessBuilder.Redirect.INHERIT); // change defaults of "pipes"

        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        try{
            Process p = pb.start(); //starts the process

            try{

                long start = System.currentTimeMillis(); // get time in milliseconds

                if (waitFor){
                    p.waitFor(); //wait for process to finish.
                }

                long end = System.currentTimeMillis();

                timeSpent += end - start;

            }

            catch (InterruptedException ex) {

                System.out.println("Illegal command");
            }

        }

        catch (IOException ex) {

            System.out.println("Illegal command");
        }
    }

    public static void history(ArrayList<String> hist){
        System.out.println("-- Command History --");
        for (int i =1; i <= hist.size(); i++){
            System.out.println(i + ". " + hist.get(i-1));
        }
    }

    public static void repeat(ArrayList<String> hist, String[] params){
        //test for integer value
        try{
            Integer.parseInt(params[1]);
        }catch (NumberFormatException ex){
            System.out.println(params[1] + " is a invalid argument");
            return;
        }
        int number = Integer.parseInt(params[1]);
        if (number > hist.size()){
            System.out.println(params[1] + " is a invalid argument");
            return;
        }
        String exe = hist.get(number-1);
        String[] newParams = splitCommand(exe);
        execute(newParams, hist);
    }

    public static void changeDir(String[] args){
        if(args.length == 1){
            File directory = new File(System.getProperty("user.home")).getAbsoluteFile().getParentFile();
            System.setProperty("user.dir", directory.getAbsolutePath());
        }
        else if(args[1].equals("..")){
            File directory = new File(System.getProperty("user.dir")).getAbsoluteFile().getParentFile();
            System.setProperty("user.dir", directory.getAbsolutePath());
        }
        else{
            File directory = new File(System.getProperty("user.dir")+ "/" +args[1]).getAbsoluteFile();
            if (directory.isDirectory()){
                System.setProperty("user.dir", directory.getAbsolutePath());
            }
        }
    }

    public static void list(){
        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();

        for (File currFile : listOfFiles){
            if (currFile.isFile()){
                System.out.print("-");
            }
            else if (currFile.isDirectory()){
                System.out.print("d");
            }
            else{
                continue;
            }
            if (currFile.canRead()){
                System.out.print("r");
            }
            else{
                System.out.print("-");
            }
            if (currFile.canWrite()){
                System.out.print("w");
            }
            else {
                System.out.print("-");
            }
            if (currFile.canExecute()){
                System.out.print("x");
            }
            else {
                System.out.print("-");
            }
            String fileSize = String.valueOf(currFile.length());
            System.out.printf("%10s ", fileSize);
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            System.out.print(sdf.format(currFile.lastModified()));
            System.out.println(" " + currFile.getName());
        }
    }

    public static void pipe(String[] params){
        String[] p1Cmd = {params[0], params[1]};
        String[] p2Cmd = {params[3], params[4]};

        ProcessBuilder pb1 = new ProcessBuilder(p1Cmd);
        ProcessBuilder pb2 = new ProcessBuilder(p2Cmd);

        pb1.redirectInput(ProcessBuilder.Redirect.INHERIT);
        pb2.redirectOutput(ProcessBuilder.Redirect.INHERIT);

        File currentDir = new File(System.getProperty("user.dir")).getAbsoluteFile();
        pb1.directory(currentDir);
        pb2.directory(currentDir);

        try{
            Process p1 = pb1.start();
            Process p2 = pb2.start();

            java.io.InputStream in = p1.getInputStream();
            java.io.OutputStream out = p2.getOutputStream();

            int c;
            while ((c = in.read()) != -1){
                out.write(c);
            }

            out.flush();
            out.close();

            p1.waitFor();
            p2.waitFor();
        }
        catch (Exception ex){
            System.out.println("invalid parameters");
        }

    }

    /**
     * Split the user command by spaces, but preserving them when inside double-quotes.
     * Code Adapted from: https://stackoverflow.com/questions/366202/regex-for-splitting-a-string-using-space-when-not-surrounded-by-single-or-double
     */
    public static String[] splitCommand(String command) {
        java.util.List<String> matchList = new java.util.ArrayList<>();

        Pattern regex = Pattern.compile("[^\\s\"']+|\"([^\"]*)\"|'([^']*)'");
        Matcher regexMatcher = regex.matcher(command);
        while (regexMatcher.find()) {
            if (regexMatcher.group(1) != null) {
                // Add double-quoted string without the quotes
                matchList.add(regexMatcher.group(1));
            } else if (regexMatcher.group(2) != null) {
                // Add single-quoted string without the quotes
                matchList.add(regexMatcher.group(2));
            } else {
                // Add unquoted word
                matchList.add(regexMatcher.group());
            }
        }
        return matchList.toArray(new String[matchList.size()]);
    }

}

