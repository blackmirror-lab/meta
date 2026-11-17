package uy.metapunto;
import org.junit.Test;import static org.junit.Assert.*;import java.io.*;import java.nio.charset.StandardCharsets;import java.nio.file.Files;import java.util.zip.*;
public class EngineTest {
 @Test public void officePreservesContentRemovesPersonalProperties()throws Exception{
 File dir=Files.createTempDirectory("meta").toFile(),in=new File(dir,"a.docx"),out=new File(dir,"b.docx");
 try(ZipOutputStream z=new ZipOutputStream(new FileOutputStream(in))){z.putNextEntry(new ZipEntry("docProps/core.xml"));z.write("<cp:coreProperties xmlns:cp='urn:core' xmlns:dc='urn:dc' xmlns:dcterms='urn:date'><dc:creator>PRIVATE AUTHOR</dc:creator><dcterms:created>2020-01-01</dcterms:created></cp:coreProperties>".getBytes(StandardCharsets.UTF_8));z.closeEntry();z.putNextEntry(new ZipEntry("word/document.xml"));z.write("UNCHANGED".getBytes(StandardCharsets.UTF_8));z.closeEntry();}
 Engine.cleanOffice(in,out);try(ZipFile z=new ZipFile(out)){String props=new String(Engine.bytes(z.getInputStream(z.getEntry("docProps/core.xml")),10000),StandardCharsets.UTF_8);assertFalse(props.contains("PRIVATE"));assertFalse(props.contains("2020"));assertTrue(props.contains(">.<"));assertEquals("UNCHANGED",new String(Engine.bytes(z.getInputStream(z.getEntry("word/document.xml")),10000),StandardCharsets.UTF_8));}
 }
 @Test(expected=org.xml.sax.SAXException.class)public void rejectsEntities()throws Exception{Engine.xml("<!DOCTYPE x [<!ENTITY a SYSTEM 'file:///etc/passwd'>]><x>&a;</x>".getBytes(StandardCharsets.UTF_8));}
 @Test(expected=IOException.class)public void rejectsInvalidJpeg()throws Exception{Engine.jpeg(new byte[]{1,2,3});}
 @Test public void jpegWritesPointAndRemovesComment()throws Exception{byte[] b={(byte)255,(byte)216,(byte)255,(byte)254,0,8,83,69,67,82,69,84,(byte)255,(byte)217};byte[] out=Engine.jpeg(b);assertArrayEquals(new byte[]{(byte)255,(byte)216,(byte)255,(byte)254,0,3,46,(byte)255,(byte)217},out);}
}
