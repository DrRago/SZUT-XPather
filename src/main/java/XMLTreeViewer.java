/**
 * This class is used to display a XML document in a form of a
 * interactive visible tree. When the window is closed, the system
 * does not exit (it only release resource). If the client of this class
 * wants to quit the whole system, the client program need either
 * set the windowListener, or need to run System.exit(0) explictly.
 */
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.io.File;


public class XMLTreeViewer extends JFrame{

    //The JTree to display the XML
    private JTree xmlTree;

    //The XML document to be output to the JTree
    private Document xmlDoc;

    DefaultMutableTreeNode tn;

    public XMLTreeViewer(Document doc){
        super();
        this.xmlDoc = doc;
        setSize(600, 450);
        tn= new DefaultMutableTreeNode("XML");
        initialize();

    }

    private void initialize(){

        xmlTree = new JTree();
        xmlTree.setName("XML Tree");
        getContentPane().add(new JScrollPane(xmlTree), BorderLayout.CENTER);

        processElement(xmlDoc.getRootElement(), tn);

        ((DefaultTreeModel)xmlTree.getModel()).setRoot(tn);
        addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent e){
                //release all the resource
                xmlTree = null;
                tn = null;
            }
        } );

        setVisible(true);
    }


    private void processElement(Element el, DefaultMutableTreeNode dmtn) {
        DefaultMutableTreeNode currentNode =
                new DefaultMutableTreeNode(el.getName());
        String text = el.getTextNormalize();
        if((text != null) && (!text.equals("")))
            currentNode.add(new DefaultMutableTreeNode(text));

        processAttributes(el, currentNode);

        for (Element o : el.getChildren()) processElement(o, currentNode);

        dmtn.add(currentNode);
    }

    private void processAttributes(Element el, DefaultMutableTreeNode dmtn) {

        for (Attribute att : el.getAttributes()) {
            DefaultMutableTreeNode attNode =
                    new DefaultMutableTreeNode("@" + att.getName());
            attNode.add(new DefaultMutableTreeNode(att.getValue()));
            dmtn.add(attNode);
        }
    }

    public static void main(String args[])
            throws Exception
    {
        if(args.length != 1){
            System.out.println("Usage: java XMLTreeViewer "+
                    "[XML Document filename]");
            return;
        }

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(new File(args[0]));
        XMLTreeViewer viewer = new XMLTreeViewer(doc);
        viewer.addWindowListener(new java.awt.event.WindowAdapter(){
            public void windowClosing(java.awt.event.WindowEvent e){
                System.exit(0);
            }
        } );
    }
}