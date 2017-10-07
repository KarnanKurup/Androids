package com.madhouse.ipic;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.madhouse.ipic.api.AlchemyAPI;
import com.madhouse.ipic.api.AlchemyAPI_ImageParams;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends AppCompatActivity {

    public TextView tv1;
    public ImageView imgv;

    public String Api_key="04fc69c8e2f166a45d79d5a1d28e54f48f398606";

    private FloatingActionButton fab;
    private Uri imageuri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tv1=(TextView)findViewById(R.id.main_txtv1);
        tv1.setMovementMethod(new ScrollingMovementMethod());

        imgv=(ImageView)findViewById(R.id.main_imgview01);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageuri = getImageFileUri();

                System.out.println(":::::::::::: "+imageuri);

                Intent cam = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cam.putExtra(MediaStore.EXTRA_OUTPUT, imageuri);
                cam.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(cam, 1002);
            }
        });

    }

    private static Uri getImageFileUri(){
        File mediaStore=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"iPicCam");
        if(!mediaStore.exists()){
            if (!mediaStore.mkdirs()){
                System.out.println("Error in folder-");
                return null;
            }
        }
        File mediaFile=new File(mediaStore.getPath()+File.separator+"IMG_iPic01.jpg");
        return Uri.fromFile(mediaFile);
    }

    public void main_btn1_Click(View v){
        SendCall();
    }
    public void main_imgview1_Click(View v){
        Intent i=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==1001 && resultCode == RESULT_OK){
            Uri selectedImage=data.getData();
            String[] filePathsColumns={MediaStore.Images.Media.DATA};
            Cursor cur=getContentResolver().query(selectedImage,filePathsColumns,null,null,null);
            cur.moveToFirst();
            int colIndex=cur.getColumnIndex(filePathsColumns[0]);
            String imgpath=cur.getString(colIndex);
            System.out.println("?????????? "+imgpath);
            cur.close();
            try{
                Bitmap imgBitmap=getScaledBitmap(imgpath,400,400);
                ExifInterface exif=new ExifInterface(imgpath);

                int orientation=exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,1);

                switch (orientation){
                    case 3:
                    {
                        Matrix mat=new Matrix();
                        mat.postRotate(90);
                        imgBitmap=Bitmap.createBitmap(imgBitmap,0,0,imgBitmap.getWidth(),imgBitmap.getHeight(),mat,true);
                        break;
                    }
                    case 6:
                    {
                        Matrix mat = new Matrix();
                        mat.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), mat, true);
                        break;
                    }
                }
                imgv.setImageBitmap(imgBitmap);

            }catch (Exception e){
                Snackbar.make(fab, "Error Loading Image: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }else if (requestCode==1002 && resultCode ==RESULT_OK){
            try {
                String imgpath = getImageFileUri().toString();
                imgpath=imgpath.substring(7);
                System.out.println("<<<<<<<<<<<< " + imgpath);

                Bitmap imgBitmap = getScaledBitmap(imgpath, 400, 400);
                ExifInterface exif = new ExifInterface(imgpath);

                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

                switch (orientation) {
                    case 3: {
                        Matrix mat = new Matrix();
                        mat.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), mat, true);
                        break;
                    }
                    case 6: {
                        Matrix mat = new Matrix();
                        mat.postRotate(90);
                        imgBitmap = Bitmap.createBitmap(imgBitmap, 0, 0, imgBitmap.getWidth(), imgBitmap.getHeight(), mat, true);
                        break;
                    }
                }
                imgv.setImageBitmap(imgBitmap);

            }catch (Exception e){
                Snackbar.make(fab, "Error Loading Image: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private Bitmap getScaledBitmap(String picturePath, int width, int height) {
        BitmapFactory.Options sizeOptions = new BitmapFactory.Options();
        sizeOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(picturePath, sizeOptions);

        int inSampleSize = calculateInSampleSize(sizeOptions, width, height);

        sizeOptions.inJustDecodeBounds = false;
        sizeOptions.inSampleSize = inSampleSize;

        return BitmapFactory.decodeFile(picturePath, sizeOptions);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
    private void SendCall(){
        Thread tr=new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SendCallBackGround();
                }catch (Exception e){
                    System.out.println("++++++++++++err1: "+e.toString());
                }
            }
        });
        tr.start();
    }
    private void SendCallBackGround(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv1.setText("Processing...");
            }
        });
        Document doc=null;
        AlchemyAPI api=null;
        try{
            api=AlchemyAPI.GetInstanceFromString(Api_key);
        }catch (IllegalArgumentException e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(fab, "Error Loading API....!!!", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
            return;
        }
        try{
            Bitmap bmp=((BitmapDrawable)imgv.getDrawable()).getBitmap();
            ByteArrayOutputStream stream=new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG,100,stream);
            byte[] imagebyteArray=stream.toByteArray();

            AlchemyAPI_ImageParams imageParams=new AlchemyAPI_ImageParams();
            imageParams.setImage(imagebyteArray);
            imageParams.setImagePostMode(AlchemyAPI_ImageParams.RAW);

            doc=api.ImageGetRankedImageKeywords(imageParams);
            showTagInTextView(doc);
        }catch (final Exception e){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Snackbar.make(fab, "Error: "+e.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
                }
            });
            return;

        }

    }

    private void showTagInTextView(final Document doc){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv1.setText("***Details***\n\n");
                Element root=doc.getDocumentElement();
                String atr;
                if(root.getElementsByTagName("gender").getLength()>0) {
                    atr = "Gender: " + root.getElementsByTagName("gender").item(1).getChildNodes().item(0).getNodeValue();
                    System.out.println(">>>>>>>>>>>>>>1 " + atr);
                    tv1.append(atr + "\n");
                }
                if(root.getElementsByTagName("ageRange").getLength()>0) {
                    atr = "Age: " + root.getElementsByTagName("ageRange").item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(">>>>>>>>>>>>>>2 " + atr);
                    tv1.append(atr + "\n");
                }
                if(root.getElementsByTagName("name").getLength()>0) {
                    atr = "Name: " + root.getElementsByTagName("name").item(0).getChildNodes().item(0).getNodeValue();
                    System.out.println(">>>>>>>>>>>>>>3 " + atr);
                    tv1.append(atr + "\n");
                }
                /*
                //Code for Image Detection

                NodeList items=root.getElementsByTagName("text");
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$" + items.getLength());
                for(int i=0;i<items.getLength();i++){
                    Node conc=items.item(i);
                    String atr= conc.getChildNodes().item(0).getNodeValue();

                    System.out.println("###############3+ "+conc.getChildNodes());

                    tv1.append("\n"+atr);
                }*/
            }
        });
    }
}
