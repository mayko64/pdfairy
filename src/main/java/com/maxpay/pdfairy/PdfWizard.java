package com.maxpay.pdfairy;

import org.apache.fop.apps.FOPException;
import org.apache.fop.apps.Fop;
import org.apache.fop.apps.FopFactory;
import org.apache.fop.apps.MimeConstants;

import javax.xml.transform.*;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;

public class PdfWizard {
    public void render(File xml, File xsl, File pdf) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(pdf))) {
            FopFactory fopFactory = FopFactory.newInstance();
            Fop fop = fopFactory.newFop(MimeConstants.MIME_PDF, out);
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(new StreamSource(xsl));

            Source src = new StreamSource(xml);
            Result res = new SAXResult(fop.getDefaultHandler());

            transformer.transform(src, res);
        } catch (FileNotFoundException e) {
            System.err.println("File " + pdf + " cannot be written: " + e);
        } catch (FOPException e) {
            System.err.println("Apache FOP exception: " + e);
        } catch (TransformerConfigurationException e) {
            System.err.println("Transformer configuration exception: " + e);
        } catch (TransformerException e) {
            System.err.println("Transformer exception: " + e);
        } catch (IOException e) {
            System.err.println("Input/output exception: " + e);
        }
    }
}
