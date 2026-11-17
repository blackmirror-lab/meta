package uy.metapunto;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import org.w3c.dom.*;
import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.*;
import com.drew.metadata.exif.*;
import com.drew.metadata.xmp.XmpDirectory;
import com.drew.lang.GeoLocation;
import com.tom_roush.pdfbox.pdmodel.*;
import com.tom_roush.pdfbox.cos.*;

final class Inspector {
 static MetadataReport read(File f,String name)throws Exception{
  MetadataReport r=new MetadataReport(name,f.length());String ext=Engine.ext(name);
  if(ext.equals("pdf")){try(PDDocument d=PDDocument.load(f)){
   r.add("PDF","pages",Integer.toString(d.getNumberOfPages()));COSDictionary info=d.getDocumentInformation().getCOSObject();
   for(COSName k:info.keySet()){COSBase value=info.getDictionaryObject(k);r.add("Propiedades PDF",k.getName(),value instanceof COSString?((COSString)value).getString():String.valueOf(value));}
   if(d.getDocumentCatalog().getMetadata()!=null)try(InputStream in=d.getDocumentCatalog().getMetadata().createInputStream()){try{xmlFields(Engine.xml(Engine.bytes(in,2*1024*1024)).getDocumentElement(),"Información adicional del PDF",r);}catch(Exception e){r.complete=false;r.notices.add("La información adicional del PDF no pudo interpretarse.");}}
  }r.notices.add("Se revisaron propiedades y datos adicionales del PDF. Imágenes, comentarios y adjuntos internos pueden guardar más información.");}
  else if(Engine.office(name)){try(ZipFile z=new ZipFile(f)){Enumeration<? extends ZipEntry> e=z.entries();while(e.hasMoreElements()){ZipEntry item=e.nextElement();if(item.getName().startsWith("docProps/")&&item.getName().endsWith(".xml"))try(InputStream in=z.getInputStream(item)){xmlFields(Engine.xml(Engine.bytes(in,2*1024*1024)).getDocumentElement(),"Propiedades del documento",r);}}}r.notices.add("Se revisaron las propiedades del documento. Comentarios, revisiones e imágenes internas pueden guardar más información.");}
  else{try{Metadata m=ImageMetadataReader.readMetadata(f);for(Directory dir:m.getDirectories()){
   for(Tag t:dir.getTags())r.add(dir.getName(),t.getTagName(),t.getDescription());
   if(dir instanceof XmpDirectory){for(Map.Entry<String,String> e:((XmpDirectory)dir).getXmpProperties().entrySet())r.add("Información adicional de imagen",e.getKey(),e.getValue());}
   if(dir instanceof GpsDirectory){GeoLocation geo=((GpsDirectory)dir).getGeoLocation();if(geo!=null&&Math.abs(geo.getLatitude())<=90&&Math.abs(geo.getLongitude())<=180){r.latitude=geo.getLatitude();r.longitude=geo.getLongitude();}}
   if(dir instanceof ExifIFD0Directory){Integer o=dir.getInteger(ExifIFD0Directory.TAG_ORIENTATION);if(o!=null)r.orientation=o;}
   for(String error:dir.getErrors()){r.complete=false;r.notices.add("El lector informó un problema en una sección del archivo.");break;}
  }}catch(Exception e){r.complete=false;r.notices.add("Este archivo no pudo inspeccionarse con el lector de imágenes disponible.");}}
  if(!Engine.supported(name))r.notices.add("La limpieza no está disponible para este formato.");return r;
 }
 static void xmlFields(Element el,String section,MetadataReport r){
  NamedNodeMap attrs=el.getAttributes();for(int i=0;i<attrs.getLength();i++){Node a=attrs.item(i);if(!a.getNodeName().startsWith("xmlns")&&!a.getNodeName().equals("rdf:about")&&!a.getNodeName().equals("xsi:type"))r.add(section,a.getNodeName(),a.getNodeValue());}
  boolean child=false;NodeList nodes=el.getChildNodes();for(int i=0;i<nodes.getLength();i++)if(nodes.item(i) instanceof Element){child=true;xmlFields((Element)nodes.item(i),section,r);}
  if(!child)r.add(section,el.getNodeName(),el.getTextContent());
 }
}
