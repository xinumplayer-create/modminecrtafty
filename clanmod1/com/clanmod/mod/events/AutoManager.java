/* Decompiler 150ms, total 384ms, lines 208 */
package com.clanmod.mod.events;

import java.lang.reflect.Field;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.class_304;
import net.minecraft.class_310;
import net.minecraft.class_3675.class_307;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWMouseButtonCallbackI;

public class AutoManager {
   private static final class_310 mc = class_310.method_1551();
   private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
   public static volatile boolean clickEnabled = false;
   public static volatile boolean adEnabled = false;
   public static volatile String adCommand = "";
   public static volatile int adCount = 0;
   public static volatile int clickCount = 0;
   private static ScheduledFuture<?> clickTask;
   private static ScheduledFuture<?> adTask;

   private static long randomInterval() {
      return 120000L + (long)(Math.random() * 60000.0D);
   }

   public static boolean isClickRunning() {
      return clickTask != null && !clickTask.isDone();
   }

   public static void startClickOnClose() {
      stopClick();
      if (clickEnabled) {
         scheduler.execute(AutoManager::doDoubleClick);
         scheduleNextClick();
      }
   }

   public static void stopClick() {
      if (clickTask != null) {
         clickTask.cancel(false);
         clickTask = null;
      }

   }

   private static void scheduleNextClick() {
      if (clickEnabled) {
         clickTask = scheduler.schedule(() -> {
            doDoubleClick();
            scheduleNextClick();
         }, randomInterval(), TimeUnit.MILLISECONDS);
      }
   }

   private static long getGLFWHandle() {
      try {
         Field[] var0 = mc.method_22683().getClass().getDeclaredFields();
         int var1 = var0.length;

         for(int var2 = 0; var2 < var1; ++var2) {
            Field f = var0[var2];
            if (f.getType() == Long.TYPE) {
               f.setAccessible(true);
               long v = (Long)f.get(mc.method_22683());
               if (v != 0L) {
                  return v;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      return 0L;
   }

   private static void doDoubleClick() {
      if (clickEnabled) {
         scheduler.schedule(() -> {
            mc.execute(() -> {
               if (clickEnabled && mc.field_1724 != null) {
                  class_304.method_1420(class_307.field_1672.method_1447(0));
               }
            });
         }, 200L, TimeUnit.MILLISECONDS);
         int[] attempts = new int[]{0};
         ScheduledFuture<?>[] pollRef = new ScheduledFuture[]{null};
         pollRef[0] = scheduler.scheduleAtFixedRate(() -> {
            int var10002 = attempts[0]++;
            if (attempts[0] > 30) {
               pollRef[0].cancel(false);
            } else {
               CompletableFuture<Boolean> screenCheck = new CompletableFuture();
               mc.execute(() -> {
                  screenCheck.complete(mc.field_1755 != null);
               });

               boolean screenOpen;
               try {
                  screenOpen = (Boolean)screenCheck.get(50L, TimeUnit.MILLISECONDS);
               } catch (Exception var5) {
                  return;
               }

               if (screenOpen) {
                  pollRef[0].cancel(false);
                  mc.execute(() -> {
                     if (clickEnabled) {
                        long handle = getGLFWHandle();
                        if (handle != 0L) {
                           double[] xArr = new double[1];
                           double[] yArr = new double[1];
                           GLFW.glfwGetCursorPos(handle, xArr, yArr);
                           double newX = xArr[0];
                           double newY = yArr[0] - 100.0D;
                           GLFW.glfwSetCursorPos(handle, newX, newY);
                        }
                     }
                  });
                  scheduler.schedule(() -> {
                     mc.execute(() -> {
                        if (clickEnabled) {
                           long handle = getGLFWHandle();
                           if (handle != 0L) {
                              double[] cx = new double[1];
                              double[] cy = new double[1];
                              GLFW.glfwGetCursorPos(handle, cx, cy);
                              if (mc.field_1755 != null) {
                                 GLFWMouseButtonCallbackI cb = GLFW.glfwSetMouseButtonCallback(handle, (GLFWMouseButtonCallbackI)null);
                                 if (cb != null) {
                                    GLFW.glfwSetMouseButtonCallback(handle, cb);
                                    cb.invoke(handle, 0, 1, 0);
                                    cb.invoke(handle, 0, 0, 0);
                                 }
                              } else {
                                 class_304.method_1420(class_307.field_1672.method_1447(0));
                              }

                              ++clickCount;
                           }
                        }
                     });
                  }, 150L, TimeUnit.MILLISECONDS);
               }
            }
         }, 800L, 100L, TimeUnit.MILLISECONDS);
      }
   }

   public static boolean isAdRunning() {
      return adTask != null && !adTask.isDone();
   }

   public static void startAdOnClose() {
      stopAd();
      if (adEnabled) {
         scheduler.execute(AutoManager::doSendAd);
         scheduleNextAd();
      }
   }

   public static void stopAd() {
      if (adTask != null) {
         adTask.cancel(false);
         adTask = null;
      }

   }

   private static void scheduleNextAd() {
      if (adEnabled) {
         adTask = scheduler.schedule(() -> {
            doSendAd();
            scheduleNextAd();
         }, randomInterval(), TimeUnit.MILLISECONDS);
      }
   }

   private static void doSendAd() {
      if (adEnabled) {
         String cmd = adCommand.trim();
         if (!cmd.isEmpty()) {
            mc.execute(() -> {
               if (mc.field_1724 != null) {
                  if (cmd.startsWith("/")) {
                     mc.field_1724.field_3944.method_45730(cmd.substring(1));
                  } else {
                     mc.field_1724.field_3944.method_45729(cmd);
                  }

                  ++adCount;
               }
            });
         }
      }
   }

   public static void shutdownAll() {
      stopClick();
      stopAd();
      scheduler.shutdownNow();
   }
}
