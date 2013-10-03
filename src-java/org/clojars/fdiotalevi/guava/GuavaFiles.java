package org.clojars.fdiotalevi.guava;

import java.io.File;

/**
 * This class contains code extracted from Guava Libraries,
 * under the Apache Software License
 */
public final class GuavaFiles {
   
   /** Maximum loop count when creating temp directories. */
   private static final int TEMP_DIR_ATTEMPTS = 10000;

   private GuavaFiles() {}   

   /**
     * Atomically creates a new directory somewhere beneath the system's
     * temporary directory (as defined by the {@code java.io.tmpdir} system
     * property), and returns its name.
     *
     * <p>Use this method instead of {@link File#createTempFile(String, String)}
     * when you wish to create a directory, not a regular file.  A common pitfall
     * is to call {@code createTempFile}, delete the file and create a
     * directory in its place, but this leads a race condition which can be
     * exploited to create security vulnerabilities, especially when executable
     * files are to be written into the directory.
     *
     * <p>This method assumes that the temporary volume is writable, has free
     * inodes and free blocks, and that it will not be called thousands of times
     * per second.
     *
     * @return the newly-created directory
     * @throws IllegalStateException if the directory could not be created
     */
    public static File createTempDir() {
      File baseDir = new File(System.getProperty("java.io.tmpdir"));
      String baseName = System.currentTimeMillis() + "-";

      for (int counter = 0; counter < TEMP_DIR_ATTEMPTS; counter++) {
        File tempDir = new File(baseDir, baseName + counter);
        if (tempDir.mkdir()) {
          return tempDir;
        }
      }
      throw new IllegalStateException("Failed to create directory within "
          + TEMP_DIR_ATTEMPTS + " attempts (tried "
          + baseName + "0 to " + baseName + (TEMP_DIR_ATTEMPTS - 1) + ')');
    }
    
 }