/* Decompiler 124ms, total 339ms, lines 115 */
package com.clanmod.mod.gui;

import com.clanmod.mod.events.AutoManager;
import net.minecraft.class_2561;
import net.minecraft.class_2583;
import net.minecraft.class_332;
import net.minecraft.class_342;
import net.minecraft.class_4185;
import net.minecraft.class_437;

public class ClanModScreen2 extends class_437 {
   private static final int BTN_H = 16;
   private static final int PAD = 6;
   private class_4185 adToggleBtn;
   private class_4185 clickToggleBtn;
   private class_342 adCmdField;

   public ClanModScreen2() {
      super(class_2561.method_43470("ClanMod — Авто"));
   }

   protected void method_25426() {
      int halfW = this.field_22789 / 2;
      int colW = halfW - 12;
      int leftX = 6;
      int rightX = halfW + 6;
      int y = 50;
      this.adCmdField = new class_342(this.field_22793, leftX, y, colW, 16, class_2561.method_43470(""));
      this.adCmdField.method_1880(256);
      this.adCmdField.method_47404(class_2561.method_43470("/команда или текст"));
      this.adCmdField.method_1852(AutoManager.adCommand);
      this.adCmdField.method_1863((val) -> {
         AutoManager.adCommand = val;
      });
      this.method_37063(this.adCmdField);
      int y = y + 24;
      this.adToggleBtn = class_4185.method_46430(this.getAdLabel(), (btn) -> {
         AutoManager.adEnabled = !AutoManager.adEnabled;
         btn.method_25355(this.getAdLabel());
      }).method_46434(leftX, y, colW, 16).method_46431();
      this.method_37063(this.adToggleBtn);
      y = 50;
      this.clickToggleBtn = class_4185.method_46430(this.getClickerLabel(), (btn) -> {
         AutoManager.clickEnabled = !AutoManager.clickEnabled;
         btn.method_25355(this.getClickerLabel());
      }).method_46434(rightX, y, colW, 16).method_46431();
      this.method_37063(this.clickToggleBtn);
      int bottomY = this.field_22790 - 26;
      int navSize = 20;
      this.method_37063(class_4185.method_46430(class_2561.method_43470("1"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen());
      }).method_46434(6, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("§e2"), (btn) -> {
      }).method_46434(6 + navSize + 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("3"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen3());
      }).method_46434(6 + (navSize + 2) * 2, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("4"), (btn) -> {
         this.field_22787.method_1507(new ClanModScreen4());
      }).method_46434(6 + (navSize + 2) * 3, bottomY, navSize, navSize).method_46431());
      this.method_37063(class_4185.method_46430(class_2561.method_43470("Close"), (btn) -> {
         this.method_25419();
      }).method_46434(this.field_22789 / 2 - 25, bottomY, 50, 16).method_46431());
   }

   public void method_25420(class_332 g, int mouseX, int mouseY, float partialTick) {
      g.method_25294(0, 0, this.field_22789, this.field_22790, -872415232);
   }

   public void method_25394(class_332 g, int mouseX, int mouseY, float delta) {
      super.method_25394(g, mouseX, mouseY, delta);
      int halfW = this.field_22789 / 2;
      int colW = halfW - 12;
      int leftX = 6;
      int rightX = halfW + 6;
      g.method_25294(halfW - 1, 10, halfW + 1, this.field_22790 - 35, 1728053247);
      g.method_27534(this.field_22793, class_2561.method_43470("— АВТО-ПАНЕЛЬ —").method_27696(class_2583.field_24360.method_10982(true)), this.field_22789 / 2, 12, -10496);
      g.method_27534(this.field_22793, class_2561.method_43470("РЕКЛАМА КЛАНА").method_27696(class_2583.field_24360.method_10982(true)), leftX + colW / 2, 28, -10748030);
      g.method_51439(this.field_22793, class_2561.method_43470("Команда / сообщение:"), leftX, 38, -5592406, false);
      g.method_51439(this.field_22793, class_2561.method_43470("§7Интервал: 2–3 минуты (случайно)"), leftX, 100, -5592406, false);
      int var10002 = AutoManager.adCount;
      g.method_51439(this.field_22793, class_2561.method_43470("§7Отправлено реклам: §f" + var10002), leftX, 114, -5592406, false);
      g.method_27534(this.field_22793, class_2561.method_43470("АВТО КЛИКЕР НА БОТА").method_27696(class_2583.field_24360.method_10982(true)), rightX + colW / 2, 28, -10772737);
      g.method_51439(this.field_22793, class_2561.method_43470("§7• Клик по центру экрана (ЛКМ)"), rightX, 76, -5592406, false);
      g.method_51439(this.field_22793, class_2561.method_43470("§7• Пауза → сдвиг -100px → ЛКМ"), rightX, 88, -5592406, false);
      g.method_51439(this.field_22793, class_2561.method_43470("§7• Интервал: 2–3 мин (случайно)"), rightX, 100, -5592406, false);
      var10002 = AutoManager.clickCount;
      g.method_51439(this.field_22793, class_2561.method_43470("§7Кликов выполнено: §f" + var10002), rightX, 114, -5592406, false);
      String adStatus = AutoManager.adEnabled ? "§aВКЛ" : "§cВЫКЛ";
      String clickStatus = AutoManager.clickEnabled ? "§aВКЛ" : "§cВЫКЛ";
      String adWork = AutoManager.isAdRunning() ? " §a[работает]" : " §7[ожид. закрытия GUI]";
      String clickWork = AutoManager.isClickRunning() ? " §a[работает]" : " §7[ожид. закрытия GUI]";
      g.method_51439(this.field_22793, class_2561.method_43470("Реклама: " + adStatus + adWork), 6, this.field_22790 - 52, -1, false);
      g.method_51439(this.field_22793, class_2561.method_43470("Кликер: " + clickStatus + clickWork), 6, this.field_22790 - 42, -1, false);
   }

   public void method_25419() {
      AutoManager.startAdOnClose();
      AutoManager.startClickOnClose();
      super.method_25419();
   }

   public boolean method_25421() {
      return false;
   }

   private class_2561 getAdLabel() {
      return class_2561.method_43470("Реклама: " + (AutoManager.adEnabled ? "§aВКЛ" : "§cВЫКЛ"));
   }

   private class_2561 getClickerLabel() {
      return class_2561.method_43470("Авто-кликер: " + (AutoManager.clickEnabled ? "§aВКЛ" : "§cВЫКЛ"));
   }
}
