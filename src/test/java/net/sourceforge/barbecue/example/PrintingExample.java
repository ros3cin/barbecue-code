package net.sourceforge.barbecue.example;

import java.awt.print.PrinterJob;

import net.sourceforge.barbecue.Barcode;
import net.sourceforge.barbecue.BarcodeFactory;

/**
 * 
 * Print a barcode using Java's print API
 * 
 * @author Sean C. Sullivan
 * 
 */
public class PrintingExample {

    public static void main(String[] args) {
        try {
            Barcode b = BarcodeFactory.createCode128("Hello");
            PrinterJob job = PrinterJob.getPrinterJob();
            job.setPrintable(b);
            if (job.printDialog()) {
                job.print();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}