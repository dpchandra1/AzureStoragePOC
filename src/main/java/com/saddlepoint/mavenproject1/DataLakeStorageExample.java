package com.saddlepoint.mavenproject1;

import com.microsoft.azure.datalake.store.ADLException;
import com.microsoft.azure.datalake.store.ADLStoreClient;
import com.microsoft.azure.datalake.store.DirectoryEntry;
import com.microsoft.azure.datalake.store.IfExists;
import com.microsoft.azure.datalake.store.oauth2.AccessTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.ClientCredsTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.DeviceCodeTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.RefreshTokenBasedTokenProvider;
import com.microsoft.azure.datalake.store.oauth2.UserPasswordTokenProvider;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class DataLakeStorageExample {

    // This needs to be filled in for the app to work
    private static String accountFQDN = "purnadatalake1.azuredatalakestore.net";  // full account FQDN, not just the account name
    private static String clientId = "763e07d5-4c99-431a-95e5-a363ebafff49";
    private static String authTokenEndpoint = "https://login.microsoftonline.com/788bd3cf-0a0b-41e6-a8f0-5b1e2cbdca2a/oauth2/token";
    private static String clientKey = "BFS-y5XP@-]9vCyjOdix3G5FM-Gt1/7.";

    private static String nativeAppId = clientId;

    public static void main(String[] args) {
        try {
            // Create client object using client creds
            AccessTokenProvider provider = new ClientCredsTokenProvider(authTokenEndpoint, clientId, clientKey);
//            AccessTokenProvider provider = new DeviceCodeTokenProvider(nativeAppId);
            ADLStoreClient client = ADLStoreClient.createClient(accountFQDN, provider);

            // create directory
            client.createDirectory("/a/b/w");

            // create file and write some content
            String filename = "/a/b/c.txt";
            OutputStream stream = client.createFile(filename, IfExists.OVERWRITE);
            PrintStream out = new PrintStream(stream);
            for (int i = 1; i <= 10; i++) {
                out.println("This is line #" + i);
                out.format("This is the same line (%d), but using formatted output. %n", i);
            }
            out.close();

            // set file permission
            client.setPermission(filename, "744");

            // append to file
            stream = client.getAppendStream(filename);
            stream.write(getSampleContent());
            stream.close();

            // Read File
            InputStream in = client.getReadStream(filename);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
            reader.close();
            System.out.println();

            // get file metadata
            DirectoryEntry ent = client.getDirectoryEntry(filename);
            printDirectoryInfo(ent);

            // create another file - this time using a byte array
            stream = client.createFile("/a/b/d.txt", IfExists.OVERWRITE);
            byte[] buf = getSampleContent();
            stream.write(buf);
            stream.close();

            // concatenate the two files into one
            List<String> fileList = Arrays.asList("/a/b/c.txt", "/a/b/d.txt");
            client.concatenateFiles("/a/b/f.txt", fileList);

            //rename the file
            client.rename("/a/b/f.txt", "/a/b/g.txt");

            // list directory contents
            List<DirectoryEntry> list = client.enumerateDirectory("/a/b", 2000);
            System.out.println("Directory listing for directory /a/b:");
            for (DirectoryEntry entry : list) {
                printDirectoryInfo(entry);
            }

            // delete directory along with all the subdirectories and files in it
//            client.deleteRecursive("/a");

        } catch (ADLException ex) {
            printExceptionDetails(ex);
        } catch (Exception ex) {
            System.out.format(" Exception: %s%n Message: %s%n", ex.getClass().getName(), ex.getMessage());
        }
    }

    private static void printExceptionDetails(ADLException ex) {
        System.out.println("ADLException:");
        System.out.format("  Message: %s%n", ex.getMessage());
        System.out.format("  HTTP Response code: %s%n", ex.httpResponseCode);
        System.out.format("  Remote Exception Name: %s%n", ex.remoteExceptionName);
        System.out.format("  Remote Exception Message: %s%n", ex.remoteExceptionMessage);
        System.out.format("  Server Request ID: %s%n", ex.requestId);
        System.out.println();
    }

    private static void printDirectoryInfo(DirectoryEntry ent) {
        System.out.format("Name: %s%n", ent.name);
        System.out.format("  FullName: %s%n", ent.fullName);
        System.out.format("  Length: %d%n", ent.length);
        System.out.format("  Type: %s%n", ent.type.toString());
        System.out.format("  Group: %s%n", ent.group);
        System.out.format("  User: %s%n", ent.user);
        System.out.format("  Permission: %s%n", ent.permission);
        System.out.format("  mtime: %s%n", ent.lastModifiedTime.toString());
        System.out.format("  atime: %s%n", ent.lastAccessTime.toString());
        System.out.println();
    }

    private static byte[] getSampleContent() {
        ByteArrayOutputStream s = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(s);
        out.println("This is a line");
        out.println("This is another line");
        out.println("This is yet another line");
        out.println("This is yet yet another line");
        out.println("This is yet yet yet another line");
        out.println("... and so on, ad infinitum");
        out.println();
        out.close();
        return s.toByteArray();
    }
}
