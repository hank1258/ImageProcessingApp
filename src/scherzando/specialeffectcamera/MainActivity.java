package scherzando.specialeffectcamera;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static int RESULT_LOAD_IMG = 1;
	String imgDecodableString;
	private SeekBar mainSeekBar;
	private SeekBar seekBar1, seekBar2, seekBar3;
	String exCommand;
	
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mEffectTitles;
    
    private TextView progressViewer;
    
    private Button touch;
    
    String filename;
	
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		touch = (Button) findViewById(R.id.buttonCom );
		touch.setOnTouchListener(touchlistener);
		
		initializeDrawer();
		
		initializeSeekBars();
		
		progressViewer = (TextView) findViewById(R.id.seekBar_progress_viewer);
		
		imgDecodableString = null;
		exCommand = null;
		filename = null;
	}

	public String getSysNowTime() {
		return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}
	
	public void save() {
		
		ImageView imgView = (ImageView) findViewById(R.id.imgView);
	        
		File publicPicFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
       
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH:mm:ss");
		
		Date curDate = new Date(System.currentTimeMillis());
		
		String str = formatter.format(curDate);
		String type=".jpg";
		filename = str+type;
		File save_image = new File(publicPicFolder,filename);
		
		imgView.setDrawingCacheEnabled(true);
		Bitmap bmp = imgView.getDrawingCache();
		   
		try{
			save_image.createNewFile();
			FileOutputStream fos = new FileOutputStream(save_image);
			bmp.compress(CompressFormat.JPEG,100,fos);
			imgView.destroyDrawingCache();
			fos.flush();
			fos.close();
			Toast.makeText(MainActivity.this, "Saved in Pictures file", Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			Toast.makeText(MainActivity.this, "Save failed", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}
	 
	Button.OnTouchListener touchlistener = new Button.OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (imgDecodableString != null) {
					ImageView imgView = (ImageView) findViewById(R.id.imgView);
					loadBitmap(imgDecodableString ,imgView, "original");
				}
				touch.setBackgroundResource(R.drawable.compare_btn_down);
			} else if (event.getAction() == MotionEvent.ACTION_UP) {
				if (imgDecodableString != null) {
					ImageView imgView = (ImageView) findViewById(R.id.imgView);
					loadBitmap(imgDecodableString ,imgView, exCommand);
				}
				touch.setBackgroundResource(R.drawable.compare_btn);
			}
			return false;
		}
	};
	 
	
	
	
	private void initializeDrawer() {
        mTitle = mDrawerTitle = getTitle();
        mEffectTitles = getResources().getStringArray(R.array.effects_array);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mEffectTitles));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the sliding drawer and the action bar app icon
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
                ) {
            public void onDrawerClosed(View view) {
                getActionBar().setTitle(mTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getActionBar().setTitle(mDrawerTitle);
                //invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
	}
	
	private void initializeSeekBars() {
		mainSeekBar = (SeekBar) findViewById(R.id.main_seekBar);
		seekBar1 = (SeekBar) findViewById(R.id.seekBar1);
		seekBar2 = (SeekBar) findViewById(R.id.seekBar2);
		seekBar3 = (SeekBar) findViewById(R.id.seekBar3);
		mainSeekBar.setOnSeekBarChangeListener(new SeekBarChangeListener());
		seekBar1.setOnSeekBarChangeListener(new SeekBarChangeListener());
		seekBar2.setOnSeekBarChangeListener(new SeekBarChangeListener());
		seekBar3.setOnSeekBarChangeListener(new SeekBarChangeListener());
	}
	
	private class SeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            int val = (progress * (seekBar.getWidth() - 2 * seekBar.getThumbOffset())) / seekBar.getMax();
            progressViewer.setText("" + progress);
            progressViewer.setX(seekBar.getX() + val + seekBar.getThumbOffset() / 2);
            progressViewer.setY(seekBar.getY() - seekBar.getHeight());
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			progressViewer.setVisibility(View.VISIBLE);
		}
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			progressViewer.setVisibility(View.GONE);
			if (imgDecodableString != null) {
				ImageView imgView = (ImageView) findViewById(R.id.imgView);
				loadBitmap(imgDecodableString ,imgView, exCommand);
			}
		}

	}
	
    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }
    
    private void selectItem(int position) {
        // Highlight the selected item, update the title, and close the drawer
        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
        exCommand = getResources().getStringArray(R.array.effects_array)[position];
        runEffects();
    }
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         // The action bar home/up action should open or close the drawer.
         // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        // Handle action buttons
        switch(item.getItemId()) {
        case R.id.action_load:
        	loadImagefromGallery();
            return true;
        case R.id.action_save:
        	if(imgDecodableString != null) {
        		save();
        	}
        	return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }
    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	public void loadImagefromGallery() {
		exCommand = "original";
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
		
	}
	
	private void runEffects() {
		// show/hide seekBar
		mainSeekBar.setVisibility(View.GONE);
		mainSeekBar.setProgress(0);
		seekBar1.setVisibility(View.GONE);
		seekBar1.setProgress(0);
		seekBar2.setVisibility(View.GONE);
		seekBar2.setProgress(0);
		seekBar3.setVisibility(View.GONE);
		seekBar3.setProgress(0);
		if (exCommand.equalsIgnoreCase("alpha") || exCommand.equalsIgnoreCase("decrease")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(255);
		} else if (exCommand.equalsIgnoreCase("bright")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(510);
		} else if (exCommand.equalsIgnoreCase("rotate")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(1000);
		} else if (exCommand.equalsIgnoreCase("sharp")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(160);
		} else if (exCommand.equalsIgnoreCase("gaussian") || exCommand.equalsIgnoreCase("flip")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(1);
		} else if (exCommand.equalsIgnoreCase("smooth")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(200);
		} else if (exCommand.equalsIgnoreCase("tint")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(360);
		} else if (exCommand.equalsIgnoreCase("gamma")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(100);
			seekBar1.setVisibility(View.VISIBLE);
			seekBar1.setMax(100);
			seekBar2.setVisibility(View.VISIBLE);
			seekBar2.setMax(100);
		} else if (exCommand.equalsIgnoreCase("tonning")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(4);
			seekBar1.setVisibility(View.VISIBLE);
			seekBar1.setMax(255);
			seekBar2.setVisibility(View.VISIBLE);
			seekBar2.setMax(255);
			seekBar3.setVisibility(View.VISIBLE);
			seekBar3.setMax(255);
		} else if (exCommand.equalsIgnoreCase("boost")) {
			mainSeekBar.setVisibility(View.VISIBLE);
			mainSeekBar.setMax(2);
			seekBar1.setVisibility(View.VISIBLE);
			seekBar1.setMax(100);
		}
		
		if (imgDecodableString != null) {
			ImageView imgView = (ImageView) findViewById(R.id.imgView);
			loadBitmap(imgDecodableString, imgView, exCommand);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if (requestCode == RESULT_LOAD_IMG && resultCode == RESULT_OK
					&& null != data) {

				Uri selectedImage = data.getData();
				String[] filePathColumn = { MediaStore.Images.Media.DATA };
				
				Cursor cursor = getContentResolver().query(selectedImage,
						filePathColumn, null, null, null);
				cursor.moveToFirst();
					
				int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
				imgDecodableString = cursor.getString(columnIndex);
				cursor.close();
				
				ImageView imgView = (ImageView) findViewById(R.id.imgView);
				loadBitmap(imgDecodableString ,imgView, "original");

			} else {
				Toast.makeText(this, "You haven't picked Image",
						Toast.LENGTH_LONG).show();
			}
		} catch (Exception e) {
			Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
					.show();
		}

	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int halfHeight = height / 2;
			final int halfWidth = width / 2;

			while ((halfHeight / inSampleSize) > reqHeight
					&& (halfWidth / inSampleSize) > reqWidth) {
				inSampleSize *= 2;
			}
		}	
		return inSampleSize;
	}
	
	public static Bitmap decodeSampledBitmapFromFile(String filePath,
	        int reqWidth, int reqHeight) {

	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    BitmapFactory.decodeFile(filePath, options);
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(filePath, options);
	}
	
	class BitmapWorkerTask extends AsyncTask<String, Void, Bitmap> {
	    private final WeakReference<ImageView> imageViewReference;
	    private String data;
	    private String type;
	    private ProgressDialog dialog = null;

	    public BitmapWorkerTask(ImageView imageView) {
	        // Use a WeakReference to ensure the ImageView can be garbage collected
	        imageViewReference = new WeakReference<ImageView>(imageView);
	    }

	    // Decode image in background.
	    @Override
	    protected Bitmap doInBackground(String... params) {
	    	int mp = mainSeekBar.getProgress();
	    	int p1 = seekBar1.getProgress();
	    	int p2 = seekBar2.getProgress();
	    	int p3 = seekBar3.getProgress();
	        data = params[0];
	        type = params[1];
	        Bitmap bitmap = decodeSampledBitmapFromFile(data, 300, 300);
	        SpecialEffectBuilder sb = new SpecialEffectBuilder(bitmap);
	        if (type.equalsIgnoreCase("original")) {
	        	return  sb.alphaModification(0);
	        } else if (type.equalsIgnoreCase("gray")) {
	        	return sb.grayScaleEffect();
	        } else if (type.equalsIgnoreCase("decrease")) {
	        	return sb.decreaseColorDepth(mp+1);
	        } else if (type.equalsIgnoreCase("gamma")) {
	        	 return sb.gammaModification((double)mp/10, (double)p1/10, (double)p2/10);
	        } else if (type.equalsIgnoreCase("tonning")) {
	        	return sb.sepiaTonningEffect(mp+1, p1, p2, p3);
	        } else if (type.equalsIgnoreCase("bright")) {
	        	return  sb.brightnessControl(mp-255);
	        } else if (type.equalsIgnoreCase("rotate")) {
	        	return sb.rotateImage((float)mp/10);
	        } else if (type.equalsIgnoreCase("sharp")) {
	        	return sb.imageSharpenEffect((double)(mp+80)/10);
	        } else if (type.equalsIgnoreCase("gaussian")) {
	        	 return sb.gaussianBlurEffect(mp);
	        } else if (type.equalsIgnoreCase("alpha")) {
	        	return  sb.alphaModification(mp);
	        } else if (type.equalsIgnoreCase("negative")) {
	        	return  sb.negativeFilmEffect();
	        } else if (type.equalsIgnoreCase("removal")) {
	        	return sb.meanRemovalEffect();
	        } else if (type.equalsIgnoreCase("smooth")) {
	        	return sb.smoothEffect((double)mp/10);
	        } else if (type.equalsIgnoreCase("boost")) {
	        	return sb.boostColor(mp+1, (float)p1/100);
	        } else if (type.equalsIgnoreCase("flip")) {
	        	return sb.flipEffect(mp+1);
	        } else if (type.equalsIgnoreCase("tint")) {
	        	return sb.tintImageEffect(mp);
	        } else if (type.equalsIgnoreCase("process")) {
	        	return sb.processingBitmap();
	        } else {
	        	return null;
	        }
	       	        
	    }

	    // Once complete, see if ImageView is still around and set bitmap.
	    @Override
	    protected void onPostExecute(Bitmap bitmap) {
	    	if (dialog != null) {
	            dialog.dismiss();
	    	}
	        if (imageViewReference != null && bitmap != null) {
	            final ImageView imageView = imageViewReference.get();
	            if (imageView != null) {
	                imageView.setImageBitmap(bitmap);
	            }
	        }
	    }
	    
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        if(exCommand != null && (exCommand.equalsIgnoreCase("gaussian") || exCommand.equalsIgnoreCase("sharp") ||
	        		exCommand.equalsIgnoreCase("process") || exCommand.equalsIgnoreCase("removal") ||
	        		exCommand.equalsIgnoreCase("smooth"))) {
		        dialog = ProgressDialog.show(MainActivity.this, "Loading",
	                    "Wait a moment, please.");
	        }
	    }
	}
	
	public void loadBitmap(String filePath, ImageView imageView, String command) {
	    BitmapWorkerTask task = new BitmapWorkerTask(imageView);
	    task.execute(filePath, command);
	}
}
