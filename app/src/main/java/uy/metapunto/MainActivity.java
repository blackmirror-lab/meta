package uy.metapunto;

import android.app.*;
import android.os.*;
import android.content.*;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.widget.*;
import android.view.*;
import android.graphics.*;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import android.animation.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;

public class MainActivity extends Activity {
 private final int BG=Color.rgb(7,17,30),PANEL=Color.rgb(15,32,49),TEXT=Color.rgb(229,242,251),MUTED=Color.rgb(155,178,199),ACCENT=Color.rgb(93,223,241);
 private final ExecutorService worker=Executors.newSingleThreadExecutor();
 private LinearLayout page,results,actions;private Button pick,clean,save;private TextView status;private MetadataAnimationView animation;
 private File input,output;private String name="archivo";private boolean busy;private MetadataReport before,after;private Bitmap thumbnail;private boolean showAfter;
 private AnimatorSet welcomeAnimation;
 int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
 @Override public void onCreate(Bundle b){super.onCreate(b);com.tom_roush.pdfbox.android.PDFBoxResourceLoader.init(getApplicationContext());getWindow().setStatusBarColor(BG);getWindow().setNavigationBarColor(BG);welcome();}
 void install(View content){LinearLayout frame=new LinearLayout(this);frame.setOrientation(1);frame.setBackgroundColor(BG);frame.addView(content,new LinearLayout.LayoutParams(-1,-1));setContentView(frame);
  frame.setOnApplyWindowInsetsListener((v,insets)->{if(Build.VERSION.SDK_INT>=30){android.graphics.Insets bars=insets.getInsets(WindowInsets.Type.systemBars()|WindowInsets.Type.displayCutout());v.setPadding(bars.left,bars.top,bars.right,bars.bottom);}else v.setPadding(insets.getSystemWindowInsetLeft(),insets.getSystemWindowInsetTop(),insets.getSystemWindowInsetRight(),insets.getSystemWindowInsetBottom());return insets;});frame.requestApplyInsets();
 }
 LinearLayout column(){LinearLayout l=new LinearLayout(this);l.setOrientation(1);return l;}
 TextView text(String s,int size,int color){TextView v=new TextView(this);v.setText(s);v.setTextSize(size);v.setTextColor(color);v.setLineSpacing(dp(3),1);return v;}
 void space(LinearLayout l,int h){View v=new View(this);l.addView(v,new LinearLayout.LayoutParams(1,dp(h)));}
 GradientDrawable rounded(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));return d;}
 Button button(String label,boolean primary){Button b=new Button(this);b.setText(label);b.setTextSize(15);b.setAllCaps(false);b.setTextColor(primary?BG:TEXT);b.setMinHeight(dp(50));b.setBackgroundTintList(new ColorStateList(new int[][]{new int[]{-android.R.attr.state_enabled},new int[]{}},new int[]{Color.rgb(27,44,59),primary?ACCENT:Color.rgb(25,62,83)}));return b;}
 void welcome(){
  LinearLayout box=column();box.setGravity(Gravity.CENTER);box.setPadding(dp(28),dp(16),dp(28),dp(16));ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);scroll.addView(box);install(scroll);
  ImageView logo=new ImageView(this);logo.setImageResource(uy.metapunto.R.drawable.app_logo);logo.setContentDescription("MetaPunto: documento protegido con un punto de luz");logo.setScaleType(ImageView.ScaleType.FIT_CENTER);box.addView(logo,new LinearLayout.LayoutParams(dp(220),dp(220)));
  TextView brand=text("MetaPunto",34,TEXT);brand.setGravity(Gravity.CENTER);box.addView(brand);space(box,8);
  TextView lead=text("Conocé lo que tus archivos cuentan de vos",20,ACCENT);lead.setGravity(Gravity.CENTER);box.addView(lead);space(box,16);
  TextView desc=text("Explorá sus datos, entendé qué significan y guardá una copia con menos información personal.",15,MUTED);desc.setGravity(Gravity.CENTER);box.addView(desc);space(box,20);
  TextView local=text("En tu celular · Sin subir archivos",13,MUTED);local.setGravity(Gravity.CENTER);box.addView(local);space(box,24);
  Button start=button("Comenzar",true);box.addView(start,new LinearLayout.LayoutParams(-1,dp(54)));start.setOnClickListener(v->{stopWelcome();build();});space(box,10);
  TextView hint=text("Vos elegís el archivo. El original se conserva.",12,MUTED);hint.setGravity(Gravity.CENTER);box.addView(hint);
  if(ValueAnimator.areAnimatorsEnabled()){
   logo.setAlpha(0);logo.setTranslationY(dp(18));logo.animate().alpha(1).translationY(0).setDuration(650).start();brand.setAlpha(0);brand.animate().alpha(1).setStartDelay(200).setDuration(600).start();
   ObjectAnimator sx=ObjectAnimator.ofFloat(logo,View.SCALE_X,1f,1.035f),sy=ObjectAnimator.ofFloat(logo,View.SCALE_Y,1f,1.035f);for(ObjectAnimator a:new ObjectAnimator[]{sx,sy}){a.setRepeatCount(ValueAnimator.INFINITE);a.setRepeatMode(ValueAnimator.REVERSE);a.setDuration(1900);}welcomeAnimation=new AnimatorSet();welcomeAnimation.playTogether(sx,sy);welcomeAnimation.setStartDelay(700);welcomeAnimation.start();
  }
 }
 void stopWelcome(){if(welcomeAnimation!=null){welcomeAnimation.cancel();welcomeAnimation=null;}}
 void build(){
  ScrollView scroll=new ScrollView(this);scroll.setFillViewport(true);page=column();page.setPadding(dp(20),dp(16),dp(20),dp(24));scroll.addView(page);install(scroll);
  LinearLayout header=new LinearLayout(this);header.setGravity(Gravity.CENTER_VERTICAL);ImageView logo=new ImageView(this);logo.setImageResource(uy.metapunto.R.drawable.app_logo);logo.setContentDescription("MetaPunto");header.addView(logo,new LinearLayout.LayoutParams(dp(44),dp(44)));TextView title=text("  MetaPunto",25,TEXT);header.addView(title,new LinearLayout.LayoutParams(0,-2,1));Button about=button("?",false);header.addView(about,new LinearLayout.LayoutParams(dp(48),dp(48)));about.setContentDescription("Cómo funciona MetaPunto");about.setOnClickListener(v->info("Tu privacidad, paso a paso","1. Elegí un archivo.\n2. Tocá las tarjetas para entender sus datos.\n3. Creá una copia y compará el antes y el después.\n4. Guardala donde prefieras.\n\nTodo se procesa en el celular. La limpieza no garantiza eliminar marcas de IA, firmas, comentarios o toda información oculta. Las ilustraciones de escaneo son decorativas."));page.addView(header);space(page,8);
  page.addView(text("Tus archivos, explicados",21,ACCENT));page.addView(text("Descubrí qué información guardan y decidí qué compartir.",14,MUTED));
  animation=new MetadataAnimationView(this);page.addView(animation,new LinearLayout.LayoutParams(-1,dp(135)));
  pick=button("Elegir archivo",true);page.addView(pick,new LinearLayout.LayoutParams(-1,dp(54)));pick.setOnClickListener(v->choose());space(page,8);
  status=text("Imágenes, PDF y documentos de Office · hasta 40 MB",12,MUTED);status.setAccessibilityLiveRegion(View.ACCESSIBILITY_LIVE_REGION_POLITE);page.addView(status);space(page,16);
  results=column();page.addView(results);actions=column();page.addView(actions);
  clean=button("Crear copia limpia",true);actions.addView(clean,new LinearLayout.LayoutParams(-1,dp(54)));clean.setOnClickListener(v->confirmClean());
  save=button("Guardar copia",false);actions.addView(save,new LinearLayout.LayoutParams(-1,dp(54)));save.setOnClickListener(v->save());actions.setVisibility(View.GONE);
 }
 void choose(){if(busy)return;Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);startActivityForResult(i,1);}
 interface Work{void run()throws Exception;}
 void work(MetadataAnimationView.Mode mode,Work w){busy=true;pick.setEnabled(false);clean.setEnabled(false);save.setEnabled(false);animation.show(mode);status.setText(mode==MetadataAnimationView.Mode.SCAN?"Revisando los datos del archivo…":mode==MetadataAnimationView.Mode.CLEAN?"Creando una copia con menos datos…":"Guardando tu copia…");worker.execute(()->{String error=null;try{w.run();}catch(Exception e){error=e.getMessage()==null?"No se pudo procesar el archivo.":e.getMessage();}String failure=error;runOnUiThread(()->{if(isFinishing()||isDestroyed())return;busy=false;pick.setEnabled(true);clean.setEnabled(input!=null&&Engine.supported(name));save.setEnabled(output!=null);animation.show(failure!=null?MetadataAnimationView.Mode.ERROR:mode==MetadataAnimationView.Mode.CLEAN?MetadataAnimationView.Mode.DONE:MetadataAnimationView.Mode.READY);
   if(failure!=null){status.setText("No se completó el proceso");info("No se pudo completar",failure);return;}
   status.setText(mode==MetadataAnimationView.Mode.CLEAN?"Copia creada. Compará los datos y guardala.":mode==MetadataAnimationView.Mode.SAVE?"Copia guardada.":"Análisis listo. Tocá una tarjeta para explorar.");render();
  });});}
 @Override protected void onActivityResult(int request,int result,Intent data){super.onActivityResult(request,result,data);if(result!=RESULT_OK||data==null||data.getData()==null)return;Uri uri=data.getData();
  if(request==1){output=null;input=null;before=null;after=null;thumbnail=null;showAfter=false;results.removeAllViews();actions.setVisibility(View.GONE);name="archivo";
   try(android.database.Cursor c=getContentResolver().query(uri,null,null,null,null)){if(c!=null&&c.moveToFirst()){int n=c.getColumnIndex(OpenableColumns.DISPLAY_NAME);if(n>=0&&c.getString(n)!=null)name=c.getString(n);}}catch(Exception ignored){}
   name=name.replaceAll("[\\\\/\\r\\n]","_");
   work(MetadataAnimationView.Mode.SCAN,()->{File f=new File(getCacheDir(),"entrada");try(InputStream in=getContentResolver().openInputStream(uri);OutputStream out=new FileOutputStream(f)){if(in==null)throw new IOException("No se pudo abrir el archivo.");byte[] buf=new byte[8192];int n;long total=0;while((n=in.read(buf))!=-1){total+=n;if(total>40L*1024*1024)throw new IOException("Elegí un archivo de hasta 40 MB.");out.write(buf,0,n);}}MetadataReport report=Inspector.read(f,name);before=report;thumbnail=preview(f,report.orientation);input=f;});
  }else if(request==2&&output!=null)work(MetadataAnimationView.Mode.SAVE,()->{try(InputStream in=new FileInputStream(output);OutputStream out=getContentResolver().openOutputStream(uri,"w")){if(out==null)throw new IOException("No se pudo abrir el destino.");Engine.copy(in,out);}});
 }
 void confirmClean(){if(busy||input==null)return;new AlertDialog.Builder(this).setTitle("Crear una copia limpia").setMessage("El original se conserva. Los campos de texto compatibles quedarán con un punto (.). Se quitarán fechas y coordenadas compatibles.\n\nPuede cambiar la orientación o el color de una foto e invalidar firmas digitales. Los comentarios, revisiones, imágenes internas y adjuntos de documentos pueden conservar información.").setPositiveButton("Crear copia",(d,w)->{output=null;after=null;work(MetadataAnimationView.Mode.CLEAN,()->{File f=new File(getCacheDir(),"salida");Engine.clean(input,f,name);after=Inspector.read(f,name);output=f;showAfter=true;});}).setNegativeButton("Cancelar",null).show();}
 void save(){if(busy||output==null)return;Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("application/octet-stream");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"limpio_"+name);startActivityForResult(i,2);}
 void info(String title,String detail){new AlertDialog.Builder(this).setTitle(title).setMessage(detail).setPositiveButton("Entendido",null).show();}
 LinearLayout card(){LinearLayout c=column();c.setPadding(dp(16),dp(15),dp(16),dp(15));c.setBackground(rounded(PANEL,18));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,-2);p.bottomMargin=dp(12);c.setLayoutParams(p);return c;}
 void render(){results.removeAllViews();MetadataReport r=showAfter&&after!=null?after:before;if(r==null)return;
  if(after!=null){LinearLayout tabs=new LinearLayout(this);Button original=button("Original",!showAfter),copy=button("Copia",showAfter);tabs.addView(original,new LinearLayout.LayoutParams(0,dp(48),1));tabs.addView(copy,new LinearLayout.LayoutParams(0,dp(48),1));original.setOnClickListener(v->{showAfter=false;render();});copy.setOnClickListener(v->{showAfter=true;render();});results.addView(tabs);space(results,12);}
  LinearLayout file=card();LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL);
  if(thumbnail!=null){ImageView photo=new ImageView(this);photo.setImageBitmap(thumbnail);photo.setScaleType(ImageView.ScaleType.CENTER_CROP);photo.setContentDescription("Vista previa del archivo original");row.addView(photo,new LinearLayout.LayoutParams(dp(72),dp(72)));photo.setOnClickListener(v->{ImageView full=new ImageView(this);full.setImageBitmap(thumbnail);full.setAdjustViewBounds(true);full.setContentDescription("Vista previa del original");new AlertDialog.Builder(this).setTitle("Vista previa del original").setView(full).setPositiveButton("Cerrar",null).show();});}else row.addView(new CategoryIcon(this,"file"),new LinearLayout.LayoutParams(dp(58),dp(58)));
  LinearLayout labels=column();labels.setPadding(dp(12),0,0,0);labels.addView(text(name,16,TEXT));labels.addView(text((showAfter?"Copia · ":"Original · ")+size(r.size)+" · "+Engine.ext(name).toUpperCase(Locale.ROOT),12,MUTED));row.addView(labels,new LinearLayout.LayoutParams(0,-2,1));file.addView(row);if(thumbnail!=null)file.addView(text("Tocá la imagen para ampliar la vista previa del original",11,MUTED));results.addView(file);
  TextView summary=text(r.complete?(r.personalCount()>0?"Se encontraron "+r.personalCount()+" campos para revisar":"Sin datos personales reconocidos en los campos leídos"):"Revisión parcial: consultá las notas",18,TEXT);results.addView(summary);results.addView(text("No encontrar un dato no demuestra que el archivo nunca lo haya tenido.",12,MUTED));space(results,16);
  for(MetadataReport.Group group:MetadataReport.Group.values())category(r,group);
  LinearLayout notes=card();notes.addView(text("Qué se pudo revisar",15,TEXT));for(String note:r.notices)notes.addView(text(note,12,MUTED));notes.addView(text("Las IP solo se muestran si están escritas en los campos extraídos. No se deduce la IP del autor. No se comprueban ni eliminan universalmente marcas de IA.",12,MUTED));results.addView(notes);
  actions.setVisibility(View.VISIBLE);clean.setEnabled(!busy&&Engine.supported(name));clean.setVisibility(Engine.supported(name)?View.VISIBLE:View.GONE);save.setVisibility(output!=null?View.VISIBLE:View.GONE);save.setEnabled(!busy&&output!=null);
 }
 void category(MetadataReport r,MetadataReport.Group group){
  java.util.List<MetadataReport.Field> fields=r.in(group);LinearLayout c=card(),heading=new LinearLayout(this);heading.setGravity(Gravity.CENTER_VERTICAL);heading.addView(new CategoryIcon(this,group.icon),new LinearLayout.LayoutParams(dp(44),dp(44)));LinearLayout labels=column();labels.setPadding(dp(12),0,0,0);labels.addView(text(group.title,16,TEXT));labels.addView(text(fields.isEmpty()?"No encontrado en los campos revisados":fields.size()+" datos · tocá para ver",12,fields.isEmpty()?MUTED:ACCENT));heading.addView(labels,new LinearLayout.LayoutParams(0,-2,1));TextView arrow=text("+",25,ACCENT);heading.addView(arrow);c.addView(heading);
  LinearLayout detail=column();detail.setVisibility(View.GONE);c.addView(detail);heading.setContentDescription(group.title+". "+(fields.isEmpty()?"Sin campos encontrados":"Mostrar "+fields.size()+" datos"));heading.setFocusable(true);heading.setOnClickListener(v->{if(fields.isEmpty()){info(group.title,group.help+"\n\nNo se encontraron estos campos con el lector disponible. Pueden haberse quitado o no estar soportados.");return;}boolean open=detail.getVisibility()!=View.VISIBLE;detail.setVisibility(open?View.VISIBLE:View.GONE);arrow.setText(open?"−":"+");if(open&&detail.getChildCount()==0){space(detail,12);detail.addView(text(group.help,13,MUTED));
   if(group==MetadataReport.Group.LOCATION&&r.latitude!=null&&r.longitude!=null){Button map=button("Abrir coordenadas en un mapa",false);detail.addView(map);map.setOnClickListener(w->{String point=String.format(Locale.US,"%.7f,%.7f",r.latitude,r.longitude);try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("geo:"+point+"?q="+Uri.encode(point))));}catch(ActivityNotFoundException e){info("Coordenadas",point+"\nNo hay una aplicación de mapas disponible.");}});}
   int count=0;for(MetadataReport.Field f:fields){if(++count>100){detail.addView(text("Hay más campos en esta categoría; se muestran los primeros 100.",12,MUTED));break;}space(detail,12);View line=new View(this);line.setBackgroundColor(Color.rgb(30,54,73));detail.addView(line,new LinearLayout.LayoutParams(-1,dp(1)));space(detail,10);detail.addView(text(f.title,14,ACCENT));TextView val=text(f.value.equals(".")?".  · campo reemplazado":f.value,15,TEXT);val.setTextIsSelectable(true);detail.addView(val);detail.addView(text(f.help,12,MUTED));TextView more=text("Ver nombre técnico",12,ACCENT);more.setPadding(0,dp(8),0,dp(8));more.setFocusable(true);more.setOnClickListener(w->info(f.title,"Nombre técnico original:\n"+f.original+"\n\n"+f.help));detail.addView(more);}
  }});results.addView(c);
 }
 String size(long bytes){return bytes<1024?bytes+" bytes":bytes<1024*1024?String.format(Locale.getDefault(),"%.1f KB",bytes/1024f):String.format(Locale.getDefault(),"%.1f MB",bytes/(1024f*1024f));}
 Bitmap preview(File f,int orientation){try{BitmapFactory.Options o=new BitmapFactory.Options();o.inJustDecodeBounds=true;BitmapFactory.decodeFile(f.getAbsolutePath(),o);if(o.outWidth<=0||o.outHeight<=0)return null;o.inSampleSize=1;while(o.outWidth/o.inSampleSize>640||o.outHeight/o.inSampleSize>640)o.inSampleSize*=2;o.inJustDecodeBounds=false;Bitmap b=BitmapFactory.decodeFile(f.getAbsolutePath(),o);if(b==null)return null;Matrix m=new Matrix();switch(orientation){case 2:m.setScale(-1,1);break;case 3:m.setRotate(180);break;case 4:m.setScale(1,-1);break;case 5:m.setRotate(90);m.postScale(-1,1);break;case 6:m.setRotate(90);break;case 7:m.setRotate(270);m.postScale(-1,1);break;case 8:m.setRotate(270);break;}return m.isIdentity()?b:Bitmap.createBitmap(b,0,0,b.getWidth(),b.getHeight(),m,true);}catch(Exception e){return null;}}
 @Override protected void onPause(){super.onPause();if(welcomeAnimation!=null)welcomeAnimation.pause();}
 @Override protected void onResume(){super.onResume();if(welcomeAnimation!=null)welcomeAnimation.resume();}
 @Override protected void onDestroy(){stopWelcome();worker.shutdownNow();super.onDestroy();}
}
