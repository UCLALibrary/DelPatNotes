package edu.ucla.library.libservices.voyager;

import edu.ucla.library.libservices.voyager.api.client.CircClient;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;

import java.util.Iterator;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class DelPatNote
{
  public static void main( String[] args )
    throws IOException, ParseException
  {
    Options options = new Options();
    options.addOption("e", true, "error file");
    options.addOption("i", true, "input file");
    options.addOption("p", true, "properties file");
    CommandLineParser parser = new PosixParser();
    CommandLine cmd = parser.parse( options, args);
    if (
        cmd.hasOption("e") &&
        cmd.hasOption("i") &&
        cmd.hasOption("p")
      )
    {
      String errorFile = cmd.getOptionValue("e");
      String inputFile = cmd.getOptionValue("i");      
      String propertiesFile = cmd.getOptionValue("p");      
      
      FileReader fr = new FileReader(inputFile);
      BufferedReader br = new BufferedReader(fr);
      String line;
      ArrayList lines = new ArrayList();
      ArrayList errors = new ArrayList();
      
      while ((line = br.readLine()) != null)
      {
        lines.add(line);
      }
      br.close();
      CircClient cc = new CircClient(propertiesFile);
      for (Iterator it = lines.iterator (); it.hasNext (); ) 
      {
        line = (String) it.next();
        String[] tokens = line.split("\t");
        if (tokens.length == 1)
        {
          String noteId = tokens[0];

          int returnCode = cc.delPatNote(noteId);
					if (returnCode != 0)
					{
						errors.add(Integer.toString(returnCode) + "\t" + line);
					}
        }
        else
        {
          errors.add("-2\t" + line);
        }
      }
      if (errors.size() > 0)
      {
        FileWriter fw = new FileWriter (errorFile) ; 
        BufferedWriter bw = new BufferedWriter(fw);
        for (Iterator it = errors.iterator (); it.hasNext (); ) 
        {
          String error = (String) it.next();
          bw.write(error);
          bw.newLine();
        }
        bw.close();
      }
    }
    else
    {
      HelpFormatter formatter = new HelpFormatter();
      formatter.printHelp( "DelPatNote", options);
    }
  }
}
