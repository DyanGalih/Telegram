package com.gamatechno.gtfwm.message;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by dyangalih on 5/26/17.
 */

public class Loader {
   private static Loader instance;
   private ProgressDialog progress;

   public static Loader getInstance() {
      if(instance==null){
         instance = new Loader();
      }
      return instance;
   }

   public void start(Context context){
      if(progress==null) {
         progress = new ProgressDialog(context);
      }
      if (!progress.isShowing()) {
         progress.setTitle("Loading");
         progress.setMessage("Wait while loading...");
         progress.show();
      }
   }

   public void stop(){
      try {
         if (progress != null && progress.isShowing()) {
            progress.dismiss();
         }
      } catch (final IllegalArgumentException e) {
         // Handle or log or ignore
      } catch (final Exception e) {
         // Handle or log or ignore
      } finally {
         progress = null;
      }
   }
}
