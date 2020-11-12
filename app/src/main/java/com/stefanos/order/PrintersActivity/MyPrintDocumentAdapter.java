package com.stefanos.order.PrintersActivity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.ParcelFileDescriptor;
import android.print.PageRange;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintDocumentInfo;
import android.print.pdf.PrintedPdfDocument;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class MyPrintDocumentAdapter extends PrintDocumentAdapter {

    private Context context;
    private int pageHeight;
    private int pageWidth;
    public PdfDocument myPdfDocument;
    private int pageHelper=0;
    public int totalpages=2 ;
    private String tableId;
    private ArrayList<PrintItem> printItemList;


    private class ConnectTrialPrinter extends AsyncTask<Void,Void,Void>{

        private PdfDocument.Page page;

        public ConnectTrialPrinter(PdfDocument.Page page) {
            this.page = page;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Socket sock = null;
            try {
                sock = new Socket("192.168.1.87", 9100);
                PrintWriter oStream = new PrintWriter(sock.getOutputStream());
                final Canvas canvas = page.getCanvas();
                int pagenumber=1; // Make sure page numbers start at 1

                final int titleBaseLine = 30;
                final int leftMargin = 54;

                final Paint paint1 = new Paint();
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(12);
                canvas.drawText(
                        tableId,
                        leftMargin,
                        titleBaseLine,
                        paint1);

                Paint paint2 = new Paint();
                paint1.setColor(Color.BLACK);
                paint1.setTextSize(11);

                final int finalPagenumber = pagenumber;

                int changeLine = 0;
                int counter=0;

                for (int j=pageHelper;j<printItemList.size();j++) {
                    canvas.drawText(String.valueOf(printItemList.get(j).getQuantityPrint()) + " " + printItemList.get(j).getNamePrint() + " " +
                            printItemList.get(j).getPriceItem(), 30, 55 + changeLine, paint2);
                    oStream.print(canvas);
                    if (!printItemList.get(j).getExtraPrint().isEmpty()) {
                        int changeLineExtra = 15;
                        int changeLineXwris = 15;
                        int changeLineExtraSameItem = 0;
                        int count = 0;
                        int temp = 0;
                        for (int i = 0; i < printItemList.get(j).extraPrint.length(); i++) {
                            if (printItemList.get(j).getExtraPrint().charAt(i) == '\n') {
                                count++;
                                if (count % 2 == 0) {

                                    canvas.drawText("--" + printItemList.get(j).getExtraPrint().substring(temp, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                                    temp = i;
                                    changeLineExtraSameItem = changeLineExtraSameItem + 15;
                                }
                            }
                        }

                        canvas.drawText("--" + printItemList.get(j).getExtraPrint().substring(temp, printItemList.get(j).extraPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                        changeLineExtra = changeLineExtraSameItem + changeLineExtra;
                        if (!printItemList.get(j).getXwrisPrint().isEmpty()) {
                            int count1 = 0;
                            int temp1 = 0;
                            for (int i = 0; i < printItemList.get(j).xwrisPrint.length(); i++) {
                                if (printItemList.get(j).getXwrisPrint().charAt(i) == '\n') {
                                    count1++;
                                    if (count1 % 2 == 0) {

                                        canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                                        temp1 = i;
                                        changeLineExtraSameItem = changeLineExtraSameItem + 15;
                                    }
                                }
                            }

                            canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, printItemList.get(j).xwrisPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                            changeLine = changeLine + changeLineExtra + changeLineXwris + changeLineExtraSameItem;
                        } else {
                            changeLine = changeLine + 15 + changeLineExtra;
                        }
                    } else {
                        if (!printItemList.get(j).getXwrisPrint().isEmpty()) {
                            int changeLineExtra = 15;
                            int changeLineXwris = 15;
                            int changeLineExtraSameItem = 0;
                            int count1 = 0;
                            int temp1 = 0;
                            for (int i = 0; i < printItemList.get(j).xwrisPrint.length(); i++) {
                                if (printItemList.get(j).getXwrisPrint().charAt(i) == '\n') {
                                    count1++;
                                    if (count1 % 2 == 0) {

                                        canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                                        temp1 = i;
                                        changeLineExtraSameItem = changeLineExtraSameItem + 15;
                                    }
                                }
                            }

                            canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, printItemList.get(j).xwrisPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                            changeLine = changeLine + changeLineExtra + changeLineXwris + changeLineExtraSameItem;
                        } else {
                            changeLine = changeLine + 15;
                        }
                    }
                    if(changeLine<240){
                        counter++;
                    }

                }
            } catch (IOException e) {
                e.printStackTrace();
            }





            return null;
        }
    }
    public MyPrintDocumentAdapter(Context context, String tableId, ArrayList<PrintItem> printItemList) {
        super();
        this.context = context;
        this.tableId = tableId;
        this.printItemList = printItemList;
    }


    @Override
    public void onLayout(PrintAttributes oldAttributes, PrintAttributes newAttributes,
                         CancellationSignal cancellationSignal, LayoutResultCallback callback, Bundle bundle) {
        myPdfDocument = new PrintedPdfDocument(context, newAttributes);

        pageHeight =
                newAttributes.getMediaSize().getHeightMils() / 1000 * 72;
        pageWidth =
                newAttributes.getMediaSize().getWidthMils() / 1000 * 72;

        if (cancellationSignal.isCanceled()) {
            callback.onLayoutCancelled();
            return;
        }

        if (totalpages > 0) {
            PrintDocumentInfo.Builder builder = new PrintDocumentInfo
                    .Builder("print_output.pdf")
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(totalpages);

            PrintDocumentInfo info = builder.build();
            callback.onLayoutFinished(info, true);
        } else {
            callback.onLayoutFailed("Page count is zero.");
        }
    }

    @Override
    public void onWrite(PageRange[] pageRanges, ParcelFileDescriptor destination,
                        CancellationSignal cancellationSignal, WriteResultCallback callback) {

        for (int i = 0; i < totalpages; i++) {
            if (pageInRange(pageRanges, i)) {
                PdfDocument.PageInfo newPage = new PdfDocument.PageInfo.Builder(pageWidth,
                        pageHeight, i).create();

                PdfDocument.Page page =
                        myPdfDocument.startPage(newPage);

                if (cancellationSignal.isCanceled()) {
                    callback.onWriteCancelled();
                    myPdfDocument.close();
                    myPdfDocument = null;
                    return;
                }
                if (i ==0) {

                        drawPageCusine(page, i);

                    myPdfDocument.finishPage(page);
                } else {
                    new ConnectTrialPrinter(page);
                    myPdfDocument.finishPage(page);
                }
            }
        }

        try {
            myPdfDocument.writeTo(new FileOutputStream(
                    destination.getFileDescriptor()));
        } catch (IOException e) {
            callback.onWriteFailed(e.toString());
            return;
        } finally {
            myPdfDocument.close();
            myPdfDocument = null;
        }

        callback.onWriteFinished(pageRanges);
    }

    private boolean pageInRange(PageRange[] pageRanges, int page) {
        for (int i = 0; i < pageRanges.length; i++) {
            if ((page >= pageRanges[i].getStart()) &&
                    (page <= pageRanges[i].getEnd()))
                return true;
        }
        return false;
    }

    private void drawPageCusine(final PdfDocument.Page page,
                                int pagenumber) {



        final Canvas canvas = page.getCanvas();
        pagenumber++; // Make sure page numbers start at 1

        final int titleBaseLine = 30;
        final int leftMargin = 54;

        final Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(12);
        canvas.drawText(
                tableId,
                leftMargin,
                titleBaseLine,
                paint1);

        Paint paint2 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(11);

        final int finalPagenumber = pagenumber;

        int changeLine = 0;
        int counter=0;

        for (int j=pageHelper;j<printItemList.size();j++) {
            canvas.drawText(String.valueOf(printItemList.get(j).getQuantityPrint()) + " " + printItemList.get(j).getNamePrint() + " " +
                    printItemList.get(j).getPriceItem(), 30, 55 + changeLine, paint2);

            if (!printItemList.get(j).getExtraPrint().isEmpty()) {
                int changeLineExtra = 15;
                int changeLineXwris = 15;
                int changeLineExtraSameItem = 0;
                int count = 0;
                int temp = 0;
                for (int i = 0; i < printItemList.get(j).extraPrint.length(); i++) {
                    if (printItemList.get(j).getExtraPrint().charAt(i) == '\n') {
                        count++;
                        if (count % 2 == 0) {

                            canvas.drawText("--" + printItemList.get(j).getExtraPrint().substring(temp, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                            temp = i;
                            changeLineExtraSameItem = changeLineExtraSameItem + 15;
                        }
                    }
                }

                canvas.drawText("--" + printItemList.get(j).getExtraPrint().substring(temp, printItemList.get(j).extraPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                changeLineExtra = changeLineExtraSameItem + changeLineExtra;
                if (!printItemList.get(j).getXwrisPrint().isEmpty()) {
                    int count1 = 0;
                    int temp1 = 0;
                    for (int i = 0; i < printItemList.get(j).xwrisPrint.length(); i++) {
                        if (printItemList.get(j).getXwrisPrint().charAt(i) == '\n') {
                            count1++;
                            if (count1 % 2 == 0) {

                                canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                                temp1 = i;
                                changeLineExtraSameItem = changeLineExtraSameItem + 15;
                            }
                        }
                    }

                    canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, printItemList.get(j).xwrisPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                    changeLine = changeLine + changeLineExtra + changeLineXwris + changeLineExtraSameItem;
                } else {
                    changeLine = changeLine + 15 + changeLineExtra;
                }
            } else {
                if (!printItemList.get(j).getXwrisPrint().isEmpty()) {
                    int changeLineExtra = 15;
                    int changeLineXwris = 15;
                    int changeLineExtraSameItem = 0;
                    int count1 = 0;
                    int temp1 = 0;
                    for (int i = 0; i < printItemList.get(j).xwrisPrint.length(); i++) {
                        if (printItemList.get(j).getXwrisPrint().charAt(i) == '\n') {
                            count1++;
                            if (count1 % 2 == 0) {

                                canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, i), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                                temp1 = i;
                                changeLineExtraSameItem = changeLineExtraSameItem + 15;
                            }
                        }
                    }

                    canvas.drawText("--" + printItemList.get(j).getXwrisPrint().substring(temp1, printItemList.get(j).xwrisPrint.length()), 30, 55 + changeLine + changeLineExtra + changeLineExtraSameItem, paint2);
                    changeLine = changeLine + changeLineExtra + changeLineXwris + changeLineExtraSameItem;
                } else {
                    changeLine = changeLine + 15;
                }
            }
            if(changeLine<240){
                counter++;
            }

        }

    }

    private void drawPageCustomer(final PdfDocument.Page page,
                                  int pagenumber) {

        final Canvas canvas = page.getCanvas();
        pagenumber++; // Make sure page numbers start at 1

        final int titleBaseLine = 30;
        final int leftMargin = 54;

        final Paint paint1 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(12);
        canvas.drawText(
                tableId,
                leftMargin,
                titleBaseLine,
                paint1);

        Paint paint2 = new Paint();
        paint1.setColor(Color.BLACK);
        paint1.setTextSize(11);

        final int finalPagenumber = pagenumber;

        int changeLine = 0;


        for (PrintItem printItem : Objects.requireNonNull(printItemList)) {
            canvas.drawText(String.valueOf(printItem.getQuantityPrint()) + " " + printItem.getNamePrint() + " " +
                    printItem.getPriceItem(), 30, 55 + changeLine, paint2);
            changeLine = changeLine + 15;

        }


    }



}
