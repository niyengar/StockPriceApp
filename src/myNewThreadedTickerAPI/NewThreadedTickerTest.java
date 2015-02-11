/*
 * This class has been written to get the stock price of a stock and store into a file.
 * The arguments to be provided by the user are - ticker value, destination, file name, interval in minutes-in that order.
 * This is a stand alone utility that runs according to particular interval provided by user
 * for a period of 9 hours everyday and appends to the file every time it is run. The utility
 * can be kick started every day at a particular time by a script. To handle graceful exits it is made sure
 * that the user has to press a key for the main to stop, once the input is provided the main waits on the other
 * thread and then exits. Entering a key is mandatory to stop. Also Ctrl+C can be used - however not graceful.
 * Also script can be written to start and terminate the utility everyday. 
 * 
 * Note - Make sure that directories or files in destination provided do not have spaces otherwise the cmd prompt takes
 * it as a separate argument. Work around this is that user can provide input in special format with a
 * delimiter in a single string and then we can manipulate the string argument and 
 * pass the parameters accordingly. 
 * 
 * Execution from cmd - java -cp Dir:\NewThreadedTicker.jar mynewThreadedTickerAPI.newThreadedTickerTest arg0 arg1 arg2 arg3
 */


package myNewThreadedTickerAPI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Pattern;

public class NewThreadedTickerTest {
	
	private static String ticker;
	private static String location;
	private static String fileName;
	private static String interval;
	private static volatile boolean running = true;
	
			public static void main(String[] args) {
			   
			    if(args[0]!=null&&args[1]!=null&&args[2]!=null&&args[3]!=null)
			    {
			    	
			    		ticker = args[0];
				    	location = args[1];
				    	fileName = args[2];
				    	interval = args[3];
				    	if(!(Double.parseDouble(interval)<=0))
				    	{
				    	Thread thread = new Thread(new TickerRunner());
				    	try
				    	{
				    		thread.start();
				    		System.out.println("Press a key to stop the utility when you want. Mandatory To press a key to stop");
				    		try {
								System.in.read();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    	}
				    	finally
				    	{
				    		running = false;
				    		try {
								thread.join();
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				    		System.out.println("Main exiting now");
				    	}
				    }
			    	else
			    	{
			    		System.out.println("Time interval cannot be 0 or less than 0");
			    		//instead can also throw a runtime exception
			    	}
			    	}
			    	
			    else
			    {
			    	System.out.println("Problem with arguments");
			    }
			}
			
			
			static class TickerRunner implements Runnable
			{
				URL url;
			    InputStream is = null;
			    BufferedReader br;
			    String line;
			    
			    static final long dayDuration = 9*60*60*1000;

				@Override
				public void run() {
					long duration = (long) (Double.parseDouble(interval)*60*1000);
						int i = 0;
						int iterations = (int) (dayDuration/duration);
				    	try {
				    		url = new URL("http://www.google.com/finance/info?q=NASDAQ%3a"+ticker.toUpperCase());
				    		while(i<=iterations&&running)
				    		{
					        is = url.openStream();  // throws an IOException
					        br = new BufferedReader(new InputStreamReader(is));
					        StringBuilder sb = new StringBuilder();
					        
					        while ((line =br.readLine()) != null) {
					        	sb.append(line);
		
					        }
					        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					        Date date = new Date();
					        Calendar.getInstance().getTimeZone();
							parseInfo(sb.toString(),location,fileName,dateFormat.format(date),TimeZone.getDefault().getDisplayName());
							Thread.sleep(duration);
							i++;
				    		}
					        
					    } catch (MalformedURLException mu) {
					    	//Optionally in catch block we can do System.exit(0) instead of waiting for input from User. 
					    	//But take care of memory leaks.
					    	System.out.println("Problem with URL. Also check Ticker Value provided");
					         mu.printStackTrace();
					    } catch (IOException e) {
					         e.printStackTrace();
					    } catch (InterruptedException e) {
							e.printStackTrace();
						} catch (Exception e) {
							System.out.println(e.getMessage()+" so throwing "+e.getClass());
						} 
					    finally {
					        try {
					            if (is != null) is.close();
					        } catch (IOException e) {
					            e.printStackTrace();
					        }
					    }
					
					}
				
			}
			
			public static synchronized void parseInfo(String line,String destination, String fileName, String date,String tz) throws Exception
			{
				if(line == null)
				{
					System.out.println("Problem in getting string - Empty");
					throw new Exception("Empty String Problem in parseInfo method");
				}
				else
				{
					String [] infoArray = line.split(Pattern.quote(","));
					 if(infoArray.length==0)
					 {
						 System.out.println("Problem in getting data in desired format");
						 throw new Exception("String Format Problem in parseInfo method");
					 }
					 else
					 {
						 File newFile = new File(destination+File.separator+fileName);
						 try(BufferedWriter wr = new BufferedWriter(new FileWriter(newFile,true)))
						 {
								 wr.append("Price: "+(infoArray[3].substring(infoArray[3].indexOf(':')+1)).trim());
								 wr.append(',');
								 wr.append("Trade Time: "+(infoArray[8].substring(infoArray[8].indexOf(':')+1)).trim()+","+infoArray[9].trim());
								 wr.append(',');
								 wr.append("File Write Time: "+date+" "+tz);
								 wr.newLine();
							 
						 } catch (IOException e) {
							System.out.println("File path not found");
							//e.printStackTrace();  Can print better to log - same for others
							throw new Exception("File path not found in parseInfo method");
							//can also create new exception class.
						}
					 }
					 
				}
				 
				
			}

		}
