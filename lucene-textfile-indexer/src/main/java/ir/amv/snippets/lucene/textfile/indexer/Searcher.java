package ir.amv.snippets.lucene.textfile.indexer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.FieldValueQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.File;
import java.nio.file.Paths;
import java.util.Date;

/**
 * This code was originally written for
 * Erik's Lucene intro java.net article
 */
public class Searcher {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Usage: java " + Searcher.class.getName()
                    + " <index dir> <query>");
        }
        File indexDir = new File(args[0]);
        String q = args[1];
        if (!indexDir.exists() || !indexDir.isDirectory()) {
            throw new Exception(indexDir +
                    " does not exist or is not a directory.");
        }
        search(indexDir, q);
    }

    public static void search(File indexDir, String q)
            throws Exception {
        Directory fsDir = FSDirectory.open(Paths.get(indexDir.getAbsolutePath()));
        IndexReader ir = DirectoryReader.open(fsDir);
        IndexSearcher is = new IndexSearcher(ir);
        Query query = new QueryParser("contents", new StandardAnalyzer()).parse(q);
        long start = new Date().getTime();
        TopDocs hits = is.search(query, 10);
        long end = new Date().getTime();
        System.err.println("Found " + hits.totalHits +
                " document(s) (in " + (end - start) +
                " milliseconds) that matched query '" +
                q + "':");
        for (int i = 0; i < hits.totalHits; i++) {
            Document doc = is.doc(hits.scoreDocs[i].doc);
            System.out.println(doc.get("filename"));
        }
    }
}