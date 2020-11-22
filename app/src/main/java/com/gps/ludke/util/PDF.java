package com.gps.ludke.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.fonts.Font;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/*
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

 */
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by caique on 27/02/2015.
 */
public class PDF {
    /*
    public String nome;
    //fontes usadas para criação do pdf
    private static Font catFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
    private static Font catFont3 = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);
    private static Font smallFont = new Font(Font.FontFamily.HELVETICA, 10, Font.BOLD);
    private static Font smallFont1 = new Font(Font.FontFamily.HELVETICA, 6, Font.NORMAL);
    private static Font dados = new Font(Font.FontFamily.HELVETICA, 9, Font.NORMAL);
    private static BaseColor color = new BaseColor(153, 255, 153);

    public PDF(){
        criandoDiretorio(diretorio);
        File file = new File(Environment.getExternalStorageDirectory(), "/"+diretorio+"/" + "nome_pdf"+".pdf");
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(file);

            if (file.exists()) {
                new FileOutputStream(file);
            } else {
                file.createNewFile();
                new FileOutputStream(file);
            }
            Document document = new Document();
            PdfWriter.getInstance(document, fileOut);
            document.open();

            Table(document);
            document.newPage();
            document.close();
            fileOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void criandoDiretorio(String diretorio){
        File file1 = new File(Environment.getExternalStorageDirectory(), "/"+diretorio+"/");
        if (!file1.exists()) {
            file1.mkdir();
            file1 = new File(Environment.getExternalStorageDirectory(), "/"+diretorio+"/");
        }
    }




}
    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        main.jaPossuoCartao = null;
        if(NetworkStatus.isOnline(main.getApplicationContext())){
            Toast.makeText(main.getApplicationContext(), "Proposta criada com sucesso. Enviando Proposta para o servidor", Toast.LENGTH_LONG).show();
            MySingleton.getInstance().setTentativas(0);
            main.startService(new Intent(main,MyService.class));
        }else{
            Toast.makeText(main.getApplicationContext(),"Proposta criada com sucesso. Sem conexão para realizar o envio para o servidor no momento",Toast.LENGTH_LONG).show();
        }
    }
    private void Table(Document document) throws DocumentException {
        PdfPTable table;
        PdfPCell c1;
        table = new PdfPTable(20);
        table.setWidthPercentage(110);
        c1 = new PdfPCell(new Phrase("Matricula"+ MySingleton.getInstance().getMatricula(), catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setColspan(20);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("JÁ POSSUO CARTÃO", smallFont));
        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
        c1.setColspan(18);
        c1.setBackgroundColor(color);
        table.addCell(c1);

        c1 = new PdfPCell();
        c1.setColspan(2);
        if(main.jaPossuoCartao.isSPC()) {
            c1.setBackgroundColor(BaseColor.BLACK);
        }else{
            c1.setBackgroundColor(BaseColor.WHITE);
        }
        table.addCell(c1);

// proxima linha
        c1 = new PdfPCell(new Phrase("Desejo:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(17);
        table.addCell(c1);
//nova linha
        c1 = new PdfPCell(new Phrase("Nome:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados",dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(10);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("CPF:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(1);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(6);
        table.addCell(c1);

        // ============= proxima linha =================
        c1 = new PdfPCell(new Phrase("Email:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(10);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("CEP:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(1);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(6);
        table.addCell(c1);
//nova linha
        c1 = new PdfPCell(new Phrase("Número:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(5);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Endereço:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(4);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(8);
        table.addCell(c1);
//proxima linha
        c1 = new PdfPCell(new Phrase("Edificio:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Apto/Sala:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);
        // proxima linha
        c1 = new PdfPCell(new Phrase("Conjunto:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Condomínio:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(4);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(6);
        table.addCell(c1);
//proxima linha
        c1 = new PdfPCell(new Phrase("Bairro:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(5);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("UF:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(1);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(2);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Cidade:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(6);
        table.addCell(c1);
//prox linha
        c1 = new PdfPCell(new Phrase("Telefone Fixo:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Telefone Fixo", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("texto", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);

        // proxima linha ============================
        c1 = new PdfPCell(new Phrase("Telefone Móvel:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("Telefone Móvel:", catFont3));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(3);
        table.addCell(c1);

        c1 = new PdfPCell(new Phrase("dados", dados));
        c1.setHorizontalAlignment(Element.ALIGN_LEFT);
        c1.setVerticalAlignment(Element.ALIGN_BOTTOM);
        c1.setColspan(7);
        table.addCell(c1);
        document.add(table);
    }

    private static void addEmptyLine(Paragraph paragraph, int number) {
        for (int i = 0; i < number; i++) {
            paragraph.add(new Paragraph(" "));
        }
    }

     */
}
