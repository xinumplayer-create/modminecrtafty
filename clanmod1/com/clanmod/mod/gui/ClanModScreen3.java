/* Decompiler 355ms, total 584ms, lines 303 */
package com.clanmod.mod.gui;

import com.clanmod.mod.config.RankConfig;
import com.clanmod.mod.config.RankConfig.RankEntry;
import java.io.File;
import java.util.List;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_310;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class ClanModScreen3 extends class_437 {
   private static final int BTN_H = 20;
   private static final int PAD = 16;
   private static final int ROW_H = 20;
   private static final int VISIBLE = 14;
   private int scrollOffset = 0;
   private int editingIndex = -1;
   private class_342 rankNameField;
   private class_342 rankKillsField;

   public ClanModScreen3() {
      super(class_2561.method_43470("ClanMod — Авто-ранк"));
      RankConfig.reload();
   }

   protected void method_25426() {
      int w = this.field_22789;
      int h = this.field_22790;
      int halfW = w / 2;
      int leftW = halfW - 32;
      int leftX = 16;
      int rightX = halfW + 16;
      int rightW = halfW - 32;
      this.method_37063(class_4185.method_46430(this.getToggleLabel(), (btn) -> {
         RankConfig cfg = RankConfig.getInstance();
         cfg.autoRankEnabled = !cfg.autoRankEnabled;
         cfg.save();
         btn.method_25355(this.getToggleLabel());
      }).method_46434(rightX, 40, rightW, 20).method_46431());
      int addY = 40;
      int killsW = 55;
      int addBtnW = 24;
      int nameW = leftW - killsW - addBtnW * 2 - 14;
      this.rankNameField = new class_342(this.field_22793, leftX, addY, nameW, 20, class_2561.method_43470(""));
      this.rankNameField.method_1880(256);
      this.rankNameField.method_47404(class_2561.method_43470("Название ранга"));
      this.method_37063(this.rankNameField);
      this.rankKillsField = new class_342(this.field_22793, leftX + nameW + 4, addY, killsW, 20, class_2561.method_43470(""));
      this.rankKillsField.method_1880(10);
      this.rankKillsField.method_47404(class_2561.method_43470("Киллов"));
      this.method_37063(this.rankKillsField);
      class_2561 addBtnLabel = this.editingIndex >= 0 ? class_2561.method_43470("§e✓") : class_2561.method_43470("§a+");
      this.method_37063(class_4185.method_46430(addBtnLabel, (btn) -> {
         this.addRank();
      }).method_46434(leftX + nameW + 4 + killsW + 4, addY, addBtnW, 20).method_46431());
      if (this.editingIndex >= 0) {
         List<RankEntry> allRanks = RankConfig.getInstance().ranks;
         if (this.editingIndex < allRanks.size()) {
            RankEntry editing = (RankEntry)allRanks.get(this.editingIndex);
            this.rankNameField.method_1852(editing.rankName);
            this.rankKillsField.method_1852(String.valueOf(editing.kills));
         } else {
            this.editingIndex = -1;
         }
      }

      int listStartY = addY + 20 + 14;
      List<RankEntry> ranks = RankConfig.getInstance().ranks;
      int editBtnW = addBtnW;
      int rightEdge = halfW - 16 - 2;
      int deleteX = rightEdge - addBtnW;
      int editX = deleteX - addBtnW - 2;

      int bottomY;
      for(bottomY = this.scrollOffset; bottomY < Math.min(this.scrollOffset + 14, ranks.size()); ++bottomY) {
         int y = listStartY + (bottomY - this.scrollOffset) * 20;
         this.method_37063(class_4185.method_46430(class_2561.method_43470("§e✎"), (btn) -> {
            this.editingIndex = bottomY;
            this.rebuildScreen();
         }).method_46434(editX, y + 1, editBtnW, 16).method_46431());
         this.method_37063(class_4185.method_46430(class_2561.method_43470("§cX"), (btn) -> {
            RankConfig cfg = RankConfig.getInstance();
            if (bottomY < cfg.ranks.size()) {
               cfg.ranks.remove(bottomY);
               cfg.save();
               if (this.editingIndex == bottomY) {
                  this.editingIndex = -1;
               } else if (this.editingIndex > bottomY) {
                  --this.editingIndex;
               }

               if (this.scrollOffset > 0 && this.scrollOffset >= cfg.ranks.size()) {
                  --this.scrollOffset;
               }

               this.rebuildScreen();
            }

         }).method_46434(deleteX, y + 1, editBtnW, 16).method_46431());
      }

      if (ranks.size() > 14) {
         bottomY = halfW - 16 - 16;
         int listH = 280;
         this.method_37063(class_4185.method_46430(class_2561.method_43470("▲"), (btn) -> {
            if (this.scrollOffset > 0) {
               --this.scrollOffset;
               this.rebuildScreen();
            }

         }).method_46434(bottomY, listStartY, 14, 20).method_46431());
         this.method_37063(class_4185.method_46430(class_2561.method_43470("▼"), (btn) -> {
            if (this.scrollOffset + 14 < RankConfig.getInstance().ranks.size()) {
               ++this.scrollOffset;
               this.rebuildScreen();
            }

         }).method_46434(bottomY, listStartY + listH - 20, 14, 20).method_46431());
      }

      bottomY = h - 26;
      int navSize = 20;
      int navX = 6;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("1"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen());
      }).method_46434(navX, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("2"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen2());
      }).method_46434(navX + navSize + 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§e3"), (btn) -> {
      }).method_46434(navX + (navSize + 2) * 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("4"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen4());
      }).method_46434(navX + (navSize + 2) * 3, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Close"), (btn) -> {
         this.method_25419();
      }).method_46434(w / 2 - 25, bottomY, 50, 20).method_46431());
      int rightX2 = halfW + 16;
      int rightW2 = halfW - 32;
      int btnW2 = (rightW2 - 4) / 2;
      int actionY = bottomY - 20 - 4;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§e\ud83d\udcc1 Папка"), (btn) -> {
         try {
            File configDir = new File(class_310.method_1551().field_1697, "config");
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("win")) {
               (new ProcessBuilder(new String[]{"explorer.exe", configDir.getAbsolutePath()})).start();
            } else if (os.contains("mac")) {
               (new ProcessBuilder(new String[]{"open", configDir.getAbsolutePath()})).start();
            } else {
               (new ProcessBuilder(new String[]{"xdg-open", configDir.getAbsolutePath()})).start();
            }
         } catch (Exception var3) {
         }

      }).method_46434(rightX2, actionY, btnW2, 20).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§a\ud83d\udd04 Обновить"), (btn) -> {
         RankConfig.reload();
         this.scrollOffset = 0;
         this.rebuildScreen();
      }).method_46434(rightX2 + btnW2 + 4, actionY, btnW2, 20).method_46431());
   }

   private void rebuildScreen() {
      this.method_37067();
      this.method_25426();
   }

   private void addRank() {
      String name = this.rankNameField.method_1882().trim();
      String kills = this.rankKillsField.method_1882().trim();
      if (!name.isEmpty() && !kills.isEmpty()) {
         int k;
         try {
            k = Integer.parseInt(kills);
         } catch (NumberFormatException var5) {
            return;
         }

         RankConfig cfg = RankConfig.getInstance();
         if (this.editingIndex >= 0 && this.editingIndex < cfg.ranks.size()) {
            cfg.ranks.set(this.editingIndex, new RankEntry(name, k));
            this.editingIndex = -1;
         } else {
            cfg.ranks.removeIf((r) -> {
               return r.rankName.equalsIgnoreCase(name);
            });
            cfg.ranks.add(new RankEntry(name, k));
         }

         cfg.ranks.sort((a, b) -> {
            return Integer.compare(b.kills, a.kills);
         });
         cfg.save();
         this.rankNameField.method_1852("");
         this.rankKillsField.method_1852("");
         this.scrollOffset = 0;
         this.rebuildScreen();
      }
   }

   public void method_25420(class_332 g, int mouseX, int mouseY, float partialTick) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -872415232);
   }

   public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
      super.method_25394(g, mouseX, mouseY, delta);
      int w = this.field_22789;
      int halfW = w / 2;
      int leftX = 16;
      int leftW = halfW - 32;
      int rightX = halfW + 16;
      int rightW = halfW - 32;
      g.method_25294(halfW - 1, 10, halfW + 1, this.field_22790 - 35, 1728053247);
      g.method_27534(this.field_22793, class_2561.method_43470("— АВТО-РАНК —").method_27696(class_2583.field_24360.method_10982(true)), w / 2, 12, -10496);
      g.method_27534(this.field_22793, class_2561.method_43470("СПИСОК РАНГОВ").method_27696(class_2583.field_24360.method_10982(true)), leftX + leftW / 2, 28, -10748030);
      int addY = 40;
      int killsW = 55;
      int addBtnW = 24;
      int nameW = leftW - killsW - addBtnW * 2 - 14;
      int listStartY = addY + 20 + 2;
      if (this.editingIndex >= 0) {
         g.method_51433(this.field_22793, "§eРедактирование...", leftX, listStartY, -8960, false);
      } else {
         g.method_51433(this.field_22793, "§7Название", leftX, listStartY, -5592406, false);
         g.method_51433(this.field_22793, "§7Киллов", leftX + nameW + 4, listStartY, -5592406, false);
      }

      List<RankEntry> ranks = RankConfig.getInstance().ranks;
      int rowStartY = listStartY + 12;

      int i;
      for(i = this.scrollOffset; i < Math.min(this.scrollOffset + 14, ranks.size()); ++i) {
         RankEntry r = (RankEntry)ranks.get(i);
         int y = rowStartY + (i - this.scrollOffset) * 20;
         if (i == this.editingIndex) {
            g.method_25294(leftX, y - 1, halfW - 16 - 18, y + 20 - 3, 1157618944);
         } else if (i % 2 == 0) {
            g.method_25294(leftX, y - 1, halfW - 16 - 18, y + 20 - 3, 587202559);
         }

         String displayName;
         for(displayName = r.rankName; displayName.length() > 1 && this.field_22793.method_1727("§f" + displayName) > nameW - 4; displayName = displayName.substring(0, displayName.length() - 1)) {
         }

         if (!displayName.equals(r.rankName)) {
            displayName = displayName + "…";
         }

         g.method_51433(this.field_22793, "§f" + displayName, leftX + 2, y + 4, -1, false);
         g.method_51433(this.field_22793, "§e" + r.kills, leftX + nameW + 6, y + 4, -1, false);
      }

      i = ranks.isEmpty() ? 0 : Math.min(this.scrollOffset + 14, ranks.size()) - this.scrollOffset;
      g.method_51433(this.field_22793, "§7Всего: §f" + ranks.size() + "  §7Показано: §f" + (ranks.isEmpty() ? 0 : this.scrollOffset + 1) + "–" + (this.scrollOffset + i), leftX, this.field_22790 - 50, -5592406, false);
      g.method_27534(this.field_22793, class_2561.method_43470("УПРАВЛЕНИЕ").method_27696(class_2583.field_24360.method_10982(true)), rightX + rightW / 2, 28, -10772737);
      int ry = 70;
      g.method_51433(this.field_22793, "§7Игрок пишет §frankup§7 в клан-чат.", rightX, ry, -5592406, false);
      g.method_51433(this.field_22793, "§7Мод запрашивает §f/c stats ник§7,", rightX, ry + 12, -5592406, false);
      g.method_51433(this.field_22793, "§7определяет киллы и выдаёт ранг.", rightX, ry + 24, -5592406, false);
      g.method_51433(this.field_22793, "§7Если не хватает — пишет сколько нужно.", rightX, ry + 36, -5592406, false);
      g.method_25294(rightX, ry + 52, rightX + rightW, ry + 53, 1157627903);
      g.method_51433(this.field_22793, "§7Форматы клан-чата:", rightX, ry + 58, -5592406, false);
      g.method_51433(this.field_22793, "§fКЛАН:  §bНик§7: rankup", rightX, ry + 70, -3355444, false);
      g.method_51433(this.field_22793, "§fКЛАН: §7[Префикс] §bНик§7: rankup", rightX, ry + 82, -3355444, false);
      g.method_25294(rightX, ry + 96, rightX + rightW, ry + 97, 1157627903);
      g.method_51433(this.field_22793, "§7Файл: §fconfig/clanmod_ranks.json", rightX, ry + 103, -5592406, false);
      g.method_51433(this.field_22793, "§7Передайте другу для синхронизации.", rightX, ry + 115, -5592406, false);
   }

   public boolean method_25401(double mouseX, double mouseY, double scrollX, double scrollY) {
      if (mouseX < (double)this.field_22789 / 2.0D) {
         int size = RankConfig.getInstance().ranks.size();
         if (scrollY < 0.0D && this.scrollOffset + 14 < size) {
            ++this.scrollOffset;
            this.rebuildScreen();
            return true;
         }

         if (scrollY > 0.0D && this.scrollOffset > 0) {
            --this.scrollOffset;
            this.rebuildScreen();
            return true;
         }
      }

      return super.method_25401(mouseX, mouseY, scrollX, scrollY);
   }

   public boolean method_25421() {
      return false;
   }

   private class_2561 getToggleLabel() {
      boolean en = RankConfig.getInstance().autoRankEnabled;
      return class_2561.method_43470("Авто-ранк: " + (en ? "§aВКЛ" : "§cВЫКЛ"));
   }
}
