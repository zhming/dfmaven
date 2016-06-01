package com.gihow.poi;

import java.io.*;
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTBookmark;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTMarkupRange;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTR;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTText;
import java.util.List;
import java.util.Iterator;
import org.apache.xmlbeans.XmlCursor;
import org.apache.xmlbeans.XmlException;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Second attempt at inserting text at a bookmark defined within a Word document.
 * Note that there is one SERIOUS limitations with the code as it stands; at
 * least only one as far as I am aware: nested bookmarks.
 *
 * It is possible to create a document and to nest one bookmark within another.
 * Typically, a bookmark is inserted into a piece of text, that is then selected
 * and another bookmark is added to that selection. The xml markup might look
 * something like this
 *
 * <pre>
 * <w:p w:rsidR="00945150" w:rsidRDefault="00945150">
 *   <w:r>
 *     <w:t xml:space="preserve">
 *     Imagine I want to insert one bookmark at the start of this
 *     </w:t>
 *   </w:r>
 *     <w:bookmarkStart w:id="0" w:name="OUTER"/>
 *       <w:r>
 *         <w:t xml:space="preserve">piece of text and another just
 *         </w:t>
 *     </w:r>
 *   <w:proofErr w:type="gramStart"/>
 *   <w:r>
 *     <w:t xml:space="preserve">here
 *     </w:t>
 *   </w:r>
 *   <w:bookmarkStart w:id="1" w:name="INNER"/>
 *   <w:bookmarkEnd w:id="1"/>
 *     <w:r>
 *       <w:t>.
 *       </w:t>
 *     </w:r>
 *   <w:bookmarkEnd w:id="0"/>
 *   <w:proofErr w:type="gramEnd"/>
 * </w:p>
 * </pre>
 *
 * In this case the codes usual behaviour - which is to remove any nodes found
 * between the bookmarkStart and bookmarkEnd nodes will simply result in the
 * 'inner' or nested bookmark being removed. So, is the default behaviour
 * correct? If it is, then the code needs to be amended to handle nested
 * bookmarks and the decision must be made about just how to handle them..
 *
 * @author Mark Beardsley
 * @version 0.20 10th June 2012
 */
public class DOCXTest {

    private XWPFDocument document = null;

    public DOCXTest() {
    }

    /**
     * Opens a Word OOXML file.
     *
     * @param filename An instance of the String class that encapsulates the
     *        path to and name of a Word OOXML (.docx) file.
     * @throws IOException  Thrown if a problem occurs within the underlying
     *         file system.
     */
    public final void openFile(String filename) throws IOException {
        File file = null;
        FileInputStream fis = null;
        try {
            // Simply open the file and store a reference into the 'document'
            // local variable.
            file = new File(filename);
            fis = new FileInputStream(file);
            this.document = new XWPFDocument(fis);
        }
        finally {
            try {
                if(fis != null) {
                    fis.close();
                    fis = null;
                }
            }
            catch(IOException ioEx) {
                // Swallow this exception. It would have occured onyl
                // when releasing the file handle and should not pose
                // problems to later processing.
            }
        }
    }

    /**
     * Saves a Word OOXML file away under the name, and to the location,
     * specified.
     *
     * @param filename An instance of the String class that encapsulates the
     *        of the file and the location into which it should be stored.
     * @throws IOException  Thrown if a problem occurs in the underlying file
     *         system.
     */
    public final void saveAs(String filename) throws IOException {
        File file = null;
        FileOutputStream fos = null;
        try {
            file = new File(filename);
            fos = new FileOutputStream(file);
            this.document.write(fos);
        }
        finally {
            if(fos != null) {
                fos.close();
                fos = null;
            }
        }
    }

