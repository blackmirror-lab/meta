package uy.metapunto;
import org.junit.Test;import static org.junit.Assert.*;
public class MetadataReportTest {
 @Test public void groupsAndExplainsFields(){MetadataReport r=new MetadataReport("foto.jpg",100);r.add("JPEG","Image Width","659 pixels");r.add("GPS","GPS Latitude","34");r.add("XMP","dc:creator[1]","Ana");r.add("PDF","Creator","Editor");r.add("XMP","IP Address","192.0.2.1");assertEquals("Ancho de la imagen",r.in(MetadataReport.Group.TECHNICAL).get(0).title);assertEquals(1,r.in(MetadataReport.Group.LOCATION).size());assertEquals(2,r.in(MetadataReport.Group.AUTHOR).size());assertEquals(1,r.in(MetadataReport.Group.SOFTWARE).size());}
 @Test public void replacedTextIsNotCountedAsPersonal(){MetadataReport r=new MetadataReport("a.pdf",100);r.add("PDF","Author",".");assertEquals(0,r.personalCount());assertEquals(1,r.in(MetadataReport.Group.AUTHOR).size());}
}
