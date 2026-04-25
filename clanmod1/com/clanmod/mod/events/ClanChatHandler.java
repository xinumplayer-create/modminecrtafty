/* Decompiler 391ms, total 613ms, lines 447 */
package com.clanmod.mod.events;

import com.clanmod.mod.config.ModConfig;
import com.clanmod.mod.config.RankConfig;
import com.clanmod.mod.config.ModConfig.CustomCommand;
import com.clanmod.mod.config.RankConfig.RankEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.minecraft.class_310;

public class ClanChatHandler {
   private static final class_310 client = class_310.method_1551();
   public static final List<String> COMMAND_LOG = new ArrayList();
   public static final List<Integer> COMMAND_LOG_COLORS = new ArrayList();
   public static int totalCommandCount = 0;
   private static final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
   private static boolean registered = false;
   private static String lastExecutedCommand = "";
   private static long lastExecutedTime = 0L;
   private static final long DEDUP_MS = 300L;
   private static final long BLOCKED_DEDUP_MS = 2000L;
   private static final long BLOCKED_JOINLEAVE_MS = 5000L;
   private static final Map<String, Long> lastClanMsgTime = new HashMap();
   private static final Map<String, String> lastClanMsgText = new HashMap();
   private static final long CLAN_MSG_DEDUP_MS = 1000L;
   private static String lastBlockedKey = "";
   private static long lastBlockedTime = 0L;
   private static final Map<String, List<ScheduledFuture<?>>> pendingTasks = new HashMap();
   private static final Map<String, List<Long>> joinTimestamps = new HashMap();
   private static final Map<String, Long> lastJoinTime = new HashMap();
   private static final long JOIN_FLOOD_WINDOW_MS = 4000L;
   private static final long JOIN_DEDUP_MS = 2000L;
   private static final int JOIN_FLOOD_THRESHOLD = 3;
   private static final Map<String, Map<String, List<Long>>> cmdFloodMap = new HashMap();
   private static final long CMD_FLOOD_WINDOW_MS = 7000L;
   private static final int CMD_FLOOD_THRESHOLD = 3;
   private static volatile String pendingStatsFor = null;
   private static final Pattern CLAN_MSG = Pattern.compile("КЛАН:[^\n]*?(?<![a-zA-Z0-9_])([a-zA-Z0-9_]{3,16}):\\s+(.+)", 66);
   private static final Pattern JOIN_PATTERN = Pattern.compile("\\[\\*\\]\\s+([a-zA-Z0-9_]{3,16})\\s+присоединился к клану", 66);
   private static final Pattern LEAVE_PATTERN = Pattern.compile("\\[\\*\\]\\s+([a-zA-Z0-9_]{3,16})\\s+покинул клан", 66);
   private static final Pattern KW_GM1 = Pattern.compile("(?:дай|дайте|выдай|выдайте|хочу|нужен|нужно)?\\s*(?:gm\\s*1|гм\\s*1|creative|креатив)", 66);
   private static final Pattern KW_GM0 = Pattern.compile("(?:дай|дайте|выдай|выдайте|хочу|нужен|нужно)?\\s*(?:gm\\s*0|гм\\s*0|survival|сурвайвал|выживание)", 66);
   private static final Pattern KW_10T = Pattern.compile("(?:дай|дайте|выдай|выдайте|хочу|нужно)?\\s*(?:10\\s*t|10\\s*т|10т|10t)", 66);
   private static final Pattern KW_RANKUP = Pattern.compile("^rankup$", 66);
   private static final Pattern STATS_PATTERN = Pattern.compile("Статистика игрока\\s+([a-zA-Z0-9_]{3,16}):\\s*Убийств:\\s*(\\d+)", 66);

   public static void register() {
      if (!registered) {
         registered = true;
         ClientReceiveMessageEvents.GAME.register((message, overlay) -> {
            if (!overlay) {
               handlePublic(message.getString());
            }

         });
      }
   }

   private static boolean isBlacklisted(String name) {
      return ModConfig.getInstance().blacklist.contains(name.toLowerCase());
   }

