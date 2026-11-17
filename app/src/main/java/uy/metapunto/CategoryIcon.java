package uy.metapunto;
import android.content.Context;import android.view.View;import android.graphics.*;
final class CategoryIcon extends View{
 final String kind;final Paint p=new Paint(Paint.ANTI_ALIAS_FLAG);
 CategoryIcon(Context c,String k){super(c);kind=k;setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);}
 @Override protected void onDraw(Canvas c){super.onDraw(c);float s=Math.min(getWidth(),getHeight())/48f;c.save();c.translate((getWidth()-48*s)/2,(getHeight()-48*s)/2);c.scale(s,s);p.setStyle(Paint.Style.FILL);p.setColor(Color.rgb(20,53,73));c.drawRoundRect(0,0,48,48,14,14,p);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(2);p.setStrokeCap(Paint.Cap.ROUND);p.setStrokeJoin(Paint.Join.ROUND);p.setColor(Color.rgb(102,224,244));Path a=new Path();
 switch(kind){
 case "pin":c.drawCircle(24,20,8,p);c.drawCircle(24,20,2,p);a.moveTo(17,25);a.lineTo(24,36);a.lineTo(31,25);c.drawPath(a,p);break;
 case "person":c.drawCircle(24,17,5,p);c.drawArc(13,25,35,43,180,180,false,p);break;
 case "camera":c.drawRoundRect(10,15,38,34,4,4,p);c.drawCircle(24,25,6,p);c.drawLine(18,11,30,11,p);break;
 case "calendar":c.drawRoundRect(12,13,36,36,3,3,p);c.drawLine(12,21,36,21,p);c.drawLine(18,10,18,16,p);c.drawLine(30,10,30,16,p);c.drawCircle(22,28,1,p);c.drawCircle(29,28,1,p);break;
 case "app":c.drawRoundRect(11,11,37,37,5,5,p);c.drawLine(11,19,37,19,p);a.moveTo(22,24);a.lineTo(18,28);a.lineTo(22,32);c.drawPath(a,p);a.reset();a.moveTo(27,24);a.lineTo(31,28);a.lineTo(27,32);c.drawPath(a,p);break;
 default:a.moveTo(16,10);a.lineTo(28,10);a.lineTo(35,17);a.lineTo(35,38);a.lineTo(16,38);a.close();c.drawPath(a,p);c.drawLine(28,10,28,17,p);c.drawLine(28,17,35,17,p);c.drawLine(21,25,30,25,p);c.drawLine(21,30,30,30,p);
 }c.restore();}
}
