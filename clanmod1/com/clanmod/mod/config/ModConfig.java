/* Decompiler 30ms, total 238ms, lines 70 */
package com.clanmod.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {
   private static final File FILE = new File("config/chatclanbot.json");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static ModConfig instance;
   public boolean autoGiveEnabled = false;
   public boolean balanceOnJoinEnabled = false;
   public boolean balanceOnLeaveEnabled = false;
   public boolean blacklistJoinEnabled = false;
   public boolean blacklistLeaveEnabled = true;
   public String adCommand = "";
   public List<com.clanmod.mod.config.ModConfig.CustomCommand> customCommands = new ArrayList();
   public List<String> blacklist = new ArrayList();

   public static ModConfig getInstance() {
      if (instance == null) {
         instance = new ModConfig();
      }

      return instance;
   }

   public static void load() {
      try {
         if (FILE.exists()) {
            Reader reader = new FileReader(FILE);
            ModConfig loaded = (ModConfig)GSON.fromJson(reader, ModConfig.class);
            reader.close();
            if (loaded != null) {
               instance = loaded;
               if (instance.customCommands == null) {
                  instance.customCommands = new ArrayList();
               }

               if (instance.blacklist == null) {
                  instance.blacklist = new ArrayList();
               }
            }
         }
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public void save() {
      try {
         FILE.getParentFile().mkdirs();
         Writer writer = new FileWriter(FILE);
         GSON.toJson(this, writer);
         writer.close();
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }
}