    /**
     * Inserts a value at a location within the Word document specified by a
     * named bookmark.
     *
     * @param bookmarkName An instance of the String class that encapsulates
     *        the name of the bookmark. Note that case is important and the case
     *        of the bookmarks name within the document and that of the value
     *        passed to this parameter must match.
     * @param bookmarkValue An instance of the String class that encapsulates
     *        the value that should be inserted into the document at the location
     *        specified by the bookmark.
     */
    public final void insertAtBookmark(String bookmarkName, String bookmarkValue) {
        List<XWPFTable> tableList = null;
        Iterator<XWPFTable> tableIter = null;
        List<XWPFTableRow> rowList = null;
        Iterator<XWPFTableRow> rowIter = null;
        List<XWPFTableCell> cellList = null;
        Iterator<XWPFTableCell> cellIter = null;
        XWPFTable table = null;
        XWPFTableRow row = null;
        XWPFTableCell cell = null;

        // Firstly, deal with any paragraphs in the body of the document.
        this.procParaList(this.document.getParagraphs(), bookmarkName, bookmarkValue);

        // Then check to see if there are any bookmarks in table cells. To do this
        // it is necessary to get at the list of paragraphs 'stored' within the
        // individual table cell, hence this code which get the tables from the
        // document, the rows from each table, the cells from each row and the
        // paragraphs from each cell.
        tableList = this.document.getTables();
        tableIter = tableList.iterator();
        while(tableIter.hasNext()) {
            table = tableIter.next();
            rowList = table.getRows();
            rowIter = rowList.iterator();
            while(rowIter.hasNext()) {
                row = rowIter.next();
                cellList = row.getTableCells();
                cellIter = cellList.iterator();
                while(cellIter.hasNext()) {
                    cell = cellIter.next();
                    this.procParaList(cell.getParagraphs(),
                            bookmarkName,
                            bookmarkValue);
                }
            }
        }
    }

    /**
     * Inserts text into the document at the position indicated by a specific
     * bookmark. Note that the current implementation does not take account
     * of nested bookmarks, that is bookmarks that contain other bookmarks. Note
     * also that any text contained within the bookmark itself will be removed.
     *
     * @param paraList An instance of a class that implements the List interface
     *        and which encapsulates references to one or more instances of the
     *        XWPFParagraph class.
     * @param bookmarkName An instance of the String class that encapsulates the
     *        name of the bookmark that identifies the position within the
     *        document some text should be inserted.
     * @param bookmarkValue An instance of the AString class that encapsulates
     *        the text that should be inserted at the location specified by the
     *        bookmark.
     */
    private final void procParaList(List<XWPFParagraph> paraList,
                                    String bookmarkName, String bookmarkValue) {
        Iterator<XWPFParagraph> paraIter = null;
        XWPFParagraph para = null;
        List<CTBookmark> bookmarkList = null;
        Iterator<CTBookmark> bookmarkIter = null;
        CTBookmark bookmark = null;
        XWPFRun run = null;
        Node nextNode = null;

        // Get an Iterator to step through the contents of the paragraph list.
        paraIter = paraList.iterator();
        while(paraIter.hasNext()) {
            // Get the paragraph, a llist of CTBookmark objects and an Iterator
            // to step through the list of CTBookmarks.
            para = paraIter.next();
            bookmarkList = para.getCTP().getBookmarkStartList();
            bookmarkIter = bookmarkList.iterator();

            while(bookmarkIter.hasNext()) {
                // Get a Bookmark and check it's name. If the name of the
                // bookmark matches the name the user has specified...
                bookmark = bookmarkIter.next();
                if(bookmark.getName().equals(bookmarkName)) {
                    // ...create the text run to insert and set it's text
                    // content and then insert that text into the document.
                    run = para.createRun();
                    run.setText(bookmarkValue);
                    // The new Run should be inserted between the bookmarkStart
                    // and bookmarkEnd nodes, so find the bookmarkEnd node.
                    // Note that we are looking for the next sibling of the
                    // bookmarkStart node as it does not contain any child nodes
                    // as far as I am aware.
                    nextNode = bookmark.getDomNode().getNextSibling();
                    // If the next node is not the bookmarkEnd node, then step
                    // along the sibling nodes, until the bookmarkEnd node
                    // is found. As the code is here, it will remove anything
                    // it finds between the start and end nodes. This, of course
                    // comepltely sidesteps the issues surrounding boorkamrks
                    // that contain other bookmarks which I understand can happen.
                    while(!(nextNode.getNodeName().contains("bookmarkEnd"))) {
                        para.getCTP().getDomNode().removeChild(nextNode);
                        nextNode = bookmark.getDomNode().getNextSibling();
                    }

                    // Finally, insert the new Run node into the document
                    // between the bookmarkStrat and the bookmarkEnd nodes.
                    para.getCTP().getDomNode().insertBefore(
                            run.getCTR().getDomNode(),
                            nextNode);
                }
            }
        }
    }

