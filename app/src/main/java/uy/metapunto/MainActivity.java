package uy.metapunto;

import android.app.*;
import android.os.Bundle;
import android.content.Intent;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.*;
import java.io.*;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
 private TextView report; private Button clean; private File input,output; private String name; private boolean busy;
 @Override public void onCreate(Bundle b){super.onCreate(b);com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(getApplicationContext());
 LinearLayout l=new LinearLayout(this);l.setOrientation(1);l.setPadding(28,35,28,20);setContentView(l);
 TextView title=new TextView(this);title.setText("MetaPunto · inspeccionar y limpiar");title.setTextSize(23);l.addView(title);
 TextView desc=new TextView(this);desc.setText("Sin conexión. Conserva el original. Texto → punto; GPS y fechas → eliminados. No garantiza borrar marcas de IA. Máximo 40 MB.");l.addView(desc);
 Button pick=new Button(this);pick.setText("Seleccionar archivo");l.addView(pick);pick.setOnClickListener(v->{if(busy)return;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,1);});
 clean=new Button(this);clean.setText("Crear copia limpia con punto");clean.setEnabled(false);l.addView(clean);clean.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Crear copia limpia").setMessage("Se reemplazan propiedades de texto por un punto. La limpieza puede invalidar firmas digitales. Comentarios, revisiones, adjuntos y contenido visible se conservan; pueden contener datos personales. No se asegura eliminar toda información oculta.").setPositiveButton("Crear copia",(d,w)->process()).setNegativeButton("Cancelar",null).show());
 Button save=new Button(this);save.setText("Guardar copia limpia");l.addView(save);save.setOnClickListener(v->{if(busy||output==null){toast("Primero creá una copia limpia");return;}Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/octet-stream");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"limpio_"+name);startActivityForResult(i,2);});
 ScrollView s=new ScrollView(this);report=new TextView(this);report.setTextIsSelectable(true);report.setText("Seleccioná JPG, PNG, WebP, PDF, DOCX, XLSX o PPTX. Otros formatos: inspección según compatibilidad. Las IP solo se muestran si aparecen en campos extraídos; no se deducen.");s.addView(report);l.addView(s,new LinearLayout.LayoutParams(-1,0,1));
 }
 void toast(String x){Toast.makeText(this,x,Toast.LENGTH_LONG).show();}
 interface Work{String run()throws Exception;}
 void work(Work w){busy=true;clean.setEnabled(false);report.setText("Procesando…");new Thread(()->{String result;try{result=w.run();}catch(Exception e){result="No se completó: "+e.getMessage();}String r=result;runOnUiThread(()->{busy=false;report.setText(r);clean.setEnabled(input!=null&&Engine.supported(name));});}).start();}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();
 if(request==1){output=null;input=null;name="archivo";try(android.database.Cursor c=getContentResolver().query(uri,null,null,null,null)){if(c!=null&&c.moveToFirst()){int n=c.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(n>=0)name=c.getString(n);}}name=name.replaceAll("[\\\\/\\r\\n]","_");work(()->{File f=new File(getCacheDir(),"entrada");try(InputStream in=getContentResolver().openInputStream(uri);OutputStream out=new FileOutputStream(f)){byte[] buf=new byte[8192];int n;long total=0;while((n=in.read(buf))!=-1){total+=n;if(total>40L*1024*1024)throw new IOException("Supera 40 MB");out.write(buf,0,n);}}input=f;return "ARCHIVO: "+name+"\n"+Engine.inspect(f,name);});}
 if(request==2&&output!=null)work(()->{try(InputStream in=new FileInputStream(output);OutputStream out=getContentResolver().openOutputStream(uri,"w")){Engine.copy(in,out);}return "Copia guardada.\n\n"+Engine.inspect(output,name);});
 }
 void process(){output=null;work(()->{File f=new File(getCacheDir(),"salida");Engine.clean(input,f,name);String after=Engine.inspect(f,name);output=f;return "COPIA CREADA. Guardala con el botón superior.\nSe limpiaron las categorías compatibles, no todo el contenido oculto.\n\nDESPUÉS:\n"+after;});}
}