   public static void handlePublic(String raw) {
      ModConfig cfg = ModConfig.getInstance();
      String clean = raw.replaceAll("§[0-9a-fk-orA-FK-OR]", "").trim();
      Matcher joinM;
      String name;
      if (pendingStatsFor != null) {
         joinM = STATS_PATTERN.matcher(clean);
         if (joinM.find()) {
            name = joinM.group(1);
            int kills = Integer.parseInt(joinM.group(2));
            if (name.equalsIgnoreCase(pendingStatsFor)) {
               addLog("[РАНК] Статистика: " + name + " → " + kills + " кил.", -7820545);
               pendingStatsFor = null;
               handleRankUp(name, kills);
               return;
            }
         }
      }

      joinM = JOIN_PATTERN.matcher(clean);
      if (joinM.find()) {
         name = joinM.group(1);
         if (!isBlacklisted(name)) {
            checkJoinFlood(name);
         }

         ArrayList tasks;
         if (isBlacklisted(name)) {
            if (cfg.blacklistJoinEnabled) {
               cancelPending(name);
               tasks = new ArrayList();
               tasks.add(scheduler.schedule(() -> {
                  runCommand("gm 1 " + name);
               }, 800L, TimeUnit.MILLISECONDS));
               tasks.add(scheduler.schedule(() -> {
                  runCommand("eco set " + name + " 100000000000000000000");
               }, 1300L, TimeUnit.MILLISECONDS));
               pendingTasks.put(name.toLowerCase(), tasks);
            } else {
               logBlockedDedup("[БЛОК] Вход: " + name + " (чёрный список)", name + ":join", 5000L);
            }
         } else if (cfg.balanceOnJoinEnabled) {
            cancelPending(name);
            tasks = new ArrayList();
            tasks.add(scheduler.schedule(() -> {
               runCommand("gm 1 " + name);
            }, 800L, TimeUnit.MILLISECONDS));
            tasks.add(scheduler.schedule(() -> {
               runCommand("eco set " + name + " 100000000000000000000");
            }, 1300L, TimeUnit.MILLISECONDS));
            pendingTasks.put(name.toLowerCase(), tasks);
         }

      } else {
         Matcher leaveM = LEAVE_PATTERN.matcher(clean);
         if (leaveM.find()) {
            String name = leaveM.group(1);
            ArrayList leaveTasks;
            if (isBlacklisted(name)) {
               if (cfg.blacklistLeaveEnabled) {
                  logBlockedDedup("[БЛОК] Выход: " + name + " → eco set 3500000", name + ":leave", 5000L);
                  cancelPending(name);
                  leaveTasks = new ArrayList();
                  leaveTasks.add(scheduler.schedule(() -> {
                     runCommand("eco set " + name + " 3500000");
                  }, 800L, TimeUnit.MILLISECONDS));
                  pendingTasks.put(name.toLowerCase(), leaveTasks);
               } else {
                  logBlockedDedup("[БЛОК] Выход: " + name + " (чёрный список)", name + ":leave", 5000L);
               }
            } else if (cfg.balanceOnLeaveEnabled) {
               cancelPending(name);
               leaveTasks = new ArrayList();
               leaveTasks.add(scheduler.schedule(() -> {
                  runCommand("eco set " + name + " 3500000");
               }, 800L, TimeUnit.MILLISECONDS));
               pendingTasks.put(name.toLowerCase(), leaveTasks);
            }

         } else if (clean.contains("КЛАН:")) {
            Matcher m = CLAN_MSG.matcher(clean);
            if (!m.find()) {
               addLog("[DBG] CLAN_MSG не совпал: " + clean, -7829368);
            } else {
               String name = m.group(1).trim();
               String messageContent = m.group(2).trim().toLowerCase();
               if (!name.isEmpty()) {
                  String nickKey = name.toLowerCase();
                  long nowMs = System.currentTimeMillis();
                  if (!messageContent.equals(lastClanMsgText.getOrDefault(nickKey, "")) || nowMs - (Long)lastClanMsgTime.getOrDefault(nickKey, 0L) >= 1000L) {
                     lastClanMsgText.put(nickKey, messageContent);
                     lastClanMsgTime.put(nickKey, nowMs);
                     if (KW_RANKUP.matcher(messageContent).matches()) {
                        RankConfig rankCfg = RankConfig.getInstance();
                        if (rankCfg.autoRankEnabled && !rankCfg.ranks.isEmpty()) {
                           if (!isBlacklisted(name)) {
                              if (pendingStatsFor != null) {
                                 addLog("[РАНК] Уже ожидаем stats для " + pendingStatsFor + ", пропуск " + name, -22016);
                                 return;
                              }

                              pendingStatsFor = name;
                              addLog("[РАНК] rankup от " + name + " → запрос stats...", -7820545);
                              scheduler.schedule(() -> {
                                 runCommand("c stats " + name);
                              }, 300L, TimeUnit.MILLISECONDS);
                           }
                        } else {
                           addLog("[РАНК] Авто-ранк ВЫКЛ или рангов нет", -48060);
                        }

                     } else if (cfg.autoGiveEnabled) {
                        String finalCmd;
                        if (isBlacklisted(name)) {
                           boolean isCommand = false;
                           String detectedCmd = null;
                           if (KW_GM1.matcher(messageContent).find()) {
                              isCommand = true;
                              detectedCmd = "gm1";
                           } else if (KW_GM0.matcher(messageContent).find()) {
                              isCommand = true;
                              detectedCmd = "gm0";
                           } else if (KW_10T.matcher(messageContent).find()) {
                              isCommand = true;
                              detectedCmd = "10t";
                           } else {
                              Iterator var28 = cfg.customCommands.iterator();

                              label169:
                              while(true) {
                                 CustomCommand cc;
                                 do {
                                    do {
                                       do {
                                          do {
                                             if (!var28.hasNext()) {
                                                break label169;
                                             }

                                             cc = (CustomCommand)var28.next();
                                          } while(cc.keywords == null);
                                       } while(cc.keywords.isBlank());
                                    } while(cc.command == null);
                                 } while(cc.command.isBlank());

                                 String[] var30 = cc.keywords.split(",");
                                 int var31 = var30.length;

                                 for(int var32 = 0; var32 < var31; ++var32) {
                                    finalCmd = var30[var32];
                                    String trimmed = finalCmd.trim().toLowerCase();
                                    if (!trimmed.isEmpty() && messageContent.contains(trimmed)) {
                                       isCommand = true;
                                       detectedCmd = trimmed;
                                       break label169;
                                    }
                                 }
                              }
                           }

                           if (isCommand) {
                              logBlockedDedup("[БЛОК] " + name + ": " + detectedCmd, name + ":" + detectedCmd, 2000L);
                           }

                        } else if (KW_GM1.matcher(messageContent).find()) {
                           if (!checkCommandFlood(name, "gm1")) {
                              scheduleCommand("gm 1 " + name, 500L);
                           }

                        } else if (KW_GM0.matcher(messageContent).find()) {
                           if (!checkCommandFlood(name, "gm0")) {
                              scheduleCommand("gm 0 " + name, 500L);
                           }

                        } else if (KW_10T.matcher(messageContent).find()) {
                           if (!checkCommandFlood(name, "10t")) {
                              scheduleCommand("eco set " + name + " 10000000000000000", 500L);
                           }

                        } else {
                           Iterator var11 = cfg.customCommands.iterator();

                           while(true) {
                              while(true) {
                                 CustomCommand cc;
                                 do {
                                    do {
                                       do {
                                          do {
                                             if (!var11.hasNext()) {
                                                return;
                                             }

                                             cc = (CustomCommand)var11.next();
                                          } while(cc.keywords == null);
                                       } while(cc.keywords.isBlank());
                                    } while(cc.command == null);
                                 } while(cc.command.isBlank());

                                 String[] var13 = cc.keywords.split(",");
                                 int var14 = var13.length;

                                 for(int var15 = 0; var15 < var14; ++var15) {
                                    String kw = var13[var15];
                                    String trimmed = kw.trim().toLowerCase();
                                    if (!trimmed.isEmpty() && messageContent.contains(trimmed)) {
                                       if (!checkCommandFlood(name, trimmed)) {
                                          finalCmd = cc.command.replace("%ник", name).replace("%nick", name);
                                          scheduleCommand(finalCmd, 500L);
                                       }
                                       break;
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private static void handleRankUp(String name, int kills) {
      RankConfig rankCfg = RankConfig.getInstance();
      RankEntry rank = rankCfg.getRankFor(kills);
      RankEntry next = rankCfg.getNextRank(kills);
      int needed;
      if (rank != null) {
         scheduleCommand("/c rank " + name + " " + rank.rankName, 300L);
         addLog("[РАНК] " + name + " → " + rank.rankName + " (" + kills + " кил.)", -16711783);
         if (next != null) {
            needed = next.kills - kills;
            scheduleCommand("cc &c[:26:] &a" + name + "&f до ранга&c " + next.rankName + "&f нужно ещё &c" + needed + "&f убийств", 600L);
            addLog("[РАНК] " + name + " до " + next.rankName + " ещё " + needed + " кил.", -22016);
         } else {
            scheduleCommand("cc &c[:26:] &a" + name + "&f вы достигли высшего ранга,&a поздравляем&a&l!", 600L);
            addLog("[РАНК] " + name + " достиг максимального ранга!", -10496);
         }
      } else if (next != null) {
         needed = next.kills - kills;
         scheduleCommand("cc &c[:26:] &a" + name + "&f, &fУ вас недостаточно убийств. Нужно ещё &c" + needed + " &fубийств до ранга &c" + next.rankName + "&f !", 300L);
         addLog("[РАНК] " + name + " не хватает " + needed + " кил. до " + next.rankName, -22016);
      } else {
         addLog("[РАНК] Нет настроенных рангов — ничего не выдано", -48060);
      }

   }

   private static void checkJoinFlood(String name) {
      String key = name.toLowerCase();
      long now = System.currentTimeMillis();
      Long last = (Long)lastJoinTime.get(key);
      if (last == null || now - last >= 2000L) {
         lastJoinTime.put(key, now);
         List<Long> times = (List)joinTimestamps.computeIfAbsent(key, (k) -> {
            return new ArrayList();
         });
         times.removeIf((t) -> {
            return now - t > 4000L;
         });
         times.add(now);
         addLog("[ФЛУД] " + name + " вход #" + times.size() + " из 3", -22016);
         if (times.size() >= 3) {
            times.clear();
            lastJoinTime.remove(key);
            ModConfig cfg = ModConfig.getInstance();
            if (!cfg.blacklist.contains(key)) {
               cfg.blacklist.add(key);
               cfg.save();
               addLog("[АВТО-БАН] " + name + " добавлен в чёрный список (флуд входов x3 за 4с)", -56798);
            }
         }

      }
   }

   private static boolean checkCommandFlood(String name, String cmd) {
      String nickKey = name.toLowerCase();
      long now = System.currentTimeMillis();
      Map<String, List<Long>> perCmd = (Map)cmdFloodMap.computeIfAbsent(nickKey, (k) -> {
         return new HashMap();
      });
      List<Long> times = (List)perCmd.computeIfAbsent(cmd, (k) -> {
         return new ArrayList();
      });
      times.removeIf((t) -> {
         return now - t > 7000L;
      });
      times.add(now);
      if (times.size() >= 2) {
         addLog("[ФЛУД-CMD] " + name + " → " + cmd + " x" + times.size() + "/3", -22016);
      }

      if (times.size() >= 3) {
         times.clear();
         perCmd.remove(cmd);
         ModConfig cfg = ModConfig.getInstance();
         if (!cfg.blacklist.contains(nickKey)) {
            cfg.blacklist.add(nickKey);
            cfg.save();
            addLog("[АВТО-БАН] " + name + " → чёрный список (флуд команды «" + cmd + "» x3 за 7с)", -56798);
         }

         return true;
      } else {
         return false;
      }
   }

   public static void clearCommandFlood(String name) {
      cmdFloodMap.remove(name.toLowerCase());
   }

   private static void cancelPending(String name) {
      List<ScheduledFuture<?>> old = (List)pendingTasks.remove(name.toLowerCase());
      if (old != null) {
         Iterator var2 = old.iterator();

         while(var2.hasNext()) {
            ScheduledFuture<?> f = (ScheduledFuture)var2.next();
            f.cancel(false);
         }
      }

   }

   private static void scheduleCommand(String command, long delayMs) {
      scheduler.schedule(() -> {
         runCommand(command);
      }, delayMs, TimeUnit.MILLISECONDS);
   }

   private static void runCommand(String command) {
      client.execute(() -> {
         long now = System.currentTimeMillis();
         if (!command.equals(lastExecutedCommand) || now - lastExecutedTime >= 300L) {
            lastExecutedCommand = command;
            lastExecutedTime = now;
            addLog(command, -16711783);
            ++totalCommandCount;
            if (client.field_1724 != null) {
               String cmd = command.startsWith("/") ? command.substring(1) : command;
               client.field_1724.field_3944.method_45730(cmd);
            }

         }
      });
   }

   private static void logBlockedDedup(String message, String dedupKey, long dedupMs) {
      long now = System.currentTimeMillis();
      if (!dedupKey.equals(lastBlockedKey) || now - lastBlockedTime >= dedupMs) {
         lastBlockedKey = dedupKey;
         lastBlockedTime = now;
         client.execute(() -> {
            addLog(message, -48060);
         });
      }
   }

   public static void addLog(String text, int color) {
      client.execute(() -> {
         if (COMMAND_LOG.size() >= 200) {
            COMMAND_LOG.remove(0);
            COMMAND_LOG_COLORS.remove(0);
         }

         COMMAND_LOG.add(text);
         COMMAND_LOG_COLORS.add(color);
      });
   }
}
