package uy.metapunto;

import java.util.*;

/** User-facing descriptions are independent from the format-specific extractor. */
final class MetadataReport {
 enum Group {
  LOCATION("Ubicación", "Coordenadas y referencias al lugar donde se creó el archivo.","pin"),
  AUTHOR("Autor y contacto", "Nombres, organización y datos de contacto guardados dentro del archivo.","person"),
  DEVICE("Dispositivo", "Cámara, teléfono o equipo que dejó información en el archivo.","camera"),
  DATES("Fechas", "Momentos de captura, creación o edición registrados dentro del archivo. Pueden ser modificados.","calendar"),
  SOFTWARE("Aplicaciones y procedencia", "Programas y campos de procedencia. Un nombre de software no demuestra por sí solo uso de IA.","app"),
  TECHNICAL("Características del archivo", "Datos para interpretar la imagen o el documento: tamaño, color y compresión. No todos son datos personales ni deben desaparecer.","file");
  final String title,help,icon;
  Group(String t,String h,String i){title=t;help=h;icon=i;}
 }
 static final class Field {
  final String original,title,value,help;final Group group;
  Field(String o,String t,String v,String h,Group g){original=o;title=t;value=v;help=h;group=g;}
 }
 final List<Field> fields=new ArrayList<>();final List<String> notices=new ArrayList<>();
 String name;long size;Double latitude,longitude;int orientation=1;boolean complete=true;
 MetadataReport(String n,long s){name=n;size=s;}
 List<Field> in(Group g){List<Field> out=new ArrayList<>();for(Field f:fields)if(f.group==g)out.add(f);return out;}
 int personalCount(){int n=0;for(Field f:fields)if(f.group!=Group.TECHNICAL&&!f.value.equals(".")&&!f.value.trim().isEmpty())n++;return n;}
 void add(String section,String key,String value){if(value==null||value.trim().isEmpty())return;if(fields.size()>=1500){if(!notices.contains("Se alcanzó el límite de 1.500 campos mostrados."))notices.add("Se alcanzó el límite de 1.500 campos mostrados.");complete=false;return;}
  String k=key.replaceFirst("^.*:","").replaceAll("\\[\\d+\\]", "").replaceAll("[^A-Za-z0-9]","").toLowerCase(Locale.ROOT);
  String title=null,help=null;Group g=Group.TECHNICAL;
  switch(k){
   case "author":case "creator":case "artist":title="Autor registrado";help="Nombre guardado por la aplicación o la cámara. No prueba quién creó realmente el contenido.";g=Group.AUTHOR;break;
   case "lastmodifiedby":title="Último editor registrado";help="Nombre de la cuenta que guardó una edición del documento.";g=Group.AUTHOR;break;
   case "ownername":case "cameraownername":title="Propietario registrado";help="Nombre del propietario configurado en el dispositivo.";g=Group.AUTHOR;break;
   case "company":case "organization":title="Organización";help="Empresa o institución guardada en las propiedades.";g=Group.AUTHOR;break;
   case "manager":title="Responsable registrado";help="Nombre incluido en la propiedad de responsable del documento.";g=Group.AUTHOR;break;
   case "copyright":case "rights":case "copyrightnotice":title="Derechos de autor";help="Texto de derechos o atribución incluido en el archivo.";g=Group.AUTHOR;break;
   case "make":title="Marca del dispositivo";help="Fabricante informado por la cámara o el teléfono.";g=Group.DEVICE;break;
   case "model":case "cameramodelname":title="Modelo del dispositivo";help="Modelo de cámara o teléfono registrado al crear la imagen.";g=Group.DEVICE;break;
   case "lensmodel":case "lens":title="Objetivo de la cámara";help="Modelo del lente utilizado para la captura.";g=Group.DEVICE;break;
   case "serialnumber":case "bodyserialnumber":case "cameraserialnumber":case "lensserialnumber":title="Número de serie";help="Identificador del equipo o lente. Puede permitir vincular archivos con un mismo dispositivo.";g=Group.DEVICE;break;
   case "software":case "application":case "creatortool":title="Aplicación registrada";help="Programa declarado dentro del archivo. Puede cambiar al editar o exportar.";g=Group.SOFTWARE;break;
   case "producer":title="Programa que produjo el PDF";help="Aplicación o biblioteca que guardó el documento PDF.";g=Group.SOFTWARE;break;
   case "appversion":title="Versión de la aplicación";help="Versión declarada por el programa que guardó el documento.";g=Group.SOFTWARE;break;
   case "datetimeoriginal":title="Fecha de captura";help="Fecha y hora de toma guardadas por la cámara; no siempre incluyen zona horaria.";g=Group.DATES;break;
   case "datetimedigitized":title="Fecha de digitalización";help="Momento registrado para la conversión a una imagen digital.";g=Group.DATES;break;
   case "created":case "createdate":case "creationdate":title="Fecha de creación registrada";help="Fecha interna del archivo; no es necesariamente la fecha de descarga o envío.";g=Group.DATES;break;
   case "modified":case "modifydate":case "moddate":case "datetime":title="Fecha de modificación registrada";help="Última edición declarada en los metadatos, que puede ser distinta de la fecha del sistema.";g=Group.DATES;break;
   case "metadatadate":title="Fecha de actualización de metadatos";help="Momento registrado para la última modificación de los metadatos.";g=Group.DATES;break;
   case "gpslatitude":title="Latitud";help="Posición al norte o al sur del ecuador. Necesita longitud para ubicar un punto.";g=Group.LOCATION;break;
   case "gpslongitude":title="Longitud";help="Posición al este o al oeste del meridiano de Greenwich.";g=Group.LOCATION;break;
   case "gpslatituderef":title="Hemisferio de latitud";help="N indica norte y S indica sur.";g=Group.LOCATION;break;
   case "gpslongituderef":title="Hemisferio de longitud";help="E indica este y W indica oeste.";g=Group.LOCATION;break;
   case "gpsaltitude":title="Altitud registrada";help="Altura registrada por el sistema GPS; su referencia depende del campo de altitud.";g=Group.LOCATION;break;
   case "gpsdatestamp":case "gpstimestamp":title="Fecha u hora del GPS";help="Referencia temporal del receptor GPS, habitualmente en UTC.";g=Group.LOCATION;break;
   case "imagewidth":case "exifimagewidth":case "pixelxdimension":title="Ancho de la imagen";help="Cantidad de píxeles de lado a lado. Es una característica necesaria de la imagen.";break;
   case "imageheight":case "exifimageheight":case "pixelydimension":title="Alto de la imagen";help="Cantidad de píxeles de arriba hacia abajo.";break;
   case "compressiontype":case "compression":title="Compresión de la imagen";help="Método para guardar la imagen ocupando menos espacio.";break;
   case "dataprecision":case "bitspersample":case "bitdepth":title="Precisión del color";help="Cantidad de bits usados para representar los valores de la imagen.";break;
   case "numberofcomponents":title="Canales de la imagen";help="Cantidad de componentes que describen brillo o color; no son personas ni dispositivos.";break;
   case "colorspace":case "colorspacedata":title="Espacio de color";help="Sistema que utiliza el archivo para representar los colores.";break;
   case "orientation":title="Orientación de la foto";help="Indica si el visor debe girar o reflejar la foto. Quitar este dato puede cambiar cómo se muestra.";break;
   case "xresolution":title="Resolución horizontal de impresión";help="Densidad usada para imprimir; no indica por sí sola el ancho real de la foto.";break;
   case "yresolution":title="Resolución vertical de impresión";help="Densidad usada para imprimir; no es la altura en píxeles.";break;
   case "resolutionunit":case "resolutionunits":title="Unidad de resolución";help="Unidad para la densidad de impresión, como pulgadas o centímetros.";break;
   case "thumbnailwidthpixels":title="Ancho de la miniatura";help="Ancho de una vista previa interna; cero puede indicar que no hay miniatura de ese tipo.";break;
   case "thumbnailheightpixels":title="Alto de la miniatura";help="Alto de una vista previa interna.";break;
   case "version":title="Versión del formato";help="Versión de la estructura de datos indicada en esta sección.";break;
   case "profilesize":title="Tamaño del perfil de color";help="Espacio ocupado por las instrucciones para reproducir los colores.";break;
   case "profiledescription":case "profiledescriptionml":title="Descripción del perfil de color";help="Nombre del perfil utilizado para mostrar o imprimir los colores.";break;
   case "profileclass":case "class":title="Tipo de perfil de color";help="Indica si el perfil describe una pantalla, impresora u otro dispositivo.";break;
   case "title":title="Título guardado";help="Título interno del documento; puede ser distinto del nombre del archivo.";g=Group.AUTHOR;break;
   case "subject":title="Asunto guardado";help="Tema del archivo escrito en sus propiedades.";g=Group.AUTHOR;break;
   case "keywords":title="Palabras clave";help="Etiquetas que pueden revelar el tema, personas o proyectos relacionados.";g=Group.AUTHOR;break;
   case "comment":case "usercomment":case "description":case "imagedescription":title="Comentario o descripción";help="Texto libre guardado dentro del archivo. Puede contener información personal.";g=Group.AUTHOR;break;
   case "pages":case "pagecount":title="Cantidad de páginas";help="Número de páginas del documento.";break;
   case "exposuretime":title="Tiempo de exposición";help="Tiempo durante el que la cámara captó luz.";break;
   case "fnumber":title="Apertura del lente";help="Ajuste de la cámara que regula la entrada de luz.";break;
   case "isospeedequivalent":case "isoequivalent":title="Sensibilidad ISO";help="Sensibilidad a la luz informada por la cámara.";break;
   case "focallength":title="Distancia focal";help="Característica del lente relacionada con el encuadre.";break;
  }
  if(title==null){
   if(k.contains("gps")||k.contains("latitude")||k.contains("longitude")||k.equals("city")||k.equals("country")||k.equals("location")){title="Referencia de ubicación";help="Campo relacionado con un lugar. No siempre representa la ubicación real de captura.";g=Group.LOCATION;}
   else if(k.contains("email")||k.contains("contact")||k.contains("phone")||k.contains("ipaddress")||k.equals("url")){title="Dato de contacto o red";help="Valor escrito en un campo del archivo. No es una IP obtenida de la conexión ni una identidad verificada.";g=Group.AUTHOR;}
   else if(k.contains("prompt")||k.contains("generator")||k.contains("c2pa")||k.contains("provenance")||k.contains("parameters")){title="Campo de generación o procedencia";help="Información declarada en el archivo. No permite por sí sola confirmar o descartar generación con IA.";g=Group.SOFTWARE;}
   else if(k.contains("date")||k.contains("time")){title="Referencia de fecha u hora";help="Valor temporal del campo indicado. Puede usar un formato o una zona horaria propios.";g=Group.DATES;}
   else if(k.startsWith("component")){title="Componente de color";help="Instrucciones internas de brillo o color para decodificar la imagen.";}
   else{title="Propiedad técnica adicional";help="Campo específico del formato, sin explicación particular disponible. Podés consultar su nombre original en el detalle.";}
  }
  if(k.equals("creator")&&section.toLowerCase(Locale.ROOT).contains("pdf")){title="Programa creador del PDF";help="Aplicación que originó el PDF; no es necesariamente el autor del contenido.";g=Group.SOFTWARE;}
  String v=value.length()>1500?value.substring(0,1500)+"… [valor recortado]":value;
  fields.add(new Field(section+" · "+key,title,translate(v),help,g));
 }
 static String translate(String v){switch(v){case "Baseline":return "JPEG estándar (Baseline)";case "Progressive":return "JPEG progresivo";case "Display Device":return "Perfil de pantalla";case "Input Device":return "Perfil de dispositivo de entrada";case "Output Device":return "Perfil de dispositivo de salida";case "none":return "Sin unidad";case "Top, left side (Horizontal / normal)":return "Normal, sin giro";case "1 dot":return "1 punto";default:return v;}}
}
