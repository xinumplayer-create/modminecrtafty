/* Decompiler 49ms, total 263ms, lines 110 */
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
import java.util.Iterator;
import java.util.List;

public class RankConfig {
   private static final File FILE = new File("config/clanmod_ranks.json");
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
   private static RankConfig instance;
   public boolean autoRankEnabled = false;
   public List<com.clanmod.mod.config.RankConfig.RankEntry> ranks = new ArrayList();

   public static RankConfig getInstance() {
      if (instance == null) {
         instance = new RankConfig();
      }

      return instance;
   }

   public static void load() {
      try {
         if (FILE.exists()) {
            Reader reader = new FileReader(FILE);
            RankConfig loaded = (RankConfig)GSON.fromJson(reader, RankConfig.class);
            reader.close();
            if (loaded != null) {
               instance = loaded;
               if (instance.ranks == null) {
                  instance.ranks = new ArrayList();
               }
            }
         }
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public static void reload() {
      instance = null;
      load();
      if (instance == null) {
         instance = new RankConfig();
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

   public com.clanmod.mod.config.RankConfig.RankEntry getRankFor(int playerKills) {
      com.clanmod.mod.config.RankConfig.RankEntry best = null;
      Iterator var3 = this.ranks.iterator();

      while(true) {
         com.clanmod.mod.config.RankConfig.RankEntry r;
         do {
            do {
               if (!var3.hasNext()) {
                  return best;
               }

               r = (com.clanmod.mod.config.RankConfig.RankEntry)var3.next();
            } while(playerKills < r.kills);
         } while(best != null && r.kills <= best.kills);

         best = r;
      }
   }

   public com.clanmod.mod.config.RankConfig.RankEntry getNextRank(int playerKills) {
      com.clanmod.mod.config.RankConfig.RankEntry next = null;
      Iterator var3 = this.ranks.iterator();

      while(true) {
         com.clanmod.mod.config.RankConfig.RankEntry r;
         do {
            do {
               if (!var3.hasNext()) {
                  return next;
               }

               r = (com.clanmod.mod.config.RankConfig.RankEntry)var3.next();
            } while(r.kills <= playerKills);
         } while(next != null && r.kills >= next.kills);

         next = r;
      }
   }
}
