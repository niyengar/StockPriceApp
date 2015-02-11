StockPriceApp
=============
This is a command line utility that has been written to get the stock price of a stock and store into a file.
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
 * Note - This utility uses Google Finance API to extract the required data instead of web scraping as that is not efficient.
 */