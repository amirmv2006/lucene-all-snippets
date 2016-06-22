package ir.amv.snippets.lucene.textfile.indexer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.SimpleFSDirectory;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Date;

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Indexer {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: java " + Indexer.class.getName()
                    + " <index dir> <data dir>");
        }
        File indexDir = new File(args[0]);
        File dataDir = new File(args[1]);
        long start = new Date().getTime();
        int numIndexed = index(indexDir, dataDir);
        long end = new Date().getTime();
        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + " milliseconds");
    }

    // open an index and start file directory traversal
    public static int index(File indexDir, File dataDir)
            throws IOException {
        if (!dataDir.exists() || !dataDir.isDirectory()) {
            throw new IOException(dataDir
                    + " does not exist or is not a directory");
        }
        IndexWriter writer = new IndexWriter(new SimpleFSDirectory(Paths.get(indexDir.getAbsolutePath())), new IndexWriterConfig(new StandardAnalyzer()));
//        writer.setUseCompoundFile(false);
        indexDirectory(writer, dataDir);
        int numIndexed = writer.numDocs();
        writer.prepareCommit();
        writer.commit();
        writer.close();
        return numIndexed;
    }

    // recursive method that calls itself when it finds a directory
    private static void indexDirectory(IndexWriter writer, File dir)
            throws IOException {
        File[] files = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File f = files[i];
            if (f.isDirectory()) {
                indexDirectory(writer, f);
            } else if (f.getName().endsWith(".txt")) {
                indexFile(writer, f);
            }
        }
    }

    // method to actually index a file using Lucene
    private static void indexFile(IndexWriter writer, File f)
            throws IOException {
        if (f.isHidden() || !f.exists() || !f.canRead()) {
            return;
        }
        System.out.println("Indexing " + f.getCanonicalPath());
        Document doc = new Document();
        doc.add(new Field("contents", new FileReader(f), TextField.TYPE_NOT_STORED));
        doc.add(new Field("filename", f.getCanonicalPath(), StringField.TYPE_STORED));
        writer.addDocument(doc);
    }

}