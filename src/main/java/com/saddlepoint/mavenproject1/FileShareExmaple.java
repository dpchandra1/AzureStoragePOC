/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.saddlepoint.mavenproject1;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.file.CloudFile;
import com.microsoft.azure.storage.file.CloudFileClient;
import com.microsoft.azure.storage.file.CloudFileDirectory;
import com.microsoft.azure.storage.file.CloudFileShare;
import com.microsoft.azure.storage.file.ListFileItem;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dpurna
 */
public class FileShareExmaple {

    // Configure the connection-string with your values
    public static final String storageConnectionString
            = "DefaultEndpointsProtocol=https;"
            + "AccountName=purnastorageaccount;"
            + "AccountKey=I5NxmrYjn4fDRfITAAS0raT1L9YmLGiI6U6NtEK/+4HRjJlXPYOo7K1FwIIJpxIQxI98qI8wrYgPT0GLFgEGRA==;"
            + "EndpointSuffix=core.windows.net";
    private static CloudStorageAccount storageAccount;
    private static CloudFileClient fileClient;
    private static CloudFileShare share;

    public FileShareExmaple() {
        try {
            // Use the CloudStorageAccount object to connect to your storage account
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
            // Create the Azure Files client.
            fileClient = storageAccount.createCloudFileClient();
            // Get a reference to the file share
            share = fileClient.getShareReference("samplefileshare");
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidKeyException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String args[]) {
        FileShareExmaple example = new FileShareExmaple();
        example.createAzureFileShare();
//        example.deleteAzureFileShare();
        example.createDirecatory();
//        example.deleteDirectory();
        example.enumerateFiles();
        example.uploadFile();
        example.downloadFile();
//        example.deleteFile();
    }

    private void createAzureFileShare() {
        try {
            System.out.println("Share exists ? "+share.exists());
            if (share.createIfNotExists()) {
                System.out.println("New share created");
            }
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteAzureFileShare() {
        try {
            if (share.deleteIfExists()) {
                System.out.println("sampleshare deleted");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createDirecatory() {
        try {
            //Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();

            //Get a reference to the sampledir directory
            CloudFileDirectory sampleDir = rootDir.getDirectoryReference("sampledir");

            if (sampleDir.createIfNotExists()) {
                System.out.println("sampledir created");
            } else {
                System.out.println("sampledir already exists");
            }
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteDirectory() {
        try {
            // Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();

            // Get a reference to the directory you want to delete
            CloudFileDirectory containerDir = rootDir.getDirectoryReference("sampledir");

            // Delete the directory
            if (containerDir.deleteIfExists()) {
                System.out.println("Directory deleted");
            }
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void enumerateFiles() {
        try {
            //Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();
            for (ListFileItem fileItem : rootDir.listFilesAndDirectories()) {
                System.out.println(fileItem.getUri());
            }
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void uploadFile() {
        try {
            //Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();
            //Get a reference to the directory that contains the file
            CloudFileDirectory sampleDir = rootDir.getDirectoryReference("sampledir");
            // Define the path to a local file.
            final String filePath = "D:\\BitBucket\\Repos\\TMS\\TMS_Microservices\\sp-tms-view\\logs\\server.log";
            CloudFile cloudFile = sampleDir.getFileReference("Readme.txt");
            cloudFile.uploadFromFile(filePath);
            System.out.println("File created.");
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void downloadFile() {
        try {
            //Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();
            //Get a reference to the directory that contains the file
            CloudFileDirectory sampleDir = rootDir.getDirectoryReference("sampledir");
            //Get a reference to the file you want to download
            CloudFile file = sampleDir.getFileReference("Readme.txt");
            //Write the contents of the file to the console.
            System.out.println(file.downloadText());
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void deleteFile() {
        try {
            // Get a reference to the root directory for the share.
            CloudFileDirectory rootDir = share.getRootDirectoryReference();
            // Get a reference to the directory where the file to be deleted is in
            CloudFileDirectory containerDir = rootDir.getDirectoryReference("sampledir");
            String filename = "Readme.txt";
            CloudFile file;
            file = containerDir.getFileReference(filename);
            if (file.deleteIfExists()) {
                System.out.println(filename + " was deleted");
            }
        } catch (StorageException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        } catch (URISyntaxException ex) {
            Logger.getLogger(FileShareExmaple.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