    public String getBookmarkText(String bookmarkName) throws XmlException, IOException {
        List<XWPFTable> tableList = null;
        Iterator<XWPFTable> tableIter = null;
        List<XWPFTableRow> rowList = null;
        Iterator<XWPFTableRow> rowIter = null;
        List<XWPFTableCell> cellList = null;
        Iterator<XWPFTableCell> cellIter = null;
        XWPFTable table = null;
        XWPFTableRow row = null;
        XWPFTableCell cell = null;
        String text = null;

        // Firstly, deal with any paragraphs in the body of the document.
        text = this.procParasForBookmarkText(this.document.getParagraphs(), bookmarkName);

        // Then check to see if there are any bookmarks in table cells. To do this
        // it is necessary to get at the list of paragraphs 'stored' within the
        // individual table cell, hence this code which get the tables from the
        // document, the rows from each table, the cells from each row and the
        // paragraphs from each cell.
        if(text == null) {
            tableList = this.document.getTables();
            tableIter = tableList.iterator();
            while(tableIter.hasNext()) {
                table = tableIter.next();
                rowList = table.getRows();
                rowIter = rowList.iterator();
                while(rowIter.hasNext()) {
                    row = rowIter.next();
                    cellList = row.getTableCells();
                    cellIter = cellList.iterator();
                    while(cellIter.hasNext()) {
                        cell = cellIter.next();
                        text = this.procParasForBookmarkText(cell.getParagraphs(),
                                bookmarkName);
                    }
                }
            }
        }
        return(text);
    }

    public String procParasForBookmarkText(List<XWPFParagraph> paraList,
                                           String bookmarkName) throws XmlException, IOException {
        Iterator<XWPFParagraph> paraIter = null;
        XWPFParagraph para = null;
        XWPFRun run = null;
        List<CTBookmark> bookmarkList = null;
        Iterator<CTBookmark> bookmarkIter = null;
        CTBookmark bookmark = null;
        Node nextNode = null;
        StringBuilder builder = null;
        String rawXML = null;
        ByteArrayOutputStream baos = null;
        // Get an Iterator to step through the contents of the paragraph list.
        paraIter = paraList.iterator();
        while(paraIter.hasNext()) {
            // Get the paragraph, a llist of CTBookmark objects and an Iterator
            // to step through the list of CTBookmarks.
            para = paraIter.next();
            bookmarkList = para.getCTP().getBookmarkStartList();
            bookmarkIter = bookmarkList.iterator();

            while(bookmarkIter.hasNext()) {
                // Get a Bookmark and check it's name. If the name of the
                // bookmark matches the name the user has specified...
                bookmark = bookmarkIter.next();
                if(bookmark.getName().equals(bookmarkName)) {

                    // Create a StringBuilder to hold the Bookmark's text.
                    builder = new StringBuilder();

                    // Now, we need to step through any nodes that may exist
                    // inbetween the two bookmark tags. So, get the next sibling
                    // node from the pargraph
                    nextNode = bookmark.getDomNode().getNextSibling();
                    while(!(nextNode.getNodeName().contains("bookmarkEnd"))) {

                        // ...and if it is a run, create a CTR object from that
                        // node and then gat at the raw XML markup the node
                        // encapsulates. Process that using brute force java
                        // String operations as it does not seem to be possible
                        // top get at the text content of the node from the
                        // CTText nodes the run contains. Odd!!
                        if(nextNode.getNodeName().equals("w:r")) {
                            CTR ctrRun = CTR.Factory.parse(nextNode);
                            baos = new ByteArrayOutputStream();
                            ctrRun.save(baos);
                            rawXML = baos.toString();
                            builder.append(this.getText(rawXML));
                        }
                        nextNode = nextNode.getNextSibling();
                    }
                }
            }
        }
        return(builder == null ? "" : builder.toString().trim());
    }

    /**
     * Strip the raw XML markup apart to get at the text that is encapsulated
     * within it. Currently, the code will get at only the first embedded w:t
     * element, but this is not forced to be the case
     *
     * @param rawXML An instance of the String class encapsulating the raw XML
     *        markup for a run of text recovered from a document node.
     * @return An instance of the String class that encapsulates the text
     *         stored within the w:t element.
     */
    private String getText(String rawXML) {
        int to = rawXML.indexOf("<w:t");
        rawXML = rawXML.substring(to);
        to = rawXML.indexOf(">");
        to++;
        rawXML = rawXML.substring(to);
        to = rawXML.indexOf("</w:t");
        rawXML = rawXML.substring(0, to);
        return(rawXML);
    }

    public static void main(String[] args) {
        try {
            DOCXTest docxTest = new DOCXTest();
            docxTest.openFile("d:/temp/aa.docx");
            System.out.println(docxTest.getBookmarkText("a002a"));
            System.out.println(docxTest.getBookmarkText("a005"));
            System.out.println(docxTest.getBookmarkText("a007"));
        }
        catch(Exception ex) {
            System.out.println("Caught a: " + ex.getClass().getName());
            System.out.println("Message: " + ex.getMessage());
            System.out.println("Stacktrace follows:.....");
            ex.printStackTrace(System.out);
        }
    }
}