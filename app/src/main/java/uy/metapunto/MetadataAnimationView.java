package uy.metapunto;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.*;
import android.view.View;
import android.view.animation.LinearInterpolator;

/** Local decorative animation; phase never represents file-processing progress. */
public final class MetadataAnimationView extends View {
 public enum Mode { INTRO, READY, SCAN, CLEAN, SAVE, DONE, ERROR }
 private Mode mode=Mode.READY;
 private ValueAnimator animator;
 private float phase;
 private final Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
 private final Path path=new Path();
 private static final int CYAN=Color.rgb(83,220,244), MUTED=Color.rgb(139,165,191);
 public MetadataAnimationView(Context c){super(c);setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);show(Mode.INTRO);}
 public void show(Mode value){stop();mode=value;phase=0;setContentDescription(label());restart();invalidate();}
 private boolean moving(){return mode==Mode.INTRO||mode==Mode.SCAN||mode==Mode.CLEAN||mode==Mode.SAVE;}
 private String label(){switch(mode){case INTRO:return "MetaPunto · privacidad en tus archivos";case SCAN:return "Inspeccionando metadatos…";case CLEAN:return "Limpiando la copia…";case SAVE:return "Guardando copia…";case DONE:return "Copia lista";case ERROR:return "No se completó el proceso";default:return "Seleccioná un archivo para comenzar";}}
 private void stop(){if(animator!=null){animator.removeAllListeners();animator.cancel();animator=null;}}
 private void restart(){if(animator!=null||!isAttachedToWindow()||getWindowVisibility()!=VISIBLE||!moving())return;if(!ValueAnimator.areAnimatorsEnabled()){if(mode==Mode.INTRO)mode=Mode.READY;setContentDescription(label());invalidate();return;}animator=ValueAnimator.ofFloat(0f,1f);animator.setDuration(mode==Mode.INTRO?1700:2300);animator.setInterpolator(new LinearInterpolator());animator.setRepeatCount(mode==Mode.INTRO?0:ValueAnimator.INFINITE);animator.addUpdateListener(a->{phase=(float)a.getAnimatedValue();invalidate();});if(mode==Mode.INTRO)animator.addListener(new AnimatorListenerAdapter(){@Override public void onAnimationEnd(Animator a){show(Mode.READY);}});animator.start();}
 @Override protected void onAttachedToWindow(){super.onAttachedToWindow();restart();}
 @Override protected void onDetachedFromWindow(){stop();super.onDetachedFromWindow();}
 @Override protected void onWindowVisibilityChanged(int visibility){super.onWindowVisibilityChanged(visibility);if(visibility==VISIBLE){if(animator==null)restart();}else stop();}
 private void line(Canvas c,float x,float y,float x2,float y2,int color,float width){paint.setColor(color);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(width);c.drawLine(x,y,x2,y2,paint);}
 private void text(Canvas c,String t,float x,float y,int color,float size){paint.setStyle(Paint.Style.FILL);paint.setColor(color);paint.setTextSize(size);paint.setTypeface(Typeface.create("sans-serif-medium",Typeface.NORMAL));paint.setTextAlign(Paint.Align.CENTER);c.drawText(t,x,y,paint);}
 @Override protected void onDraw(Canvas canvas){super.onDraw(canvas);float scale=Math.min(getWidth()/360f,getHeight()/190f);if(scale<=0)return;canvas.save();canvas.translate((getWidth()-360*scale)/2,(getHeight()-190*scale)/2);canvas.scale(scale,scale);
 paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(1);paint.setColor(Color.rgb(25,51,73));canvas.drawCircle(180,80,68,paint);canvas.drawCircle(180,80,50,paint);
 path.reset();path.moveTo(153,32);path.lineTo(192,32);path.lineTo(209,49);path.lineTo(209,121);path.lineTo(153,121);path.close();paint.setStyle(Paint.Style.FILL);paint.setColor(Color.rgb(17,37,56));canvas.drawPath(path,paint);paint.setStyle(Paint.Style.STROKE);paint.setStrokeWidth(2);paint.setColor(CYAN);canvas.drawPath(path,paint);line(canvas,192,32,192,49,CYAN,1.5f);line(canvas,192,49,209,49,CYAN,1.5f);
 for(int j=0;j<3;j++)line(canvas,164,65+j*13,198-(j%2)*8,65+j*13,Color.rgb(63,101,129),2);
 if(mode==Mode.DONE){paint.setStyle(Paint.Style.FILL);paint.setColor(Color.rgb(42,196,146));canvas.drawCircle(207,113,17,paint);line(canvas,199,113,205,119,Color.WHITE,2.5f);line(canvas,205,119,216,106,Color.WHITE,2.5f);}
 else if(mode==Mode.ERROR){text(canvas,"!",209,124,Color.rgb(255,170,124),29);}
 else{
 String[] labels=mode==Mode.CLEAN?new String[]{"Nombre","Autor","Título","Programa"}:new String[]{"Lugar","Autor","Fecha","Programa"};float[] xs={79,281,79,281},ys={54,54,115,115};
 for(int j=0;j<4;j++){float bob=moving()?(float)Math.sin(phase*Math.PI*2+j)*3:0;line(canvas,xs[j]+(j%2==0?23:-23),ys[j]-4, j%2==0?149:213,76,Color.rgb(35,74,98),1);String t=labels[j];int color=MUTED;
 if(mode==Mode.CLEAN){float tphase=(phase+j*.16f)%1f;if(tphase>.45f){t=".";color=CYAN;}}
 text(canvas,t,xs[j],ys[j]+bob,color,13);}
 if(moving()){float y=39+phase*77;paint.setShader(new LinearGradient(142,y-18,142,y,new int[]{Color.TRANSPARENT,Color.argb(55,83,220,244)},null,Shader.TileMode.CLAMP));paint.setStyle(Paint.Style.FILL);canvas.drawRect(143,y-18,219,y,paint);paint.setShader(null);line(canvas,143,y,219,y,CYAN,2);}
 }
 text(canvas,label(),180,165,mode==Mode.ERROR?Color.rgb(255,170,124):CYAN,14);canvas.restore();
 }
}
