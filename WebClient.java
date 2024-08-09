
/**
 * WebClient Class
 * 
 * CPSC 441
 * Assignment 2
 * Abrar Zawad Safwan
 * 301508932
 * CPSC 441 Assignment 1
 * @author 	Majid Ghaderi
 * @version	2024
 *
 */

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.*;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class WebClient {

    private static final Logger logger = Logger.getLogger("WebClient"); // global logger
    InputStream inputStream;
    OutputStream outputStream;
    Socket socket;
    String response_stat_header; // status and header line

    /**
     * Default no-arg constructor
     */
    public WebClient() {
        // nothing to do!
    }

    /**
     * Downloads the object specified by the parameter url.
     *
     * @param url URL of the object to be downloaded. It is a fully qualified URL.
     */
    public void getObject(String url) {

        // 1: Parse url to extract protocol, server name, and object path
        String protocol, object_path, server_name;
        String[] parsed_URL = url.split("://|/", 3);
        // protocol://hostname[:port]/pathname
        protocol = parsed_URL[0];
        server_name = parsed_URL[1];
        object_path = parsed_URL[2];

        // 2.3 Establishing TCP Connection

        try { // 2: if protocol == HTTP then // 3: Establish a regular TCP connection to the
              // server
            if (protocol.equalsIgnoreCase("http")) {
                socket = new Socket(server_name, 80);

            } else { // for https secure connection
                SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
                socket = (SSLSocket) factory.createSocket(server_name, 443);

            }
            // Streams
            OutputStream outputStream = socket.getOutputStream();
            InputStream inputStream = socket.getInputStream();

            // 2.4 Constructing GET Request

            String get_request = "GET /" + object_path + " HTTP/1.1\r\n" + "Host: " + server_name + "\r\n"
                    + "Connection: close\r\n" + "\r\n";

            System.out.println(" \n");// for nice formatting seperatimng the get request with downloading statement
            // HTTP request (request line and header lines) PRitning
            System.out.println(get_request); // printing Http requeest line and headers

            // 7: Send a GET request for the specified object
            byte[] String_to_byte_GetR = get_request.getBytes("US-ASCII");
            // sending to output stream
            outputStream.write(String_to_byte_GetR);
            outputStream.flush();

            // 8: Read the server response status and header lines
            int numBytes;
            byte[] response_body = new byte[4096]; // for reading beyond header line
            char status_line; // reads the status and header line in char
            response_stat_header = ""; // contains string format of the status and header

            // extracting status
            while ((numBytes = inputStream.read()) != -1) {
                status_line = (char) numBytes;// changing to char to add it with string
                response_stat_header += status_line; // adss by character
                if (response_stat_header.contains("\r\n\r\n")) // stops when the status and header line finishes
                    break;
            }

            // printing status and header line
            System.out.println(response_stat_header);

            // 9: if response status == OK then
            // 11: Read the response body from the socket and write to the local file
            // 12: end if
            if (response_stat_header.contains("200 OK")) {
                // 10: Create a local file with the object name
                // file name extract
                String file_name = object_path.substring(object_path.lastIndexOf('/') + 1);
                FileOutputStream file_OutputStream = new FileOutputStream(file_name);
                // read from respnse body and write to the file
                while ((numBytes = inputStream.read(response_body)) != -1) {
                    file_OutputStream.write(response_body, 0, numBytes);
                }
                file_OutputStream.close();
            } else { // error handling
                System.out.println("Bad Request.");
                System.out.println("No File was created.");
            }
            // cleanup
            socket.close();
            outputStream.close();
            inputStream.close();

        } catch (Exception e) {
            System.out.println("Error while truing to connect to server: " + e.getMessage());
            System.out.println("No File was created.");
            e.printStackTrace();

        }

    }// get object closed

}