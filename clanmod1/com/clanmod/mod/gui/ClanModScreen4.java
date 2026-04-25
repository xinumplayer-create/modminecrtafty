/* Decompiler 241ms, total 466ms, lines 336 */
package com.clanmod.mod.gui;

import com.clanmod.mod.events.ClanChatHandler;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_268;
import net.minecraft.class_269;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;
import net.minecraft.class_5251;
import net.minecraft.class_640;

public class ClanModScreen4 extends class_437 {
   private static final int BTN_H = 16;
   private static final int PAD = 10;
   private class_342 nickField;
   private String lastInput = "";
   private List<String> suggestions = new ArrayList();
   private String foundNick = "";
   private String foundPrefix = "";
   private String foundSuffix = "";
   private String copiedLabel = "";
   private long copiedTime = 0L;

   public ClanModScreen4() {
      super(class_2561.method_43470("ClanMod — Utilities"));
   }

   protected void method_25426() {
      int bottomY = this.field_22790 - 26;
      int navSize = 20;
      int navX = 6;
      int halfW = this.field_22789 / 2;
      int leftX = 10;
      int leftW = halfW - 20;
      int shortW = leftW / 2 - 2;
      int rightColX = leftX + shortW + 4;
      int rightColW = leftW - shortW - 4;
      int nickW = shortW;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("1"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen());
      }).method_46434(navX, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("2"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen2());
      }).method_46434(navX + navSize + 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("3"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen3());
      }).method_46434(navX + (navSize + 2) * 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§e4"), (btn) -> {
      }).method_46434(navX + (navSize + 2) * 3, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Close"), (btn) -> {
         this.method_25419();
      }).method_46434(this.field_22789 / 2 - 25, bottomY, 50, 16).method_46431());
      int block1Y = 20;
      int row2Y = block1Y + 16 + 3;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§bПоставить Префикс"), (btn) -> {
         if (this.field_22787.field_1724 != null) {
            String cmd = this.foundPrefix.trim().isEmpty() ? "tab prefix clear" : "tab prefix set " + this.foundPrefix;
            this.field_22787.field_1724.field_3944.method_45730(cmd);
            ClanChatHandler.addLog("[TAB] /" + cmd, -16711766);
         }

      }).method_46434(leftX, block1Y, shortW, 16).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§dПоставить Суффикс"), (btn) -> {
         if (this.field_22787.field_1724 != null) {
            String cmd = this.foundSuffix.trim().isEmpty() ? "tab suffix clear" : "tab suffix set " + this.foundSuffix;
            this.field_22787.field_1724.field_3944.method_45730(cmd);
            ClanChatHandler.addLog("[TAB] /" + cmd, -16711766);
         }

      }).method_46434(leftX, row2Y, shortW, 16).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§aCopy Tab"), (btn) -> {
         this.copyTabToFile();
      }).method_46434(rightColX, block1Y, rightColW, 16).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§eОткрыть папку"), (btn) -> {
         try {
            File dir = new File("config/clanmod");
            dir.mkdirs();
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
               (new ProcessBuilder(new String[]{"explorer.exe", dir.getAbsolutePath()})).start();
            } else if (os.contains("mac")) {
               (new ProcessBuilder(new String[]{"open", dir.getAbsolutePath()})).start();
            } else {
               (new ProcessBuilder(new String[]{"xdg-open", dir.getAbsolutePath()})).start();
            }
         } catch (Exception var3) {
         }

      }).method_46434(rightColX, row2Y, rightColW, 16).method_46431());
      int nickY = row2Y + 16 + 14;
      this.nickField = new class_342(this.field_22793, leftX, nickY, shortW, 16, class_2561.method_43470(""));
      this.nickField.method_1880(64);
      this.nickField.method_47404(class_2561.method_43470("Введите ник..."));
      this.nickField.method_1852(this.foundNick);
      this.nickField.method_1863((val) -> {
         String trimmed = val.trim();
         if (!trimmed.equals(this.lastInput)) {
            this.lastInput = trimmed;
            this.suggestions.clear();
            if (!trimmed.isEmpty() && this.field_22787.method_1562() != null) {
               String lower = trimmed.toLowerCase();
               Iterator var4 = this.field_22787.method_1562().method_2880().iterator();

               while(var4.hasNext()) {
                  class_640 info = (class_640)var4.next();
                  String nick = getNick(info);
                  if (nick.toLowerCase().startsWith(lower) && !nick.equalsIgnoreCase(trimmed)) {
                     this.suggestions.add(nick);
                     if (this.suggestions.size() >= 5) {
                        break;
                     }
                  }
               }
            }

            this.lookupPlayer(trimmed);
            this.rebuildScreen();
         }

      });
      this.method_37063(this.nickField);
      int suggY = nickY + 16 + 2;

      int infoY;
      for(infoY = 0; infoY < this.suggestions.size(); ++infoY) {
         String chosen = (String)this.suggestions.get(infoY);
         this.method_37063(class_4185.method_46430(class_2561.method_43470("§f" + chosen), (btn) -> {
            this.nickField.method_1852(chosen);
            this.lastInput = chosen;
            this.suggestions.clear();
            this.lookupPlayer(chosen);
            this.rebuildScreen();
         }).method_46434(leftX, suggY + infoY * 18, nickW, 16).method_46431());
      }

      infoY = suggY + 90 + 4;
      if (!this.foundNick.isEmpty()) {
         this.method_37063(class_4185.method_46430(class_2561.method_43470("§fCopy Prefix"), (btn) -> {
            if (!this.foundPrefix.trim().isEmpty()) {
               class_310.method_1551().field_1774.method_1455(this.foundPrefix);
               this.copiedLabel = "prefix";
               this.copiedTime = System.currentTimeMillis();
            }

         }).method_46434(leftX, infoY + 30, shortW, 16).method_46431());
         this.method_37063(class_4185.method_46430(class_2561.method_43470("§fCopy Suffix"), (btn) -> {
            if (!this.foundSuffix.trim().isEmpty()) {
               class_310.method_1551().field_1774.method_1455(this.foundSuffix);
               this.copiedLabel = "suffix";
               this.copiedTime = System.currentTimeMillis();
            }

         }).method_46434(leftX, infoY + 30 + 16 + 2, shortW, 16).method_46431());
      }

   }

   private void lookupPlayer(String nick) {
      this.foundNick = nick;
      this.foundPrefix = "";
      this.foundSuffix = "";
      if (!nick.isEmpty() && this.field_22787.field_1687 != null) {
         class_268 team = this.field_22787.field_1687.method_8428().method_1164(nick);
         if (team != null) {
            this.foundPrefix = toLegacy(team.method_1144());
            this.foundSuffix = toLegacy(team.method_1136());
         }

      }
   }

   private void rebuildScreen() {
      this.method_37067();
      this.method_25426();
   }

   public void method_25420(class_332 g, int mouseX, int mouseY, float partialTick) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -872415232);
   }

   public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
      super.method_25394(g, mouseX, mouseY, delta);
      int leftX = 10;
      int halfW = this.field_22789 / 2;
      int leftW = halfW - 20;
      g.method_27534(this.field_22793, class_2561.method_43470("— UTILITIES —").method_27696(class_2583.field_24360.method_10982(true)), this.field_22789 / 2, 6, -10496);
      g.method_51439(this.field_22793, class_2561.method_43470("PREFIX / SUFFIX").method_27696(class_2583.field_24360.method_10982(true)), leftX, 11, -10772737, false);
      int block1Y = 20;
      int row2Y = block1Y + 16 + 3;
      int nickY = row2Y + 16 + 14;
      g.method_51439(this.field_22793, class_2561.method_43470("ПОИСК ИГРОКА").method_27696(class_2583.field_24360.method_10982(true)), leftX, nickY - 9, -10772737, false);
      int suggY = nickY + 16 + 2;
      int infoY = suggY + 90 + 4;
      if (!this.foundNick.isEmpty()) {
         int clipRight = leftX + leftW;
         g.method_51433(this.field_22793, "§7Игрок: §f" + this.foundNick, leftX, infoY, -1, false);
         g.method_44379(leftX, infoY + 8, clipRight, infoY + 30);
         int prefLabelW = this.field_22793.method_1727("Prefix:");
         g.method_51433(this.field_22793, "Prefix:", leftX, infoY + 10, -1, false);
         String prefVal = this.foundPrefix.trim().isEmpty() ? "§8(нет)" : this.foundPrefix;
         g.method_51433(this.field_22793, " " + prefVal, leftX + prefLabelW + 2, infoY + 10, -1, false);
         int sufLabelW = this.field_22793.method_1727("Suffix:");
         g.method_51433(this.field_22793, "Suffix:", leftX, infoY + 20, -1, false);
         String sufVal = this.foundSuffix.trim().isEmpty() ? "§8(нет)" : this.foundSuffix;
         g.method_51433(this.field_22793, " " + sufVal, leftX + sufLabelW + 2, infoY + 20, -1, false);
         g.method_44380();
         if (!this.copiedLabel.isEmpty() && System.currentTimeMillis() - this.copiedTime < 1500L) {
            g.method_51433(this.field_22793, "§a✔ скопировано: " + this.copiedLabel, leftX, infoY + 30 + 32 + 6, -11141291, false);
         }
      }

   }

   private static String getNick(class_640 info) {
      try {
         Field f = info.method_2966().getClass().getDeclaredField("name");
         f.setAccessible(true);
         Object v = f.get(info.method_2966());
         return v != null ? v.toString() : "";
      } catch (Exception var3) {
         return "";
      }
   }

   private static String toLegacy(class_2561 component) {
      StringBuilder sb = new StringBuilder();
      component.method_27658((style, text) -> {
         if (style.method_10973() != null) {
            class_5251 tc = style.method_10973();
            class_124 fmt = class_124.method_533(tc.method_27721());
            if (fmt != null) {
               sb.append("&").append(fmt.method_36145());
            } else {
               sb.append(String.format("&#%06X", tc.method_27716() & 16777215));
            }
         }

         if (Boolean.TRUE.equals(style.method_10984())) {
            sb.append("&l");
         }

         if (Boolean.TRUE.equals(style.method_10966())) {
            sb.append("&o");
         }

         if (Boolean.TRUE.equals(style.method_10965())) {
            sb.append("&n");
         }

         if (Boolean.TRUE.equals(style.method_10986())) {
            sb.append("&m");
         }

         if (Boolean.TRUE.equals(style.method_10987())) {
            sb.append("&k");
         }

         sb.append(text);
         return Optional.empty();
      }, class_2583.field_24360);
      return sb.toString();
   }

   private void copyTabToFile() {
      if (this.field_22787 != null && this.field_22787.method_1562() != null) {
         File dir = new File("config/clanmod");
         dir.mkdirs();
         File file = new File(dir, "tab_players.txt");

         try {
            PrintWriter pw = new PrintWriter(new FileWriter(file, false));

            try {
               class_269 scoreboard = this.field_22787.field_1687 != null ? this.field_22787.field_1687.method_8428() : null;
               Iterator var5 = this.field_22787.method_1562().method_2880().iterator();

               while(true) {
                  if (!var5.hasNext()) {
                     int count = this.field_22787.method_1562().method_2880().size();
                     ClanChatHandler.addLog("[TAB] Сохранено " + count + " → config/clanmod/tab_players.txt", -16711766);
                     break;
                  }

                  class_640 info = (class_640)var5.next();
                  String nick = getNick(info);
                  String prefix = "";
                  String suffix = "";
                  if (scoreboard != null) {
                     class_268 team = scoreboard.method_1164(nick);
                     if (team != null) {
                        prefix = toLegacy(team.method_1144());
                        suffix = toLegacy(team.method_1136());
                     }
                  }

                  pw.println((prefix + nick + suffix).trim());
               }
            } catch (Throwable var12) {
               try {
                  pw.close();
               } catch (Throwable var11) {
                  var12.addSuppressed(var11);
               }

               throw var12;
            }

            pw.close();
         } catch (IOException var13) {
            ClanChatHandler.addLog("[TAB] Ошибка: " + var13.getMessage(), -48060);
         }

      } else {
         ClanChatHandler.addLog("[TAB] Нет подключения к серверу", -48060);
      }
   }

   public boolean method_25421() {
      return false;
   }
}
