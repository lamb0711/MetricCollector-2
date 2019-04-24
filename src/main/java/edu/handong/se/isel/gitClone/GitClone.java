package edu.handong.se.isel.gitClone;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class GitClone {

	int startNumberOfPage = 1;
	String resultDirectory = null;
	boolean verbose;
	boolean help;
	boolean roop = true;
	private static URL url; //URL address
	public ArrayList<String> eachAddress;
	public HttpURLConnection code; //Connection with wep
	public BufferedReader br; // Read URL code line
	private String protocol = "GET";
	Random r = new Random();

	public ArrayList<String> addresses;

	private void getURL() throws IOException {
		int maxPage = startNumberOfPage+2;
		addresses = new ArrayList<String>();
		
		while(maxPage != startNumberOfPage) {
			System.out.println(startNumberOfPage);
			String address = "https://api.github.com/search/repositories?q=language:java&sort=stars&order=desc&page="+startNumberOfPage;

			try {
				url = new URL(address); // Make URL object
				eachAddress = new ArrayList<String>();
				code = (HttpURLConnection)url.openConnection(); // Connection with this address
				code.setRequestMethod(protocol);

				br = new BufferedReader(new InputStreamReader(code.getInputStream()));

				String line= br.readLine();
				Pattern pattern = Pattern.compile(".+\":\"(.+//.+/.+/.+)\"");

				String[] tempStr = line.split(","); 
				for (int i = 0; i < tempStr.length; i++) { 
					if(tempStr[i].contains("html_url")) {
						Matcher matcher = pattern.matcher(tempStr[i]);
						while(matcher.find()) {
							addresses.add(matcher.group(1)+".git");
						}

					}
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			}

			br.close();
			startNumberOfPage++;
			int randomNumber=r.nextInt(3000);
			try {
				Thread.sleep(randomNumber);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
 

	private void printGitCloneAddress() {
		
		for(int i=0; i<addresses.size(); i++) {
			System.out.println(addresses.get(i));
//			String command = "'"+resultDirectory+"/git clone "+addresses.get(i)+"'";
//			try {
//				Process process = new ProcessBuilder(command).start();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}

	public static void main(String[] args){
		GitClone runner = new GitClone();
		runner.run(args);
	}

	private void run(String[] args) {
		Options options = createOptions();

		if(parseOptions(options, args)){
			if (help){
				printHelp(options);
				return;
			}

			try {
				getURL();
				printGitCloneAddress();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
			if(verbose) {
				System.out.println("Your program is terminated. (This message is shown because you turned on -v option!");
			}
		}
	}

	private boolean parseOptions(Options options, String[] args) {
		CommandLineParser parser = new DefaultParser();

		try {

			CommandLine cmd = parser.parse(options, args);

			startNumberOfPage =  Integer.parseInt(cmd.getOptionValue("n"));
			resultDirectory = cmd.getOptionValue("o");
			help = cmd.hasOption("h");

		} catch (Exception e) {
			printHelp(options);
			return false;
		}

		return true;
	}

	// Definition Stage
	private Options createOptions() {
		Options options = new Options();

		// add options by using OptionBuilder
		options.addOption(Option.builder("n").longOpt("start number of page").desc(
				"Three input type: URL or URI(github.com, reference file having github URLs, Local " + "Repository)")
				.hasArg().argName("URI or URL").required().build());

		options.addOption(Option.builder("o").longOpt("result").desc("directory will have result file")
				.argName("directory").build());

		options.addOption(Option.builder("h").longOpt("help").desc("Help").build());

		return options;
	}

	private void printHelp(Options options) {
		// automatically generate the help statement
		HelpFormatter formatter = new HelpFormatter();
		String header = "Collecting bug-patch program";
		String footer = "\nPlease report issues at https://github.com/HGUISEL/BugPatchCollector/issues";
		formatter.printHelp("BugPatchCollector", header, options, footer, true);
	}
	

}
