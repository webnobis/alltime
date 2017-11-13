package example.delete.after.finish.com.webnobis.alltime.export;

import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.OutsetBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;

public class PdfTableExample {

	public static void main(String[] args) throws FileNotFoundException {
		table();
	}
	
	private static void table() throws FileNotFoundException {
		Path path = Paths.get("target");
		PdfWriter writer = new PdfWriter(path.resolve("table.pdf").toFile());
		PdfDocument pdfDocument = new PdfDocument(writer);
		Document document = new Document(pdfDocument, PageSize.A4.rotate());
		//document.setHorizontalAlignment(HorizontalAlignment.CENTER);
		
		Table table = new Table(new float[]{1,1,3,1}, true);
		table.setDocument(document);
		
		Stream.of("Ich", "bin", "die", "Überschrift")
			.map(s -> new Cell().setBold().setFontSize(12.5f).setFontColor(Color.MAGENTA).add(s))
			.forEach(c -> table.addHeaderCell(c));
			
		IntStream.rangeClosed(1, 20).forEach(i -> table.addCell("Banane " + i));
		
		table.addFooterCell(new Cell(1, 4).setBackgroundColor(Color.GREEN).add("Susi sorglos"));
		table.setBorder(new OutsetBorder(2));
		table.complete();
		
		document.close();
	}

}
